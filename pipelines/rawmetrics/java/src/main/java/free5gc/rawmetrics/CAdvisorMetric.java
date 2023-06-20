package free5gc.rawmetrics;

import org.apache.beam.sdk.coders.DefaultCoder;
import org.apache.beam.sdk.extensions.avro.coders.AvroCoder;

@DefaultCoder(AvroCoder.class)
public class CAdvisorMetric {
    
    String containerName;
    Long timestamp= 0L;
    Long memUsage = 0L;
    Long cpuUsage = 0L;
    Long rx_bytes = 0L;
    Long rx_errors = 0L;
    Long rx_dropped = 0L;
    Long tx_bytes = 0L;
    Long tx_errors = 0L;
    Long tx_dropped = 0L;

    public CAdvisorMetric() {}
    
    public CAdvisorMetric(String containerName, Long timestamp, Long memUsage, Long cpuUsage, Long rx_bytes, Long rx_errors, Long rx_dropped, Long tx_bytes, Long tx_errors, Long tx_dropped) {
        this.containerName = containerName;
        this.timestamp = timestamp;
        this.memUsage = memUsage;
        this.cpuUsage = cpuUsage;
        this.rx_bytes = rx_bytes;
        this.rx_errors = rx_errors;
        this.rx_dropped = rx_dropped;
        this.tx_bytes = tx_bytes;
        this.tx_errors = tx_errors;
        this.tx_dropped = tx_dropped;
    }

    public void setContainerName(String containerName) {
        this.containerName = containerName;
    }
    public void setTimeStamp(Long timestamp) {
        this.timestamp = timestamp;
    }
    public void setMemUsage(Long memUsage) {
        this.memUsage = memUsage;
    }
    public void setCpuUsage(Long CpuUsage) {
        this.memUsage = CpuUsage;
    }
    public void setRx_bytes(Long rx_bytes) {
        this.rx_bytes = rx_bytes;
    }
    public void setRx_errors(Long rx_errors) {
        this.rx_errors = rx_errors;
    }
    public void setRx_dropped(Long rx_dropped) {
        this.rx_dropped = rx_dropped;
    }
    public void setTx_bytes(Long tx_bytes) {
        this.tx_bytes = tx_bytes;
    }
    public void setTx_errors(Long tx_errors) {
        this.tx_errors = tx_errors;
    }
    public void setTx_dropped(Long tx_dropped) {
        this.tx_dropped = tx_dropped;
    }
    
    public String getContainerName() {
        return containerName;
    }    
    public Long getTimeStamp() {
        return timestamp;
    }
    public Long getCpuUsage() {
        return cpuUsage;
    }
    public Long getMemUsage() {
        return memUsage;
    }
    public Long getRx_bytes() {
        return rx_bytes;
    }
    public Long getRx_errors() {
        return rx_errors;
    }
    public Long getRx_dropped() {
        return rx_dropped;
    }
    public Long getTx_bytes() {
        return tx_bytes;
    }
    public Long getTx_errors() {
        return tx_errors;
    }
    public Long getTx_dropped() {
        return tx_dropped;
    }
    
    public String toString() {
        return String.format("containerName = %s, timestamp = %d, memUsage = %d, cpuUsage = %d, rx_bytes = %d, rx_errors = %d, rx_dropped = %d, tx_bytes = %d, tx_errors = %d, tx_dropped = %d", containerName, timestamp, memUsage, cpuUsage, rx_bytes, rx_errors, rx_dropped, tx_bytes, tx_errors, tx_dropped);
    }
}
