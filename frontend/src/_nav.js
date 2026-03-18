// src/_nav.js — REPLACE
import React from 'react'
import CIcon from '@coreui/icons-react'
import {
  cilSpeedometer, cilBook, cilApps, cilCode, cilUser, cilEducation,
  cilLink, cilCreditCard, cilDescription, cilMoney, cilBuilding,
  cilChartPie, cilCash,
} from '@coreui/icons'
import { CNavItem, CNavTitle } from '@coreui/react'

const _nav = [
  {
    component: CNavItem,
    name: 'Dashboard',
    to: '/dashboard',
    icon: <CIcon icon={cilSpeedometer} customClassName="nav-icon" />,
  },

  { component: CNavTitle, name: 'Catalog' },
  {
    component: CNavItem, name: 'Courses',
    to: '/courses',
    icon: <CIcon icon={cilBook} customClassName="nav-icon" />,
  },
  {
    component: CNavItem, name: 'Internships',
    to: '/interns',
    icon: <CIcon icon={cilApps} customClassName="nav-icon" />,
  },
  {
    component: CNavItem, name: 'Projects',
    to: '/projects',
    icon: <CIcon icon={cilCode} customClassName="nav-icon" />,
  },

  { component: CNavTitle, name: 'Students' },
  {
    component: CNavItem, name: 'Students',
    to: '/students',
    icon: <CIcon icon={cilEducation} customClassName="nav-icon" />,
  },
  {
    component: CNavItem, name: 'Allocations',
    to: '/allocations',
    icon: <CIcon icon={cilLink} customClassName="nav-icon" />,
  },
  {
    component: CNavItem, name: 'Payments',
    to: '/payments',
    icon: <CIcon icon={cilCreditCard} customClassName="nav-icon" />,
  },
  {
    component: CNavItem, name: 'Certificates',
    to: '/certificates',
    icon: <CIcon icon={cilDescription} customClassName="nav-icon" />,
  },
  {
    component: CNavItem, name: 'Invoices',
    to: '/invoices',
    icon: <CIcon icon={cilMoney} customClassName="nav-icon" />,
  },

  { component: CNavTitle, name: 'Office' },
  {
    component: CNavItem, name: 'Office Projects',
    to: '/office-projects',
    icon: <CIcon icon={cilBuilding} customClassName="nav-icon" />,
  },
  {
    component: CNavItem, name: 'Salary & Payroll',
    to: '/salary',
    icon: <CIcon icon={cilCash} customClassName="nav-icon" />,
    badge: { color: 'warning', text: 'HR' },
  },
  {
    component: CNavItem, name: 'Finance',
    to: '/finance',
    icon: <CIcon icon={cilChartPie} customClassName="nav-icon" />,
  },

  { component: CNavTitle, name: 'Admin' },
  {
    component: CNavItem, name: 'Users',
    to: '/users',
    icon: <CIcon icon={cilUser} customClassName="nav-icon" />,
  },
]

export default _nav