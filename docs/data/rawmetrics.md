# Raw Metrics

The sections below detail the raw metric payloads collected in this project.

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

### IP Flow Statistics

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


### GTP flow statistics

The following GTP tunnel metrics are provided by nprobe

* %GTPV2_REQ_MSG_TYPE               GTPv2 Request Msg Type
* %GTPV2_RSP_MSG_TYPE               GTPv2 Response Msg Type
* %GTPV2_C2S_S1U_GTPU_TEID          GTPv2 Client->Svr S1U GTPU TEID
* %GTPV2_C2S_S1U_GTPU_IP            GTPv2 Client->Svr S1U GTPU IP
* %GTPV2_S2C_S1U_GTPU_TEID          GTPv2 Srv->Client S1U GTPU TEID
* %GTPV2_S2C_S1U_GTPU_IP            GTPv2 Srv->Client S1U GTPU IP
* %GTPV2_END_USER_IMSI              GTPv2 End User IMSI
* %GTPV2_END_USER_MSISDN            GTPv2 End User MSISDN
* %GTPV2_APN_NAME                   GTPv2 APN Name
* %GTPV2_ULI_MCC                    GTPv2 Mobile Country Code
* %GTPV2_ULI_MNC                    GTPv2 Mobile Network Code
* %GTPV2_ULI_CELL_TAC               GTPv2 Tracking Area Code
* %GTPV2_ULI_CELL_ID                GTPv2 Cell Identifier
* %GTPV2_RESPONSE_CAUSE             GTPv2 Cause of Operation

## Application logs/events

Free5gc logs

* Errors
* Warnings

## Syslog events

The following linux system events are reported through Syslog and captured with fluentd

Host and container kernel events/messages

* Emergency
* Alert
* Critical
* Error
* Warning

