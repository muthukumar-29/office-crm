// src/context/AuthContext.js — REPLACE
import React, { createContext, useContext, useState } from 'react'

const AuthContext = createContext(null)

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(() => {
    try { return JSON.parse(localStorage.getItem('crm_user')) } catch { return null }
  })
  const [token, setToken] = useState(() => localStorage.getItem('crm_token') || null)

  const loginSuccess = (data) => {
    localStorage.setItem('crm_token', data.token)
    localStorage.setItem('crm_user', JSON.stringify({
      id:       data.id     || data.userId,
      userId:   data.userId || '',
      name:     data.name,
      email:    data.email,
      role:     data.role,
    }))
    setToken(data.token)
    setUser({ id: data.id || data.userId, userId: data.userId||'', name: data.name, email: data.email, role: data.role })
  }

  const logout = () => {
    localStorage.removeItem('crm_token')
    localStorage.removeItem('crm_user')
    setToken(null)
    setUser(null)
  }

  /** Helpers */
  const isLoggedIn    = ()   => !!token
  const isAdmin       = ()   => ['SUPER_ADMIN','ADMIN'].includes(user?.role)
  const isSuperAdmin  = ()   => user?.role === 'SUPER_ADMIN'
  const isEmployee    = ()   => user?.role === 'EMPLOYEE' || user?.role === 'SUB_ADMIN'
  const hasRole       = (r)  => user?.role === r

  return (
    <AuthContext.Provider value={{ user, token, loginSuccess, logout,
      isLoggedIn, isAdmin, isSuperAdmin, isEmployee, hasRole }}>
      {children}
    </AuthContext.Provider>
  )
}

export const useAuth = () => useContext(AuthContext)
