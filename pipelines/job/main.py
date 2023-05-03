import logging

import apache_beam as beam
import json
import pprint
from apache_beam.options.pipeline_options import PipelineOptions
from beam_nuggets.io.kafkaio import KafkaConsume

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

def run(
    bootstrap_servers,
    topic,
    pipeline_options):

  window_size = 10  # size of the Window in seconds.

  class FilterInventory(beam.DoFn):
    def process(self, record):
      logging.info("filtering inventory")
      key,message=record
      m=json.loads(message)
      if 'container_Name' in m:
        if m['container_Name'] in inventory:
          logging.info('record '+ m['container_Name'] + ' in inventory')
          yield m

  with beam.Pipeline(options=pipeline_options) as pipeline:
    _ = (
        pipeline
            | "Read from Kafka topic" >> KafkaConsume( 
                  consumer_config={'bootstrap_servers': bootstrap_servers,
                                    'topic': topic,
                                    'auto_offset_reset': 'earliest',
                                    'group_id': 'transaction_classification'}
                  # value_decoder=lambda m: json.loads(m.decode('utf-8'))
              )
            | "Fixed window 5s" >> beam.WindowInto(beam.window.FixedWindows(window_size))
            | "Filter out non-inventory related elements" >> beam.ParDo(FilterInventory())
            | "Print elements" >> beam.Map(pprint.pprint)
        )

if __name__ == '__main__':
  logging.getLogger().setLevel(logging.INFO)
  beam_options = PipelineOptions(streaming=True,save_main_session=True, setup_file="./setup.py")

  run(
      "192.168.10.100:29092",
      "cadvisor",
      beam_options)