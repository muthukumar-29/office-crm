// src/components/common/ui.jsx
import React from 'react'

export function Preloader({ show }) {
  if (!show) return null
  return (
    <div className="crm-preloader">
      <div className="spinner" />
    </div>
  )
}

export function CrmPagination({ page, totalPages, onPageChange }) {
  if (totalPages <= 1) return null
  return (
    <div className="crm-pagination">
      <button disabled={page === 0} onClick={() => onPageChange(page - 1)}>‹</button>
      {Array.from({ length: Math.min(totalPages, 7) }, (_, i) => {
        const p = totalPages <= 7 ? i : (page < 4 ? i : (page > totalPages - 4 ? totalPages - 7 + i : page - 3 + i))
        return (
          <button key={p} className={page === p ? 'active' : ''} onClick={() => onPageChange(p)}>
            {p + 1}
          </button>
        )
      })}
      <button disabled={page === totalPages - 1} onClick={() => onPageChange(page + 1)}>›</button>
    </div>
  )
}

export function Badge({ type = 'muted', children }) {
  return <span className={`crm-badge badge-${type}`}>{children}</span>
}

export function StatCard({ label, value, icon, color = 'blue', sub }) {
  return (
    <div className={`stat-card stat-${color}`}>
      <div className="d-flex align-items-start justify-content-between">
        <div>
          <div className="stat-label">{label}</div>
          <div className="stat-value">{value}</div>
          {sub && <div className="text-xs text-muted-crm mt-1">{sub}</div>}
        </div>
        <div className="stat-icon">{icon}</div>
      </div>
    </div>
  )
}

export function CrmModal({ show, onClose, title, size = 'modal-md', children, footer }) {
  if (!show) return null
  return (
    <div className="crm-modal-backdrop" onClick={onClose}>
      <div className={`crm-modal ${size}`} onClick={e => e.stopPropagation()}>
        <div className="modal-header">
          <h5>{title}</h5>
          <button className="modal-close" onClick={onClose}>×</button>
        </div>
        <div className="modal-body">{children}</div>
        {footer && <div className="modal-footer">{footer}</div>}
      </div>
    </div>
  )
}

export function FormRow({ children, cols = 2 }) {
  return (
    <div className={`row g-3`}>
      {React.Children.map(children, (child, i) => (
        <div key={i} className={`col-md-${12 / cols}`}>{child}</div>
      ))}
    </div>
  )
}

export function Field({ label, children }) {
  return (
    <div>
      <label className="crm-label">{label}</label>
      {children}
    </div>
  )
}

export function Input({ ...props }) {
  return <input className="crm-input" {...props} />
}

export function Select({ children, ...props }) {
  return <select className="crm-input" {...props}>{children}</select>
}

export function Textarea({ ...props }) {
  return <textarea className="crm-input" {...props} />
}

export function PageHeader({ title, subtitle, actions }) {
  return (
    <div className="page-header">
      <div>
        <h1 className="page-title">{title}</h1>
        {subtitle && <p className="page-subtitle">{subtitle}</p>}
      </div>
      {actions && <div className="d-flex gap-2">{actions}</div>}
    </div>
  )
}

export function EmptyState({ message = 'No records found' }) {
  return (
    <tr><td colSpan="20" style={{ textAlign: 'center', padding: '3rem', color: 'var(--text-muted)', fontSize: '0.875rem' }}>
      <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>⊘</div>
      {message}
    </td></tr>
  )
}
