# Tezos Digisign - Quick Start

This quick start deployment does not need any compilation. It uses the docker images in docker hub.

## Requirements

* Docker (for development) and Docker compose.

## Deployment

Launch the Tezos nodes (sandbox)  
`docker-compose -f docker-compose.sandbox.d.yml up -d node`
  
Install the Tezos client  
`./install_tezos_client.sh`
  
Create the contract (wait 30 seconds after the launch of the Tezos node)  
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
trying to restart tezos-digisign-daemon and tezos-digisign-rest with [KT1Qk67N2sbKfr9Q22UBLBxLXkpXGKP1nEUR]
Error response from daemon: No such container: tezos-digisign-daemon
Error response from daemon: No such container: tezos-digisign-rest
```  
The 2 last errors are normal  
  
Launch all the services  
`cd ..`  
`docker-compose -f docker-compose.sandbox.d.yml up -d`  
  
Connect to the website:  
`http://localhost:9090/index.html#/signature-check`
  
Connect to the Tezos Index and check the storage (after signing a document):  
`http://localhost:8000/explorer/bigmap/1/values`
  
Connect to the database with mongo express:  
`http://localhost:8081/#/login`
  
Check the api with swagger:  
`http://localhost:9090/webjars/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config`  
  
Check the fake SMTP server:  
`http://localhost:5080/`



