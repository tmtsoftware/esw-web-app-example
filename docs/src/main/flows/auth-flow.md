# Adding Authentication

## Add protected route in backend
To demonstrate authorization, we will need to create a "protected" route, that is, an endpoint that requires a
valid authorization token to access.

### Add new route with protection

We will add a new route to our server which is protected. To access this route, the request should 
contain a token containing the role `esw-user`.  We have set up some 
[sample users](https://tmtsoftware.github.io/csw/apps/cswservices.html#predefined-users-) when we start 
`csw-services` with the Authentication and Authorization Service enabled, and we will use one of these users for our
tutorial.

Add the following route below to `SampleRoute.scala`.  Note it requires the user to have the `esw-user` role to
access the endpoint.  If you deleted the tilde (~) at the end of your route in the last tutorial, be sure to put it back,
and then append the following:

Scala
: @@snip [SampleRoute.scala](../../../../backend/src/main/scala/org/tmt/sample/http/SampleRoute.scala) { #add-secured-route }

## Consume protected route in frontend
Now, we will create a component in our frontend UI that uses our protected route.

### Add secured Fetch

Add the following method in `api.ts`, which sends a request to our `/securedRaValues` backend route.

Typescript
: @@snip [api.ts](../../../../frontend/src/utils/api.ts) { #secured-fetch-data }

Note that this method requires a token, which is then passed to the server with the request.

### Create a React component to consume our secured route

In the `pages` folder, create a file named `SecuredRaDecInput.tsx`.  Then create a `SecuredRaDecInput` React component
with the following form.

Typescript
: @@snip [SecuredRaDecInput.tsx](../../../../frontend/src/components/pages/SecuredRaDecInput.tsx) { #add-component }

### Use secured fetch in our component

Again, a reference to the Location Service is obtained via a context named `LocationServiceProvider`.
Since this component requires authorization, we use another context to get a reference to the authorization system.

Add the following as first lines inside the `SecuredRaDecInput` component.

Typescript
: @@snip [SecuredRaDecInput.tsx](../../../../frontend/src/components/pages/SecuredRaDecInput.tsx) { #use-location-service-from-context }

The `useAuth` method is a hook provided in `hooks/useAuth.tsx` which accesses the context.  Like `LocationServiceProvider`,
this context, `AuthContextProvider` is made available to the component during construction in `App.tsx`.

Now, add an `onFinish` handler above the return statement, similar to our non-secured component.  Note this time we will obtain the token from the 
authorization context and pass that to our API method.

Typescript
: @@snip [SecuredRaDecInput.tsx](../../../../frontend/src/components/pages/SecuredRaDecInput.tsx) { #use-fetch }

Add the necessary imports.

### Connect our new component

Next, we will add the protected route in `Routes.tsx` within the `<Switch>` block.

Typescript
: @@snip [Routes.tsx](../../../../frontend/src/routes/Routes.tsx) { #add-protected-route }

Add an action for our new route in `MenuBar.tsx` below previously added RaDec `Menu.Item`

Typescript
: @@snip [MenuBar.tsx](../../../../frontend/src/components/menu/MenuBar.tsx) { #add-protected-route-action }

### Add Login & Logout functionality

To provide login and logout capabilities, we will make use of the generated `Login` and `Logout` components.

Add menu item actions for logging in and logging out in `MenuBar.tsx` below the previously added SecuredRaDec `Menu.Item`
The menu item will change depending on whether the user is logged in or not.

Typescript
: @@snip [MenuBar.tsx](../../../../frontend/src/components/menu/MenuBar.tsx) { #add-login-logout }

Note the authorization hook is used again here to get a handle to the authorization store.

Typescript
: @@snip [MenuBar.tsx](../../../../frontend/src/components/menu/MenuBar.tsx) { #use-auth-hook }

## Try it out

Compile the backend and restart it.  Then run the UI as before and try it out.  Clicking on the `SecuredRaDec` menu
item will take you to the login page. Be sure to login with the`esw-user1` user with the password `esw-user1`.  Once
logged in, you will be able to use this form.  The behavior is the same as the non-secured version, but it gives you 
the idea of how pages and routes can be protected.  You will have to switch to the `RaDec` tab to see your inputs.