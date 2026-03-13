import { getAll } from "../crudService";

const CATEGORY_URL = "/category";

export const getAllCategories = () => getAll(CATEGORY_URL);