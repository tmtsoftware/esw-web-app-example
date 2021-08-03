# Creating a Web Application

## Overview

This template contains steps to create an example application using the template.
It has below flows, Basic Flow is mandatory and others are extensions after doing basic flow.

* Basic flow - Creating a Web Application
* Adding Authentication
* Adding Persistence
* Adding Documentation

Basic flow will show you how to add a routes to backend application and consume them in your frontend.
Next two sections are extensions, if you need to add authentication in your application or
if you need to save your data to a database.

At any point in time if you want to see code with complete file, you can refer final [example app](https://github.com/tmtsoftware/esw-web-app-example) and compare your changes.

---

Basic flow starts here.

## Generate application

First we need to generate a scaffolding application using our gitter8 template

```bash
g8 tmtsoftware/esw-web-app-template.g8 --name=sample
```

It will generate a sample folder with two sub-folders, `frontend` and `backend`

## Compile frontend

This is where your frontend application is present, it uses Typescript, React and node.
Make sure node is installed in your machine. Let's compile our generated application.

```bash
cd sample/frontend
npm install
npm run build
```

## Compile backend

This is where your backend application is present, it uses Scala ecosystem.
Make sure [coursier](https://tmtsoftware.github.io/csw/apps/csinstallation.html), openjdk 11 and latest sbt version is installed in your machine.
Let's compile our generated application.

```bash
cd sample/backend
sbt
sbt:backend> compile
```

## Add routes to backend

Open backend folder in you editor(e.g. Intellij)
e.g.

```bash
cd sample/backend
idea .
```

### Cleanup existing sample

Generated code contains a sample application, we will delete its code and add ours where appropriate.

* Delete folder `src/main/java` ,`src/test`
* Delete Existing files from `core/models` package in `src`
* Delete existing files from `service` package in `src`
* Delete existing files from `impl` package in `src`
* Delete file `JSampleImplWrapper.scala` from `http` package in `src`

### Add our Models classes

These model classes will be used to serialized and deserialized request and response.

* Go to `core/models` in `src`
* Add `RaDecRequest.scala` model class

Scala
: @@snip [RaDecRequest.scala](../../../../backend/src/main/scala/org/tmt/sample/core/models/RaDecRequest.scala) { #request-model }

Add `RaDecResponse.scala` model class

Scala
: @@snip [RaDecResponse.scala](../../../../backend/src/main/scala/org/tmt/sample/core/models/RaDecResponse.scala) { #response-model }

### Add our implementation

* Go to `service` in `src`
* Add a scala trait in `RaDecService.scala` file and add our `raDecToString` contract, using our request and response model  

Scala
: @@snip [RaDecService.scala](../../../../backend/src/main/scala/org/tmt/sample/service/RaDecService.scala) { #raDecToString-contract }

Add `getRaDecValues` contract in service `RaDecService.scala`

Scala
: @@snip [RaDecService.scala](../../../../backend/src/main/scala/org/tmt/sample/service/RaDecService.scala) { #getRaDecValues-contract }

* Go to `impl` package in `src`
* Add `RaDecImpl.scala`
* Extend `RaDecService.scala` to implement `raDecToString`

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/RaDecImpl.scala) { #raDecToString-impl }

Implement `getRaDecValues` contract in `RaDecImpl.scala`

Scala
: @@snip [RaDecImpl.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/RaDecImpl.scala) { #getRaDecValues-impl }

Lets now try to compile our code

```bash
sbt:backend> compile
```

* It will give compilation errors hence, delete references to earlier deleted classes from `SampleWiring.scala`
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

Delete references to earlier deleted classes from scala files `SampleRoute`, `HttpCodecs`

Try compiling code again, this time it should compile

```bash
sbt:backend> compile
```

### Add Route for our implementation

* Go to `SampleWiring.scala`
* Add `raDecImpl` reference

Scala
: @@snip [SampleWiring.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/SampleWiring.scala) { #raDecImpl-ref }

Add Route in placeholder

Scala
: @@snip [SampleWiring.scala](../../../../backend/src/main/scala/org/tmt/sample/impl/SampleWiring.scala) { #add-route }

* Go to `SampleRoute.scala`
* Add dependency of `raDecService` to `SampleRoute`

Scala
: @@snip [SampleRoute.scala](../../../../backend/src/main/scala/org/tmt/sample/http/SampleRoute.scala) { #raDecImpl-ref }

Add route along with an implicit execution context which is provided by ServerWiring.

We are using akka routing dsl to compose our http routes. visit [here](https://doc.akka.io/docs/akka-http/current/routing-dsl/overview.html) to learn more about routing dsl.

Scala
: @@snip [SampleRoute.scala](../../../../backend/src/main/scala/org/tmt/sample/http/SampleRoute.scala) { #add-routes }

@@@note
The tilda (~) at the end, is used as a path concatenator in akka dsl.
You can safely remove it for now. However, in the following section of this tutorial we are going to add new routes to this file, at that point you would want to add it again to concat multiple routes.
@@@

After we add the route, it will show some compilation errors, to fix that we need to add the codec to serialize/deserialize our request/response

### Add Codecs

We are using [borer](https://sirthias.github.io/borer/) to serialize/deserialize. It has support for two formats Json and Cbor(binary format)

Add codecs in `HttpCodecs.scala`

Scala
: @@snip [HttpCodecs.scala](../../../../backend/src/main/scala/org/tmt/sample/http/HttpCodecs.scala) { #add-codec }

SampleRoute should compile now successfully and ready to use.

### Manually test our application

Start location service with authentication service (we will use auth in next section of tutorial)

```bash
cs install csw-services:v4.0.0-M1
csw-services start -k 
```

Try running our backend application

```bash
sbt:backend> run start
```

If application is successfully started it, will show log with `server_ip` and `app_port` registered to location service.

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

Successful response contains the formattedRa and formattedDec value with a unique id.

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

Successful response contains list of with formattedRa value.

```bash
[
  {
    "id": "d6a16719-72bf-4928-8bf3-abb125186f49",
    "formattedRa": "8h 8m 9.602487087684134s",
    "formattedDec": "124°54'17.277618670114634\""
  }
]
```

## Consume routes in frontend

In this section, we will be consuming data with using our React components.
We will show how to create a client side route to add/render custom components within the application.

First lets cleanup unwanted code

* Go to `components/pages` folder in `src`, delete all component files under this directory
* Delete folder `components/form`
* Go to `pages` folder in `test`, delete all test files under this directory
* Remove the contents of `api.ts` file

### Add models

* Go to `Models.ts`
* Delete existing model interfaces Add our request and response models

Typescript
: @@snip [Models.ts](../../../../frontend/src/models/Models.ts) { #add-models }

### Add Fetch

Implement the following methods to consume data from our POST and GET routes in `api.ts` file.

For POST Route

Typescript
: @@snip [api.ts](../../../../frontend/src/utils/api.ts) { #fetch-data }

For GET Route

Typescript
: @@snip [api.ts](../../../../frontend/src/utils/api.ts) { #fetch-saved-ra-dec-values }

### Add our React component

* In `pages` folder, create `RaDecInput.tsx`
* Add a simple input form to the `RaDecInput` react component

Typescript
: @@snip [RaDecInput.tsx](../../../../frontend/src/components/pages/RaDecInput.tsx) { #add-component }

You would require locationService instance for getting backend url. This instance is available via context named `LocationServiceProvider`.
Add the following as first line inside the `RaDecInput` component.

Typescript
: @@snip [RaDecInput.tsx](../../../../frontend/src/components/pages/RaDecInput.tsx) { #use-location-service-from-context }

Add `onFinish` handler and use `postRaDecValues` method in our component

Typescript
: @@snip [RaDecInput.tsx](../../../../frontend/src/components/pages/RaDecInput.tsx) { #use-fetch }

* In `pages` folder ,Add new component `RaDecTable.tsx` to display ra values table

Typescript
: @@snip [RaDecTable.tsx](../../../../frontend/src/components/pages/RaDecTable.tsx) { #add-component }

Add columns for the table in this component

Typescript
: @@snip [RaDecTable.tsx](../../../../frontend/src/components/pages/RaDecTable.tsx) { #add-columns }

Use our `getRaDecValues` method in this component

Typescript
: @@snip [RaDecTable.tsx](../../../../frontend/src/components/pages/RaDecTable.tsx) { #use-fetch }

* In `pages` folder ,Add new component `RaDec.tsx` to compose above created components and display in a page

Typescript
: @@snip [RaDec.tsx](../../../../frontend/src/components/pages/RaDec.tsx) { #add-component }

Next, we need to show our newly created `RaDec` component.

Update `Routes.tsx` file and delete references to deleted files and their routes, map our new created `RaDec` component to `/` path.

Typescript
: @@snip [Routes.tsx](../../../../frontend/src/routes/Routes.tsx) { #add-route }

Now, we need a link to let user navigate to `Ra` form from different parts of application.

Update `MenuBar.tsx` and delete existing `Menu` and its `Menu.Item`. Add our menu item.

Typescript
: @@snip [MenuBar.tsx](../../../../frontend/src/components/menu/MenuBar.tsx) { #add-route-action }

Now, we have linked all pieces of our frontend application.

```bash
$:frontend> npm start
```

It will launch application in Browser with an input form.

* Add a value like '2.13' and '2.18' and click submit.
* Refresh page
* You will see formatted ra and dec value in table below the input form.

To build the application for its production deployment

```bash
$:frontend> npm run build
```
