#!/bin/bash
#? Create a directory for serving as org
mkdir ./temp/org

#? Put the orgs tls-msp
find ../crypto/*/tls-msp/ -type d -regex ".*/org.$" -print0 | while read -d $'\0' dir; do
#? Create the directory case it does not exist
mkdir -p ./temp/org/$(basename $dir)/tls-msp
#? Copy the files into it
cp -r ${dir}/* ./temp/org/$(basename $dir)/tls-msp/.
done

#? Put the orgs msp
find ../crypto/*/msp/ -type d -regex ".*/org.$" -print0 | while read -d $'\0' dir; do
#? Create the directory case it does not exist
mkdir -p ./temp/org/$(basename $dir)/msp
#? Copy the files into it
cp -r ${dir}/* ./temp/org/$(basename $dir)/msp/.
#? Put tlscacerts in it
cp -r ./temp/org/$(basename $dir)/tls-msp/tlscacerts ./temp/org/$(basename $dir)/msp/.
done

#? Put the orderers msp
find ../crypto/*/msp/ -type d -regex ".*/org.-orderer$" -print0 | while read -d $'\0' dir; do
name=$(basename $dir)
name="${name#org}"
name="${name%%-*}"
#? Create the directory case it does not exist
mkdir -p ./temp/msp-other-orderers/org${name}/msp
#? Copy the files into it
cp -r ${dir}/* ./temp/msp-other-orderers/org${name}/msp/.
done

#? Put the orderers tls-msp
find ../crypto/*/tls-msp/ -type d -regex ".*/org.-orderer$" -print0 | while read -d $'\0' dir; do
name=$(basename $dir)
name="${name#org}"
name="${name%%-*}"
#? Create the directory case it does not exist
mkdir -p ./temp/msp-other-orderers/org${name}/tls-msp
#? Copy the files into it
cp -r ${dir}/* ./temp/msp-other-orderers/org${name}/tls-msp/.
done