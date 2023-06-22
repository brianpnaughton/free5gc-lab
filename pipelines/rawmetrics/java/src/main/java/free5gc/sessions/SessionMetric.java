package free5gc.sessions;

import java.time.Instant;

import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.extensions.avro.coders.AvroCoder;
import org.apache.beam.sdk.transforms.DoFn;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@DefaultCoder(AvroCoder.class)
public class SessionMetric {
    private String imsi;
    private String service;
    private Long timestamp;
    private Long response_code;
    private Double response_time;

    public SessionMetric() {}

    public SessionMetric(Long timestamp,String imsi, String service, Long response_code, Double response_time) {
        this.imsi = imsi;
        this.service = service;
        this.timestamp = timestamp;
        this.response_code = response_code;
        this.response_time = response_time;
    }

    public String getImsi() {
        return imsi;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public Long getResponse_code() {
        return response_code;
    }

    public void setResponse_code(Long response_code) {
        this.response_code = response_code;
    }

    public Double getResponse_time() {
        return response_time;
    }

    public void setResponse_time(Double response_time) {
        this.response_time = response_time;
    }

    public String toString() {
        return String.format("SessionMetric: imsi=%s, service=%s, timestamp=%d, response_code=%d, response_time=%f", imsi, service, timestamp, response_code, response_time);
    }

    public static class FormatSessions extends DoFn<String, SessionMetric> {

        @ProcessElement
        public void processElement(ProcessContext c) {
            // parse the json c.element() and extract the metrics we are interested in
            JSONParser parser = new JSONParser(); 
            
            try {

                Instant now = Instant.now();
                Long timestamp = now.getEpochSecond();
                JSONObject json = (JSONObject) parser.parse(c.element());
                String imsi = (String) json.get("imsi");
                String service = (String) json.get("service");
                Long response_code = (Long) json.get("response_code");
                Double response_time = (Double) json.get("response_time");
                
                SessionMetric metric=new SessionMetric(timestamp,imsi,service,response_code,response_time);
                c.output(metric);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }
}
