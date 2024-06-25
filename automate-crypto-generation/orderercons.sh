#!/bin/bash
CN=$1
MACHINE_IP=$2
export CN
export MACHINE_IP
#? Put the template in temp
mkdir ./temp/$CN
cp -r ../template/orderer/* ./temp/$CN

#? Put the orderer.yaml in the config directory
cp -r ../config/$MACHINE_IP/$CN/orderer.yaml ./temp/$CN/config/.

#? Put the msp inside of the correct directory
cp -r ../crypto/$MACHINE_IP/msp/$CN/* ./temp/$CN/msp/.

#? Put the tls-msp inside of the correct directory
cp -r ../crypto/$MACHINE_IP/tls-msp/$CN/* ./temp/$CN/tls-msp/.

#? Put the orgs tls-msp
find ../crypto/*/tls-msp/ -type d -regex ".*/org.$" -print0 | while read -d $'\0' dir; do
    #? Create the directory case it does not exist
    mkdir -p ./temp/$CN/org/$(basename $dir)/tls-msp
    #? Copy the files into it
    cp -r ${dir}/* ./temp/$CN/org/$(basename $dir)/tls-msp/.
done

#? Put the orgs msp
find ../crypto/*/msp/ -type d -regex ".*/org.$" -print0 | while read -d $'\0' dir; do
    #? Create the directory case it does not exist
    mkdir -p ./temp/$CN/org/$(basename $dir)/msp
    #? Copy the files into it
    cp -r ${dir}/* ./temp/$CN/org/$(basename $dir)/msp/.
    #? Put tlscacerts in it
    cp -r ./temp/$CN/org/$(basename $dir)/tls-msp/tlscacerts ./temp/$CN/org/$(basename $dir)/msp/.
done

#? Put the orderers msp
find ../crypto/*/msp/ -type d -regex ".*/org.-orderer$" -print0 | while read -d $'\0' dir; do
    if [ $(basename $dir) != $CN ]; then
        name=$(basename $dir)
        name="${name#org}"
        name="${name%%-*}"
        #? Create the directory case it does not exist
        mkdir -p ./temp/$CN/msp-other-orderers/org${name}/msp
        #? Copy the files into it
        cp -r ${dir}/* ./temp/$CN/msp-other-orderers/org${name}/msp/.
    fi
done

#? Put the orderers tls-msp
find ../crypto/*/tls-msp/ -type d -regex ".*/org.-orderer$" -print0 | while read -d $'\0' dir; do
    if [ $(basename $dir) != $CN ]; then
        name=$(basename $dir)
        name="${name#org}"
        name="${name%%-*}"
        #? Create the directory case it does not exist
        mkdir -p ./temp/$CN/msp-other-orderers/org${name}/tls-msp
        #? Copy the files into it
        cp -r ${dir}/* ./temp/$CN/msp-other-orderers/org${name}/tls-msp/.
    fi
done
