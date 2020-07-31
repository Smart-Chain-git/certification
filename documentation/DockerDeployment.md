[Index](../README.md) | [Smart contract](../contract/README.md) | [Docker Deployment](./DockerDeployment.md)

# Docker Deployment

## Deployment options

### Sandbox node

Components:
* MongoDB database
* MongoDB administration UI
* FakeSMTP server
* Tezos sandbox node
* Tzindex block indexer
* Tezos Signature REST server (+ client)
* Tezos Signature daemon

Configuration:
* Daemon configuration file: **compose-config/sandbox/application-daemon.yml**.
* REST server configuration file: **compose-config/sandbox/application-rest.yml**.

### Carthagenet mode

Components:
* MongoDB database
* MongoDB administration UI
* FakeSMTP server
* Tezos Signature REST server (+ client)
* Tezos Signature daemon

Configuration:
* Daemon configuration file: **compose-config/carthagenet/application-daemon.yml**.
* REST server configuration file: **compose-config/carthagenet/application-rest.yml**.
* To add your tezos keys: edit the daemon configuration (property *tezos.keys.admin*).

### Mainnet mode

Components:
* MongoDB database
* MongoDB administration UI
* FakeSMTP server
* Tezos Signature REST server (+ client)
* Tezos Signature daemon

Configuration:
* Daemon configuration file: **compose-config/mainnet/application-daemon.yml**.
* REST server configuration file: **compose-config/mainnet/application-rest.yml**.
* To add your tezos keys: edit the daemon configuration (property *tezos.keys.admin*).
  
The only difference between **Carthagenet mode** and **Mainnet mode** is the configuration (node URL, smart contract address, tezos keys).

## Deploy the solution

### Docker images build (*either build or import*)

* Build and unpack all services
```
./gradelw unpack
```

* Build the images (after each code update)

#### Sandbox

```
docker-compose -f docker-compose.sandbox.yml build
```

#### Carthagenet

```
docker-compose -f docker-compose.carthagenet.yml build
```

#### Mainnet

```
docker-compose -f docker-compose.mainnet.yml build
```

### Docker images import (*either build or import*)

* Import the docker images of each provided service
```
docker load -i TAR_FILE
```

### Run the services

#### Sandbox

* Run the services
```
docker-compose -f docker-compose.sandbox.yml up -d
```
* The same way as development mode, for sandbox you need to originate the contract.
```
(cd contract; make originate)
```
* Then, edit the file **compose-config/sandbox/application-daemon.yml** and replace the property *tezos.contract.address* by the contract address.
* Finally, restart the daemon container:
```
docker restart tezos-signature-daemon
```

#### Carthagenet

* Run the services
```
docker-compose -f docker-compose.carthagenet.yml up -d
```

#### Mainnet:

* Run the services
```
docker-compose -f docker-compose.mainnet.yml up -d
```

### Stop the services

#### Sandbox

* Stop the services (add **-v** to reset database)
```
docker-compose -f docker-compose.sandbox.yml down
```

#### Carthagenet

* Stop the services (add **-v** to reset database)
```
docker-compose -f docker-compose.carthagenet.yml down
```

#### Mainnet

* Stop the services (add **-v** to reset database)
```
docker-compose -f docker-compose.mainnet.yml down
```

### Access the services

* Access the UI at `http://localhost:9090` (default credentials: admin/Sword@35)
* Access the REST API documentation at `http://localhost:9090/swagger-ui.html`
* Access the MongoDB UI at `http://localhost:8081`
* To authenticate on the REST API: click on the **Authorize** button and provide the access token (retrieved through the UI)

