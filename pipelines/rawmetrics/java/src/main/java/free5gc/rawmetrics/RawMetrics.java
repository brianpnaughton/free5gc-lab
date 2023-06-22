package free5gc.rawmetrics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.io.kafka.KafkaRecord;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.DefaultValueFactory;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.values.PCollectionTuple;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TupleTagList;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;

import free5gc.cadvisor.WriteMetrics;
import free5gc.sessions.WriteSessions;

public class RawMetrics {
    static ArrayList <String> containerList = new ArrayList<String>();

    private static void buildContainerList(){
        containerList.add("webserver");
        containerList.add("mongodb");
        containerList.add("nrf");
        containerList.add("amf");
        containerList.add("ausf");
        containerList.add("nssf");
        containerList.add("pcf");
        containerList.add("smf");
        containerList.add("udm");
        containerList.add("udr");
        containerList.add("ueransim");
    }

    public interface RawMetricsOptions extends PipelineOptions {
        @Description("If set to true results will be printed to stdout")
        @Default.Boolean(false)
        boolean getTest();
        void setTest(boolean value);

        @Description("Kafka Server Address")
        @Default.String("10.211.55.3:29092")
        String getKafkaServer();
        void setKafkaServer(String value);

        @Description("BigQuery project")
        @Default.String("free5gc-project")
        String getBQProject();
        void setBQProject(String project);

        @Description("BigQuery dataset name")
        @Default.String("free5gc-dataset")
        String getBQDataset();
        void setBQDataset(String dataset);

        @Description("BigQuery table name")
        @Default.InstanceFactory(BigQueryTableFactory.class)
        String getBQTable();
        void setBQTable(String table);

        /** Returns the job name as the default BigQuery table name. */
        class BigQueryTableFactory implements DefaultValueFactory<String> {
            @Override
            public String create(PipelineOptions options) {
            return options.getJobName().replace('-', '_');
            }
        }
    }

    public static void main(String[] args) {
        buildContainerList();

        RawMetricsOptions options =  PipelineOptionsFactory.fromArgs(args).withValidation().as(RawMetricsOptions.class);
        final Pipeline p = Pipeline.create(options);

        final TupleTag<String> cadvisorTag = new TupleTag<String>("cadvisor"){};
        final TupleTag<String> logTag = new TupleTag<String>("logs"){};
        final TupleTag<String> sessionsTag = new TupleTag<String>("sessions"){};
        final TupleTag<String> syslogTag = new TupleTag<String>("syslog"){};
        final TupleTag<String> emptyOutputTag = new TupleTag<String>("empty") {};
        final TupleTag<String> additionalOutputTag = new TupleTag<String>("extra") {};

        List<String> topics = Arrays.asList("cadvisor", "free5gc-log", "sessions", "syslog-messages");
        
        PCollectionTuple topicCollection=p.apply(
            KafkaIO.<Long, String>read()
                .withBootstrapServers(options.getKafkaServer()) 
                .withTopics(topics)
                .withKeyDeserializer(LongDeserializer.class)
                .withValueDeserializer(StringDeserializer.class))
            .apply("fork topics",
                ParDo.of(
                    new DoFn<KafkaRecord<Long,String>, String>() {
                        @ProcessElement
                        public void processElement(ProcessContext c) {
                            // tag kafka messages by topic
                            if (c.element().getTopic().contains("cadvisor")){
                                c.output(cadvisorTag, c.element().getKV().getValue().toString());
                            }else if (c.element().getTopic().contains("free5gc-log")){
                                c.output(logTag, c.element().getKV().getValue());
                            }else if (c.element().getTopic().contains("sessions")){
                                c.output(sessionsTag, c.element().getKV().getValue());
                            }else if (c.element().getTopic().contains("syslog-messages")){
                                c.output(syslogTag, c.element().getKV().getValue());
                            }
                        }
                    }
                ).withOutputTags(emptyOutputTag, TupleTagList.of(cadvisorTag).and(logTag).and(sessionsTag).and(syslogTag).and(additionalOutputTag))
            );

        topicCollection.get(cadvisorTag)
            .apply(ParDo.of(new free5gc.cadvisor.CAdvisorMetric.FormatMetrics(containerList)))
            .apply("write cadvisor output", new WriteMetrics<>(options.getTest(), options.getBQProject(), options.getBQDataset(), options.getBQTable()));

        topicCollection.get(sessionsTag)
            .apply(ParDo.of(new free5gc.sessions.SessionMetric.FormatSessions()))
            .apply("write sessions output", new WriteSessions<>(options.getTest(), options.getBQProject(), options.getBQDataset(), options.getBQTable()));

        topicCollection.get(syslogTag)
            .apply(ParDo.of(new free5gc.syslog.SyslogMessage.FormatSyslog()));

        topicCollection.get(logTag)
            .apply(ParDo.of(new free5gc.logs.LogMessage.FormatLogs()));

        p.run().waitUntilFinish();    
    }    
}
