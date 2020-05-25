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
  $tezos_client import secret key alice unencrypted:edsk3QoqBuvdamxouPhin7swCvkQNgq4jP5KZPbwWNnwdZpSpJiEbq --force
  $tezos_client import secret key bob unencrypted:edsk3RFfvaFaxbHx8BMtEW1rKQcPtDML3LXjNqMNLCzC3wLC1bWbAt --force
}

download_tezos_client
import_sandbox_accounts

exit 0