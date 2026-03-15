import apiClient from '../apiClient'
export const getAllOfficeProjects = (s) => apiClient.get(s ? `/office-projects?status=${s}` : '/office-projects')
export const createOfficeProject = (d) => apiClient.post('/office-projects', d)
export const updateOfficeProject = (id, d) => apiClient.put(`/office-projects/${id}`, d)
export const updateOfficeStatus = (id, s) => apiClient.patch(`/office-projects/${id}/status?status=${s}`)
export const addProjectMember = (id, d) => apiClient.post(`/office-projects/${id}/members`, d)
export const removeProjectMember = (id, uid) => apiClient.delete(`/office-projects/${id}/members/${uid}`)
