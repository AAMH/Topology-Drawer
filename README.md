# Topology-Drawer
A tool for generating the topology of a Docker cluster.

This program consists of two parts:

  - Netfilter module gathers the connectivity information of several nodes, using a set of hooks inside the linux kernel. This data is sent to a remote node acting as the observer.
  
  - Java program that uses the data from the previous step to generate the topology.
  
  
A sample output of the program:

<p align="center"> 
<img src="sampleoutput.png">
</p>

PCs in the figure are containers connected to each other in a single docker host. IP adresses and the number of transferred packets are also shown.
