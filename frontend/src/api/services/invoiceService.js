import apiClient from '../apiClient'
export const createInvoice = (d) => apiClient.post('/invoices', d)
export const getAllInvoices = () => apiClient.get('/invoices')
export const downloadInvoice = (id) => apiClient.get(`/invoices/${id}/download`, { responseType: 'blob' })
