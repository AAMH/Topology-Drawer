# Topology-Drawer
A tool for generating the topology of a Docker cluster

This program consists of two parts:
  A netfilter module that gathers the connectivity information of several nodes. This data is sent to a remote node acting as the observer.
  
  A java program that uses the data from the previous step to generate the topology.
  
  
A sample output of the program:

<p align="center"> 
<img src="sampleoutput.png">
</p>

PCs in the figure are containers connected to each other in a single docker host. 
