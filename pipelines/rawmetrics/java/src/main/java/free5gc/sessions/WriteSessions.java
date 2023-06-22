package free5gc.sessions;

import java.util.Arrays;

import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.Write.CreateDisposition;
import org.apache.beam.sdk.io.gcp.bigquery.BigQueryIO.Write.WriteDisposition;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.PTransform;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;

import com.google.api.services.bigquery.model.TableFieldSchema;
import com.google.api.services.bigquery.model.TableRow;
import com.google.api.services.bigquery.model.TableSchema;

public class WriteSessions<InputT> extends PTransform<PCollection<InputT>, PDone>  {
    private boolean isTest=false;
    private String project;
    private String dataset;
    private String table;

    public WriteSessions() {}

    public WriteSessions(boolean test, String project, String dataset, String table) {
        this.isTest = test;
        this.project=project;
        this.dataset=dataset;
        this.table=table;

        System.out.printf("BQ project=%s, dataset=%s, tablename=%s",project,dataset,table);
    }

    protected class BuildRowFn extends DoFn<InputT, TableRow> {
        @ProcessElement
        public void processElement(ProcessContext c, BoundedWindow window) {
            TableRow row = new TableRow();
            row.set("timestamp", ((SessionMetric) c.element()).getTimestamp());
            row.set("imsi", ((SessionMetric) c.element()).getImsi());
            row.set("service", ((SessionMetric) c.element()).getService());
            row.set("code", ((SessionMetric) c.element()).getResponse_code());
            row.set("time", ((SessionMetric) c.element()).getResponse_time());
            c.output(row);
        }
    }
    protected TableSchema getSchema() {
        TableSchema schema =
            new TableSchema()
                .setFields(
                    Arrays.asList(
                        new TableFieldSchema().setName("timestamp").setType("TIMESTAMP"),
                        new TableFieldSchema().setName("imsi").setType("STRING"),
                        new TableFieldSchema().setName("service").setType("STRING"),
                        new TableFieldSchema().setName("code").setType("LONG"),
                        new TableFieldSchema().setName("time").setType("FLOAT")
                   ));
        return schema;
    }

    protected class BuildLine extends DoFn<InputT, String> {
        @ProcessElement
        public void processElement(ProcessContext c, BoundedWindow window) {
            System.out.println("+++++++++++++Session: " + c.element().toString()+"++++++++++++++");
            c.output(c.element().toString());
        }
    }

    @Override
    public PDone expand(PCollection<InputT> input) {
        if (isTest) {
            input
                .apply("generate lines", ParDo.of(new BuildLine()));
        }else{
            input
                .apply("generate rows", ParDo.of(new BuildRowFn()))
                .apply(
                    "Write to BigQuery",
                    BigQueryIO.writeTableRows()
                        .to(String.format("%s:%s.%s", project, dataset, table))
                        .withSchema(getSchema())
                        .withCreateDisposition(CreateDisposition.CREATE_IF_NEEDED)
                        .withWriteDisposition(WriteDisposition.WRITE_APPEND));
        }
        return PDone.in(input.getPipeline());
    }     
}
