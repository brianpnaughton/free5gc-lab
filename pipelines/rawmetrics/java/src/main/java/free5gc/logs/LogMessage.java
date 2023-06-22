package free5gc.logs;

import java.util.ArrayList;

import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.extensions.avro.coders.AvroCoder;
import org.apache.beam.sdk.transforms.DoFn;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

@DefaultCoder(AvroCoder.class)
public class LogMessage {
    public static class FormatLogs extends DoFn<String, LogMessage> {
        ArrayList <String> containerList;

        public FormatLogs() {}

        @ProcessElement
        public void processElement(ProcessContext c) {
            System.out.printf("FormatLogs %s", c.element().toString());
        }
    }
}
