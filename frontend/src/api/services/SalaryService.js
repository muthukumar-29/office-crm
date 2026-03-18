import apiClient from '../apiClient'

export const getAllSalaries    = ()     => apiClient.get('/salary')
export const getSalaryById     = (id)   => apiClient.get(`/salary/${id}`)
export const getMyPayslips     = (empId) => apiClient.get(`/salary/employee/${empId}`)
export const createSalary      = (d)    => apiClient.post('/salary', d)
export const markSalaryPaid    = (id)   => apiClient.patch(`/salary/${id}/pay`)
export const getPayslipHtml    = (id)   => apiClient.get(`/salary/${id}/payslip`, { responseType: 'text' })
