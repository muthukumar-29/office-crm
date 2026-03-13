import { getAll } from "../crudService";

const DOMAIN_URL = "/domain";

export const getAllDomains = () => getAll(DOMAIN_URL);