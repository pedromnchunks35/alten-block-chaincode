#!/bin/bash

## Function to update YAML file using yq
update_yaml() {
  local FILE=$1
  local KEY=$2
  local NEW_VALUE=$3

  yq eval ".$KEY = \"$NEW_VALUE\"" -i "$FILE"
}

## FOR THE ORGX-PEER1
FILE_1="./orgx-couch-peer1/pv.yaml"
KEY_1="spec.local.path"
NEW_VALUE_1="/mnt/c/Users/psousaesilva/Documents/blockchain-identity-project/config/storage/orgx-couch-peer1"
KEY_12="spec.nodeAffinity.required.nodeSelectorTerms[0].matchExpressions[0].values[0]"
NEW_VALUE_12="0324123g-alten"

update_yaml "$FILE_1" "$KEY_1" "$NEW_VALUE_1"
update_yaml "$FILE_1" "$KEY_12" "$NEW_VALUE_12"

## FOR THE ORGX-PEER1
FILE_2="./orgx-peer1/pv.yaml"
KEY_2="spec.local.path"
NEW_VALUE_2="/mnt/c/Users/psousaesilva/Documents/blockchain-identity-project/config/storage/orgx-peer1"
KEY_22="spec.nodeAffinity.required.nodeSelectorTerms[0].matchExpressions[0].values[0]"
NEW_VALUE_22="0324123g-alten"

update_yaml "$FILE_2" "$KEY_2" "$NEW_VALUE_2"
update_yaml "$FILE_2" "$KEY_22" "$NEW_VALUE_22"

## FOR THE ORGY-PEER1
FILE_3="./orgy-couch-peer1/pv.yaml"
KEY_3="spec.local.path"
NEW_VALUE_3="/mnt/c/Users/psousaesilva/Documents/blockchain-identity-project/config/storage/orgy-couch-peer1"
KEY_32="spec.nodeAffinity.required.nodeSelectorTerms[0].matchExpressions[0].values[0]"
NEW_VALUE_32="0324123g-alten"

update_yaml "$FILE_3" "$KEY_3" "$NEW_VALUE_3"
update_yaml "$FILE_3" "$KEY_32" "$NEW_VALUE_32"

## FOR THE ORGY-PEER1
FILE_4="./orgy-peer1/pv.yaml"
KEY_4="spec.local.path"
NEW_VALUE_4="/mnt/c/Users/psousaesilva/Documents/blockchain-identity-project/config/storage/orgy-peer1"
KEY_42="spec.nodeAffinity.required.nodeSelectorTerms[0].matchExpressions[0].values[0]"
NEW_VALUE_42="0324123g-alten"

update_yaml "$FILE_4" "$KEY_4" "$NEW_VALUE_4"
update_yaml "$FILE_4" "$KEY_42" "$NEW_VALUE_42"

## FOR THE ORGZ-ORDERER
FILE_5="./orgz-orderer/pv.yaml"
KEY_5="spec.local.path"
NEW_VALUE_5="/mnt/c/Users/psousaesilva/Documents/blockchain-identity-project/config/storage/orgz-orderer"
KEY_52="spec.nodeAffinity.required.nodeSelectorTerms[0].matchExpressions[0].values[0]"
NEW_VALUE_52="0324123g-alten"

update_yaml "$FILE_5" "$KEY_5" "$NEW_VALUE_5"
update_yaml "$FILE_5" "$KEY_52" "$NEW_VALUE_52"

## FOR THE CA
FILE_6="./ca/pod.yaml"
KEY_6="spec.template.spec.volumes[0].hostPath.path"
NEW_VALUE_6="/mnt/c/Users/psousaesilva/Documents/blockchain-identity-project/config/storage/ca"
update_yaml "$FILE_6" "$KEY_6" "$NEW_VALUE_6"

## FOR THE CA
FILE_6="./tls-ca/pod.yaml"
KEY_6="spec.template.spec.volumes[0].hostPath.path"
NEW_VALUE_6="/mnt/c/Users/psousaesilva/Documents/blockchain-identity-project/config/storage/tls-ca"
update_yaml "$FILE_6" "$KEY_6" "$NEW_VALUE_6"
