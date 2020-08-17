#!/usr/bin/env bash

echo "Contract creation"

cd ../contract
make updateClient

echo "Inscription"
make originate > originate.log
CONTRACT_ID=`grep "New contract" originate.log | grep "originated" | cut -d" " -f 3`
echo
echo "CONTRACT_ID=["$CONTRACT_ID"]"
echo

sed "s/TEZOS_CONTRACT_ADDRESS/$CONTRACT_ID/g" ../compose-config/sandbox/application-daemon.yml > ../compose-config/sandbox/application-daemon.yml.tmp
mv ../compose-config/sandbox/application-daemon.yml.tmp ../compose-config/sandbox/application-daemon.yml



echo "------------------------------------------------------------"
cat ../compose-config/sandbox/application-daemon.yml
echo "------------------------------------------------------------"

docker restart tezos-digisign-daemon

echo "tezos-digisign-daemon restarted with ["$CONTRACT_ID"]"

exit 0
