import { create, getByPage, getById, update, remove, getAll } from "../crudService";

const STUDENT_URL = "/students";

export const createStudent = (data) => create(STUDENT_URL, data);
export const getAllStudents = () => getAll(STUDENT_URL);
export const getStudentsByPage = (page, size) => getByPage(STUDENT_URL, page, size);
export const getStudentById = (id) => getById(STUDENT_URL + "/id", id);
export const updateStudent = (id, data) => update(STUDENT_URL + "/id", id, data);
export const deleteStudent = (id) => remove(STUDENT_URL + "id", id);
