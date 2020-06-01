#!/usr/bin/env bash

PRIVATEKEY=$1


echo "Insert Key in the configuration file of the daemon"


sed "s/TO_REPLACE_WITH_YOUR_COMPUTED_KEY/$PRIVATEKEY/g" ../compose-config/application-daemon.light.template.yml > app.yml

mv app.yml ../compose-config/application-daemon.light.yml

echo "------------------------------------------------------------"
cat ../compose-config/application-daemon.light.yml
echo "------------------------------------------------------------"


exit 0