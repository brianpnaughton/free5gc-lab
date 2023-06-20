# Raw Metric Pipeline

## Build the Beam job

To build the beam jar file, run the following commands. You must have java and maven installed on your machine.

```
cd java
mvn package
```

You will now have a file called __free5gc-metrics-bundled-0.1.jar__ in the target direction

## Runt the beam job

The following options can be provided to the job.

* --kafkaserver: address of the kafaka bootstrap server, in the format <hostname>:<port>
* --bqproject: name of the project
* --bqdataset: name of the bq dataset
* --bqtablename: name of the bq tablename (if blank will generate based on the job name)
* --test: data will not be written to big query, but printed to stdout instead

To run a test job that connects to a local kafka server and prints the metrics to stdout run the following command. 

```
java -jar java -jar target/free5gc-metrics-bundled-0.1.jar --kafkaserver=localhost:9092 --test=true
```

