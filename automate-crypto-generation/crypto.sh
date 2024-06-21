#!/bin/bash
#? Set enviroment variables
#!/bin/bash
MACHINE_IP=$1
CN=$2
ORGN=$3
PASSWORD=$4
COMPONENT_TYPE=$5
COUNTRY=$6
STATE=$7
LOCATION=$8
ORGANIZATION=$9
ADDITIONAL_MACHINES=$10
IP_ADDRESS_CA=$11
IP_ADDRESS_TLS_CA=$12

export MACHINE_IP
export CN
export ORGN
export PASSWORD
export COMPONENT_TYPE
export COUNTRY
export STATE
export LOCATION
export ORGANIZATION
export ADDITIONAL_MACHINES
export IP_ADDRESS_CA
export IP_ADDRESS_TLS_CA

#? Move to the right place and make it the fabric ca home
cd ../client
export FABRIC_CA_CLIENT_HOME=$PWD

#? Veryfing if the msp of the component exists
if [[ ! -d ../crypto/${MACHINE_IP}/msp/${CN} ]]; 
then
echo "Registing and Enrolling the component msp.."
#? Register the component msp
./fabric-ca-client register -d -u http://${IP_ADDRESS_CA} --id.name $CN --id.secret $PASSWORD --id.type $COMPONENT_TYPE --csr.cn $CN --csr.names "C=${COUNTRY},ST=${STATE},L=${LOCATION},O=${ORGANIZATION}" --csr.hosts "${MACHINE_IP},localhost,${CN},${ADDITIONAL_MACHINES}" --tls.certfiles ./root.tls.cert/carroot.cert --mspdir ./adm.msp/root.adm/
wait
#? Enroll the component msp
./fabric-ca-client enroll -d -u http://${CN}:${PASSWORD}@${IP_ADDRESS_CA} --id.type $COMPONENT_TYPE --csr.cn $CN --csr.names "C=${COUNTRY},ST=${STATE},L=${LOCATION},O=${ORGANIZATION}" --csr.hosts "${MACHINE_IP},localhost,${CN},${ADDITIONAL_MACHINES}" --tls.certfiles ./root.tls.cert/carroot.cert --mspdir ../crypto/${MACHINE_IP}/msp/${CN}
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
./fabric-ca-client register -d -u http://${IP_ADDRESS_TLS_CA}  --id.name $CN --id.secret $PASSWORD --id.type $COMPONENT_TYPE --csr.cn $CN --csr.names "C=${COUNTRY},ST=${STATE},L=${LOCATION},O=${ORGANIZATION}" --csr.hosts "${MACHINE_IP},localhost,${CN},${ADDITIONAL_MACHINES}" --tls.certfiles ./root.tls.cert/tlsroot.cert --mspdir ./adm.msp/tls.adm/
wait
#? Enroll the component tls-msp
./fabric-ca-client enroll -d -u http://${CN}:${PASSWORD}@${IP_ADDRESS_TLS_CA} --id.type $COMPONENT_TYPE --csr.cn $CN  --csr.names "C=${COUNTRY},ST=${STATE},L=${LOCATION},O=${ORGANIZATION}"  --csr.hosts "${MACHINE_IP},localhost,${CN},${ADDITIONAL_MACHINES}"  --enrollment.profile tls --tls.certfiles ./root.tls.cert/tlsroot.cert --mspdir ../crypto/${MACHINE_IP}/tls-msp/${CN}
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
./fabric-ca-client register -d -u http://${IP_ADDRESS_CA}  --id.name $ORGN --id.secret $PASSWORD --id.type $COMPONENT_TYPE --csr.cn $ORGN --csr.names "C=${COUNTRY},ST=${STATE},L=${LOCATION},O=${ORGANIZATION}" --csr.hosts "${MACHINE_IP},localhost,${CN},${ADDITIONAL_MACHINES}" --tls.certfiles ./root.tls.cert/carroot.cert --mspdir ./adm.msp/root.adm/
wait
#? Enroll the component msp
./fabric-ca-client enroll -d -u http://${ORGN}:${PASSWORD}@${IP_ADDRESS_CA} --id.type $COMPONENT_TYPE --csr.cn $ORGN --csr.names "C=${COUNTRY},ST=${STATE},L=${LOCATION},O=${ORGANIZATION}" --csr.hosts "${MACHINE_IP},localhost,${CN},${ADDITIONAL_MACHINES}" --tls.certfiles ./root.tls.cert/carroot.cert --mspdir ../crypto/${MACHINE_IP}/msp/${ORGN}
wait
#? Renaming the private key
for file in ../crypto/${MACHINE_IP}/msp/${ORGN}/keystore/*; do
mv "$file" "../crypto/${MACHINE_IP}/msp/${ORGN}/keystore/key.pem" 
done
#? Renaming the ca
for file in ../crypto/${MACHINE_IP}/msp/${CN}/cacerts/*; do
mv "$file" "../crypto/${MACHINE_IP}/msp/${CN}/cacerts/ca.pem" 
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
./fabric-ca-client register -d -u http://${IP_ADDRESS_TLS_CA}  --id.name $ORGN --id.secret $PASSWORD --id.type $COMPONENT_TYPE --csr.cn $ORGN --csr.names "C=${COUNTRY},ST=${STATE},L=${LOCATION},O=${ORGANIZATION}" --csr.hosts "${MACHINE_IP},localhost,${CN},${ADDITIONAL_MACHINES}" --tls.certfiles ./root.tls.cert/tlsroot.cert --mspdir ./adm.msp/tls.adm/
wait
#? Enroll the component tls-msp
./fabric-ca-client enroll -d -u http://${ORGN}:${PASSWORD}@${IP_ADDRESS_TLS_CA} --id.type $COMPONENT_TYPE --csr.cn $ORGN  --csr.names "C=${COUNTRY},ST=${STATE},L=${LOCATION},O=${ORGANIZATION}"  --csr.hosts "${MACHINE_IP},localhost,${CN},${ADDITIONAL_MACHINES}"  --enrollment.profile tls --tls.certfiles ./root.tls.cert/tlsroot.cert --mspdir ../crypto/${MACHINE_IP}/tls-msp/${ORGN}
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