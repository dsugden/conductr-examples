# -*- mode: ruby -*-
# vi: set ft=ruby :

Vagrant.configure(2) do |config|

  # we are starting from a custom box "package.box"
  # you can add this box to vagrant
  # with:
  # vagrant box add package.box

  config.vm.box = "ubuntu/trusty64_oraclejava8"

  config.vm.synced_folder ".", "/vagrant"
  config.vm.provider "virtualbox" do |v|
      v.memory = 2048
  end

  if Vagrant.has_plugin?("vagrant-cachier")
    config.cache.scope = :box
    config.cache.enable :apt
  end



  config.vm.define "seed" do |seed|
     seed.vm.network "private_network", ip: "192.168.77.20"
     seed.vm.network "forwarded_port", guest: 9005, host: 9005
     seed.vm.provision "ansible" do |ansible|
       ansible.extra_vars = {
         conductr_ip:  "192.168.77.20",
         seed_ip: "192.168.77.20",
         CONDUCTR_PKG: "conductr_1.0.11_all.deb",
         CONDUCTR_HAPROXY_PKG: "conductr-haproxy_1.0.11_all.deb",
         INSTALL_CLI: true,
         HAS_ROLE: false,
         verbose: 'vvv'
       }
       ansible.playbook = "ansible/build-cluster-vagrant-seed.yml"
     end
   end


  (2..4).each do |i|
     config.vm.define "member_#{i}" do |member|
        member.vm.network "private_network", ip: "192.168.77.2#{i}"
        member.vm.provision "ansible" do |ansible|

            if i % 2 == 0
              ansible.extra_vars = {
                conductr_ip:  "192.168.77.2#{i}",
                seed_ip: "192.168.77.20",
                node_akka_role: "backend",
                CONDUCTR_PKG: "conductr_1.0.11_all.deb",
                CONDUCTR_HAPROXY_PKG: "conductr-haproxy_1.0.11_all.deb",
                INSTALL_CLI: false,
                HAS_ROLE: true,
                verbose: 'vvv'
              }
            else
              ansible.extra_vars = {
                conductr_ip:  "192.168.77.2#{i}",
                seed_ip: "192.168.77.20",
                node_akka_role: "frontend",
                CONDUCTR_PKG: "conductr_1.0.11_all.deb",
                CONDUCTR_HAPROXY_PKG: "conductr-haproxy_1.0.11_all.deb",
                INSTALL_CLI: false,
                HAS_ROLE: true,
                verbose: 'vvv'
              }
            end

            ansible.playbook = "ansible/build-cluster-vagrant.yml"
        end
      end
  end




end
