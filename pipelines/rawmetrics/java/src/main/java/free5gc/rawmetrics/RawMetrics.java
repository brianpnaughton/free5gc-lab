package free5gc.rawmetrics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.SimpleFunction;
import org.apache.beam.sdk.transforms.Values;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class RawMetrics {
    static ArrayList <String> containerList = new ArrayList<String>();

    static class FormatMetrics extends DoFn<String, String> {
        @ProcessElement
        public void processElement(ProcessContext c) {
            try{
                // parse the json c.element() and extract the metrics we are interested in
                JSONParser parser = new JSONParser(); 
                JSONObject json = (JSONObject) parser.parse(c.element());

                // search string for a list of container names
                for (String element : containerList){
                    if (element.contains(json.get("container_Name").toString())){
                        System.out.printf("Container = %s\n", element);

                        JSONObject stats = (JSONObject) json.get("container_stats");
                        System.out.printf("timestamp = %s\n", stats.get("timestamp").toString());

                        // report total cpu usage
                        JSONObject cpu = (JSONObject) stats.get("cpu");
                        JSONObject cpuUsage = (JSONObject) cpu.get("usage");
                        System.out.printf("cpu = %s\n", cpuUsage.get("total").toString());

                        // get memory usage

                        // get network usage

                        // get disk usage

                    }
                    c.output(c.element());
                }

            } catch (Exception e) {
                System.out.printf("error %s \n\n\n", e);
            }
        }        
    }

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

    public static void main(String[] args) {
        buildList();

        PipelineOptions options =  PipelineOptionsFactory.create();
        final Pipeline p = Pipeline.create(options);

        p.apply(
            KafkaIO.<Long, String>read()
                .withBootstrapServers(
                    "10.211.55.3:29092") 
                .withTopicPartitions(
                    Collections.singletonList(
                        new TopicPartition(
                            "cadvisor",
                            0))) 
                .withKeyDeserializer(LongDeserializer.class)
                .withValueDeserializer(StringDeserializer.class)
                .withoutMetadata())
        .apply(Values.create())
        .apply(ParDo.of(new FormatMetrics()));
        // .apply(
        //     "FormatResults",
        //     MapElements.via(
        //         new SimpleFunction<String, String>() {
        //           @Override
        //           public String apply(String input) {
        //             System.out.printf("value %s \n\n", input);
        //             return input;
        //           }
        //         }));

        p.run().waitUntilFinish();    
    }    
}
