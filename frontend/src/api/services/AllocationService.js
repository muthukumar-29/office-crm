import { create, getByPage, update, remove, getById } from "../crudService";

const ALLOCATION_URL = "/allocation";

export const createAllocation = (data) => create(ALLOCATION_URL, data);
export const getAllocationById = (id) => getById(ALLOCATION_URL, id);
export const getAllocationsByPage = (page, size) => getByPage(ALLOCATION_URL, page, size);
export const updateAllocation = (id, data) => update(ALLOCATION_URL, id, data);
export const deleteAllocation = (id) => remove(ALLOCATION_URL, id);
