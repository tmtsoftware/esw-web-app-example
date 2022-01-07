import React from 'react'
import { Route } from 'react-router-dom'
import type { RouteProps } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { RedirectToLogin } from './RedirectToLogin'

export const ProtectedRoute = (routeProps: RouteProps): JSX.Element => {
  const { auth } = useAuth()
  if (!auth) return <div>Loading</div>
  const isAuthenticated = auth?.isAuthenticated() ?? false
  return isAuthenticated ? <Route {...routeProps} /> : <RedirectToLogin />
}
