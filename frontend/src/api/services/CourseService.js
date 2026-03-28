// frontend/src/api/services/courseService.js
// RENAME CourseService.js → courseService.js (Linux case-sensitive fix)

import apiClient from '../apiClient'
export const getAllCourses = () => apiClient.get('/course')
export const getCoursesByPage = (page, size) => apiClient.get(`/course/page?page=${page}&size=${size}`)
export const getCoursesByDomain = (id) => apiClient.get(`/course/domainId/${id}`)
export const createCourse = (d) => apiClient.post('/course', d)
export const updateCourse = (id, d) => apiClient.put(`/course/id/${id}`, d)
export const deleteCourse = (id) => apiClient.delete(`/course/id/${id}`)