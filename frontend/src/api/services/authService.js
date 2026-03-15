import apiClient from '../apiClient'
export const loginApi = (d) => apiClient.post('/auth/login', d)
export const registerApi = (d) => apiClient.post('/auth/register', d)
