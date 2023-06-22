package free5gc.logs;

import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.checkerframework.checker.initialization.qual.Initialized;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.UnknownKeyFor;

public class WriteLogs<InputT> extends PTransform<PCollection<InputT>, PDone>   {

    @Override
    public PDone expand(PCollection<InputT> input) {
        System.out.printf("WriteLogs %s", input.toString());
        return null;
    }
    
}
