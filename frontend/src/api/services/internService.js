import { create, getAll, getById, getByPage, update, remove } from "../crudService";

const INTERN_URL = "/intern";

export const createIntern = (data) => create(INTERN_URL, data);
export const getAllInterns = () => getAll(INTERN_URL);
export const getInternById = (id) => getById(INTERN_URL+"/id", id);
export const getInternsByDomain = (id) => getById(INTERN_URL+"/domainId", id);
export const getInternsByPage = (page, size) => getByPage(INTERN_URL, page, size);
export const updateIntern = (id, data) => update(INTERN_URL+"/id", id, data);
export const deleteIntern = (id) => remove(INTERN_URL+"/id", id);
