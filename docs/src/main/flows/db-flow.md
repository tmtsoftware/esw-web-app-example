# Adding Database Persistence

We will be using `postgres` to persist our data. We will be using Jooq dsl to write our queries, which is packaged inside CSW Database service.

## Database setup

Follow the installation guide to download & install postgres on your machine, if not already installed.
[Link](https://www.postgresql.org/download/)

The CSW Database Service needs to be running before starting the App.
Follow below instructions to run database service along with location service and authentication service:

```bash
cs install csw-services:v4.0.0-M1
csw-services start -k -d
```

Login to your postgres with your default user and create a new user, this db user will be used to operate on our application.

```bash
psql -d postgres
postgres => CREATE USER postgres with password 'postgres';
postgres => /q;
psql -d postgres -U postgres
```

This application performs fetch and insert queries on the `RAVALUES` table in the database, thus it needs to be present.
Following command can be used to create a table

```sql
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

In the application, the `database name`, `username`, `password` is picked up from the `application.conf`
Update `application.conf` , add these entries.

Scala
: @@snip [application.conf](../../../../backend/src/main/resources/application.conf) { #db-conf }

## Update backend Implementation

Add csw-database dependency in `Libs.scala`

Scala
: @@snip [Libs.scala](../../../../backend/project/Libs.scala) { #add-db }

Use csw-database dependency in `build.sbt` and reload project in your IDE.

Scala
: @@snip [build.sbt](../../../../backend/build.sbt) { #add-db }

Go to `impl` package, Add repository class `RaDecRepository.scala` using dsl context provided by CSW Database

Scala
: @@snip [RaDecRepository.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecRepository.scala) { #add-repository }

Add method to insert data to db

Scala
: @@snip [RaDecRepository.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecRepository.scala) { #insert-raDecValue-in-db }

Add method to get data from db

Scala
: @@snip [RaDecRepository.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecRepository.scala) { #get-raDecValues-from-db }

Add repository dependency and implicit execution context in `RaDecImpl.scala`

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecImpl.scala) { #add-repository  }

Update `raDecToString` implementation to use insert query method in `RaDecImpl.scala`

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecImpl.scala) { #raDecToString-impl }

Update `getRaDecValues` implementation to use get values query method in `RaDecImpl.scala`

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecImpl.scala) { #getRaDecValues-impl }

Update `SampleWiring.scala`

Add db setup in `SampleWiring.scala`

Scala
: @@snip [SampleWiring.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/SampleWiring.scala) { #db-wiring-setup }

Add repository reference and Update implementation reference

Scala
: @@snip [SampleWiring.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/SampleWiring.scala) { #raDecImpl-db-ref }

Run backend application, test your application using `apptest.http` and verify data is saved in your postgres table.

```sbt
sbt:backend> run start
```
