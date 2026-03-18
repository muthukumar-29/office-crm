// src/routes.js — REPLACE
import React from 'react'

const Dashboard = React.lazy(() => import('./pages/Dashboard'))
const Students = React.lazy(() => import('./pages/Students'))
const Users = React.lazy(() => import('./pages/Users'))
const Courses = React.lazy(() => import('./pages/Courses'))
const Interns = React.lazy(() => import('./pages/Interns'))
const Projects = React.lazy(() => import('./pages/Projects'))
const Allocations = React.lazy(() => import('./pages/Allocations'))
const Payments = React.lazy(() => import('./pages/Payments'))
const Certificates = React.lazy(() => import('./pages/Certificates'))
const Invoices = React.lazy(() => import('./pages/Invoices'))
const OfficeProjects = React.lazy(() => import('./pages/OfficeProjects'))
const Salary = React.lazy(() => import('./pages/Salary'))
const Finance = React.lazy(() => import('./pages/Finance'))

const routes = [
  { path: '/', name: 'Home' },
  { path: '/dashboard', name: 'Dashboard', element: Dashboard },
  { path: '/students', name: 'Students', element: Students },
  { path: '/users', name: 'Users', element: Users },
  { path: '/courses', name: 'Courses', element: Courses },
  { path: '/interns', name: 'Internships', element: Interns },
  { path: '/projects', name: 'Projects', element: Projects },
  { path: '/allocations', name: 'Allocations', element: Allocations },
  { path: '/payments', name: 'Payments', element: Payments },
  { path: '/certificates', name: 'Certificates', element: Certificates },
  { path: '/invoices', name: 'Invoices', element: Invoices },
  { path: '/office-projects', name: 'Office Projects', element: OfficeProjects },
  { path: '/salary', name: 'Salary & Payroll', element: Salary },
  { path: '/finance', name: 'Finance', element: Finance },
]

export default routes
