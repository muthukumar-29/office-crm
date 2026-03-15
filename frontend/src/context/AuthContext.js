import React, { createContext, useContext, useState } from 'react'
const AuthContext = createContext(null)
export const AuthProvider = ({ children }) => {
  const [user,  setUser]  = useState(() => { try { return JSON.parse(localStorage.getItem('crm_user')) } catch { return null } })
  const [token, setToken] = useState(() => localStorage.getItem('crm_token') || null)
  const loginSuccess = (data) => {
    localStorage.setItem('crm_token', data.token)
    localStorage.setItem('crm_user', JSON.stringify({ id: data.userId, name: data.name, email: data.email, role: data.role }))
    setToken(data.token)
    setUser({ id: data.userId, name: data.name, email: data.email, role: data.role })
  }
  const logout = () => {
    localStorage.removeItem('crm_token'); localStorage.removeItem('crm_user')
    setToken(null); setUser(null)
  }
  return (
    <AuthContext.Provider value={{ user, token, loginSuccess, logout, isLoggedIn: () => !!token, isAdmin: () => ['SUPER_ADMIN','ADMIN'].includes(user?.role) }}>
      {children}
    </AuthContext.Provider>
  )
}
export const useAuth = () => useContext(AuthContext)
