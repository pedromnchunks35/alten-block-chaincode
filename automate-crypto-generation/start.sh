#!/bin/bash
echo "Please give the ca ip with its correct port:"
read CA_IP_ADDRESS
echo "Please give the tls ca ip with its correct port:"
read TLS_CA_IP_ADDRESS
echo "Please provide additional a additional csr.hosts (use commmas, you must also provide atleast 1 for the peer gateway):"
read GENERAL_IP_ADDRESS_SERVICE
echo "How many components you wish to construct?"
read items
for (( i=0 ; i < items ; i++ )); 
do
echo "Ip address of the machine?"
read MACHINE_IP
echo "The name of the component?"
read CN
echo "Organization of hyperledger name?"
read ORGN
echo "The password for cryptographic materials?"
read PASSWORD
echo "Component type?"
read COMPONENT_TYPE
echo "Country?"
read COUNTRY
echo "State?"
read STATE
echo "Location?"
read LOCATION
echo "Organization?"
read ORGANIZATION
#? Generate the cryptographic material
./crypto.sh $MACHINE_IP $CN  $ORGN $PASSWORD $COMPONENT_TYPE $COUNTRY $STATE $LOCATION $ORGANIZATION $GENERAL_IP_ADDRESS_SERVICE $CA_IP_ADDRESS $TLS_CA_IP_ADDRESS
wait
#? Clear the temp directory
rm -f -r temp/*
#? Construct the configs
./construction.sh $MACHINE_IP $COMPONENT_TYPE $CN $ORGN
wait
#? Share the configs
./shareconfigs.sh
wait
#? Clear the temp directory
rm -f -r temp/*
done
#? Make a reformulation of the msps present in each orderer of each machine
./constructOrgsAndOtherOrderersMsp.sh
wait
./mspDepOrderersPassing.sh
wait
rm -f -r temp/*
echo "Done"