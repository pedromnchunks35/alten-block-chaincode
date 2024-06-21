#!/bin/bash
CN=$1
MACHINE_IP=$2
couchCN=$3
export CN
export MACHINE_IP
export couchCN

#? Create the template for the peer
cp -r ../template/peer ./temp/$CN

#? Put the core.yaml in the correct directory
cp -r ../config/yaml-hlf-files/$CN/core.yaml ./temp/$CN/config/.

#? Put the msp in the correct directory
cp -r ../crypto/$MACHINE_IP/msp/$CN/* ./temp/$CN/msp/.

#? Put the tls-msp in the correct directory
cp -r ../crypto/$MACHINE_IP/tls-msp/$CN/* ./temp/$CN/tls-msp/.

#? Create the template for the couch
cp -r ../template/couch ./temp/$couchCN