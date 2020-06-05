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
if [ -f ../compose-config/application-daemon.yml.save ]
then
	echo "recreation"
else
	cp ../compose-config/application-daemon.yml ../compose-config/application-daemon.yml.save
fi
sed "s/KT1KPzcYY3Yb7gFGZWa1Eu9wu7t9eU764cDx/$CONTRACT_ID/g" ../compose-config/application-daemon.yml.save > app.yml

mv app.yml ../compose-config/application-daemon.yml

echo "------------------------------------------------------------"
cat ../compose-config/application-daemon.yml
echo "------------------------------------------------------------"

docker restart tezos-signature-daemon

echo "tezos-signature-daemon restarted"

exit 0