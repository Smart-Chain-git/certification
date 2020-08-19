# Tezos Digisign - Quick Start

## Requirements

* Docker (for development) and Docker compose.

## Deployment

Launch the Tezos nodes (sandbox)  
`docker-compose -f docker-compose.sandbox.d.yml up -d node`

Create the contract (wait 30 seconds after the previous command)  
`cd scripts`
`./create_contract.sh`

The logs are:  
```
  urls:
    api:
      storage: "http://127.0.0.1:8000/explorer/bigmap/$bigmapId/$rootHash"
      transaction: "http://127.0.0.1:8000/explorer/op/$transactionHash"
    web:
      provider: "http://127.0.0.1:9090"
------------------------------------------------------------
Error response from daemon: No such container: tezos-digisign-daemon
Error response from daemon: No such container: tezos-digisign-rest
tezos-digisign-daemon and tezos-digisign-rest restarted with [KT1T2fwtc5U4n45y4RSMzf9TqMP7CMVrEmGT]
```
The 2 last errors are normal  
  
Launch all the services  
`cd ..`
`docker-compose -f docker-compose.sandbox.d.yml up -d`