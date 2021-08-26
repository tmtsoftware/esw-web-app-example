# Adding Database Persistence

In this section of the tutorial, we will add a database to our backend application to store the RA/Dec coordinates entered
through the UI instead of the state variable in the backend. With this change our coordinates will be stored between
observing runs, and we won't lose all our precious coordinates. 

This flow also shows how to use the CSW Database Service
in a backend and how to pass database query results back to the UI. We will be using the CSW Database Service's Jooq DSL to 
write our queries.  Under the covers CSW Database Service is using the `PostgreSQL` relational database to persist our data.

First, we will update our backend server to use the database, then we will set up the database itself, and then
we will run the application.

## Update the Backend Implementation

First, we have to make the CSW Database Service accessible in our project.  
Add a `csw-database` dependency in `project/Libs.scala`

Scala
: @@snip [Libs.scala](../../../../backend/project/Libs.scala) { #add-db }

Use the `csw-database` dependency in your `build.sbt` file and reload the project in your IDE (in IntelliJ, this can be
done from the sbt tab, typically on the right side of the IDE).  Or type `reload` at the sbt prompt.

Scala
: @@snip [build.sbt](../../../../backend/build.sbt) { #add-db }

### Create a Database access class

Now we can implement our database access code. Go to the `impl` package in backend and add a repository class `RaDecRepository.scala` 
using the DSL context provided by JOOQ as part of the CSW Database Service.  This will be constructed and injected later
in our wiring.

Scala
: @@snip [RaDecRepository.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecRepository.scala) { #add-repository }

Add a method to this class to insert data into the DB.  The `query` method of the `DSLContext` is used to construct an SQL `INSERT`
statement to insert our formatted RA/Dec strings along with a UUID. The CSW Database Service provides an 
asynchronous execute method, which returns a negative value on error.  Since the insert is done asynchronously, 
this method returns a `Future`.

Scala
: @@snip [RaDecRepository.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecRepository.scala) { #insert-raDecValue-in-db }

We will similarly add a method to get data from the DB:

Scala
: @@snip [RaDecRepository.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecRepository.scala) { #get-raDecValues-from-db }

Add the necessary imports.  The imports should look something like this:

Scala
: @@snip [RaDecRepository.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecRepository.scala) { #repo-imports }


### Update backend service implementation

Update `RaDecImpl.scala` to inject the repository dependency. We will also need an implicit execution context 
curried into this class since our `RaDecRepository` class requires one.

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecImpl.scala) { #add-repository  }

Update the `raDecToString` implementation in `RaDecImpl.scala` to use the insert query method instead the locally stored
list.

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecImpl.scala) { #raDecToString-impl }

Update the `getRaDecValues` implementation in `RaDecImpl.scala` to use the get values query method.

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/RaDecImpl.scala) { #getRaDecValues-impl }

References to the locally stored coordinate list can now be deleted since it is no longer needed.

### Update wiring

Now we need to put everything together by updating the application wiring.

First, create the DB setup in `SampleWiring.scala`

Scala
: @@snip [SampleWiring.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/SampleWiring.scala) { #db-wiring-setup }

Here you can see the database name, username, and password are obtained from the application configuration.  These
values are used to create the JOOQ `DSLContext` passed into our repository class.  Let's instantiate the repository,
passing in our DSL context, and then update the implementation reference to receive the repository.

Scala
: @@snip [SampleWiring.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/db/SampleWiring.scala) { #raDecImpl-db-ref }

@@@ note
It may not be a great security practice to keep database login info in a config file checked into GitHub, but we are not working on bank software here.
Some applications may need a different approach but for most applications this is probably good enough. See also the CSW documentation.
@@@ 

We now need to update our application configuration file with the database configuration.  Edit `application.conf` in
the `src/main/resources` folder.

Scala
: @@snip [application.conf](../../../../backend/src/main/resources/application.conf) { #db-conf }

The database username and password are obtained from the environment variables `DB_USERNAME` and `DB_PASSWORD` respectively.
We will set these variables later before we run our backend.

## Run the New Application

CSW Database Service uses the PostgreSQL database, which must be installed for the application to work properly.  Once it is installed, the 
application database must be initially configured with to work with our application.

### Database setup

Follow the installation guide to download and install PostgreSQL on your machine, if not already installed
[Link](https://www.postgresql.org/download/). See also the CSW documentation. For linux specific troubleshooting [refer](https://github.com/tmtsoftware/csw/blob/master/docs/src/main/services/database.md#database-service)

At this point, we will re-run `csw-services` with the CSW Database Service enabled.  This will run a PostgreSQL
instance that we can then configure.

The following instructions show how to run the Database Service along with the Location Service and the 
Authentication and Authorization Service using `csw-services`:

@@@note
For running the Database Service using `csw-services`, the PGDATA environment variable must be set to the 
Postgres data directory where Postgres is installed e.g. for mac: “/usr/local/var/postgres”.
@@@

```bash
cs install csw-services:v4.0.0-M1
csw-services start -k -d
```

Login to postgres with your default user and create a new user to be used with our application.  The example code 
below sets up a user "postgres" with a password "postgres", but you can use different credentials if you prefer.

```bash
psql -d postgres
postgres => CREATE USER postgres with password 'postgres';
postgres => \q
psql -d postgres -U postgres
```

Now for the application initialization. We will create an `RADECVALUES` table in the database that the application can use to perform fetch and insert queries.
The following commands can be used to create a table:

```bash
postgres =>
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
sbt:sample-backend> run start
```

Test your application either with the UI or by using `apptest.http` as described in previous tutorials,
and verify the data is saved in your postgres table.  This can be done in `psql` using the `TABLE` command:

```bash
postgres => TABLE RADECVALUES
```