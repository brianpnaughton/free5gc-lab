$script = <<-SCRIPT
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
sudo add-apt-repository \
   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
   $(lsb_release -cs) \
   stable"
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io
sudo curl -L "https://github.com/docker/compose/releases/download/1.27.4/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose
sudo groupadd docker
sudo usermod -aG docker vagrant

sudo apt install -y linux-headers-$(uname -r)
git clone https://github.com/free5gc/gtp5g.git && cd gtp5g
sudo make clean && make
sudo make install

# run a full stack grafana+prom+cadvisor on host
git clone https://github.com/vegasbrianc/prometheus
cd prometheus
docker-compose up -d

SCRIPT

Vagrant.configure(2) do |config|
  config.vm.define '5gtest' do |nodeconfig|
    nodeconfig.vm.hostname = '5gtest'
    nodeconfig.vm.box = 'ubuntu20'
	nodeconfig.vm.network "private_network", ip: "192.168.10.100"
	# open port for free5gc webui
	nodeconfig.vm.network "forwarded_port", guest: 5000, host: 5000
	# open port for grafana
	nodeconfig.vm.network "forwarded_port", guest: 3000, host: 3000
	# open port for prometheus server
	nodeconfig.vm.network "forwarded_port", guest: 9090, host: 9090
	# open port for cadvisor
	nodeconfig.vm.network "forwarded_port", guest: 8080, host: 8080

    nodeconfig.vm.provider :libvirt do |libvirt|
      libvirt.cpus = 4
      libvirt.memory = 8092
      libvirt.nested = true
    end
    nodeconfig.vm.provision "ansible" do |ansible|
      ansible.playbook = "setup.yml"
    end
    nodeconfig.vm.provision "shell", inline: $script
  end
end
