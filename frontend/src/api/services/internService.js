import apiClient from '../apiClient'
export const getAllInterns = () => apiClient.get('/intern')
export const getInternsByDomain = (id) => apiClient.get(`/intern/domainId/${id}`)
export const createIntern = (d) => apiClient.post('/intern', d)
export const updateIntern = (id, d) => apiClient.put(`/intern/id/${id}`, d)
export const deleteIntern = (id) => apiClient.delete(`/intern/id/${id}`)
