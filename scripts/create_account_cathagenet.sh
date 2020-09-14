#!/usr/bin/env bash

echo "Account creation"

if [ $# -eq 0 ]
then

        echo "Usage $0 <token> <account> <handle> <fullname> <password>"
        exit 1
fi
ACCOUNT=$2
HANDLE=$3
FULLNAME=$4
TOKEN=$1
PASSWORD=$5

echo "Inscription"
echo "ACCOUNT="$ACCOUNT
echo "FULLNAME="$FULLNAME
echo "HANDLE="$HANDLE
echo "TOKEN="$TOKEN
echo "PASSWORD="$PASSWORD

if [ ! -f $HANDLE ]
then
        echo "Error: file "$HANDLE" does not exist!"
        exit 1
fi

#bin/tezos-client -A carthagenet.smartpy.io  -P 80  config update 2>tc.log
bin/tezos-client -A carthagenet.smartpy.io -P 80 config update 2>tc.log
#bin/tezos-client -A 127.0.0.1 -P 18731  config update 2>tc.log
NBR=`wc -l tc.log | cut -d" " -f1`

echo "Activate account"
bin/tezos-client activate account $ACCOUNT with $HANDLE --force

bin/tezos-client show address $ACCOUNT -S 1>key_$ACCOUNT.log


PUBLIC_KEY=`grep Public key_$ACCOUNT.log | cut -d":" -f2 | sed -e "s/ //g"`
PRIVATE_KEY=`grep Secret key_$ACCOUNT.log | cut -d":" -f3 | sed -e "s/ //g"`
HASH_KEY=`grep Hash key_$ACCOUNT.log | cut -d":" -f2 | sed -e "s/ //g"`



echo "    "$ACCOUNT":" >config_$ACCOUNT.txt
echo '      hash: "'$HASH_KEY'"'>>config_$ACCOUNT.txt
echo '      publicKey: "'$PUBLIC_KEY'"'>>config_$ACCOUNT.txt
echo '      secretKey: "'$PRIVATE_KEY'"'>>config_$ACCOUNT.txt

echo "--------------------"
cat config_$ACCOUNT.txt
echo "--------------------" 
 
 
 

echo "Add in database"
curl -X POST "http://rest.tezos-signature.10.20.2.179.xip.io/api/accounts" -o creation_$ACCOUNT.log -H "accept: application/json" -H "origin: me" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "{\"login\":\"$ACCOUNT\",\"email\":\"$ACCOUNT@fake.fr\",\"fullName\":\"$FULLNAME\",\"company\":\"none\",\"country\":\"FRANCE\",\"publicKey\":\"$PUBLIC_KEY\",\"hash\":\"$HASH_KEY\",\"isAdmin\":false}"

IDACCOUNT=`cat creation_$ACCOUNT.log | sed 's/,/\n/g' | grep '"id"' | cut -d":" -f2 | sed 's/"//g'`


curl -X PATCH "http://rest.tezos-signature.10.20.2.179.xip.io/api/accounts/$IDACCOUNT" -H "accept: application/json" -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d "{\"login\":\"$ACCOUNT\",\"email\":\"$ACCOUNT@fake.fr\",\"password\":\"$PASSWORD\",\"fullName\":\"$FULLNAME\",\"company\":\"none\",\"country\":\"FRANCE\",\"publicKey\":\"$PUBLIC_KEY\",\"hash\":\"$HASH_KEY\",\"isAdmin\":false,\"disabled\":false}"

bin/tezos-client transfer 1 from $ACCOUNT to alice  --burn-cap 0.257

exit 0