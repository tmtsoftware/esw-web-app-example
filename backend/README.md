# library

This project implements a sample HTTP server-based application using
TMT Executive Software ([ESW](https://github.com/tmtsoftware/esw)) APIs.

## Build Instructions

The build is based on sbt and depends on libraries generated from the
[ESW](https://github.com/tmtsoftware/esw) project.

See [here](https://www.scala-sbt.org/1.0/docs/Setup.html) for instructions on installing sbt.
## About

This application is an example of accessing the database service using jooq DSLContext. Here we take an example of a 
simple library management system with functionality to list all the books in the library and to enter a book in the library.

## Prerequisites for Running App

* We recommend using coursier for installing and running the apps. Steps for installing coursier are documented 
[here](https://tmtsoftware.github.io/csw/apps/csinstallation.html). The CSW Database Service needs to be running before starting the App.
Follow below instructions to run database service:

```
cs install csw-services:v3.0.0-M1
csw-services start -d
```

* We depend upon some environment variables to pick up username and password for the database, thus `DB_USERNAME` and
  `DB_PASSWORD` need to be set. To set environment variables, use the command `export DB_USERNAME=<VALUE> DB_PASSWORD=<VALUE>`
  
* The database name is picked up from the application.conf and is set to `postgres`. 

* This application performs fetch and insert queries on the `BOOKS` table in the database, thus it needs to be present. 
  Following command can be used to create table.
  ```bash
  CREATE TABLE BOOKS(
                      id TEXT PRIMARY KEY     NOT NULL,
                      title           TEXT    NOT NULL,
                      author            TEXT     NOT NULL,
                      available BOOLEAN NOT NULL
  );
  ```


## Running the App

Before we start the app we need to set the following environment variables:
* TMT_LOG_HOME

To set environment variables, use the command `export <ENV_VAR> = <VALUE>`

By default, an interface name will be selected for you.  However, if you are having problems or have more than a single 
network interface, you may need to set the environment variables `INTERFACE_NAME` and `AAS_INTERFACE_NAME` explicitly. 
For development, these two variables can be set to the primary machine interface name. For example, `en0`.  See the 
CSW documentation on [Network Topology](http://tmtsoftware.github.io/csw/deployment/network-topology.html) for more
information.

To start the app, run:
`sbt "run start"`
This will start the app with default port 8080. 

If you want to start the app at custom port,
run `sbt "run start -p <port number>`

## How to Use the Project
```bash
.
├── src
│   ├── main
│   │   └── scala
```

* The routes can be added in [LibraryRoute](src/main/scala/org/tmt/library/http/LibraryRoutes.scala).
Some example routes have been provided.
  
* The API implementation can be added in [LibraryImpl](./src/main/scala/org/tmt/library/core/LibraryImpl.scala).
This template provides an implementation that matches the example routes.

* Core models for supporting the APIs should be added in the [models](src/main/scala/org/tmt/library/models) package.
Codecs for these models should be added in [HttpCodecs](./src/main/scala/org/tmt/library/http/HttpCodecs.scala).

* [LibraryWiring](./src/main/scala/org/tmt/library/LibraryWiring.scala) is where the implementation wired up with the routes.

* [LibraryApp](./src/main/scala/org/tmt/library/LibraryApp.scala) is the main runnable application. The command line arguments 
for starting the app are defined in [LibraryAppCommand](./src/main/scala/org/tmt/library/LibraryAppCommand.scala). Any new command  
or option for command can be added like so:
```
 @CommandName("<command_name>")
  final case class <command_name>(
     @HelpMessage("<help message>")
     @ExtraName("<option>")
     option: <type>
   ) extends SampleAppCommand
```
* The newly added command/options need to handled in LibraryApp

* Any new application specific configuration can be added in [application.conf](./src/main/resources/application.conf)
