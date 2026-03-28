// frontend/src/api/services/allocationService.js
// RENAME AllocationService.js → allocationService.js (Linux case-sensitive fix)

import apiClient from '../apiClient'

export const getAllAllocations        = ()           => apiClient.get('/allocations')
export const createAllocation         = (d)          => apiClient.post('/allocations', d)
export const getAllocationsByStudent  = (id)         => apiClient.get(`/allocations/student/${id}`)
export const getAllocationsByEmployee = (id)         => apiClient.get(`/allocations/employee/${id}`)
export const updateAllocationStatus  = (id, d)      => apiClient.patch(`/allocations/${id}/status`, d)
export const updateAllocationAssign  = (id, d)      => apiClient.patch(`/allocations/${id}/assign`, d)
export const getCatalogItems         = (cat, did)   => apiClient.get(`/allocations/catalog/items?category=${cat}&domainId=${did}`)