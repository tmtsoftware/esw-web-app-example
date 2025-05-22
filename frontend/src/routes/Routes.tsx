import React from 'react'
import { Route, Routes as RouterRoutes } from 'react-router-dom'
import { NotFound } from '../components/error/NotFound'
import { RaDec } from '../components/pages/RaDec'
import { SecuredRaDecInput } from '../components/pages/SecuredRaDecInput'
import { ProtectedRoute } from './ProtectedRoute'

export const Routes = (): React.JSX.Element => {
  return (
    <RouterRoutes>
      {/*// #add-route */}
      <Route path='/' element={<RaDec />} />
      {/*// #add-route */}
      {/*// #add-protected-route */}
      <Route
        path='/securedRaDec'
        element={
          <ProtectedRoute>
            <SecuredRaDecInput />
          </ProtectedRoute>
        }
      />
      {/*// #add-protected-route */}
      <Route path='*' element={<NotFound />} />
    </RouterRoutes>
  )
}
