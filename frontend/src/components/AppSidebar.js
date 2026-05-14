import React from 'react'
import { useSelector, useDispatch } from 'react-redux'
import {
  CCloseButton,
  CSidebar,
  CSidebarBrand,
  CSidebarHeader,
} from '@coreui/react'
import { AppSidebarNav } from './AppSidebarNav'
import navigation from '../_nav'

const AppSidebar = () => {
  const dispatch = useDispatch()
  const sidebarShow = useSelector((state) => state.sidebarShow)

  return (
    <CSidebar
      className="border-end"
      colorScheme="dark"
      position="fixed"
      visible={sidebarShow}
      onVisibleChange={(visible) => {
        dispatch({ type: 'set', sidebarShow: visible })
      }}
    >
      <CSidebarHeader className="border-bottom">
        <CSidebarBrand to="/" className="text-decoration-none">
          <div style={{ padding: '4px 0' }}>
            <div style={{
              fontSize: '1.05rem',
              fontWeight: 800,
              color: '#38bdf8',
              letterSpacing: '-0.02em',
              lineHeight: 1.1,
            }}>
              Anjana Infotech
            </div>
            <div style={{
              fontSize: '0.65rem',
              color: 'rgba(148, 163, 184, 0.65)',
              marginTop: '2px',
              letterSpacing: '0.04em',
            }}>
              ISO 9001:2015 Certified
            </div>
          </div>
        </CSidebarBrand>
        <CCloseButton
          className="d-lg-none"
          style={{ filter: 'invert(1) brightness(2)' }}
          onClick={() => dispatch({ type: 'set', sidebarShow: false })}
        />
      </CSidebarHeader>
      <AppSidebarNav items={navigation} />
    </CSidebar>
  )
}

export default React.memo(AppSidebar)