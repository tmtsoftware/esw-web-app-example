import React from 'react'
import { Route, Switch } from 'react-router-dom'
import { NotFound } from '../components/error/NotFound'
import { RaDec } from '../components/pages/RaDec'
import { SecuredRaDecInput } from '../components/pages/SecuredRaDecInput'
import { ProtectedRoute } from './ProtectedRoute'

export const Routes = (): JSX.Element => {
  return (
    <Switch>
      {/*// #add-route */}
      <Route exact path='/' component={RaDec} />
      {/*// #add-route */}
      {/*// #add-protected-route */}
      <ProtectedRoute path='/securedRaDec' component={SecuredRaDecInput} />
      {/*// #add-protected-route */}
      <Route path='*' component={NotFound} />
    </Switch>
  )
}
