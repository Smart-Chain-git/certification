[Index](../README.md) | [Architecture](./architecture.md) | [Smart contract](../contract/README.md) | [Docker Deployment](./docker-deployment.md) | [Quick Start](./quickstart.md)

# Tezos Digisign - Quick Start

The create_account_cathagenet.sh scripts enables to create conveniently one user account.

## Requirements

* Tezos client installed (see install_tezos_client.sh script)
* An admin user created in the Tezos Digisign with a token


## Creation

Go to the Tezos Faucet [https://faucet.tzalpha.net/](https://faucet.tzalpha.net/)  
  
Retrieve the json file and put it on your server  
  
Launch the script 
`create_account_cathagenet.sh <url> <token> <account> <handle> <fullname> <password>`
