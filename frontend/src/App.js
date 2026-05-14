// src/App.js — REPLACE existing file
import React, { Suspense, useEffect } from 'react'
import { HashRouter, Route, Routes } from 'react-router-dom'
import { useSelector } from 'react-redux'
import { CSpinner, useColorModes } from '@coreui/react'

import { AuthProvider } from './context/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'

import './scss/style.scss'
import './scss/examples.scss'
import './scss/custom.scss'   // ← light theme overrides

// Lazy pages
const DefaultLayout = React.lazy(() => import('./layout/DefaultLayout'))
const Login         = React.lazy(() => import('./views/pages/login/Login'))
const Register      = React.lazy(() => import('./views/pages/register/Register'))
const Page404       = React.lazy(() => import('./views/pages/page404/Page404'))
const Page500       = React.lazy(() => import('./views/pages/page500/Page500'))

const App = () => {
  const { isColorModeSet, setColorMode } = useColorModes('coreui-free-react-admin-template-theme')
  const storedTheme = useSelector((state) => state.theme)

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.href.split('?')[1])
    const theme = urlParams.get('theme') && urlParams.get('theme').match(/^[A-Za-z0-9\s]+/)[0]
    if (theme) { setColorMode(theme) }
    if (isColorModeSet()) return
    setColorMode(storedTheme)
  }, []) // eslint-disable-line react-hooks/exhaustive-deps

  return (
    // ✅ AuthProvider wraps everything so useAuth() works in all components
    <AuthProvider>
      <HashRouter>
        <Suspense
          fallback={
            <div className="pt-3 text-center">
              <CSpinner color="primary" variant="grow" />
            </div>
          }
        >
          <Routes>
            {/* Public routes — no auth needed */}
            <Route path="/login"    name="Login Page"    element={<Login />} />
            <Route path="/register" name="Register Page" element={<Register />} />
            <Route path="/404"      name="Page 404"      element={<Page404 />} />
            <Route path="/500"      name="Page 500"      element={<Page500 />} />

            {/* ✅ All other routes are protected — redirects to /login if no token */}
            <Route
              path="*"
              name="Home"
              element={
                <ProtectedRoute>
                  <DefaultLayout />
                </ProtectedRoute>
              }
            />
          </Routes>
        </Suspense>
      </HashRouter>
    </AuthProvider>
  )
}

export default App