package free5gc.rawmetrics;
/*
 * To change the runner, specify:
 * --runner=YOUR_SELECTED_RUNNER
 *
 * To execute this pipeline, specify a local output file (if using the {@code DirectRunner}
 * output prefix on a supported distributed file system.
 *
 * --output=[YOUR_LOCAL_FILE | YOUR_OUTPUT_PREFIX]
 */

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
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.time.Instant;

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

    static class FormatMetrics extends DoFn<String, CAdvisorMetric> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            try{
                // parse the json c.element() and extract the metrics we are interested in
                JSONParser parser = new JSONParser(); 
                JSONObject json = (JSONObject) parser.parse(c.element());

                // search string for a list of container names
                for (String element : containerList){
                    if (element.contains(json.get("container_Name").toString())){
                        JSONObject stats = (JSONObject) json.get("container_stats");

                        Instant now = Instant.now();
                        Long timestamp = now.getEpochSecond();

                        // get total cpu usage
                        JSONObject cpu = (JSONObject) stats.get("cpu");
                        JSONObject cpuUsage = (JSONObject)cpu.get("usage");

                        // get memory usage
                        JSONObject memory = (JSONObject) stats.get("memory");
                        Long memUsage = (Long)memory.get("usage");

                        // get network usage
                        JSONObject network = (JSONObject) stats.get("network");
                        Long rx_bytes = (Long) network.get("rx_bytes");
                        Long rx_errors = (Long) network.get("rx_errors");
                        Long rx_dropped = (Long) network.get("rx_dropped");
                        Long tx_bytes = (Long) network.get("tx_bytes");
                        Long tx_errors = (Long) network.get("tx_errors");
                        Long tx_dropped = (Long) network.get("tx_dropped");

                        CAdvisorMetric metric = new CAdvisorMetric(
                            json.get("container_Name").toString(),
                            timestamp,
                            memUsage,
                            (Long)cpuUsage.get("total"),
                            rx_bytes,
                            rx_errors,
                            rx_dropped,
                            tx_bytes,
                            tx_errors,
                            tx_dropped
                        );

                        c.output(metric);
                    }
                }

            } catch (Exception e) {
                System.out.printf("error %s \n\n\n", e);
            }
        }        
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
        .apply(ParDo.of(new FormatMetrics()))
        .apply("write output", new WriteMetrics<>(options.getTest(), options.getBQProject(), options.getBQDataset(), options.getBQTable()));

        p.run().waitUntilFinish();    
    }    
}
