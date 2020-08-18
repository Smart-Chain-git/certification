#!/usr/bin/env bash

echo "Contract creation"


echo "Inscription"
./bin/tezos-client -A 127.0.0.1 -P 18731  config update
./bin/tezos-client -A 127.0.0.1 -P 18731 originate contract hash_timestamping transferring 0 from alice running hash_timestamping.tz --init '{}' --burn-cap 5 --force > originate.log
CONTRACT_ID=`grep "New contract" originate.log | grep "originated" | cut -d" " -f 3`
echo
echo "CONTRACT_ID=["$CONTRACT_ID"]"
echo

sed "s/TEZOS_CONTRACT_ADDRESS/$CONTRACT_ID/g" ../compose-config/sandbox/application-daemon.yml > ../compose-config/sandbox/application-daemon.yml.tmp
mv ../compose-config/sandbox/application-daemon.yml.tmp ../compose-config/sandbox/application-daemon.yml
sed "s/TEZOS_CONTRACT_ADDRESS/$CONTRACT_ID/g" ../compose-config/sandbox/application-rest.yml > ../compose-config/sandbox/application-rest.yml.tmp
mv ../compose-config/sandbox/application-rest.yml.tmp ../compose-config/sandbox/application-rest.yml



echo "daemon------------------------------------------------------------"
cat ../compose-config/sandbox/application-daemon.yml
echo "------------------------------------------------------------"
echo "rest------------------------------------------------------------"
cat ../compose-config/sandbox/application-rest.yml
echo "------------------------------------------------------------"

docker restart tezos-digisign-daemon
docker restart tezos-digisign-rest

echo "tezos-digisign-daemon and tezos-digisign-rest restarted with ["$CONTRACT_ID"]"

exit 0
