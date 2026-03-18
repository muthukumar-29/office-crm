import React from 'react'
import { CFooter } from '@coreui/react'

const AppFooter = () => {
  return (
    <CFooter className="px-4">
      <div>
        <span className="ms-1">
          &copy; {new Date().getFullYear()}&nbsp;
          <strong style={{ color: '#60a5fa' }}>Anjana Infotech</strong>
          &nbsp;— ISO 9001:2015 Certified
        </span>
      </div>
      <div className="ms-auto" style={{ fontSize: '0.75rem', color: '#64748b' }}>
        info@anjanainfotech.in &nbsp;|&nbsp; +91 97879 70633
      </div>
    </CFooter>
  )
}

export default React.memo(AppFooter)