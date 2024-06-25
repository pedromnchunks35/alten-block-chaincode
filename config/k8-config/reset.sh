#!/bin/sh
for file in ./*/; do
    kubectl delete -f ${file}
    sleep 2
done
for file in ./*/; do
    kubectl apply -f ${file}
    sleep 2
done
