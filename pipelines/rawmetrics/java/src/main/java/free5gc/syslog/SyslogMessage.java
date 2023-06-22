package free5gc.syslog;

import java.util.ArrayList;

import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.extensions.avro.coders.AvroCoder;
import org.apache.beam.sdk.transforms.DoFn;

@DefaultCoder(AvroCoder.class)
public class SyslogMessage {

    public static class FormatSyslog extends DoFn<String, SyslogMessage> {
        ArrayList <String> containerList;

        public FormatSyslog() {}

        @ProcessElement
        public void processElement(ProcessContext c) {
            System.out.printf("FormatSyslog %s", c.element().toString());
        }
    }
}
