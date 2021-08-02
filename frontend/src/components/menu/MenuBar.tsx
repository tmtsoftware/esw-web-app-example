import { Menu } from 'antd'
import React from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import { Login } from './Login'
import { Logout } from './Logout'

export const MenuBar = (): JSX.Element => {
  // #use-auth-hook
  const { auth, login, logout } = useAuth()
  const isAuthenticated = auth?.isAuthenticated() ?? false
  // #use-auth-hook

  return (
    <>
      {/*// #add-route-action */}
      <Menu mode='horizontal'>
        <Menu.Item key='raDec'>
          <Link to='/'>RaDec</Link>
        </Menu.Item>
        {/*// #add-route-action */}
        {/*// #add-protected-route-action */}
        <Menu.Item key='securedRaDec'>
          <Link to='/securedRaDec'>SecuredRaDec</Link>
        </Menu.Item>
        {/*// #add-protected-route-action */}
        {/*// #add-login-logout */}
        {isAuthenticated ? <Logout logout={logout} /> : <Login login={login} />}
        {/*// #add-login-logout */}
        {/*// #add-route-action */}
      </Menu>
      {/*// #add-route-action */}
    </>
  )
}
