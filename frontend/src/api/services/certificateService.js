import apiClient from '../apiClient'
export const previewCertificate = (id) => apiClient.get(`/certificates/preview/${id}`)
export const issueCertificate = (d) => apiClient.post('/certificates', d)
export const getAllCertificates = () => apiClient.get('/certificates')
export const downloadCertificate = (id) => apiClient.get(`/certificates/download/${id}`, { responseType: 'blob' })
