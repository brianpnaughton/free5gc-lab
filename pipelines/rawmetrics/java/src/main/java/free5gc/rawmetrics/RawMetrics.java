package free5gc.rawmetrics;

import java.util.ArrayList;
import java.util.Collections;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.options.Default;
import org.apache.beam.sdk.options.DefaultValueFactory;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Values;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.beam.sdk.transforms.ParDo;

import free5gc.cadvisor.WriteMetrics;

public class RawMetrics {
    static ArrayList <String> containerList = new ArrayList<String>();

    private static void buildList(){
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
        buildList();

        RawMetricsOptions options =  PipelineOptionsFactory.fromArgs(args).withValidation().as(RawMetricsOptions.class);
        final Pipeline p = Pipeline.create(options);

        p.apply(
            KafkaIO.<Long, String>read()
                .withBootstrapServers(options.getKafkaServer()) 
                .withTopicPartitions(
                    Collections.singletonList(
                        new TopicPartition(
                            "cadvisor",
                            0))) 
                .withKeyDeserializer(LongDeserializer.class)
                .withValueDeserializer(StringDeserializer.class)
                .withoutMetadata())
        .apply(Values.create())
        .apply(ParDo.of(new free5gc.cadvisor.CAdvisorMetric.FormatMetrics(containerList)))
        .apply("write output", new WriteMetrics<>(options.getTest(), options.getBQProject(), options.getBQDataset(), options.getBQTable()));

        p.run().waitUntilFinish();    
    }    
}
