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
      v.memory = 3072
  end

  if Vagrant.has_plugin?("vagrant-cachier")
    config.cache.scope = :box
    config.cache.enable :apt
  end


  # This expects conductr_1.0.0-b2a_all.deb to be in this dir
  # You can get this pckg by contacting Typesafe

  config.vm.define "seed" do |seed|
     seed.vm.network "private_network", ip: "192.168.77.20"
     seed.vm.network "forwarded_port", guest: 9005, host: 9005
     seed.vm.provision "ansible" do |ansible|
       ansible.extra_vars = {
         conductr_ip:  "192.168.77.20",
         conductr_dist: "conductr_1.0.0-b2a_all.deb"
       }
       ansible.playbook = "ansible/seed.yml"
     end
   end



  (2..3).each do |i|
     config.vm.define "member_#{i}" do |member|
        member.vm.network "private_network", ip: "192.168.77.2#{i}"
        member.vm.provision "ansible" do |ansible|

            if #{i} % 2 == 0
              ansible.extra_vars = {
                conductr_ip:  "192.168.77.2#{i}",
                seed_ip: "192.168.77.20",
                node_akka_role: "backend",
                conductr_dist: "conductr_1.0.0-b2a_all.deb"
              }
            else
              ansible.extra_vars = {
                conductr_ip:  "192.168.77.2#{i}",
                seed_ip: "192.168.77.20",
                node_akka_role: "frontend",
                conductr_dist: "conductr_1.0.0-b2a_all.deb"
              }
            end

            ansible.playbook = "ansible/member.yml"
        end
      end
  end
end
