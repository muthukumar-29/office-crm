import apiClient from "./apiClient";

//create
export const create = (url, data) =>{
    return apiClient.post(url, data);
}

// Get all
export const getAll = (url) => {
  return apiClient.get(url);
};

// Get by ID
export const getById = (url, id) => {
  return apiClient.get(`${url}/${id}`);
};

// Get with pagination
export const getByPage = (url, page, size) => {
  return apiClient.get(`${url}/page?page=${page}&size=${size}`);
};

// Update
export const update = (url, id, data) => {
  return apiClient.put(`${url}/${id}`, data);
};

// Delete
export const remove = (url, id) => {
  return apiClient.delete(`${url}/${id}`);
};
