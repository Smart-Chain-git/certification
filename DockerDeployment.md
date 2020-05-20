[Index](./README.md) [Smart contract](contract/README.md) [Docker Deployment](./DockerDeployment.md)

# Docker Deployment (all microservices of the stack)

## Tezos sandbox node

* Launch the tezos sandbox node
```
scripts/sandbox_node.sh start
```

* Install the tezos client and import bootstrap accounts (first time only)
```
scripts/install_tezos_client
export PATH=$PATH:$PWD/scripts/bin
```

* Originate the smart contract (after first start or after reset)
```
(cd contract; make originate)
```

Notice: after reboot, the node is not automatically restarted so it should be resumed using the **start** command.

## Signature microservices

### Docker images build (*either build or import*)

* Build and unpack all services
```
./gradelw unpack
```

* Build the images (after each code update)
```
docker-compose -f docker-compose.all.yml build
```

### Docker images import (*either build or import*)

* Import the docker images of each provided service
```
docker load -i TAR_FILE
```

### Run the services

* Run the services
```
docker-compose -f docker-compose.all.yml up -d
```

* Stop the services (add **-v** to reset database)
```
docker-compose -f docker-compose.all.yml down
```

### Access the services

* Access the UI at ``http://localhost:8080`` (default credentials: admin/password)
* Access the MongoDB UI at ``http://localhost:8081``
* Access the REST API documentation at ``http://localhost:9090/swagger-ui.html``
* To authenticate on the REST API: click on the **Authorize** button and provide the access token (retrieved through the UI)

