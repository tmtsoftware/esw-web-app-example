# Creating a Web Application
The flow demonstrates how to use the template to create our project, how to add simple routes, UI components, and how to build and test it. 

## Generate application

First we need to generate a scaffolding application using our gitter8 template:

```bash
g8 tmtsoftware/esw-web-app-template.g8 --name=sample
```

It will generate a sample folder with two sub-folders, `frontend` and `backend`

## Compile the frontend

The `frontend` sub-folder is where your frontend application is located.  It uses Typescript, React and node.
Make sure node is installed in your machine. Let's compile our generated application.

```bash
cd sample/frontend
npm install
npm run build
```

## Compile the backend

The `backend` sub-folde is where your backend application is located.  It uses the Scala ecosystem.
Make sure [coursier](https://tmtsoftware.github.io/csw/apps/csinstallation.html), OpenJDK 11 and the latest version of sbt version are installed in your machine.
Let's compile our generated application.

```bash
cd sample/backend
sbt
sbt:backend> compile
```

## Add routes to the backend

Open the `backend` folder in you editor (e.g. Intellij)
e.g.

```bash
cd sample/backend
idea .
```

### Cleanup existing sample

Generated code contains a sample application. We will delete its code and add ours where appropriate.

* Delete the folders `src/main/java` ,`src/test`
* Delete the existing files from `core/models` package in `src`
* Delete the existing files from `service` package in `src`
* Delete the file `SampleImpl.scala` from `impl` package in `src`
* Delete the file `JSampleImplWrapper.scala` from `http` package in `src`

### Add our Models classes

These model classes will be used to serialize and deserialize requests and responses.

* Go to `core/models` in `src`
* Add `RaDecRequest.scala` model class

Scala
: @@snip [RaDecRequest.scala](../../../../backend/src/main/scala/org/tmt/sample/core/models/RaDecRequest.scala) { #request-model }

Add the `RaDecResponse.scala` model class

Scala
: @@snip [RaDecResponse.scala](../../../../backend/src/main/scala/org/tmt/sample/core/models/RaDecResponse.scala) { #response-model }

### Add our implementation

* Go to `service` in `src`
* Add a scala trait in the `RaDecService.scala` file and add our `raDecToString` contract, using our request and response models  

Scala
: @@snip [RaDecService.scala](../../../../backend/src/main/scala/org/tmt/sample/service/RaDecService.scala) { #raDecToString-contract }

Add the `getRaDecValues` contract in the service `RaDecService.scala`

Scala
: @@snip [RaDecService.scala](../../../../backend/src/main/scala/org/tmt/sample/service/RaDecService.scala) { #getRaDecValues-contract }

* Go to the `impl` package in `src`
* Add `RaDecImpl.scala`
* Extend `RaDecService.scala` to implement `raDecToString`

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/RaDecImpl.scala) { #raDecToString-impl }

Implement `getRaDecValues` contract in `RaDecImpl.scala`

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/RaDecImpl.scala) { #getRaDecValues-impl }

Now, lets try to compile our code

```bash
sbt:backend> compile
```
You will notice it will give some compilation errors here.  To fix these:

* Delete references to the classes we deleted earlier from `SampleWiring.scala`
* Add a placeholder for route

```scala
override lazy val routes: Route = ???
```

* Go to `SampleRoute.scala`
* Delete existing route
* Add a placeholder for route

```scala
val route: Route = ???
```

Delete references to the previously deleted classes from the Scala files `SampleRoute`, `HttpCodecs`

Try compiling code again, this time it should compile successfully.

```bash
sbt:backend> compile
```

### Add a Route for our implementation

* Go to `SampleWiring.scala`
* Add a `raDecImpl` reference

Scala
: @@snip [SampleWiring.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/SampleWiring.scala) { #raDecImpl-ref }

Replace the route placeholder with our route

Scala
: @@snip [SampleWiring.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/SampleWiring.scala) { #add-route }

* Go to `SampleRoute.scala`
* Add dependency of `raDecService` to `SampleRoute`

Scala
: @@snip [SampleRoute.scala](../../../../backend/src/main/scala/org/tmt/sample/http/SampleRoute.scala) { #raDecImpl-ref }

Add route along with an implicit execution context which is provided by ServerWiring.

We are using the Akka routing DSL to compose our HTTP routes. Visit [here](https://doc.akka.io/docs/akka-http/current/routing-dsl/overview.html) to learn more about the routing DSL.

Scala
: @@snip [SampleRoute.scala](../../../../backend/src/main/scala/org/tmt/sample/http/SampleRoute.scala) { #add-routes }

@@@note
The tilda (~) at the end, is used as a path concatenator in the Akka DSL.
You can safely remove it for now. However, in the following section of this tutorial we are going to add new routes to this file. At that point, you would want to add it again to concatenate multiple routes.
@@@

After we add the route, it will show some compilation errors, to fix that we need to add the codec to serialize/deserialize our request/response

### Add Codecs

We are using [borer](https://sirthias.github.io/borer/) to serialize/deserialize. It has support for two formats: Json and Cbor(binary format)

Add the codecs in `HttpCodecs.scala`

Scala
: @@snip [HttpCodecs.scala](../../../../backend/src/main/scala/org/tmt/sample/http/HttpCodecs.scala) { #add-codec }

`SampleRoute` should now compile successfully and is ready to use.

### Manually test our application

Start the Location Service with the Authorization and Authentication Service (we will use auth in the next section of the tutorial)

```bash
cs install csw-services:v4.0.0-M1
csw-services start -k 
```

Try running our backend application

```bash
sbt:backend> run start
```

If application is successfully started it, will show a log with the `server_ip` and `app_port` registered to the Location Service.

Update `apptest.http` and test your `raDecValues` POST route

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

Add this to your `apptest.http` and test your `raDecValues` GET route

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

## Consume routes in the frontend

In this section, we will be consuming data with using our React components.
We will show how to create a client side route to add/render custom components within the application.

First lets cleanup unwanted code

* Go to the `components/pages` folder in `src` and delete all component files under this directory
* Delete the folder `components/form`
* Go to the `pages` folder in `test` and delete all test files under this directory
* Go to the `utils` folder and remove the contents of the `api.ts` file

### Add models

* Go to `Models.ts`
* Delete existing model interfaces
* Add our request and response models

Typescript
: @@snip [Models.ts](../../../../frontend/src/models/Models.ts) { #add-models }

### Add Fetch

Implement the the following methods to consume data from our POST and GET routes in the `api.ts` file.

For POST Route

Typescript
: @@snip [api.ts](../../../../frontend/src/utils/api.ts) { #fetch-data }

For GET Route

Typescript
: @@snip [api.ts](../../../../frontend/src/utils/api.ts) { #fetch-saved-ra-dec-values }

### Add our React component

* In the `pages` folder, create `RaDecInput.tsx`
* Add a simple input form to the `RaDecInput` React component

Typescript
: @@snip [RaDecInput.tsx](../../../../frontend/src/components/pages/RaDecInput.tsx) { #add-component }

A Location Service instance is required to get the URL of the backend service. This instance is available via a context named `LocationServiceProvider`.
Add the following as the first line inside the `RaDecInput` component.

Typescript
: @@snip [RaDecInput.tsx](../../../../frontend/src/components/pages/RaDecInput.tsx) { #use-location-service-from-context }

Add an `onFinish` handler and use the `postRaDecValues` method in our component

Typescript
: @@snip [RaDecInput.tsx](../../../../frontend/src/components/pages/RaDecInput.tsx) { #use-fetch }

* In the `pages` folder, add a new component `RaDecTable.tsx` to display the RA values table.

Typescript
: @@snip [RaDecTable.tsx](../../../../frontend/src/components/pages/RaDecTable.tsx) { #add-component }

Add columns for the table in this component

Typescript
: @@snip [RaDecTable.tsx](../../../../frontend/src/components/pages/RaDecTable.tsx) { #add-columns }

Use our `getRaDecValues` method in this component

Typescript
: @@snip [RaDecTable.tsx](../../../../frontend/src/components/pages/RaDecTable.tsx) { #use-fetch }

* In the `pages` folder, add a new component `RaDec.tsx` to compose the components created above and display in a page

Typescript
: @@snip [RaDec.tsx](../../../../frontend/src/components/pages/RaDec.tsx) { #add-component }

Next, we need to show our newly created `RaDec` component.

Update the `Routes.tsx` file and delete references to deleted files and their routes, and map our newly created `RaDec` component to the `/` path.

Typescript
: @@snip [Routes.tsx](../../../../frontend/src/routes/Routes.tsx) { #add-route }

Now, we need a link to let the user navigate to the `Ra` form from different parts of the application.

Update `MenuBar.tsx` and delete the existing `Menu` and its `Menu.Item`. Add our menu item.

Typescript
: @@snip [MenuBar.tsx](../../../../frontend/src/components/menu/MenuBar.tsx) { #add-route-action }

Now, we have linked all pieces of our frontend application.

```bash
$:frontend> npm start
```

It will launch application in your default browser with an input form.

* Add a value like '2.13' and '2.18' and click Submit.
* Refresh the page
* You will see the formatted RA and Dec values in the table below the input form.

To build the application for its production deployment

```bash
$:frontend> npm run build
```
