#!/bin/bash
#? Search for all the orderers
find . -type d -name "../config/storage/*orderer" -print0 | while read -d $'\0' dir; do
#? Copy the files from the org formation and paste it
rm -f -r ${dir}/org/*
rm -f -r ${dir}/msp-other-orderers/*
cp -r ./temp/org/* ${dir}/org/.
cp -r ./temp/msp-other-orderers/* ${dir}/msp-other-orderers/.
done