for file in ./or*-couch-peer1; do
    rm -f -r $file/*
done
for file in ./org*-peer1; do
    rm -f -r $file/data-vault/*
    rm -f -r $file/snapshots/*
done
for file in ./org*-orderer; do
    rm -f -r $file/etcdraft-wal/*
    rm -f -r $file/ledger-vault/*
    rm -f -r $file/snapshot/*
done
