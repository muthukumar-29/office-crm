import React from 'react'
import { useNavigate } from 'react-router-dom'
import {
  CDropdown,
  CDropdownDivider,
  CDropdownHeader,
  CDropdownItem,
  CDropdownMenu,
  CDropdownToggle,
} from '@coreui/react'
import { cilLockLocked, cilUser, cilSettings } from '@coreui/icons'
import CIcon from '@coreui/icons-react'
import { useAuth } from '../../context/AuthContext'

const ROLE_COLOR = {
  SUPER_ADMIN: '#ef4444',
  ADMIN:       '#f59e0b',
  SUB_ADMIN:   '#0ea5e9',
  EMPLOYEE:    '#64748b',
}

const InitialAvatar = ({ name }) => {
  const letter = (name || 'U').charAt(0).toUpperCase()
  return (
    <div style={{
      width: 36,
      height: 36,
      borderRadius: '50%',
      background: 'var(--brand-primary)',
      color: '#ffffff',
      fontWeight: 700,
      fontSize: '1rem',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      cursor: 'pointer',
      userSelect: 'none',
      flexShrink: 0,
      letterSpacing: '-0.01em',
    }}>
      {letter}
    </div>
  )
}

const AppHeaderDropdown = () => {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <CDropdown variant="nav-item">
      <CDropdownToggle placement="bottom-end" className="py-0 pe-0" caret={false}>
        <InitialAvatar name={user?.name} />
      </CDropdownToggle>

      <CDropdownMenu className="pt-0" placement="bottom-end">
        <CDropdownHeader className="bg-body-secondary fw-semibold mb-2">
          <div style={{ fontSize: '0.85rem', color: 'var(--text-primary)' }}>{user?.name || 'User'}</div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{user?.email}</div>
          <div style={{
            display: 'inline-block',
            marginTop: '4px',
            padding: '2px 8px',
            borderRadius: '12px',
            fontSize: '0.68rem',
            fontWeight: 600,
            background: 'rgba(14,165,233,0.12)',
            color: ROLE_COLOR[user?.role] || '#64748b',
          }}>
            {user?.role || 'USER'}
          </div>
        </CDropdownHeader>

        <CDropdownItem href="#">
          <CIcon icon={cilUser} className="me-2" /> Profile
        </CDropdownItem>
        <CDropdownItem href="#">
          <CIcon icon={cilSettings} className="me-2" /> Settings
        </CDropdownItem>

        <CDropdownDivider />

        <CDropdownItem onClick={handleLogout} style={{ color: '#dc2626', cursor: 'pointer' }}>
          <CIcon icon={cilLockLocked} className="me-2" /> Sign out
        </CDropdownItem>
      </CDropdownMenu>
    </CDropdown>
  )
}

export default AppHeaderDropdown
