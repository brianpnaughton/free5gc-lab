package free5gc.sessions;

import java.time.Instant;
import java.util.ArrayList;

import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.extensions.avro.coders.AvroCoder;
import org.apache.beam.sdk.transforms.DoFn;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;


@DefaultCoder(AvroCoder.class)
public class SessionMetric {

    public static class FormatSessions extends DoFn<String, SessionMetric> {
        ArrayList <String> containerList;

        public FormatSessions() {}

        @ProcessElement
        public void processElement(ProcessContext c) {
            System.out.printf("FormatSessions %s", c.element().toString());
        }
    }
}
