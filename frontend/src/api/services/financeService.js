import apiClient from '../apiClient'
export const recordTransaction = (d) => apiClient.post('/finance/transactions', d)
export const getAllTransactions = (params={}) => { const q = new URLSearchParams(params).toString(); return apiClient.get(`/finance/transactions${q ? '?'+q : ''}`) }
export const getFinanceSummary = (s, e) => apiClient.get(s ? `/finance/summary?start=${s}&end=${e}` : '/finance/summary')
