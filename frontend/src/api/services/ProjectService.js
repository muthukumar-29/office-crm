import apiClient from '../apiClient'
export const getAllProjects = () => apiClient.get('/project')
export const getProjectsByDomain = (id) => apiClient.get(`/project/domainId/${id}`)
export const createProject = (d) => apiClient.post('/project', d)
export const updateProject = (id, d) => apiClient.put(`/project/id/${id}`, d)
export const deleteProject = (id) => apiClient.delete(`/project/id/${id}`)
