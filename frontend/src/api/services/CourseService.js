import { create, getByPage, update, remove, getById } from "../crudService";

const COURSE_URL = "/course";

export const createCourse = (data) => create(COURSE_URL, data);
export const getCoursesByPage = (page, size) => getByPage(COURSE_URL, page, size);
export const getCourseById = (id) => getById(COURSE_URL+"/id", id);
export const getCoursesByDomain = (id) => getById(COURSE_URL+"/domainId", id);
export const updateCourse = (id, data) => update(COURSE_URL+"/id", id, data);
export const deleteCourse = (id) => remove(COURSE_URL+"/id", id);
