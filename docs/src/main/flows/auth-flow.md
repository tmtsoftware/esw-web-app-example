# Adding Authentication

## Add protected route in backend
To demonstrate authorization, we will need to create a "protected" route, that is, an endpoint that require a
valid authorization token to access.

### Add new route with protection

We will add a new route in `SampleRoute.scala` which is protected. To access this route, the request should contain a token containing the role `esw-user`.
We have set up some [sample users](https://tmtsoftware.github.io/csw/apps/cswservices.html#predefined-users-) when we start `csw-services` with Authentication.

Scala
: @@snip [SampleRoute.scala](../../../../backend/src/main/scala/org/tmt/sample/http/SampleRoute.scala) { #add-secured-route }

## Consume protected route in frontend
Now, we will create a component in our frontend UI that uses our protected route.

### Add secured Fetch

Add the following method in `api.ts`, which sends a request to our `/securedRaValues` backend route.

Typescript
: @@snip [api.ts](../../../../frontend/src/utils/api.ts) { #secured-fetch-data }

### Add our React component to consume secured fetch

* In the `pages` folder, create a file named `SecuredRaDecInput.tsx`
* Add the following form to the `SecuredRaDecInput` React component

Typescript
: @@snip [SecuredRaDecInput.tsx](../../../../frontend/src/components/pages/SecuredRaDecInput.tsx) { #add-component }

### Use secured fetch in our component

A Location Service instance is required to get the URL of the backend service. This instance is available via a context named `LocationServiceProvider`.
Also, we need auth hook to get the token. Add the following as first lines inside the `SecuredRa` component.

Typescript
: @@snip [SecuredRaDecInput.tsx](../../../../frontend/src/components/pages/SecuredRaDecInput.tsx) { #use-location-service-from-context }

Add an `onFinish` handler

Typescript
: @@snip [SecuredRaDecInput.tsx](../../../../frontend/src/components/pages/SecuredRaDecInput.tsx) { #use-fetch }

Add the protected route in `Routes.tsx`

Typescript
: @@snip [Routes.tsx](../../../../frontend/src/routes/Routes.tsx) { #add-protected-route }

Add an action for our new route in `MenuBar.tsx` below previously added RaDec `Menu.Item`

Typescript
: @@snip [MenuBar.tsx](../../../../frontend/src/components/menu/MenuBar.tsx) { #add-protected-route-action }

### Add Login & Logout functionality

To provide login and logout capabilities, we will make use of the generated `Login` and `Logout` components .

Add menu item actions for logging in and logging out in `MenuBar.tsx` below the previously added SecuredRaDec `Menu.Item`

Typescript
: @@snip [MenuBar.tsx](../../../../frontend/src/components/menu/MenuBar.tsx) { #add-login-logout }

Use a authorization hook to get a handle to the authorization store.

Typescript
: @@snip [MenuBar.tsx](../../../../frontend/src/components/menu/MenuBar.tsx) { #use-auth-hook }
