# pip install kafka-python

import requests
import time
import json
from threading import Thread
from kafka import KafkaProducer
import random

# kafka server
producer = KafkaProducer(bootstrap_servers=['{{ ansible_default_ipv4.address }}:29092'])
topic = "sessions"

# Set the HTTP URL to call
url = "http://172.168.56.2"

def main(num_requests):

    # Create a list of threads
    threads = []
    for i in range(num_requests):
        thread = Thread(target=make_request)
        threads.append(thread)

    # Start all of the threads
    for thread in threads:
        # wait a random time to start each thread
        time.sleep(random.randint(1, 10))
        thread.start()

    # Wait for all of the threads to finish
    for thread in threads:
        thread.join()

def make_request():
    # Make the HTTP request
    start_time = time.time()
    response = requests.get(url)
    end_time = time.time()

    # Get the response code
    response_code = response.status_code

    # Get the response time
    response_time = end_time - start_time

    # Create a JSON message
    message = {
        "timestamp": start_time,
        "imsi":"{{ IMSI }}",
        "service": "Internet",
        "response_code": response_code,
        "response_time": response_time
    }

    # Send the message to the Kafka topic
    producer.send(topic, json.dumps(message).encode("utf-8"))
    producer.push()

if __name__ == "__main__":
    # Number of parallel requests to make
    num_requests = 10

    # loop forever
    while True:
        main(num_requests)