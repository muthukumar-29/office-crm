// src/_nav.js — REPLACE existing file
import React from 'react'
import CIcon from '@coreui/icons-react'
import {
  cilSpeedometer,
  cilBook,
  cilApps,
  cilCode,
  cilUser,
  cilEducation,
  cilLink,
  cilCreditCard,
  cilDescription,
  cilMoney,
  cilBuilding,
  cilChartPie,
  cilStar,
} from '@coreui/icons'
import { CNavGroup, CNavItem, CNavTitle } from '@coreui/react'

const _nav = [
  {
    component: CNavItem,
    name: 'Dashboard',
    to: '/dashboard',
    icon: <CIcon icon={cilSpeedometer} customClassName="nav-icon" />,
  },

  { component: CNavTitle, name: 'Catalog' },

  {
    component: CNavItem,
    name: 'Courses',
    to: '/courses',                         // ✅ was /course
    icon: <CIcon icon={cilBook} customClassName="nav-icon" />,
  },
  {
    component: CNavItem,
    name: 'Internships',
    to: '/interns',                         // ✅ was /intern
    icon: <CIcon icon={cilApps} customClassName="nav-icon" />,
  },
  {
    component: CNavItem,
    name: 'Projects',
    to: '/projects',                        // ✅ was /project
    icon: <CIcon icon={cilCode} customClassName="nav-icon" />,
  },

  { component: CNavTitle, name: 'Students' },

  {
    component: CNavItem,
    name: 'Students',
    to: '/students',
    icon: <CIcon icon={cilEducation} customClassName="nav-icon" />,
  },
  {
    component: CNavItem,
    name: 'Allocations',
    to: '/allocations',                     // ✅ was /allocate
    icon: <CIcon icon={cilLink} customClassName="nav-icon" />,
  },
  {
    component: CNavItem,
    name: 'Payments',
    to: '/payments',
    icon: <CIcon icon={cilCreditCard} customClassName="nav-icon" />,
  },
  {
    component: CNavItem,
    name: 'Certificates',
    to: '/certificates',
    icon: <CIcon icon={cilDescription} customClassName="nav-icon" />,
  },
  {
    component: CNavItem,
    name: 'Invoices',
    to: '/invoices',
    icon: <CIcon icon={cilMoney} customClassName="nav-icon" />,
  },

  { component: CNavTitle, name: 'Office' },

  {
    component: CNavItem,
    name: 'Office Projects',
    to: '/office-projects',
    icon: <CIcon icon={cilBuilding} customClassName="nav-icon" />,
  },
  {
    component: CNavItem,
    name: 'Finance',
    to: '/finance',
    icon: <CIcon icon={cilChartPie} customClassName="nav-icon" />,
  },

  { component: CNavTitle, name: 'Admin' },

  {
    component: CNavItem,
    name: 'Users',
    to: '/users',
    icon: <CIcon icon={cilUser} customClassName="nav-icon" />,
  },

  // ---- keep the Pages group for fallback access ----
  { component: CNavTitle, name: 'Extras' },
  {
    component: CNavGroup,
    name: 'Pages',
    icon: <CIcon icon={cilStar} customClassName="nav-icon" />,
    items: [
      { component: CNavItem, name: 'Login',    to: '/login' },
      { component: CNavItem, name: 'Error 404', to: '/404' },
      { component: CNavItem, name: 'Error 500', to: '/500' },
    ],
  },
]

export default _nav