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
    # open port for portainer
    nodeconfig.vm.network "forwarded_port", guest: 9443, host: 9443

    nodeconfig.vm.provider :libvirt do |libvirt|
      libvirt.cpus = 4
      libvirt.memory = 8092
      libvirt.nested = true
    end

    nodeconfig.vm.provision "ansible" do |ansible|
      ansible.playbook = "setup.yaml"
    end
  end
end
