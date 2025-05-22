import { Menu } from 'antd'
import React from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'

// #use-auth-hook
export const MenuBar = (): React.JSX.Element => {
  const { auth, login, logout } = useAuth()
  const isAuthenticated = auth?.isAuthenticated() ?? false
  // #use-auth-hook

  // #add-route-action
  // #add-protected-route-action
  const items: MenuItem[] = [
    {
      key: 'raDec',
      label: <Link to='/'>RaDec</Link>
    },
    // #add-route-action
    // #add-login-logout
    {
      key: 'securedRaDec',
      label: <Link to='/securedRaDec'>SecuredRaDec</Link>
    },
    // #add-protected-route-action
    isAuthenticated
      ? {
          key: 'logout',
          label: 'Logout',
          onClick: logout
        }
      : {
          key: 'login',
          label: 'Login',
          onClick: login
        }
    // #add-login-logout
    // #add-route-action
    // #add-protected-route-action
  ]
  // #add-protected-route-action
  // #add-route-action

  return <Menu mode='horizontal' items={items} />
}
