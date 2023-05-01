# free5GC lab

This project deploys a free5gc based lab environment that can demonstrate operational and AI use cases leveraging  Google Cloud Analtics and AI tools.

More information on the [target Analytics/AI architecture](docs/architecture/README.md) can be found here. 

## Create free5GC lab VM

The lab can be deployed to a local machine using vagrant. You must install vagrant on your local machine with a plugin apppropriate for your local hypervisor. 

To create the lab VM, run the following commands in the __free5gc-lab__ directory:

```bash
vagrant up
vagrant ssh
```

## Google Cloud

To deploy the lab on Google Cloud do the following

* Ensure secure VM policy is disabled. This is required because we will install GTP kernel module during the setup process. 
* Create a VPC network and update the firewall policies to allow the following ports

  * 9000 (kafdrop)
  * 5000 (webui)
  * 3000 (grafana)
  * 9090 (prometheus)
  * 8080 (cadvisor)
  * 9443 (portainer)

* Create an Ubuntu 20.04 (LTS) virtual machine and attach to the VPC network created in the previous step

Once the VM is running, log in and run the following commands:

```bash
sudo apt update
sudo apt install -y python3 python3-pip
pip install ansible
git clone https://github.com/brianpnaughton/free5gc-lab.git
cd free5gc
```

Change the __setup.yaml__ file to point to __"localhost"__. Make sure the top lines in that file look as follows:

```
- hosts: localhost
  become: yes
```

Then run the __setup.yaml__ playbook

```bash
ansible-playbook setup.yaml
```

## Data and Analytics Use Cases

The [data and analytics use cases for this lab are described here](docs/data/README.md). 

### Running test scenarios

Once the VM is running, you can deploy the following free5gc scenarios. 

* [Run a basic simple scenario](scenarios/basic/basic.md)
* [Run an advanced scenario](scenarios/advanced/advanced.md)