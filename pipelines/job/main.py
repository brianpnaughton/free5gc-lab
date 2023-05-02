import logging

import apache_beam as beam
from apache_beam.options.pipeline_options import PipelineOptions
from beam_nuggets.io.kafkaio import KafkaConsume

def run(
    bootstrap_servers,
    topic,
    pipeline_options):

  window_size = 10  # size of the Window in seconds.

  class LogData(beam.DoFn):
    def process(self, record):
      logging.info("brian")
      logging.info(record)
      return [len(record)]

  with beam.Pipeline(options=pipeline_options) as pipeline:
    _ = (
        pipeline
            | "Read from Kafka topic" >> KafkaConsume( 
                  consumer_config={'bootstrap_servers': bootstrap_servers,
                                    'topic': topic,
                                    'auto_offset_reset': 'earliest',
                                    'group_id': 'transaction_classification'})
            | "Fixed window 5s" >> beam.WindowInto(beam.window.FixedWindows(window_size))
            | "Print elements" >> beam.ParDo(LogData())
        )

if __name__ == '__main__':
  logging.getLogger().setLevel(logging.INFO)
  beam_options = PipelineOptions(streaming=True,save_main_session=True, setup_file="./setup.py")

  run(
      "192.168.10.100:29092",
      "cadvisor",
      beam_options)