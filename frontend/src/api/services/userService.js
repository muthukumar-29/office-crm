import { create, getByPage, getById, update, remove } from "../crudService";

const USER_URL = "/users";

export const createUser = (data) => create(USER_URL, data);
export const getUsersByPage = (page, size) => getByPage(USER_URL, page, size);
export const getUserById = (id) => getById(USER_URL + "/id", id);
export const updateUser = (id, data) => update(USER_URL + "/id", id, data);
export const deleteUser = (id) => remove(USER_URL + "/id", id);
