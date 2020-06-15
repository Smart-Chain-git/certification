[Index](./README.md) | [Smart contract](contract/README.md) | [Docker Deployment](./DockerDeployment.md)

# Tezos signature servers 

## Modules

### Frontend modules

* UI (module **frontend/ui**): webserver providing UI for end-users.
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
* Tezos writer (module **lib/tezos-writer**): services which writes into the blockchain.
* Web Core (module **lib/web-core**): authentication and utility services for frontend modules. 

## Requirements

* Java 11 or higher.
* Docker (for development).

## Development

Setup the database and mail server:
* Launch mongoDB, mongo express (admin UI), and fakeSMTP docker containers: `docker-compose up -d`
* Access the mongoDB administration: [http://localhost:8081](http://localhost:8081)
* Access the fakeSMTP UI: [http://localhost:5080](http://localhost:5080)

Run the daemon:
* Edit the configuration files: `backend/daemon/src/main/resources/application.yml`
* Launch the web server: `./gradlew :backend:daemon:bootrun`

Run the UI server:
* Edit the configuration files: `frontend/ui/src/main/resources/application.yml`
* Launch the UI server: `./gradlew :frontend:ui:bootrun`
* Access the UI: [http://localhost:8080](http://localhost:8080)

Run the REST server:
* Edit the configuration files: `frontend/rest/src/main/resources/application.yml`
* Launch REST web server: `./gradlew :frontend:rest:bootrun`
* Reach the REST API: [http://localhost:9090](http://localhost:9090)
* Access the Swagger documentation: [SwaggerUI](http://localhost:9090/swagger-ui.html)

Run the RSocket server:
* Edit the configuration files: `frontend/rsocket/src/main/resources/application.yml`
* Launch the web server: `./gradlew :frontend:rsocket:bootrun`
* Reac the RSocket API: [http://localhost:6666](http://localhost:6666)

## Production

Build the JARs: `./gradlew build`

Retrieve the JARs:
* `frontend/ui/build/libs/$JAR_FILE`
* `frontend/rest/build/libs/$JAR_FILE`
* `frontend/rsocket/build/libs/$JAR_FILE`
* `backend/daemon/build/libs/$JAR_FILE`

Run the servers in production mode
* Move the configuration files next to the JARs.
* Edit the configuration files to match the production settings.
* Run the JARs: `java -jar $JAR_FILE`
