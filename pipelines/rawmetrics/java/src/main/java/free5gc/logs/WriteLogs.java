package free5gc.logs;

import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;

public class WriteLogs<InputT> extends PTransform<PCollection<InputT>, PDone>   {

    @Override
    public PDone expand(PCollection<InputT> input) {
        System.out.printf("WriteLogs %s", input.toString());
        return null;
    }
    
}
