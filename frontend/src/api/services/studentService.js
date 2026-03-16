// src/api/services/studentService.js — REPLACE existing file
import apiClient from '../apiClient'

export const getAllStudents  = ()        => apiClient.get('/students')
export const getStudentsByPage = (page, size) => apiClient.get(`/students/page?page=${page}&size=${size}`)
export const getStudentById = (id)      => apiClient.get(`/students/${id}`)

// ✅ Fixed: was /students/id/{id} → now /students/{id}  (matches StudentController)
export const createStudent  = (data)    => apiClient.post('/students', data)
export const updateStudent  = (id, data) => apiClient.put(`/students/${id}`, data)

// ✅ Fixed typo: was /students+id/{id} (missing slash) → now /students/{id}
export const deleteStudent  = (id)      => apiClient.delete(`/students/${id}`)

export const searchStudents = (q)       => apiClient.get(`/students/search?q=${encodeURIComponent(q)}`)