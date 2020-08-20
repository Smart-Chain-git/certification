# Tezos Digisign - Quick Start

This quick start deployment does not need any compilation. It uses the docker images in docker hub.
To build the VM with the requirement, it's possible to use vangrant+VirtualBox. A vagrant file with the right external port and ansible script is available: http://edivgitlab.swordgroup.lan/tezos/signature/deployment.

## Requirements

* Docker (for development) and Docker compose.

## VM deployment with vagrant

VirtualBox and Vagrant must have been installed on your computer.
After downloading http://edivgitlab.swordgroup.lan/tezos/signature/deployment, go into the tezos-node-light and launch the command:  
`vagrant up`
  
connect to the VM:  
`vagrant ssh`  



## Deployment

Retrieve the code digisign  
`git clone http://edivgitlab.swordgroup.lan/tezos/signature/tezos-servers`  
`cd tezos-servers`  

Launch the Tezos nodes (sandbox)  
`docker-compose -f docker-compose.sandbox.d.yml up -d node`
  
Install the Tezos client  
`cd scripts`  
`./install_tezos_client.sh`  
  
Create the contract (wait 30 seconds after the launch of the Tezos node)   
`./create_contract.sh`  
  
The logs are:  
```
...
    api:
      storage: "http://127.0.0.1:8000/explorer/bigmap/$bigmapId/$rootHash"
      transaction: "http://127.0.0.1:8000/explorer/op/$transactionHash"
    web:
      provider: "http://127.0.0.1:9090"
------------------------------------------------------------
Configuration of tezos-digisign-daemon and tezos-digisign-rest with [KT18yjgmJ6pLkYsT6se4ETc4L4NFa6pPZJnq] completed
```  
KT18yjgmJ6pLkYsT6se4ETc4L4NFa6pPZJnq is the id of the smart contract  
  
Launch all the services  
`cd ..`  
`docker-compose -f docker-compose.sandbox.d.yml up -d`  
  
  
In a Web browser, you can connect to Digisign:     
  
Connect to the website (the login/password is admin/Sword@35):  
`http://localhost:9090/index.html#/signature-check`
  
Connect to the Tezos Index and check the storage (after signing a document):  
`http://localhost:8000/explorer/bigmap/1/values`
  
Connect to the database with mongo express:  
`http://localhost:8081/#/login`
  
Check the api with swagger:  
`http://localhost:9090/webjars/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config`  
  
Check the fake SMTP server:  
`http://localhost:5080/`


## Stop the VM with vagrant

Quit the linux machine:  
`exit`  

Stop the VM:  
`vagrant halt`  


