# Adding Database Persistence

In this section of the tutorial, we will add a database to our application to store the RA/Dec coordinates entered
in the UI.  We will be using `postgres` to persist our data. We will be using the Jooq DSL to write our queries, 
which is packaged in the CSW Database Service.

First, we will update our backend server to use the database, then we will set up the database itself, and then
we will run the application.

## Update backend Implementation

First, we have to make the CSW Database Service library accessible in our project.  
Add a `csw-database` dependency in `project/Libs.scala`

Scala
: @@snip [Libs.scala](../../../../backend/project/Libs.scala) { #add-db }

Use the `csw-database` dependency in your `build.sbt` file and reload the project in your IDE (in IntelliJ, this can be
done from the sbt tab, typically on the right side of the IDE).

Scala
: @@snip [build.sbt](../../../../backend/build.sbt) { #add-db }

### Create a Database access class
Now we can implement our database access code. Go to the `impl` package and add a repository class `RaDecRepository.scala` 
using the DSL context provided by JOOQ as part of the CSW Database Service.  This will be constructed and injected later
in our wiring.

Scala
: @@snip [RaDecRepository.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecRepository.scala) { #add-repository }

Add a method to insert data into the DB.  The `query` method of the `DSLContext` is used to construct a SQL `INSERT`
statement to insert our formatted RA/Dec strings along with a random UUID. The CSW Database Service provides an 
asynchronous execute method, which returns a negative value on error.  Since the insert is done asynchronously, 
this method returns a `Future`.

Scala
: @@snip [RaDecRepository.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecRepository.scala) { #insert-raDecValue-in-db }

We will similarly add a method to get data from the DB:

Scala
: @@snip [RaDecRepository.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecRepository.scala) { #get-raDecValues-from-db }

Add the necessary imports.  It should look something like this:

Scala
: @@snip [RaDecRepository.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecRepository.scala) { #repo-imports }


### Update backend service implementation

Update `RaDecImpl.scala` to inject the repository dependency. We will also an implicit execution context that can 
be passed into this class since our `RaDecRepository` class requires one.

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecImpl.scala) { #add-repository  }

Update the `raDecToString` implementation in `RaDecImpl.scala` to use the insert query method instead the locally stored
list.

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecImpl.scala) { #raDecToString-impl }

Update the `getRaDecValues` implementation in `RaDecImpl.scala` to use the get values query method.

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecImpl.scala) { #getRaDecValues-impl }

References to the locally stored list can now be deleted.

### Update wiring

Now we need to put everything together by update the wiring.

First, create the DB setup in `SampleWiring.scala`

Scala
: @@snip [SampleWiring.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/SampleWiring.scala) { #db-wiring-setup }

Here, you can see the database name, username, and password are obtained from the application configuration.  These
value are used to create the JOOQ `DSLContext` passed into our repository class.  Let's instantiate the repository,
passing in our DSL context, and then update the implementation reference to receive the repository.

Scala
: @@snip [SampleWiring.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/SampleWiring.scala) { #raDecImpl-db-ref }

We now need to update our application configuration file with the database configuration.

Scala
: @@snip [application.conf](../../../../backend/src/main/resources/application.conf) { #db-conf }

As you can see, the database username and password are obtained from environment variables.  We will set these 
variables later before we run our backend.

## Run the new application

A PostgreSQL database is used in our application.  It must first be installed and then initially configured to 
work with our applciation.

### Database setup

Follow the installation guide to download & install postgres on your machine, if not already installed.
[Link](https://www.postgresql.org/download/)

At this point, we will re-run `csw-services` with the CSW Database Service enabled.  This will run an PostgreSQL
instance that we can then configure.

The following instructions show how to run the Database Service along with the Location Service and the 
Authentication and Authorization Service:

```bash
cs install csw-services:v4.0.0-M1
csw-services start -k -d
```

Login to postgres with your default user and create a new user to be used with our application.  The example code 
below sets up a user "postgres" with a password "postgres", but you can use different credentials if you prefer.

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

As mentioned above, we depend on environment variables to pick up your username and password for the database, 
thus DB_USERNAME and DB_PASSWORD need to be set to the credentials you provided above.

To set environment variables, use the command

```bash
export DB_USERNAME=<VALUE> DB_PASSWORD=<VALUE>
```

Now, we are ready to run the backend application:

```bash
sbt:backend> run start
```

Test your application either with the UI or by using `apptest.http` as described in previous tutorials,
and verify the data is saved in your postgres table.