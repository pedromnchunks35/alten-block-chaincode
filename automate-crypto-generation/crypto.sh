#!/bin/bash

MACHINE_IP=${1} 
CN=${2}
ORGN=${3} 
PASSWORD=${4} 
COMPONENT_TYPE=${5} 
COUNTRY=${6}
STATE=${7}
LOCATION=${8} 
ORGANIZATION=${9} 
GENERAL_IP_ADDRESS_SERVICE=${10} 
CA_IP_ADDRESS=${11} 
TLS_CA_IP_ADDRESS=${12} 

export MACHINE_IP
export CN
export ORGN
export PASSWORD
export COMPONENT_TYPE
export COUNTRY
export STATE
export LOCATION
export ORGANIZATION
export GENERAL_IP_ADDRESS_SERVICE
export CA_IP_ADDRESS
export TLS_CA_IP_ADDRESS
echo $GENERAL_IP_ADDRESS_SERVICE $CA_IP_ADDRESS $TLS_CA_IP_ADDRESS
#? Move to the right place and make it the fabric ca home
cd ../client
export FABRIC_CA_CLIENT_HOME=$PWD

#? Veryfing if the msp of the component exists
if [[ ! -d ../crypto/${MACHINE_IP}/msp/${CN} ]]; 
then
echo "Registing and Enrolling the component msp.."
#? Register the component msp
fabric-ca-client register -d -u http://${CA_IP_ADDRESS} --id.name $CN --id.secret $PASSWORD --id.type $COMPONENT_TYPE --csr.cn $CN --csr.names "C=${COUNTRY},ST=${STATE},L=${LOCATION},O=${ORGANIZATION}" --csr.hosts "${MACHINE_IP},localhost,${CN},${GENERAL_IP_ADDRESS_SERVICE}" --tls.certfiles ./root.tls.cert/carroot.cert --mspdir ./adm.msp/msp
wait
#? Enroll the component msp
fabric-ca-client enroll -d -u http://${CN}:${PASSWORD}@${CA_IP_ADDRESS} --id.type $COMPONENT_TYPE --csr.cn $CN --csr.names "C=${COUNTRY},ST=${STATE},L=${LOCATION},O=${ORGANIZATION}" --csr.hosts "${MACHINE_IP},localhost,${CN},${GENERAL_IP_ADDRESS_SERVICE}" --tls.certfiles ./root.tls.cert/carroot.cert --mspdir ../crypto/${MACHINE_IP}/msp/${CN}
wait
#? Renaming the private key
for file in ../crypto/${MACHINE_IP}/msp/${CN}/keystore/*; do
mv "$file" "../crypto/${MACHINE_IP}/msp/${CN}/keystore/key.pem" 
done
#? Renaming the ca
for file in ../crypto/${MACHINE_IP}/msp/${CN}/cacerts/*; do
mv "$file" "../crypto/${MACHINE_IP}/msp/${CN}/cacerts/ca.pem" 
done
#? Put a config.yaml inside of the cryptographic material
cp ../template/msp/config.yaml ../crypto/${MACHINE_IP}/msp/${CN}/.
else
echo "The msp of the component already exists,ignoring..."
fi
#? Veryfing if the tls-msp of the component exists
if [[ ! -d ../crypto/${MACHINE_IP}/tls-msp/${CN} ]]; 
then
echo "Registing and Enrolling the component tls-msp.."
#? Register the component tls-msp
fabric-ca-client register -d -u http://${TLS_CA_IP_ADDRESS}  --id.name $CN --id.secret $PASSWORD --id.type $COMPONENT_TYPE --csr.cn $CN --csr.names "C=${COUNTRY},ST=${STATE},L=${LOCATION},O=${ORGANIZATION}" --csr.hosts "${MACHINE_IP},localhost,${CN},${GENERAL_IP_ADDRESS_SERVICE}" --tls.certfiles ./root.tls.cert/tlsroot.cert --mspdir ./adm.msp/tls-msp/
wait
#? Enroll the component tls-msp
fabric-ca-client enroll -d -u http://${CN}:${PASSWORD}@${TLS_CA_IP_ADDRESS} --id.type $COMPONENT_TYPE --csr.cn $CN  --csr.names "C=${COUNTRY},ST=${STATE},L=${LOCATION},O=${ORGANIZATION}"  --csr.hosts "${MACHINE_IP},localhost,${CN},${GENERAL_IP_ADDRESS_SERVICE}"  --enrollment.profile tls --tls.certfiles ./root.tls.cert/tlsroot.cert --mspdir ../crypto/${MACHINE_IP}/tls-msp/${CN}
#? Renaming the private key
for file in ../crypto/${MACHINE_IP}/tls-msp/${CN}/keystore/*; do
mv "$file" "../crypto/${MACHINE_IP}/tls-msp/${CN}/keystore/key.pem" 
done
#? Renaming the tls ca
for file in ../crypto/${MACHINE_IP}/tls-msp/${CN}/tlscacerts/*; do
mv "$file" "../crypto/${MACHINE_IP}/tls-msp/${CN}/tlscacerts/tls-ca.pem" 
done
#? Put a config.yaml inside of the cryptographic material
cp ../template/tls-msp/config.yaml ../crypto/${MACHINE_IP}/tls-msp/${CN}/.
else
echo "The tls-msp of the component already exists,ignoring..."
fi
#? Veryfing if the org msp already exists or not
if [[ ! -d ../crypto/${MACHINE_IP}/msp/${ORGN} ]];
then
    echo "The organization msp does not exist, lets create it..."
#? Register the component msp
fabric-ca-client register -d -u http://${CA_IP_ADDRESS}  --id.name $ORGN --id.secret $PASSWORD --id.type $COMPONENT_TYPE --csr.cn $ORGN --csr.names "C=${COUNTRY},ST=${STATE},L=${LOCATION},O=${ORGANIZATION}" --csr.hosts "${MACHINE_IP},localhost,${CN},${GENERAL_IP_ADDRESS_SERVICE}" --tls.certfiles ./root.tls.cert/carroot.cert --mspdir ./adm.msp/msp
wait
#? Enroll the component msp
fabric-ca-client enroll -d -u http://${ORGN}:${PASSWORD}@${CA_IP_ADDRESS} --id.type $COMPONENT_TYPE --csr.cn $ORGN --csr.names "C=${COUNTRY},ST=${STATE},L=${LOCATION},O=${ORGANIZATION}" --csr.hosts "${MACHINE_IP},localhost,${CN},${GENERAL_IP_ADDRESS_SERVICE}" --tls.certfiles ./root.tls.cert/carroot.cert --mspdir ../crypto/${MACHINE_IP}/msp/${ORGN}
wait
#? Renaming the private key
for file in ../crypto/${MACHINE_IP}/msp/${ORGN}/keystore/*; do
mv "$file" "../crypto/${MACHINE_IP}/msp/${ORGN}/keystore/key.pem" 
done
#? Renaming the ca
for file in ../crypto/${MACHINE_IP}/msp/${ORGN}/cacerts/*; do
mv "$file" "../crypto/${MACHINE_IP}/msp/${ORGN}/cacerts/ca.pem" 
done
#? Put a config.yaml inside of the cryptographic material
cp ../template/msp/config.yaml ../crypto/${MACHINE_IP}/msp/${ORGN}/.
else
    echo "The organization msp already exists, ignoring.."
fi
#? Veryfing if the org msp already exists or not
if [[ ! -d ../crypto/${MACHINE_IP}/tls-msp/${ORGN} ]];
then
echo "Registing and Enrolling the component tls-msp.."
#? Register the component tls-msp
fabric-ca-client register -d -u http://${TLS_CA_IP_ADDRESS}  --id.name $ORGN --id.secret $PASSWORD --id.type $COMPONENT_TYPE --csr.cn $ORGN --csr.names "C=${COUNTRY},ST=${STATE},L=${LOCATION},O=${ORGANIZATION}" --csr.hosts "${MACHINE_IP},localhost,${CN},${GENERAL_IP_ADDRESS_SERVICE}" --tls.certfiles ./root.tls.cert/tlsroot.cert --mspdir ./adm.msp/tls-msp/
wait
#? Enroll the component tls-msp
fabric-ca-client enroll -d -u http://${ORGN}:${PASSWORD}@${TLS_CA_IP_ADDRESS} --id.type $COMPONENT_TYPE --csr.cn $ORGN  --csr.names "C=${COUNTRY},ST=${STATE},L=${LOCATION},O=${ORGANIZATION}"  --csr.hosts "${MACHINE_IP},localhost,${CN},${GENERAL_IP_ADDRESS_SERVICE}"  --enrollment.profile tls --tls.certfiles ./root.tls.cert/tlsroot.cert --mspdir ../crypto/${MACHINE_IP}/tls-msp/${ORGN}
#? Renaming the private key
for file in ../crypto/${MACHINE_IP}/tls-msp/${ORGN}/keystore/*; do
mv "$file" "../crypto/${MACHINE_IP}/tls-msp/${ORGN}/keystore/key.pem" 
done
#? Renaming the private key
for file in ../crypto/${MACHINE_IP}/tls-msp/${ORGN}/tlscacerts/*; do
mv "$file" "../crypto/${MACHINE_IP}/tls-msp/${ORGN}/tlscacerts/tls-ca.pem" 
done
#? Put a config.yaml inside of the cryptographic material
cp ../template/tls-msp/config.yaml ../crypto/${MACHINE_IP}/tls-msp/${ORGN}/.
else
echo "The organization tls-msp already exists, ignoring.."
fi