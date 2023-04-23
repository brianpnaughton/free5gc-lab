# Basic test setup

The picture below shows the logical topology of the lab environment

![topology](lab.drawio.svg)

The picture below shows the networking that connects all network functions together.

![networking](networking.drawio.svg)


## Start free5gc environment

It is assumed the following commands are run from within the virtual machine started in the Readme.md

To start the free5gc environment run the following command:

```bash
cd /vagrant/scenarios/basic
ansible-playbook start.yaml
```

## Run the test

To run iperf traffic through the free5gc environment, run the following command

```bash
ansible-playbook runtest.yaml
```

## View the network traffic

From your host machine, you can log onto the following monitoring tools

* [cAdvisor](http://192.168.10.100:8080)
* [Grafana](http://192.168.10.100:3000) username/password = admin/foobar

## Remove the environment

```bash
docker-compose down -v
```