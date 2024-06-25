#!/usr/bin/expect -f
#? Enviroment Variables
set MACHINE_IP [lindex $argv 0]
set COMPONENT_TYPE [lindex $argv 1]
set CN [lindex $argv 2]
set ORGN [lindex $argv 3]

#? Verify if cryptographic material and configuration are generated
if { ![file exists "../crypto/$MACHINE_IP/msp/$CN"] } {
puts "The msp of that component is missing"
exit
} elseif {![file exists "../crypto/$MACHINE_IP/tls-msp/$CN"]} {
puts "The tls-msp of that component is missing"
exit
} elseif {![file exists "../crypto/$MACHINE_IP/msp/$ORGN"]} {
puts "The msp of the org is missing"
exit
} elseif {![file exists "../crypto/$MACHINE_IP/tls-msp/$ORGN"]} {
puts "The tls-msp of the org is missing"
exit
} elseif {![file exists "../config/$MACHINE_IP/$CN"]} {
    puts "The configuration of the component does not exist"
    exit
}

#? Make the normal operation
if { $COMPONENT_TYPE == "orderer" } {
#? Make the construction of the orderer
spawn ./orderercons.sh $CN $MACHINE_IP
wait

} elseif { $COMPONENT_TYPE == "peer" } {

#? Verify if the ledger configuration is created
set couchCN [string map { "peer1" "couch-peer1" } $CN]

#? Check the configs
if {![file exists "../config/$MACHINE_IP/$couchCN"]} {
    puts "The configuration of the ledger does not exist"
    exit
}

#? Make the construction of a peer
spawn ./peercons.sh $CN $MACHINE_IP $couchCN
wait

} else {
    puts "Component Type is missing"
    exit
}