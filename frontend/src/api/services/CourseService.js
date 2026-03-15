import apiClient from '../apiClient'
export const getAllCourses = () => apiClient.get('/course')
export const getCoursesByDomain = (id) => apiClient.get(`/course/domainId/${id}`)
export const createCourse = (d) => apiClient.post('/course', d)
export const updateCourse = (id, d) => apiClient.put(`/course/id/${id}`, d)
export const deleteCourse = (id) => apiClient.delete(`/course/id/${id}`)
