import apiClient from '../apiClient'
export const getAllUsers = () => apiClient.get('/users')
export const createUser = (d) => apiClient.post('/auth/register', d)
export const updateUser = (id, d) => apiClient.put(`/users/id/${id}`, d)
export const deleteUser = (id) => apiClient.delete(`/users/id/${id}`)
