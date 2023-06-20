package free5gc.cadvisor;

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


public class WriteMetrics<InputT> extends PTransform<PCollection<InputT>, PDone>  {

    private boolean isTest=true;
    private String project;
    private String dataset;
    private String table;

    public WriteMetrics() {}

    public WriteMetrics(boolean test, String project, String dataset, String table) {
        this.isTest = test;
    }

    // build big query row from cadvisor metric
    protected class BuildRowFn extends DoFn<InputT, TableRow> {
        @ProcessElement
        public void processElement(ProcessContext c, BoundedWindow window) {
            TableRow row = new TableRow();
            row.set("container_name", ((CAdvisorMetric)c.element()).getContainerName());
            row.set("timestamp", ((CAdvisorMetric)c.element()).getTimeStamp());
            row.set("cpu_usage", ((CAdvisorMetric)c.element()).getCpuUsage());
            row.set("mem_usage", ((CAdvisorMetric)c.element()).getMemUsage());
            row.set("rx_bytes", ((CAdvisorMetric)c.element()).getRx_bytes());
            row.set("rx_errors", ((CAdvisorMetric)c.element()).getRx_errors());
            row.set("rx_dropped", ((CAdvisorMetric)c.element()).getRx_dropped());
            row.set("tx_bytes", ((CAdvisorMetric)c.element()).getTx_bytes());
            row.set("tx_errors", ((CAdvisorMetric)c.element()).getTx_errors());
            row.set("tx_dropped", ((CAdvisorMetric)c.element()).getTx_dropped());
            c.output(row);
        }
    }

    protected TableSchema getSchema() {
        TableSchema schema =
            new TableSchema()
                .setFields(
                    Arrays.asList(
                        new TableFieldSchema().setName("container_name").setType("STRING"),
                        new TableFieldSchema().setName("timestamp").setType("TIMESTAMP"),
                        new TableFieldSchema().setName("cpu_usage").setType("BYTES"),
                        new TableFieldSchema().setName("mem_usage").setType("BYTES"),
                        new TableFieldSchema().setName("rx_bytes").setType("BYTES"),
                        new TableFieldSchema().setName("rx_errors").setType("BYTES"),
                        new TableFieldSchema().setName("rx_dropped").setType("BYTES"),
                        new TableFieldSchema().setName("tx_bytes").setType("BYTES"),
                        new TableFieldSchema().setName("tx_errors").setType("BYTES"),
                        new TableFieldSchema().setName("tx_dropped").setType("BYTES")
                   ));
        return schema;
    }

    // build text line from cadvisor metric
    protected class BuildLine extends DoFn<InputT, String> {
        @ProcessElement
        public void processElement(ProcessContext c, BoundedWindow window) {
            System.out.println("BuildLine: " + c.element().toString());
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
                        .withWriteDisposition(WriteDisposition.WRITE_TRUNCATE));
        }
        return PDone.in(input.getPipeline());
    }
}
