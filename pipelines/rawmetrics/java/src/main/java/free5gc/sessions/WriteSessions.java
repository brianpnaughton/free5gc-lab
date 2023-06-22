package free5gc.sessions;

import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;

public class WriteSessions<InputT> extends PTransform<PCollection<InputT>, PDone>  {

    @Override
    public PDone expand(PCollection<InputT> input) {
        System.out.printf("WriteSessions %s", input.toString());
        return null;
    } 
    
}
