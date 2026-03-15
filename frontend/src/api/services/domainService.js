import apiClient from '../apiClient'
export const getAllDomains = () => apiClient.get('/domain')
export const createDomain = (d) => apiClient.post('/domain', d)
export const updateDomain = (id, d) => apiClient.put(`/domain/id/${id}`, d)
