import { element } from 'prop-types'
import React from 'react'

const Dashboard = React.lazy(() => import('./views/dashboard/Dashboard'))

const Widgets = React.lazy(() => import('./views/widgets/Widgets'))

const Intern = React.lazy(()=> import('./pages/Intern'))
const Users = React.lazy(()=> import('./pages/Users'))
const Students = React.lazy(()=> import('./pages/Students'))
const Course = React.lazy(()=> import('./pages/Course'))
const Project = React.lazy(()=> import('./pages/Project'))
const Allocation = React.lazy(()=> import('./pages/Allocation'))

const routes = [
  { path: '/', exact: true, name: 'Home' },
  { path: '/dashboard', name: 'Dashboard', element: Dashboard },

  {path: '/intern', name: 'Intern', element: Intern},
  {path: '/users', name: 'Users', element: Users},
  {path: '/students', name: 'Students', element: Students},
  {path: '/course', name: 'course', element: Course},
  {path: '/project', name: 'project', element: Project},
  {path: '/allocate', name: 'allocation', element: Allocation},

  { path: '/widgets', name: 'Widgets', element: Widgets },
]

export default routes
