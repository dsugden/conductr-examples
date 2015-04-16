Collection of projects used to demo the features of [Typesafe conductR](http://typesafe.com/products/conductr)


Currently using  **conductr_1.0.0-b2a_all.deb*


##### Build a conductr cluster 

1. Contact typesafe, get a copy of conductr.deb, add it into your clone of this repo
2. Build cluster with vagrant + ansible in project.  This vagrant uses a custom base-box... you can use this: https://github.com/dsugden/vagrant-ansible-ubuntu-oracle-java8 to create this box


These example projects are using [sbt-conductr](https://github.com/sbt/sbt-typesafe-conductr) 0.25.0

As this plugin evolves, this repo will be updated.


##### Build the **singlemicro** bundle


    sbt singlemicro/bundle:dist
    


##### build the akkacluster bundles

There is a frontend and a backend. The frontend is the seed, and also has a spray http service.

The backend will join cluster, then register with front end. The frontend will forward work to the backend.


    sbt
    project akkaclusterFront
    
    # replace with the IP of your conductr server
    # 192.168.77.20 is for the Vagrantfile conductr custer in this repo
    controlServer 192.168.77.20:9005
    
    clean
    bundle:dist
    
     
    conduct load <Tab for bundle file >  <path to project>/init-cluster.sh-a802635856ee251147550871a5f88b46e7f25b7f72cd276942c8bbd2622023bc.zip
    conduct run <bundleId>

    
    
    project akkaclusterBack
    clean
    bundle:dist
    conduct load <Tab for bundle file >  <path to project>/init-cluster.sh-a802635856ee251147550871a5f88b46e7f25b7f72cd276942c8bbd2622023bc.zip
    conduct run <bundleId>
    
    
##### ConductR roles

The test ConductR network in this repo will bring up a 4 node network with the following akka.cluster.roles

1. 192.168.77.20  akka.cluster.roles=[all=conductrs] 
2. 192.168.77.22  akka.cluster.roles=[all=backend]
3. 192.168.77.23  akka.cluster.roles=[all=frontend]
4. 192.168.77.24  akka.cluster.roles=[all=backend]
    
    
In each of the sub-projects sbt projects specification, a role is assigned for that app eg:


    BundleKeys.roles  := Set("backend")
    
    
When this app gets deployed to the ConductR cluster, it will only be replicated/ started on a node with the matching role
    

The **all-conductrs** role is the default, it will allow any role. 
    
    


    
    
    

