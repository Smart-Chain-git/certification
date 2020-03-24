# Tezos signature servers 

## Modules

* Web server (module **frontend:ihm**): webserver providing the UI for end-users.
* API (**lib**): business services and entities (**business**) and mongoDB collections representations and migrations (**model**).
* Daemon: *not released yet*.

## Requirements

* Java 11 or higher.
* Docker (for development).

## Development

Run the web server in development mode:
* Launch mongoDB docker container: `docker-compose up -d`
* Edit the configuration file: `frontend/ihm/src/application.yml`
* Launch the web server: `./gradlew bootrun`

Access the web server:
* Access the web server UI: [http://localhost:8080](http://localhost:8080)
* Access the mongoDB administration: [http://localhost:8081](http://localhost:8081)

## Production

Build the web server:
* Build the JAR: `./gradlew build`
* Retrieve the JAR `frontend/ihm/build/libs/$JAR_FILE`

Run the web server in production mode
* Move the configuration file next to the JAR.
* Edit the configuration file to match the production settings.
* Run the JAR: `java -jar $JAR_FILE`