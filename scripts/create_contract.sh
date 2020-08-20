#!/usr/bin/env bash

echo "Contract creation"


echo "Inscription"
./bin/tezos-client -A 127.0.0.1 -P 18731  config update 2>tc.log
NBR=`wc -l tc.log | cut -d" " -f1`
if [ $NBR -gt 0 ]
then
	echo "Error trying to connect to Tezos Node"
	cat tc.log
	exit 1
fi
./bin/tezos-client -A 127.0.0.1 -P 18731 originate contract hash_timestamping transferring 0 from alice running hash_timestamping.tz --init '{}' --burn-cap 5 --force 1> originate.log 2>tc.log
NBR=`wc -l tc.log | cut -d" " -f1`
if [ $NBR -gt 0 ]
then
	echo "Error trying to connect to Tezos Node"
	cat tc.log
	exit 1
fi
CONTRACT_ID=`grep "New contract" originate.log | grep "originated" | cut -d" " -f 3`
echo
echo "CONTRACT_ID=["$CONTRACT_ID"]"
echo

sed "s/TEZOS_CONTRACT_ADDRESS/$CONTRACT_ID/g" ../compose-config/sandbox/application-daemon.yml > ../compose-config/sandbox/application-daemon.d.yml
sed "s/TEZOS_CONTRACT_ADDRESS/$CONTRACT_ID/g" ../compose-config/sandbox/application-rest.yml > ../compose-config/sandbox/application-rest.d.yml



echo "daemon------------------------------------------------------------"
cat ../compose-config/sandbox/application-daemon.d.yml
echo "------------------------------------------------------------"
echo "rest------------------------------------------------------------"
cat ../compose-config/sandbox/application-rest.d.yml
echo "------------------------------------------------------------"

echo "Configuration of tezos-digisign-daemon and tezos-digisign-rest with ["$CONTRACT_ID"] completed"


exit 0
