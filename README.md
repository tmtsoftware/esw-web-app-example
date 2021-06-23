# Library Service

## About

This application is an example of accessing the database service using jooq DSLContext. Here we take an example of a simple library management system which has following functionality.

- list all the books in the library.
- enter a book in the library.


## Running the Backend Server

### Prerequisites

- We recommend using coursier for installing and running the apps. Steps for installing coursier are documented [here](https://tmtsoftware.github.io/csw/apps/csinstallation.html). The CSW Database Service needs to be running before starting the App.
Follow below instructions to run database service:

```
cs install csw-services:v3.0.0-M1
csw-services start -d
```

- We depend upon some environment variables to pick up username and password for the database, thus `DB_USERNAME` and `DB_PASSWORD` need to be set. To set environment variables, use the command `export DB_USERNAME=<VALUE> DB_PASSWORD=<VALUE>`
  
- The database name is picked up from the application.conf and is set to `postgres`. 

- This application performs fetch and insert queries on the `BOOKS` table in the database, thus it needs to be present. Following command can be used to create table.
  ```bash
  CREATE TABLE BOOKS(
                      id TEXT PRIMARY KEY     NOT NULL,
                      title           TEXT    NOT NULL,
                      author            TEXT     NOT NULL,
                      available BOOLEAN NOT NULL
  );
  ```

- set the `TMT_LOG_HOME` environment variable

- By default, an interface name will be selected for you.  However, if you are having problems or have more than a single network interface, you may need to set the environment variables `INTERFACE_NAME` and `AAS_INTERFACE_NAME` explicitly. 
For development, these two variables can be set to the primary machine interface name. For example, `en0`.  See the 
CSW documentation on [Network Topology](http://tmtsoftware.github.io/csw/deployment/network-topology.html) for more
information.

### Command to start the server

To start the app, go into backend folder and run:

```bash
sbt "run start"
```

This will start the app with default port 8080.

If you want to start the app at custom port,
run `sbt "run start -p <port number>`

## Running the Frontend App

To run the front end app. First go into the frontend folder and do the following:

### Prerequisites for Running the Frontend App

- The latest version of [Node.js](https://nodejs.org/en/download/package-manager/) must be installed.
  
- Backend server should running.
  
### To Run the App in Local Environment

Run following commands in the terminal.

   ```bash
   npm install
   npm start
   ```

Then, open [localhost:8080](http://localhost:8080) in a browser

### To Build the App for Production

Run following commands in the terminal.

```bash
npm install
npm run build
```
