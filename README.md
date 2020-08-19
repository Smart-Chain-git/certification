[Index](./README.md) | [Smart contract](contract/README.md) | [Docker Deployment](documentation/DockerDeployment.md) | [Quick Start](scripts/QuickStart.md)

# Tezos Digisign

## Modules

### Frontend modules

* JSClient (module **frontend/jsclient**): client providing UI for end-users (included in the REST API assets at build).
* Rest (module **frontend/rest**): REST API.
* RSocket (module **frontend/rsocket**): RSocket API.

### Backend modules

* Daemon (module **backend/daemon**): daemon that interact with the blockchain to anchor data.

### Librairies

* API (module **lib/api**): API between the REST/RSocket clients and servers.
* Business (module **lib/business**): business services and entities.
* Common (module **lib/common**): utility code shared between libraries.
* Merkle tree (module **lib/merkle-tree**): merkle tree library.
* Model (module **lib/model**): definition of mongoDB entities, indices, and repositories.
* Tezos reader (module **lib/tezos-reader**): services which retrieves blockchain data from indexer.
* Tezos writer (module **lib/tezos-writer**): services which connect to a tezos node and writes into the blockchain.
* Web Core (module **lib/web-core**): authentication and utility services for frontend modules. 

## Requirements

* Java 11 or higher.
* Docker (for development).

## Development

Setup the database, mail server, and local sandbox tezos node:
* Launch mongoDB, mongo express (admin UI), fakeSMTP, and sandbox tezos node docker containers: `docker-compose up -d`
* Access the mongoDB administration: [http://localhost:8081](http://localhost:8081)
* Access the fakeSMTP UI: [http://localhost:5080](http://localhost:5080)
* Reach the sandbox tezos node: [http://localhost:18731](http://localhost:18731)

Originate the smart contract in the local sandbox tezos node
* Install the tezos-client, if not done already: `scripts/install_tezos_client.sh`
* Originate the smart contract: `(cd contract; make originate)`
Please note that the node data do not persist so if the node container restarts, the smart contract needs to be originated again.
* Optional: to update the client configuration with the current node host and port in order to execute other commands: `(cd contract; make updateClient)`

Run the daemon:
* Edit the configuration files: `backend/daemon/src/main/resources/application.yml`
* Launch the web server: `./gradlew :backend:daemon:bootrun`

Run the REST server:
* Edit the configuration files: `frontend/rest/src/main/resources/application.yml`
* Launch REST web server: `./gradlew :frontend:rest:bootrun`
* Reach the REST API: [http://localhost:9090](http://localhost:9090)
* Access the Swagger documentation: [SwaggerUI](http://localhost:9090/swagger-ui.html)

Run the RSocket server (only if you have a RSocket client):
* Edit the configuration files: `frontend/rsocket/src/main/resources/application.yml`
* Launch the web server: `./gradlew :frontend:rsocket:bootrun`
* Reach the RSocket API: [http://localhost:6666](http://localhost:6666)

Run the JSClient in development mode:
* Move to the client directory: `cd frontend/jsclient`
* Install client dependencies: `npm install`
* Launch the client in development mode: `npm run serve`
* Access the UI: [http://localhost:8082](http://localhost:8082)

## Production

Build the JARs: `./gradlew build`

Retrieve the JARs:
* `frontend/rest/build/libs/$JAR_FILE`
* `frontend/rsocket/build/libs/$JAR_FILE`
* `backend/daemon/build/libs/$JAR_FILE`

Run the servers in production mode
* Move the configuration files next to the JARs (one folder per JAR file).
* Edit the configuration files to match the production settings.
* Run the JARs: `java -jar $JAR_FILE`
