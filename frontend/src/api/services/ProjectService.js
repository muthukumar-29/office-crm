import { create, getByPage, update, remove, getById } from "../crudService";

const PROJECT_URL = "/project";

export const createProject = (data) => create(PROJECT_URL, data);
export const getProjectById = (id) => getById(PROJECT_URL + "/id", id);
export const getProjectsByDomain = (id) => getById(PROJECT_URL+"/domainId", id);
export const getProjectsByPage = (page, size) => getByPage(PROJECT_URL, page, size);
export const updateProject = (id, data) => update(PROJECT_URL + "/id", id, data);
export const deleteProject = (id) => remove(PROJECT_URL, id);