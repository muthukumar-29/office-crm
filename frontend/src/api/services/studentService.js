import apiClient from '../apiClient'
export const getAllStudents = () => apiClient.get('/students')
export const createStudent = (d) => apiClient.post('/students', d)
export const updateStudent = (id, d) => apiClient.put(`/students/${id}`, d)
export const deleteStudent = (id) => apiClient.delete(`/students/${id}`)
export const searchStudents = (q) => apiClient.get(`/students/search?q=${encodeURIComponent(q)}`)
