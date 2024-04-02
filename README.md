# book-service project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Prerequisites

You need to generate a Public and Private Keys pair for example with OpenSSL.
Add both keys files privateKey.pem and publicKey.pem to the resources folder.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```
mvn quarkus:dev
```

## Packaging and running the application with Dockerfile.jvm

In order to run your application in Docker you need to package it using `./mvnw package`. It produces the book-service.jar file in the /target directory.

The application is now runnable using `java -jar target/book-service.jar`.

Build the docker container image using `docker build -f src/main/docker/Dockerfile.jvm -t quarkus/book-service-jvm .`

Then run the container using `docker run -i --rm -p 8989:8686 quarkus/book-service-jvm` 

Surely you can change the ports depending on your system.

## Creating a native executable with Dockerfile.native

You can create a native executable using: `./mvnw package -Pnative`.

Build the docker image using `docker build -f src/main/docker/Dockerfile.native -t quarkus/book-service .`

Then run the container using `docker run -i --rm -p 8080:8080 quarkus/book-service`

In the case that you don't have GraalVM installed, you can run the native executable build in a container using `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/book-service`

If you want to learn more about building native executables, please consult https://quarkus.io/guides/building-native-image.
