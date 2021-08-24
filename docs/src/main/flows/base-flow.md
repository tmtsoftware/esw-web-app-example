# Creating a Web Application
This flow demonstrates how to use the template to create our project, how to add simple routes, UI components, 
and how to build and test it. 

In this tutorial, we will first generate the application from the template, and build it to ensure tools are in place. 
Then, we will delete the default implementation and replace it our own implementation of a coordinate formatter.  To 
do this, we will delete much of the template code and rewrite our own classes custom to our implementation.

## Generate application

First we need to generate a scaffolding application using our giter8 template:

```bash
g8 tmtsoftware/esw-web-app-template.g8 --name=sample
```

It will generate a sample folder with two sub-folders, `frontend` and `backend`.  For a sanity check, let's go ahead and 
build the front and back ends created by the template.  This will also help ensure you have the necessary tools installed.

You are welcome to try out the generated sample project, which is basically a "Hello World" application, by following
the  instructions in the READMEs in each sub-folder. 

### Compile the frontend

The `frontend` sub-folder is where your frontend application is located.  It uses Typescript, React and node.
Make sure node is installed in your machine. Let's compile our generated application.

```bash
cd sample/frontend
npm install
npm run build
```

### Compile the backend

The `backend` sub-folder is where your backend application is located.  It uses the Scala ecosystem, which uses [sbt](https://www.scala-sbt.org/) as its build tool.
Make sure [coursier](https://tmtsoftware.github.io/csw/apps/csinstallation.html), OpenJDK 11 and the latest version of sbt version are installed in your machine.
Let's compile our generated application.

```bash
cd sample/backend
sbt
sbt:backend> compile
```

## Open in development environment

At this point, you may want to open the project in an Integrated Development Environment, such as Intellij, 
if you are using one. 

In Intellij, you do this by clicking on File->New Project from Existing Sources... 
and then browsing to the root directory, `sample`.  It should have a build.sbt file in it.
Create an sbt project, and then accept the defaults, making sure the JDK is set to your installation of 
OpenJDK 11.

## Develop Backend
We will start with the backend HTTP service first.  We will start by deleting the existing application and then
creating our custom code.  Our backend will be written in Scala, so all Java code will be removed.

### Cleanup existing sample

To remove the generated code from the sample application, go to the `backend` folder and:

* Delete the folders `src/main/java` ,`src/test`

Browse to `src/main/scala/org/tmt/sample` and 

* Delete the existing files from `core/models`
* Delete the existing files from `service`
* Delete the file `SampleImpl.scala` from `impl`
* Delete the file `JSampleImplWrapper.scala` from `http`

### Add our Models classes

We will now create the models used by our application.  The first model is our request, in which we will take 
a RA/Dec pair as decimals.  The other model is the response, which returns the pair as formatted Strings.  It also
includes a UUID String for the request.
These model classes will be used to serialize and deserialize requests and responses.

* Go to `core/models` in `src`
* Add `RaDecRequest.scala` model class

Scala
: @@snip [RaDecRequest.scala](../../../../backend/src/main/scala/org/tmt/sample/core/models/RaDecRequest.scala) { #request-model }

Add the `RaDecResponse.scala` model class

Scala
: @@snip [RaDecResponse.scala](../../../../backend/src/main/scala/org/tmt/sample/core/models/RaDecResponse.scala) { #response-model }

### Add our implementation

Now we will create our backend logic.  First, we create the service API as a Scala trait.
Our service will have two methods.  The first method will take the RA/Dec request, format it, and then return the response.
Our server will also maintain a list of all requests, so the method will also store this pair as formatted strings.

The second method returns this stored list of all processed RA/Dec pairs.

* Go to `service` in `src`
* Create a `RaDecService.scala` file and add our `raDecToString` contract to our API, `RaDecService`, using our request and response models  
* Add the `getRaDecValues` contract in the service `RaDecService.scala`

Scala
: @@snip [RaDecService.scala](../../../../backend/src/main/scala/org/tmt/sample/service/RaDecService.scala) { #raDecService-contract }

Now we will create the implementation class of this service.  We use the `Angle` class from CSW to create the 
sexagesimal strings.

* Go to the `impl` package in `src`
* Add `RaDecImpl.scala`
* Extend `RaDecService.scala` to implement `raDecToString`
* Implement `getRaDecValues` contract in `RaDecImpl.scala`

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/RaDecImpl.scala) { #raDec-impl }

Now, lets try to compile our code

```bash
sbt:backend> compile
```
You will notice it will give some compilation errors here.  To fix these:

* Delete references to the classes we deleted earlier from `src/main/org/tmt/sample/impl/SampleWiring.scala`
* Add a placeholder for route

```scala
override lazy val routes: Route = ???
```

* Go to `src/main/org/tmt/sample/http/SampleRoute.scala`
* Delete references to the previously deleted classes from the Scala files
* Delete existing route
* Add a placeholder for route

```scala
val route: Route = ???
```

Delete references to the previously deleted classes from the Scala files in `src/main/org/tmt/sample/http/HttpCodecs`

Be sure to delete references to deleted classes from import statements as well.

Try compiling code again, this time it should compile successfully.

```bash
sbt:backend> compile
```

### Add a Route for our implementation
Next, we will provide the routing that links our HTTP request methods on endpoints to backend server processing 
created in our implementation class `RaDecImpl`.  

First, we need to inject a reference to an implementation of our service API to the routes, so that the appropriate 
method will be called for the matching endpoint and request method.  Change the constructor of the route to take such
an instance, and add an import for `RaDecService`:

Scala
: @@snip [SampleRoute.scala](../../../../backend/src/main/scala/org/tmt/sample/http/SampleRoute.scala) { #raDecImpl-ref }

Next, we will add our routes.  We will have a single endpoint `raDecValues` that supports two methods: POST and GET.
The POST method will cause our backend service method `raDecToString` to be called, and GET will call `getRaDecValues`.
We are using the Akka routing DSL to compose our HTTP routes. Visit [here](https://doc.akka.io/docs/akka-http/current/routing-dsl/overview.html) to learn more about the routing DSL.

Be sure to add an import for our model `RaDecRequest`.

Scala
: @@snip [SampleRoute.scala](../../../../backend/src/main/scala/org/tmt/sample/http/SampleRoute.scala) { #add-routes }

@@@note
The tilda (~) at the end, is used as a path concatenator in the Akka DSL.
You can safely remove it for now. However, in the following section of this tutorial we are going to add new routes to this file. At that point, you would want to add it again to concatenate multiple routes.
@@@

Next, let's connect our routes to our service implementation in the wiring.

* Go to `SampleWiring.scala`
* Add a `raDecImpl` reference

Scala
: @@snip [SampleWiring.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/SampleWiring.scala) { #raDecImpl-ref }

Replace the route placeholder with our route, injecting our service API implementation, and add an import for `SampleRoute`.

Scala
: @@snip [SampleWiring.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/SampleWiring.scala) { #add-route }

After we add the route, it will show some compilation errors, to fix that we need to add the codec to serialize/deserialize our request/response

### Add Codecs

We are using [borer](https://sirthias.github.io/borer/) to serialize/deserialize. It has support for two formats: Json and Cbor(binary format)

Add the codecs in `HttpCodecs.scala`, along with imports for our models.

Scala
: @@snip [HttpCodecs.scala](../../../../backend/src/main/scala/org/tmt/sample/http/HttpCodecs.scala) { #add-codec }

`SampleRoute` should now compile successfully and is ready to use.

### Manually test our application

Start the Location Service with the Authorization and Authentication Service (we will use auth in the next section of the tutorial)

```bash
cs install csw-services:v4.0.0-M1
csw-services start -k 
```

Set `INTERFACE_NAME` and `AAS_INTERFACE_NAME` environment variables with Network interface of your machine, these are needed during startup of the application, so that it is able to connect to location service and register its ip address.

* During development in your local machine, these can point to same Network interface
* During production deployment, these should point to respective Network interface

For more details, refer CSW [environment variables](https://tmtsoftware.github.io/csw/deployment/env-vars.html) and [network topology](https://tmtsoftware.github.io/csw/deployment/network-topology.html)

Try running our backend application

```bash
sbt:backend> run start
```

If application is successfully started it, will show a log with the `server_ip` and `app_port` registered to the Location Service.

Update `apptest.http` with the code below, replacing the `<server_ip>:<app_port>` with the ones for your server as 
displayed in the log messages.  Then run the request to test your `raDecValues` POST route:

```bash
#### Request to test raDecValues endpoint
POST http://<server_ip>:<app_port>/raDecValues
Content-Type: application/json

{
  "raInDecimals": 2.13,
  "decInDecimals": 2.18
}

```

Successful response contains the `formattedRa` and `formattedDec` values with a unique id.

```bash
{
  "id": "80ab3f42-a4cf-4249-b9a0-2b209aab48e8",
  "formattedRa": "8h 8m 9.602487087684134s",
  "formattedDec": "124°54'17.277618670114634\""
}
```

Add this to your `apptest.http`, again replacing the server IP and port number,  and test your `raDecValues` GET route

```http
####
GET http://<server_ip>:<app_port>/raDecValues
```

A successful response contains a list with the previous formatted RA/DEC entry.

```bash
[
  {
    "id": "d6a16719-72bf-4928-8bf3-abb125186f49",
    "formattedRa": "8h 8m 9.602487087684134s",
    "formattedDec": "124°54'17.277618670114634\""
  }
]
```

Change the numbers in the POST test and run it again.  Then, run the GET test again, and you will see both entries 
displayed in the list.

## Create the frontend

In this section, we will be constructing a browser-based UI using React components in Typescript.  We will create
components that allow the user to specify an RA/Dec pair, and then a Submit button that will send the data to our 
backend.  The response will then be rendered in UI components. We will also provide components to get and display the
list of stored coordinates.  This section of the tutorial will show how to add and render custom components within the 
application that act as clients to consume our backend routes.

First, let's cleanup unwanted code:

* Go to the `components/pages` folder in `src` and delete all component files under this directory
* Delete the folder `components/form`
* Go to the `utils` folder and remove the contents of the `api.ts` file
* Go to the `pages` folder in `test` and delete all test files under this directory

### Add models

Now, we need to add Typescript models for the data we will send to and receive from our backend.

* Go to `Models.ts` in `src/models`
* Delete existing model interfaces
* Add our request and response models

Typescript
: @@snip [Models.ts](../../../../frontend/src/models/Models.ts) { #add-models }

### Add Fetch

Implement the the following methods to consume data from our POST and GET routes in the `api.ts` file.  These
methods will be called by our React components and perform HTTP requests to the backend.  The `post` and `get` methods
are defined in `Http.ts` and handle the formatting of the request method and arguments into a proper HTTP request, as
well as formatting the response.

For POST Route

Typescript
: @@snip [api.ts](../../../../frontend/src/utils/api.ts) { #fetch-data }

For GET Route

Typescript
: @@snip [api.ts](../../../../frontend/src/utils/api.ts) { #fetch-saved-ra-dec-values }

You will need the following imports: 

Typscript

: @@snip [api.ts](../../../../frontend/src/utils/api.ts) { #fetch-imports }


### Create a React component

The first component we will create is the form for entering a coordinate and submitting it to the backend server.
We are using the [Ant Design](https://ant.design/) UI Component library for our components, so here we use an Ant.d Form.

* In the `pages` folder, create `RaDecInput.tsx`
* Add a simple input form to the `RaDecInput` React component

Typescript
: @@snip [RaDecInput.tsx](../../../../frontend/src/components/pages/RaDecInput.tsx) { #add-component }

It will be necessary to add the appropriate imports.

Note the first line after the constructor stores a reference to the Location Service in the component.

Typescript
: @@snip [RaDecInput.tsx](../../../../frontend/src/components/pages/RaDecInput.tsx) { #use-location-service-from-context }

This instance is required to get the URL of the backend service. It is part of a React [context](https://reactjs.org/docs/context.html) 
named `LocationServiceProvider`, defined in the `contexts` folder.  This is made available to our component 
when we put the components together in `App.tsx`.  

Finally, let's add an `onFinish` handler to specify the behavior when the submit button is pressed.  It will
and use the `postRaDecValues` method in our component.  Insert this code above the `return` statement defined above.

Typescript
: @@snip [RaDecInput.tsx](../../../../frontend/src/components/pages/RaDecInput.tsx) { #use-fetch }

Be sure to add the necessary imports.  It should look something like this:

Typescript
: @@snip [RaDecInput.tsx](../../../../frontend/src/components/pages/RaDecInput.tsx) { #input-imports }


### Create table component

In the `pages` folder, add a new component `RaDecTable.tsx` to display the RA/Dec values table.  Again, we are using
an Ant.d component, `Table`:

Typescript
: @@snip [RaDecTable.tsx](../../../../frontend/src/components/pages/RaDecTable.tsx) { #add-component }

The table will display a list of `RaDecResponse`s obtained from the backend server, described below.  
In this table, the key for rows will come from the `id` field of the response model.  

The columns will be defined in a constant outside of this class.  Insert the following code above the `RaDecTable` declaration:

Typescript
: @@snip [RaDecTable.tsx](../../../../frontend/src/components/pages/RaDecTable.tsx) { #add-columns }

The data for the table will come from an `raDecValues` constant that gets populated using a 
React [Hook](https://reactjs.org/docs/hooks-effect.html).  The `useEffect` hook is called whenever the component is 
rendered.  In our hook, we call the method `getRaDecValues` which does the GET request to the backend, whose URL is obtained
using the Location Service, obtained from the context.

Typescript
: @@snip [RaDecTable.tsx](../../../../frontend/src/components/pages/RaDecTable.tsx) { #use-fetch }

Imports for this file will look something like this:

Typescript
: @@snip [RaDecTable.tsx](../../../../frontend/src/components/pages/RaDecTable.tsx) { #table-imports }


### Putting out components together

In the `pages` folder, add a new component `RaDec.tsx` to compose the components created above and display in a page

Typescript
: @@snip [RaDec.tsx](../../../../frontend/src/components/pages/RaDec.tsx) { #add-component }

Next, we need to show our newly created `RaDec` component.

Update the `Routes.tsx` file and delete references to deleted files and their routes, and map our newly created `RaDec` component to the `/` path.

Typescript
: @@snip [Routes.tsx](../../../../frontend/src/routes/Routes.tsx) { #add-route }

Now, we need a link to let the user navigate to the RA/Dec form from different parts of the application.

Update `MenuBar.tsx` and delete the existing `Menu` and its `Menu.Item`. Add our menu item.

Typescript
: @@snip [MenuBar.tsx](../../../../frontend/src/components/menu/MenuBar.tsx) { #add-route-action }

Now, we have linked all pieces of our frontend application.

```bash
$:frontend> npm start
```

It will launch application in your default browser with an input form.

* Add a value like '2.13' and '2.18' and click Submit.
* You will see the formatted RA and Dec values in the table below the input form.

To build the application for its production deployment

```bash
$:frontend> npm run build
```
