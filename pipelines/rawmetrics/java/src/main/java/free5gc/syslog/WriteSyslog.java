package free5gc.syslog;

import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;

public class WriteSyslog<InputT> extends PTransform<PCollection<InputT>, PDone>   {

    @Override
    public PDone expand(PCollection<InputT> input) {
        System.out.printf("WriteSyslog %s", input.toString());
        return null;
    }
    
}
