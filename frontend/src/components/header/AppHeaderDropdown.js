// src/components/header/AppHeaderDropdown.js — REPLACE existing file
import React from 'react'
import { useNavigate } from 'react-router-dom'
import {
  CAvatar,
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

import avatar8 from './../../assets/images/avatars/artificial-intelligence.png'

const ROLE_COLOR = {
  SUPER_ADMIN: '#ef4444',
  ADMIN:       '#f59e0b',
  SUB_ADMIN:   '#0ea5e9',
  EMPLOYEE:    '#94a3b8',
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
        <CAvatar src={avatar8} size="md" />
      </CDropdownToggle>

      <CDropdownMenu className="pt-0" placement="bottom-end">
        <CDropdownHeader className="bg-body-secondary fw-semibold mb-2">
          <div style={{ fontSize: '0.85rem' }}>{user?.name || 'User'}</div>
          <div style={{ fontSize: '0.75rem', opacity: 0.7 }}>{user?.email}</div>
          <div style={{
            display: 'inline-block',
            marginTop: '4px',
            padding: '2px 8px',
            borderRadius: '12px',
            fontSize: '0.68rem',
            fontWeight: 600,
            background: 'rgba(14,165,233,0.15)',
            color: ROLE_COLOR[user?.role] || '#94a3b8',
          }}>
            {user?.role || 'EMPLOYEE'}
          </div>
        </CDropdownHeader>

        <CDropdownItem href="#">
          <CIcon icon={cilUser} className="me-2" /> Profile
        </CDropdownItem>
        <CDropdownItem href="#">
          <CIcon icon={cilSettings} className="me-2" /> Settings
        </CDropdownItem>

        <CDropdownDivider />

        {/* ✅ Logout wired up */}
        <CDropdownItem
          onClick={handleLogout}
          style={{ color: '#ef4444', cursor: 'pointer' }}
        >
          <CIcon icon={cilLockLocked} className="me-2" /> Sign out
        </CDropdownItem>
      </CDropdownMenu>
    </CDropdown>
  )
}

export default AppHeaderDropdown