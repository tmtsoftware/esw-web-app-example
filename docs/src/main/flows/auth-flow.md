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

Typescript
: @@snip [SecuredRaDecInput.tsx](../../../../frontend/src/components/pages/SecuredRaDecInput.tsx) { #use-fetch }

You would require locationService instance for getting backend url. This instance is available via context named `LocationServiceProvider`.
Add the following as first line inside the `SecuredRa` component.

Typescript
: @@snip [SecuredRaDecInput.tsx](../../../../frontend/src/components/pages/SecuredRaDecInput.tsx) { #use-location-service-from-context }

Add protected route in `App.tsx`

Typescript
: @@snip [App.tsx](../../../../frontend/src/routes/Routes.tsx) { #add-protected-route }

Update Menubar

* Remove the `/greeting` menu item
* Update menu item action for protected route in `App.tsx` from `AdminGreeting` to `SecuredRa`.

Typescript
: @@snip [App.tsx](../../../../frontend/src/components/menu/MenuBar.tsx) { #add-protected-route-action }

### Add Login & Logout functionality

Make use of generated `Login` and `Logout` components .

Add menu items actions for `logging in` and `logging out` in `MenuBar.tsx`.

Typescript
: @@snip [App.tsx](../../../../frontend/src/components/menu/MenuBar.tsx) { #add-login-logout }

Use Auth Hook to get handle on auth store.

Typescript
: @@snip [App.tsx](../../../../frontend/src/components/menu/MenuBar.tsx) { #use-auth-hook }
