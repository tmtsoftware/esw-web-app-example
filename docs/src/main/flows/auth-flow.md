# Adding Authentication

## Add protected route in backend

### Add new route with protection

We will add a new route in `SampleRoute.scala` which is protected, to access this route, request should contain a token containing role `esw-user`.
We have set up some [sample users](https://tmtsoftware.github.io/csw/apps/cswservices.html#predefined-users-) when we start `csw-services` with Authentication.

Scala
: @@snip [SampleRoute.scala](../../../../backend/src/main/scala/org/tmt/sample/http/SampleRoute.scala) { #add-secured-route }

## Consume protected route in frontend

### Add secured Fetch

Add the following method in `api.ts` which sends request to `/securedRaValues` backend route.

Typescript
: @@snip [api.ts](../../../../frontend/src/utils/api.ts) { #secured-fetch-data }

### Add our React component to consume secured fetch

* In `pages` folder, create a file named `SecuredRaDecInput.tsx`
* Add the following form to the `SecuredRaDecInput` react component

Typescript
: @@snip [SecuredRaDecInput.tsx](../../../../frontend/src/components/pages/SecuredRaDecInput.tsx) { #add-component }

### Use secured fetch in our component

You would require locationService instance for getting backend url. This instance is available via context named `LocationServiceProvider`.
Also, we need auth hook to get token. Add the following as first lines inside the `SecuredRa` component.

Typescript
: @@snip [SecuredRaDecInput.tsx](../../../../frontend/src/components/pages/SecuredRaDecInput.tsx) { #use-location-service-from-context }

Add `onFinish` handler

Typescript
: @@snip [SecuredRaDecInput.tsx](../../../../frontend/src/components/pages/SecuredRaDecInput.tsx) { #use-fetch }

Add protected route in `Routes.tsx`

Typescript
: @@snip [Routes.tsx](../../../../frontend/src/routes/Routes.tsx) { #add-protected-route }

Add action for our new route in `MenuBar.tsx` below previously added RaDec `Menu.Item`

Typescript
: @@snip [MenuBar.tsx](../../../../frontend/src/components/menu/MenuBar.tsx) { #add-protected-route-action }

### Add Login & Logout functionality

Make use of generated `Login` and `Logout` components .

Add menu items actions for `logging in` and `logging out` in `MenuBar.tsx` below previously added SecuredRaDec `Menu.Item`

Typescript
: @@snip [MenuBar.tsx](../../../../frontend/src/components/menu/MenuBar.tsx) { #add-login-logout }

Use Auth Hook to get handle on auth store.

Typescript
: @@snip [MenuBar.tsx](../../../../frontend/src/components/menu/MenuBar.tsx) { #use-auth-hook }
