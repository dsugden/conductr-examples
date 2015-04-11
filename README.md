Collection of projects used to demo the features of [Typesafe conductR](http://typesafe.com/products/conductr)


##### Build a conductr cluster 

1. Contact typesafe, get a copy of conductr.deb, add it into your clone of this repo
2. Build cluster with vagrant + ansible in project.  This vagrant uses a custom base-box... you can use this: https://github.com/dsugden/vagrant-ansible-ubuntu-oracle-java8 to create this box





##### Build the **singlemicro** bundle


    sbt singlemicro/bundle:dist


##### build the akkacluster bundles

There is a frontend and a backend. The frontend is the seed, and also has a spray http service.

The backend will join cluster, then register with front end. The frontend will forward work to the backend.


    sbt
    project akkaclusterFront
    clean
    bundle:dist
    loadBundle <Tab for bundle file >  <path to project>/init-cluster.sh-a802635856ee251147550871a5f88b46e7f25b7f72cd276942c8bbd2622023bc.zip
    startBundle <bundleId>

    project akkaclusterBack
    clean
    bundle:dist
    loadBundle <Tab for bundle file >  <path to project>/init-cluster.sh-a802635856ee251147550871a5f88b46e7f25b7f72cd276942c8bbd2622023bc.zip
    startBundle <bundleId>

