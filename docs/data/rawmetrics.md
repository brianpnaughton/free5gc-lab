# Data

The sections below detail the raw metrics.

## cAdvisor performance metrics

CAdvisor is used to generate Free5gc Container Performance metrics, for each Free5gc container the following most interesting metrics are captured:

* CPU load average
* CPU throttled (seconds)
* Memory usage (bytes)
* Memory fail count
* Network receive errors
* Network transmit errors

Thresholds are set on each and an alarm generated when breached (assuming flapping period)

## nprobe/ntopng flows

nprobe/ntopng/nDPI are used to collect flow data.

### Flow Statistics

Network flow information

* Client IP
* Client port
* Server IP
* Server port
* Layer-4 protocol
* Application
* Actual throughput
* Total Bytes
* Info

Performance thresholds are set for each application type and an alarm is generated when a flows target  performance is breached

SLAs look like the following

* Voice
* Web

## Application logs/events

Free5gc logs

* Errors
* Warnings

## System events

The following linux system events are reported through Syslog and captured with fluentd

Host and container kernel events/messages

* Emergency
* Alert
* Critical
* Error
* Warning


When a error is captured an impact analysis is performed


## Root Cause Analysis