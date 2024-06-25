# Just a simple project to create a blockchain that they can use for their research
## Info
- It will be single cluster
- We will use the algorithm for single cluster orderers since it is just for testing
- We will deploy the chaincode as a service
- 2 organizations
- Same CA just to don't give much trouble
- We wish to create a chaincode that receives a registration from a external centralized service that:
 - Will register its identification table based on biometrics and polynomial functions
 - Will be able to give a ticket to a given user that can use that ticket to use in another centralized service that enrolls certificates for the client using that precise ticket
- Network components
    - CA
    - TLS-CA
    - orgx-peer1
    - orgx-couch-peer1
    - orgy-peer1
    - orgy-couch-peer1
    - orgz-orderer (single orderer algorithm)
- We will try to apply a load balancer so it gets simpler
# Install kubernetes
- Install the following
  - docker
  - cri-dockerd
  - kubernetes
  - kubelet
  - kubectl
- After cluster running install the following
  - Calico network plugin
    - Install by manifest
    - Change the "CALICO_IPV4POOL_CIDR" to "10.244.0.0/16"
# Run the cluster
```
sudo kubeadm init --pod-network-cidr=10.244.0.0/16 --apiserver-advertise-address=IPV4 --control-plane-endpoint=IPV4 --cri-socket=unix:///var/run/cri-dockerd.sock
```
# Configs you must do
- Since you prob are running this in another machine you should change the pv's (the volume and the name of the node for the nodeaffinity to match) inside of each config file in the k8-config section
- Also ca's and tls-ca's require their volume to be changed