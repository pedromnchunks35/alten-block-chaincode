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
## Windows (garbadge) check of ports
```
Test-NetConnection -ComputerName localhost -Port 30007


output:
WARNING: TCP connect to (::1 : 30007) failed


ComputerName     : localhost
RemoteAddress    : 127.0.0.1
RemotePort       : 30007
InterfaceAlias   : Loopback Pseudo-Interface 1
SourceAddress    : 127.0.0.1
TcpTestSucceeded : True
```