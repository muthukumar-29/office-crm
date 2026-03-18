// src/views/pages/login/Login.js — REPLACE
import React, { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { loginApi } from '../../../api/services/authService'
import { useAuth } from '../../../context/AuthContext'

const Login = () => {
  const navigate         = useNavigate()
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

        {/* Logo / brand */}
        <div style={{ marginBottom: '1.75rem', textAlign: 'center' }}>
          <div style={{
            fontSize: '1.6rem',
            fontWeight: 800,
            color: '#60a5fa',
            letterSpacing: '-0.03em',
            lineHeight: 1.1,
          }}>
            Anjana Infotech
          </div>
          <div style={{ fontSize: '0.72rem', color: '#64748b', marginTop: '4px', letterSpacing: '0.06em' }}>
            ISO 9001:2015 Certified
          </div>
          <div style={{
            marginTop: '0.75rem',
            fontSize: '0.8rem',
            color: 'var(--text-muted)',
          }}>
            Sign in to your workspace
          </div>
        </div>

        {error && (
          <div style={{
            background: 'var(--danger-soft)',
            border: '1px solid rgba(239,68,68,0.3)',
            borderRadius: '6px',
            padding: '0.65rem 0.9rem',
            marginBottom: '1rem',
            fontSize: '0.8rem',
            color: '#ef4444',
          }}>
            {typeof error === 'string' ? error : 'Login failed'}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div style={{ marginBottom: '1rem' }}>
            <label className="crm-label">Email</label>
            <input
              className="crm-input"
              type="email"
              placeholder="you@anjanainfotech.in"
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
            style={{
              width: '100%',
              padding: '0.65rem',
              fontSize: '0.9rem',
              borderRadius: '8px',
              letterSpacing: '0.02em',
              justifyContent: 'center',
            }}
          >
            {loading ? 'Signing in…' : 'Sign in'}
          </button>
        </form>

        {/* Contact info */}
        <div style={{
          marginTop: '2rem',
          padding: '0.9rem 1rem',
          background: 'rgba(14,165,233,0.06)',
          borderRadius: '8px',
          border: '1px solid rgba(14,165,233,0.15)',
          fontSize: '0.75rem',
          color: 'var(--text-muted)',
          textAlign: 'center',
          lineHeight: 1.7,
        }}>
          <div style={{ fontWeight: 600, color: '#60a5fa', marginBottom: '2px' }}>Anjana Infotech</div>
          372, Mudangiyar Road, Opp. AKDR Market, Rajapalayam<br />
          +91 97879 70633 &nbsp;|&nbsp; info@anjanainfotech.in
        </div>
      </div>
    </div>
  )
}

export default Login