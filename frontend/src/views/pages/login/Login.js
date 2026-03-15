// src/views/pages/login/Login.js  — REPLACE
import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { loginApi } from '../../../api/services/authService'
import { useAuth } from '../../../context/AuthContext'

const Login = () => {
  const navigate        = useNavigate()
  const { loginSuccess } = useAuth()
  const [email,    setEmail]    = useState('')
  const [password, setPassword] = useState('')
  const [loading,  setLoading]  = useState(false)
  const [error,    setError]    = useState('')

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (!email || !password) { setError('Email and password are required'); return }
    setLoading(true); setError('')
    try {
      const res  = await loginApi({ email, password })
      const data = res.data?.data || res.data
      if (!data?.token) throw new Error('Invalid response')
      loginSuccess(data)
      navigate('/dashboard')
    } catch (err) {
      setError(err.response?.data?.message || err.response?.data || 'Login failed. Check credentials.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-bg">
      <div className="login-card">
        <div className="login-logo">⬡ Office CRM</div>
        <p className="login-subtitle">Sign in to your workspace</p>

        {error && (
          <div style={{ background: 'var(--danger-soft)', border: '1px solid rgba(239,68,68,0.3)', borderRadius: '6px', padding: '0.65rem 0.9rem', marginBottom: '1rem', fontSize: '0.8rem', color: '#ef4444' }}>
            {typeof error === 'string' ? error : 'Login failed'}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '1rem' }}>
            <label className="crm-label">Email</label>
            <input
              className="crm-input"
              type="email"
              placeholder="you@example.com"
              value={email}
              onChange={e => setEmail(e.target.value)}
              autoComplete="email"
            />
          </div>

          <div style={{ marginBottom: '1.5rem' }}>
            <label className="crm-label">Password</label>
            <input
              className="crm-input"
              type="password"
              placeholder="••••••••"
              value={password}
              onChange={e => setPassword(e.target.value)}
              autoComplete="current-password"
            />
          </div>

          <button
            type="submit"
            className="btn-crm-primary"
            disabled={loading}
            style={{ width: '100%', padding: '0.65rem', fontSize: '0.9rem', borderRadius: '8px', letterSpacing: '0.02em' }}
          >
            {loading ? 'Signing in…' : 'Sign in'}
          </button>
        </form>

        <div style={{ marginTop: '2rem', padding: '1rem', background: 'rgba(14,165,233,0.06)', borderRadius: '8px', border: '1px solid rgba(14,165,233,0.15)' }}>
          <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)', marginBottom: '0.5rem', textTransform: 'uppercase', letterSpacing: '0.06em' }}>Demo credentials</div>
          <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>admin@crm.com / password</div>
        </div>
      </div>
    </div>
  )
}

export default Login
