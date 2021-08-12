# Adding Database Persistence

We will be using `postgres` to persist our data. We will be using the Jooq DSL to write our queries, 
which is packaged in the CSW Database service.

## Database setup

Follow the installation guide to download & install postgres on your machine, if not already installed.
[Link](https://www.postgresql.org/download/)

The CSW Database Service needs to be running before starting the App.
The following instructions show how to run the Database Service along with the Location Service and the Authentication and Authorization Service:

```bash
cs install csw-services:v4.0.0-M1
csw-services start -k -d
```

Login to postgres with your default user and create a new user to be used with our application.

```bash
psql -d postgres
postgres => CREATE USER postgres with password 'postgres';
postgres => /q;
psql -d postgres -U postgres
```

For this application, we will create an `RAVALUES` table in the database that the application can use to perform fetch and insert queries.
The following commands can be used to create a table

```bash
postgres = >
CREATE TABLE RADECVALUES(
id TEXT             PRIMARY KEY     NOT NULL,
formattedRa TEXT                    NOT NULL,
formattedDec TEXT                    NOT NULL
);
```

In the application, we depend on environment variables to pick up your username and password for the database, thus DB_USERNAME and DB_PASSWORD need to be set.
To set environment variables, use the command

```bash
export DB_USERNAME=<VALUE> DB_PASSWORD=<VALUE>
```

In the application, the `database name`, `username`, `password` is picked up from the `application.conf` configuration file.
Update `application.conf` by adding these entries.

Scala
: @@snip [application.conf](../../../../backend/src/main/resources/application.conf) { #db-conf }

## Update backend Implementation

Add a `csw-database` dependency in `Libs.scala`

Scala
: @@snip [Libs.scala](../../../../backend/project/Libs.scala) { #add-db }

Use the `csw-database` dependency in your `build.sbt` file and reload the project in your IDE.

Scala
: @@snip [build.sbt](../../../../backend/build.sbt) { #add-db }

Go to the `impl` package and add a repository class `RaDecRepository.scala` using the DSL context provided by the CSW Database Service

Scala
: @@snip [RaDecRepository.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecRepository.scala) { #add-repository }

Add a method to insert data into the DB

Scala
: @@snip [RaDecRepository.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecRepository.scala) { #insert-raDecValue-in-db }

Add a method to get data from the DB

Scala
: @@snip [RaDecRepository.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecRepository.scala) { #get-raDecValues-from-db }

Add the repository dependency and an implicit execution context in `RaDecImpl.scala`

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecImpl.scala) { #add-repository  }

Update the `raDecToString` implementation to use an insert" query method in `RaDecImpl.scala`

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecImpl.scala) { #raDecToString-impl }

Update the `getRaDecValues` implementation to use a "get values" query method in `RaDecImpl.scala`

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecImpl.scala) { #getRaDecValues-impl }

Update `SampleWiring.scala`

Add the DB setup in `SampleWiring.scala`

Scala
: @@snip [SampleWiring.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/SampleWiring.scala) { #db-wiring-setup }

Add a reference to the repository and update the implementation reference

Scala
: @@snip [SampleWiring.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/SampleWiring.scala) { #raDecImpl-db-ref }

Run the backend application, test your application using `apptest.http`, and verify the data is saved in your postgres table.

```bash
sbt:backend> run start
```
