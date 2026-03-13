import axios from "axios";

const apiClient = axios.create({
    baseURL: import.meta.env.VITE_BACKEND_URL,
    headers: {
        "Content-Type":"application/json",
        // "Authorization": `Bearer ${localStorage.getItem('token')}`
    }
});

apiClient.interceptors.response.use(
    (response) => response,
    (error) => {
        console.error("API Error:", error.response || error.message);
        return Promise.reject(error);        
    }
)

export default apiClient;