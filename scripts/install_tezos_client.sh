#!/usr/bin/env bash

output_dir=$(dirname "$0")/bin
tezos_client=$output_dir/tezos-client

download_tezos_client () {
  if [ ! -f "$tezos_client" ]; then
    /usr/bin/wget https://github.com/serokell/tezos-packaging/releases/latest/download/tezos-client -P "$output_dir"
    chmod u+x "$tezos_client"
  fi
}

import_sandbox_accounts() {
  SK[1]="unencrypted:edsk3gUfUPyBSfrS9CCgmCiQsTCHGkviBDusMxDJstFtojtc1zcpsh"
  SK[2]="unencrypted:edsk39qAm1fiMjgmPkw1EgQYkMzkJezLNewd7PLNHTkr6w9XA2zdfo"
  SK[3]="unencrypted:edsk4ArLQgBTLWG5FJmnGnT689VKoqhXwmDPBuGx3z4cvwU9MmrPZZ"
  SK[4]="unencrypted:edsk2uqQB9AY4FvioK2YMdfmyMrer5R8mGFyuaLLFfSRo8EoyNdht3"
  SK[5]="unencrypted:edsk4QLrcijEffxV31gGdN2HU7UpyJjA8drFoNcmnB28n89YjPNRFm"
  for i in $(seq 1 5)
  do
    $tezos_client import secret key "bootstrap$i" ${SK[i]} --force
  done
}

download_tezos_client
import_sandbox_accounts

exit 0