# data-center-api project

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Prerequisites

You can find the Core Services in the local development stack: [local-dev-stack-istos-components](https://bitbucket.app.dmgmori.com/projects/ISTOS-SC/repos/local-dev-stack-istos-components/browse/core-services)

You need to add the line `127.0.0.1 keycloak` to your hosts file at `c:\Windows\System32\drivers\etc\hosts` as an Administrator.

Make sure to replace the value of the `quarkus.oidc-client.credentials.secret` property with your own.

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```
mvn quarkus:dev
```

## Running the application in Docker

You can run your application in Docker using:

docker-compose -f docker-compose.yml up -d --build
