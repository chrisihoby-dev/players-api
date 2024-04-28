# PLAYERS API

## Features
Just a Simple CRUD for players

## Tech

it uses the following frameworks,tools and libraries:

- [Kotlin] - Programming language
- [Ktor] - Reactive web framework 
- [Koin] - Kotlin dependency injection library.
- [Arrow] - Functional programming and Domain modeling library
- [Kotest] - Test library.
- [TestContainers] - test library.
- [Mockk] - test library.
- [Gradle] - Build Tool
- [KtorSwaggerUI] - Rest API Documentation library.



## Installation 

it requires [Java](https://www.oracle.com/fr/java/technologies/downloads/#java17) version 21, Docker and Docker Compose to run.

## Build
You can run the following script to build the application:
```sh
cd build-script
./build-app.sh
```
It will build, test and generate jar and docker image for the application 

## Run jar (build and docker running required)
you can run the following script to launch the generated jar. You can specify the database url endpoint (DB_ENDPOINT) and the app port (PORT)
variables in the app-jar-launch.sh file. If not specified the database url endpoint would be 'http://localhost:8000' and the port 8080

```sh
cd launch-script
./app-jar-launch.sh
```
## Run containers (build and docker running required)
you can run the following script to containers. You can specify the database url endpoint (DB_ENDPOINT) and the app port (PORT)
variables in docker-compose.yml file. If not specified the database url endpoint would be 'http://localhost:8000' and the port 8081

```sh
cd launch-script
./container-launch.sh
```
you can stop containers by running the following command
```sh
cd launch-script
./container-stop.sh
```
the variables AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY and AWS_SESSION_TOKEN can also be specified in the launch script
or in the docker compose file if necessary 

## API documentation

When the application is running, the api documentation will be available under /swagger-ui/index.html
- example http://localhost:8080/swagger-ui/index.html 


[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen.)

[Java]: <https://www.oracle.com/uk/java/technologies/downloads/#java21>
[KtorSwaggerUI]: <https://github.com/SMILEY4/ktor-swagger-ui>
[Gradle]: <https://gradle.org>
[Koin]: <https://insert-koin.io>
[Kotlin]: <https://kotlinlang.org>
[Ktor]: <https://ktor.io>
[Kotest]: <https://kotest.io>
[TestContainers]: <https://testcontainers.com>
[Arrow]: <https://arrow-kt.io>
[Mockk]: <https://mockk.io/>

  
