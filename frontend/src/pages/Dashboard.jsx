// src/pages/Dashboard.jsx — REPLACE Dashboard.js
import React, { useEffect, useState } from 'react'
import { getAllStudents } from '../api/services/studentService'
import { getAllAllocations } from '../api/services/allocationService'
import { getAllUsers } from '../api/services/userService'
import { getFinanceSummary } from '../api/services/financeService'
import { getAllCertificates } from '../api/services/certificateService'
import { useAuth } from '../context/AuthContext'
import { StatCard } from '../components/common/ui'

export default function Dashboard() {
  const { user } = useAuth()
  const [stats, setStats] = useState({ students: 0, allocations: 0, users: 0, income: 0, certificates: 0 })
  const [recent, setRecent] = useState([])
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const load = async () => {
      try {
        const [sRes, aRes, uRes, fRes, cRes] = await Promise.allSettled([
          getAllStudents(), getAllAllocations(), getAllUsers(), getFinanceSummary(), getAllCertificates(),
        ])
        const students     = sRes.status === 'fulfilled' ? (sRes.value.data?.data || sRes.value.data || []) : []
        const allocations  = aRes.status === 'fulfilled' ? (aRes.value.data?.data || aRes.value.data || []) : []
        const users        = uRes.status === 'fulfilled' ? (uRes.value.data?.data || uRes.value.data || []) : []
        const finance      = fRes.status === 'fulfilled' ? (fRes.value.data?.data || fRes.value.data || {}) : {}
        const certs        = cRes.status === 'fulfilled' ? (cRes.value.data?.data || cRes.value.data || []) : []

        setStats({
          students:     students.length,
          allocations:  allocations.length,
          users:        users.length,
          income:       finance.totalIncome || 0,
          certificates: certs.length,
        })

        // Recent allocations as activity feed
        const acts = allocations.slice(0, 6).map(a => ({
          text: `${a.student?.name || 'Student'} allocated to ${a.category?.toLowerCase() || 'program'}`,
          color: a.category === 'PROJECT' ? 'blue' : a.category === 'INTERN' ? 'green' : 'amber',
          time: a.createdAt ? new Date(a.createdAt).toLocaleDateString('en-IN', { day: '2-digit', month: 'short' }) : 'Recent',
        }))
        setRecent(acts)
      } catch (e) { console.error(e) }
      finally { setLoading(false) }
    }
    load()
  }, [])

  return (
    <div>
      {/* Welcome */}
      <div style={{ marginBottom: '1.5rem' }}>
        <h1 style={{ fontSize: '1.35rem', fontWeight: 700, color: 'var(--text-primary)', margin: 0, letterSpacing: '-0.02em' }}>
          Good {new Date().getHours() < 12 ? 'morning' : new Date().getHours() < 18 ? 'afternoon' : 'evening'}, {user?.name?.split(' ')[0] || 'there'} 👋
        </h1>
        <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', margin: '0.25rem 0 0' }}>
          Here's what's happening with your CRM today.
        </p>
      </div>

      {/* Stats Grid */}
      <div className="row g-3 mb-4">
        <div className="col-md-4 col-6">
          <StatCard label="Total Students"   value={stats.students}     icon="🎓" color="blue"   sub="Registered students" />
        </div>
        <div className="col-md-4 col-6">
          <StatCard label="Allocations"      value={stats.allocations}  icon="🔗" color="green"  sub="Active & completed" />
        </div>
        <div className="col-md-4 col-6">
          <StatCard label="Staff Users"      value={stats.users}        icon="👥" color="purple" sub="Admin & staff" />
        </div>
        <div className="col-md-4 col-6">
          <StatCard label="Total Income"     value={`₹${Number(stats.income).toLocaleString('en-IN')}`} icon="💰" color="amber" sub="This month" />
        </div>
        <div className="col-md-4 col-6">
          <StatCard label="Certificates"     value={stats.certificates} icon="📜" color="red"    sub="Issued certificates" />
        </div>
        <div className="col-md-4 col-6">
          <StatCard label="System Status"    value="Online"             icon="✅" color="green"  sub="All services running" />
        </div>
      </div>

      {/* Recent Allocations — full width */}
      <div className="row g-3">
        <div className="col-12">
          <div className="card">
            <div className="card-header d-flex align-items-center justify-content-between">
              <span>Recent Allocations</span>
              <span className="crm-badge badge-info">{recent.length}</span>
            </div>
            <div className="card-body" style={{ padding: '0.5rem 1.25rem' }}>
              {recent.length === 0 && !loading
                ? <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem', padding: '1rem 0' }}>No allocations yet</p>
                : recent.map((a, i) => (
                  <div key={i} className="activity-item">
                    <div className={`activity-dot ${a.color}`} />
                    <span className="activity-text">{a.text}</span>
                    <span className="activity-time">{a.time}</span>
                  </div>
                ))}
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
