import axios from 'axios'
const apiClient = axios.create({ baseURL: import.meta.env.VITE_BACKEND_URL, headers: { 'Content-Type': 'application/json' } })
apiClient.interceptors.request.use(config => { const t = localStorage.getItem('crm_token'); if (t) config.headers.Authorization = `Bearer ${t}`; return config })
apiClient.interceptors.response.use(r => r, err => { if (err.response?.status === 401) { localStorage.removeItem('crm_token'); localStorage.removeItem('crm_user'); window.location.hash = '#/login' } return Promise.reject(err) })
export default apiClient
