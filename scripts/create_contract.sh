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

sed "s/CONTRACT_ADDRESS/$CONTRACT_ID/g" ../compose-config/application-daemon.template.yml > ../compose-config/application-daemon.all.yml



echo "------------------------------------------------------------"
cat ../compose-config/application-daemon.yml
echo "------------------------------------------------------------"

docker restart tezos-signature-daemon

echo "tezos-signature-daemon restarted with ["$CONTRACT_ID"]"

exit 0
