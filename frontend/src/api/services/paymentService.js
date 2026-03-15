import apiClient from '../apiClient'
export const recordPayment = (d) => apiClient.post('/payments', d)
export const getPaymentsByAllocation = (id) => apiClient.get(`/payments/allocation/${id}`)
export const getPaymentsByDateRange = (s, e) => apiClient.get(`/payments?start=${s}&end=${e}`)
