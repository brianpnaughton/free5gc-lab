import logging

import apache_beam as beam
import json
import pprint
from apache_beam.options.pipeline_options import PipelineOptions
from beam_nuggets.io.kafkaio import KafkaConsume

# need to define SLA thresholds for voice and internet services

# need to define thresholds for the CPU/Network/Memory metrics

# names of the containers we are interested in
inventory=['upf',
           'iperfs',
           'mongodb',
           'nrf',
           'amf',
           'ausf',
           'nssf',
           'pcf',
           'smf',
           'udm',
           'udr',
           'ueransim']
  

class FilterMetrics(beam.DoFn):
  def process(self, record):
    logging.info("filtering inventory")
    key,message=record
    m=json.loads(message)
    if 'container_Name' in m:
      if m['container_Name'] in inventory:
        logging.info('record '+ m['container_Name'] + ' in inventory')
        yield m['container_Name'],m

class FilterSyslog(beam.DoFn):
  def process(self, record):
    key,message=record
    m=json.loads(message)
    if 'container_Name' in m:
      if m['container_Name'] in inventory:
        logging.info('record '+ m['container_Name'] + ' in inventory')
        yield m

def combine_metrics_syslogs(metrics, syslogs):
    combined_data = {
        'container_name': metrics['container_name'],
        'cpu_usage': metrics['usage']['cpu'],
        'memory_usage': metrics['usage']['memory'],
        'syslog_message': syslogs['message']
    }
    return combined_data


class CombineMetricsSyslogs(beam.DoFn):
  def process(self, element):
    container_metrics, syslog = element
    return [combine_metrics_syslogs(container_metrics, syslog)]

def run(
    bootstrap_servers,
    pipeline_options):

  window_size = 10  # size of the Window in seconds.

  with beam.Pipeline(options=pipeline_options) as pipeline:
    metrics = (
        pipeline
            | "Read metrics from Kafka topic" >> KafkaConsume( 
                  consumer_config={'bootstrap_servers': bootstrap_servers,
                                    'topic': 'cadvisor',
                                    'auto_offset_reset': 'earliest',
                                    'group_id': 'transaction_classification'}
              )
            # | "Fixed window 5s" >> beam.WindowInto(beam.window.FixedWindows(window_size))
            | "Filter out metrics" >> beam.ParDo(FilterMetrics())
            | "Print metrics" >> beam.Map(pprint.pprint)
        )

    syslog = (
        pipeline
            | "Read syslog from Kafka topic" >> KafkaConsume( 
                  consumer_config={'bootstrap_servers': bootstrap_servers,
                                    'topic': 'syslog-messages',
                                    'auto_offset_reset': 'earliest',
                                    'group_id': 'transaction_classification'}
              )
            # | "Fixed window 5s" >> beam.WindowInto(beam.window.FixedWindows(window_size))
            # | "Filter out syslog events" >> beam.ParDo(FilterSyslog())
            | "Print syslog" >> beam.Map(pprint.pprint)
        )

    free5gclogs = (
        pipeline
            | "Read logs from Kafka topic" >> KafkaConsume( 
                  consumer_config={'bootstrap_servers': bootstrap_servers,
                                    'topic': 'free5gc-logs',
                                    'auto_offset_reset': 'earliest',
                                    'group_id': 'transaction_classification'}
              )
            # | "Fixed window 5s" >> beam.WindowInto(beam.window.FixedWindows(window_size))
            # | "Filter out syslog events" >> beam.ParDo(FilterSyslog())
            | "Print logs" >> beam.Map(pprint.pprint)
        )

    flows = (
        pipeline
            | "Read flows from Kafka topic" >> KafkaConsume( 
                  consumer_config={'bootstrap_servers': bootstrap_servers,
                                    'topic': 'flows',
                                    'auto_offset_reset': 'earliest',
                                    'group_id': 'transaction_classification'}
              )
            # | "Fixed window 5s" >> beam.WindowInto(beam.window.FixedWindows(window_size))
            # | "Filter out syslog events" >> beam.ParDo(FilterSyslog())
            | "Print flows" >> beam.Map(pprint.pprint)
        )

    # correlated_data = ((metrics, syslog)
    #         | 'CoGroup by Key' >> beam.CoGroupByKey()
    #         | 'Combine Metrics and Syslogs' >> beam.ParDo(CombineMetricsSyslogs())
    #       )

    # _ = (correlated_data
    #         | "Print elements" >> beam.Map(pprint.pprint)
    #       )

if __name__ == '__main__':
  logging.getLogger().setLevel(logging.INFO)
  beam_options = PipelineOptions(streaming=True,save_main_session=True, setup_file="./setup.py")

  run(
      "192.168.10.100:29092",
      beam_options)