// src/components/header/AppHeaderDropdown.js — REPLACE
import React from 'react'
import { useNavigate } from 'react-router-dom'
import { CAvatar, CDropdown, CDropdownDivider, CDropdownHeader, CDropdownItem, CDropdownMenu, CDropdownToggle } from '@coreui/react'
import { cilLockLocked, cilUser, cilSettings } from '@coreui/icons'
import CIcon from '@coreui/icons-react'
import { useAuth } from '../../context/AuthContext'
import avatar8 from './../../assets/images/avatars/artificial-intelligence.png'

const ROLE_COLOR = { SUPER_ADMIN: '#ef4444', ADMIN: '#f59e0b', EMPLOYEE: '#94a3b8', SUB_ADMIN: '#0ea5e9' }

const AppHeaderDropdown = () => {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  return (
    <CDropdown variant="nav-item">
      <CDropdownToggle placement="bottom-end" className="py-0 pe-0" caret={false}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', cursor: 'pointer' }}>
          <CAvatar src={avatar8} size="md" />
          <div style={{ textAlign: 'left', display: 'none' }} className="d-md-block">
            <div style={{ fontSize: '0.8rem', fontWeight: 600, color: 'var(--text-primary)', lineHeight: 1 }}>{user?.name || 'User'}</div>
            <div style={{ fontSize: '0.68rem', color: ROLE_COLOR[user?.role] || '#94a3b8', marginTop: '2px' }}>{user?.role}</div>
          </div>
        </div>
      </CDropdownToggle>
      <CDropdownMenu className="pt-0" placement="bottom-end" style={{ background: 'var(--surface-2)', border: '1px solid var(--border-medium)', borderRadius: '10px', minWidth: '200px' }}>
        <CDropdownHeader style={{ background: 'var(--surface-1)', borderBottom: '1px solid var(--border-subtle)', padding: '0.75rem 1rem' }}>
          <div style={{ fontSize: '0.875rem', fontWeight: 600, color: 'var(--text-primary)' }}>{user?.name}</div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{user?.email}</div>
          <span className="crm-badge badge-info" style={{ marginTop: '0.35rem', display: 'inline-block' }}>{user?.role}</span>
        </CDropdownHeader>
        <CDropdownItem style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', padding: '0.5rem 1rem' }}>
          <CIcon icon={cilUser} className="me-2" /> Profile
        </CDropdownItem>
        <CDropdownItem style={{ color: 'var(--text-secondary)', fontSize: '0.875rem', padding: '0.5rem 1rem' }}>
          <CIcon icon={cilSettings} className="me-2" /> Settings
        </CDropdownItem>
        <CDropdownDivider style={{ borderColor: 'var(--border-subtle)' }} />
        <CDropdownItem
          onClick={() => { logout(); navigate('/login') }}
          style={{ color: '#ef4444', fontSize: '0.875rem', padding: '0.5rem 1rem', cursor: 'pointer' }}
        >
          <CIcon icon={cilLockLocked} className="me-2" /> Sign out
        </CDropdownItem>
      </CDropdownMenu>
    </CDropdown>
  )
}

export default AppHeaderDropdown
