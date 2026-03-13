import React, { useEffect, useState } from "react";
import {
    createUser,
    getUsersByPage,
    updateUser,
    deleteUser
} from "../api/services/userService";

import "../styles/modal.css"

import Preloader from "../components/common/Preloader";

import Swal from "sweetalert2";
import Toast from "../utils/toast";
import CIcon from "@coreui/icons-react";
import { cilTrash, cilPen } from "@coreui/icons";

export default function Users() {

    const [loading, setLoading] = useState(false);

    const [users, setUsers] = useState([]);
    const [showModal, setShowModal] = useState(false);

    const ROLE_OPTIONS = ["SUPER_ADMIN", "ADMIN", "SUB_ADMIN", "EMPLOYEE"];
    const EMPLOYMENT_TYPE_OPTIONS = ["FULL_TIME", "PART_TIME"];


    const [form, setForm] = useState({
        name: "",
        email: "",
        phone: "",
        role: "",
        employmentType: "",
        position: "",
        dateOfJoining: "",
        password: ""
    });

    const [editId, setEditId] = useState(null);

    // Pagination
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const size = 5;

    useEffect(() => {
        loadUsers(page);
    }, [page]);

    const loadUsers = async (page) => {
        setLoading(true);
        try {
            const res = await getUsersByPage(page, size);
            setUsers(res.data.content);
            setTotalPages(res.data.totalPages);
        } catch (err) {
            Toast.fire({ icon: "error", title: "Failed to load users" });
        } finally {
            setLoading(false);
        }
    };

    const handleChange = (e) => {
        setForm({ ...form, [e.target.name]: e.target.value });
    };

    // 🔹 ADD / UPDATE
    const handleSave = async () => {

        if (!form.name || !form.email) {
            Toast.fire({ icon: "warning", title: "Name and Email are required" });
            return;
        }

        // Password validation for CREATE
        if (!editId && !form.password) {
            Toast.fire({ icon: "warning", title: "Password is required" });
            return;
        }

        try {
            let payload = { ...form };

            // ❗ On update: if password is empty, remove it from payload
            if (editId && !form.password) {
                delete payload.password;
            }

            if (editId) {
                await updateUser(editId, payload);
                Toast.fire({ icon: "success", title: "User updated" });
            } else {
                await createUser(payload);
                Toast.fire({ icon: "success", title: "User created" });
            }

            resetForm();
            loadUsers(page);

        } catch (err) {
            Toast.fire({ icon: "error", title: "Save failed" });
        }
    };

    // 🔹 EDIT
    const handleEdit = (user) => {
        setEditId(user.id);
        setForm({
            name: user.name || "",
            email: user.email || "",
            phone: user.phone || "",
            role: user.role || "",
            employmentType: user.employmentType || "",
            position: user.position || "",
            dateOfJoining: user.dateOfJoining || ""
        });
        setShowModal(true);
    };

    // 🔹 DELETE
    const handleDelete = async (id) => {
        const result = await Swal.fire({
            title: "Are you sure?",
            text: "This user will be permanently deleted!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#d33",
            cancelButtonColor: "#3085d6",
            confirmButtonText: "Yes, delete!"
        });

        if (!result.isConfirmed) return;

        try {
            await deleteUser(id);
            Toast.fire({ icon: "success", title: "User deleted" });
            loadUsers(page);
        } catch (err) {
            Toast.fire({ icon: "error", title: "Delete failed" });
        }
    };

    const resetForm = () => {
        setForm({
            name: "",
            email: "",
            phone: "",
            role: "",
            employmentType: "",
            position: "",
            dateOfJoining: "",
            password: ""
        });
        setEditId(null);
        setShowModal(false);
    };

    return (
        <div className="container mt-4">

            <Preloader show={loading} />

            {/* Add Button */}
            <div className="mb-3">
                <button className="btn btn-primary" onClick={() => setShowModal(true)}>
                    + Add User
                </button>
            </div>

            {/* Modal */}
            {showModal && (
                <>
                    <div
                        className="modal-backdrop fade show"
                        onClick={resetForm}   // 👈 click outside closes modal
                    ></div>
                    <div className="modal show fade d-block" tabIndex="-1" onClick={resetForm}>
                        <div className="modal-dialog modal-lg" onClick={(e) => e.stopPropagation()}>
                            <div className="modal-content">

                                <div className="modal-header">
                                    <h5 className="modal-title">
                                        {editId ? "Edit User" : "Add User"}
                                    </h5>
                                    <button className="btn-close" onClick={resetForm}></button>
                                </div>

                                <div className="modal-body">
                                    <div className="row g-3">

                                        <div className="col-md-6">
                                            <label>Name</label>
                                            <input name="name" className="form-control" value={form.name} onChange={handleChange} />
                                        </div>

                                        <div className="col-md-6">
                                            <label>Email</label>
                                            <input name="email" className="form-control" value={form.email} onChange={handleChange} />
                                        </div>

                                        <div className="col-md-6">
                                            <label>Phone</label>
                                            <input name="phone" className="form-control" value={form.phone} onChange={handleChange} />
                                        </div>

                                        <div className="col-md-6">
                                            <label>Role</label>
                                            <select
                                                name="role"
                                                className="form-select"
                                                value={form.role}
                                                onChange={handleChange}
                                            >
                                                <option value="">-- Select Role --</option>
                                                {ROLE_OPTIONS.map((role) => (
                                                    <option key={role} value={role}>{role}</option>
                                                ))}
                                            </select>
                                        </div>

                                        <div className="col-md-6">
                                            <label>Employment Type</label>
                                            <select
                                                name="employmentType"
                                                className="form-select"
                                                value={form.employmentType}
                                                onChange={handleChange}
                                            >
                                                <option value="">-- Select Employment Type --</option>
                                                {EMPLOYMENT_TYPE_OPTIONS.map((type) => (
                                                    <option key={type} value={type}>{type}</option>
                                                ))}
                                            </select>
                                        </div>


                                        <div className="col-md-6">
                                            <label>Position</label>
                                            <input name="position" className="form-control" value={form.position} onChange={handleChange} />
                                        </div>

                                        <div className="col-md-6">
                                            <label>Date of Joining</label>
                                            <input type="date" name="dateOfJoining" className="form-control" value={form.dateOfJoining} onChange={handleChange} />
                                        </div>

                                        {!editId &&
                                            <div className="col-md-6">
                                                <label>Password</label>
                                                <input
                                                    type="password"
                                                    name="password"
                                                    className="form-control"
                                                    value={form.password}
                                                    onChange={handleChange}
                                                    placeholder="Enter password"
                                                />
                                            </div>
                                        }

                                    </div>
                                </div>

                                <div className="modal-footer">
                                    <button className="btn btn-secondary" onClick={resetForm}>Cancel</button>
                                    <button className="btn btn-primary" onClick={handleSave}>
                                        {editId ? "Update" : "Save"}
                                    </button>
                                </div>

                            </div>
                        </div>
                    </div>
                </>
            )}

            {/* Table */}
            <table className="table table-bordered">
                <thead>
                    <tr>
                        <th>#</th>
                        <th>User Id</th>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Position</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {users.length === 0 ? (
                        <tr>
                            <td colSpan="6" className="text-center">No users found</td>
                        </tr>
                    ) : (
                        users.map((user, i) => (
                            <tr key={user.id}>
                                <td>{page * size + i + 1}</td>
                                <td>{user.userId}</td>
                                <td>{user.name}</td>
                                <td>{user.email}</td>
                                <td>{user.role}</td>
                                <td>{user.position}</td>
                                <td className="d-flex gap-2">
                                    <button className="btn btn-success btn-sm" onClick={() => handleEdit(user)}>
                                        <CIcon icon={cilPen} />
                                    </button>
                                    <button className="btn btn-danger btn-sm" onClick={() => handleDelete(user.id)}>
                                        <CIcon icon={cilTrash} />
                                    </button>
                                </td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>

            {/* Pagination */}
            {totalPages > 1 && (
                <ul className="pagination">
                    {Array.from({ length: totalPages }, (_, i) => (
                        <li key={i} className={`page-item ${page === i ? "active" : ""}`}>
                            <button className="page-link" onClick={() => setPage(i)}>
                                {i + 1}
                            </button>
                        </li>
                    ))}
                </ul>
            )}

        </div>
    );
}
