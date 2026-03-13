import React, { useEffect, useState } from "react";
import {
    createCourse,
    getCoursesByPage,
    updateCourse,
    deleteCourse
} from "../api/services/CourseService";

import "../styles/modal.css"

import CIcon from "@coreui/icons-react";
import { cilTrash, cilPen } from "@coreui/icons";
import Swal from "sweetalert2";
import Toast from "../utils/toast";
import Preloader from "../components/common/Preloader";

export default function Course() {

    const [courses, setCourses] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [loading, setLoading] = useState(false);

    const [courseId, setCourseId] = useState("");
    const [name, setName] = useState("");
    const [duration, setDuration] = useState("");
    const [amount, setAmount] = useState("");

    const [editId, setEditId] = useState(null);

    // Pagination
    const [currentPage, setCurrentPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const pageSize = 5;

    useEffect(() => {
        fetchCourses(currentPage);
    }, [currentPage]);

    const generateCourseId = (title) => {
        if (!title || title.length < 2) return "";

        const prefix = title.substring(0, 2).toUpperCase();
        const randomNumber = Math.floor(1000 + Math.random() * 9000); // 4-digit
        return `CO-${prefix}-${randomNumber}`;
    };

    // 🔹 Fetch Courses
    const fetchCourses = async (page) => {
        setLoading(true);
        try {
            const res = await getCoursesByPage(page, pageSize);
            setCourses(res.data.content);
            setTotalPages(res.data.totalPages);
        } catch (err) {
            Toast.fire({ icon: "error", title: "Failed to load courses" });
        } finally {
            setLoading(false);
        }
    };

    // 🔹 Save (Create / Update)
    const handleSave = async () => {
        if (!courseId || !name || !duration || !amount) {
            Toast.fire({ icon: "warning", title: "All fields are required" });
            return;
        }

        setLoading(true);

        const payload = { courseId, name, duration, amount };

        try {
            if (editId) {
                await updateCourse(editId, payload);
                Toast.fire({ icon: "success", title: "Course updated" });
            } else {
                await createCourse(payload);
                Toast.fire({ icon: "success", title: "Course created" });
            }

            resetForm();
            fetchCourses(currentPage);

        } catch (err) {
            Toast.fire({ icon: "error", title: "Failed to save course" });
        } finally {
            setLoading(false);
        }
    };

    // 🔹 Edit
    const handleEdit = (course) => {
        setEditId(course.id);
        setCourseId(course.courseId);
        setName(course.name);
        setDuration(course.duration);
        setAmount(course.amount);
        setShowModal(true);
    };

    // 🔹 Delete
    const handleDelete = async (id) => {
        const result = await Swal.fire({
            title: "Are you sure?",
            text: "This course will be permanently deleted!",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#d33",
            confirmButtonText: "Yes, delete it!"
        });

        if (!result.isConfirmed) return;

        setLoading(true);

        try {
            await deleteCourse(id);
            Toast.fire({ icon: "success", title: "Course deleted" });
            fetchCourses(currentPage);
        } catch (err) {
            Toast.fire({ icon: "error", title: "Delete failed" });
        } finally {
            setLoading(false);
        }
    };

    const resetForm = () => {
        setCourseId("");
        setName("");
        setDuration("");
        setAmount("");
        setEditId(null);
        setShowModal(false);
    };

    return (
        <div className="container mt-4">

            <Preloader show={loading} />

            {/* Add Button */}
            <div className="mb-3">
                <button
                    className="btn btn-primary"
                    disabled={loading}
                    onClick={() => { resetForm(); setShowModal(true); }}
                >
                    + Add Course
                </button>
            </div>

            {/* Modal */}
            {showModal && (
                <>

                    <div className="modal show fade d-block" tabIndex="-1" onClick={resetForm}>
                        <div className="modal-dialog" onClick={(e) => e.stopPropagation()}>
                            <div className="modal-content">

                                <div className="modal-header">
                                    <h5 className="modal-title">
                                        {editId ? "Edit Course" : "Add Course"}
                                    </h5>
                                    <button className="btn-close" onClick={resetForm}></button>
                                </div>

                                <div className="modal-body">

                                    <div className="mb-3">
                                        <label className="form-label">Course ID</label>
                                        <input
                                            className="form-control"
                                            value={courseId}
                                            readOnly
                                        />
                                    </div>

                                    <div className="mb-3">
                                        <label className="form-label">Course Name</label>
                                        <input
                                            className="form-control"
                                            value={name}
                                            onChange={(e) => {
                                                const value = e.target.value;
                                                setName(value);

                                                // 🔹 Generate Course ID only when creating (not editing)
                                                if (!editId) {
                                                    const newCourseId = generateCourseId(value);
                                                    setCourseId(newCourseId);
                                                }
                                            }}
                                        />
                                    </div>


                                    <div className="mb-3">
                                        <label className="form-label">Duration</label>
                                        <input
                                            className="form-control"
                                            value={duration}
                                            onChange={(e) => setDuration(e.target.value)}
                                            placeholder="e.g. 6 Months"
                                        />
                                    </div>

                                    <div className="mb-3">
                                        <label className="form-label">Amount</label>
                                        <input
                                            type="number"
                                            className="form-control"
                                            value={amount}
                                            onChange={(e) => setAmount(e.target.value)}
                                        />
                                    </div>

                                </div>

                                <div className="modal-footer">
                                    <button className="btn btn-secondary" onClick={resetForm}>Cancel</button>
                                    <button className="btn btn-primary" disabled={loading} onClick={handleSave}>
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
                        <th>Course ID</th>
                        <th>Name</th>
                        <th>Duration</th>
                        <th>Amount</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {courses.length === 0 ? (
                        <tr>
                            <td colSpan="6" className="text-center">No courses found</td>
                        </tr>
                    ) : (
                        courses.map((course, index) => (
                            <tr key={course.id}>
                                <td>{currentPage * pageSize + index + 1}</td>
                                <td>{course.courseId}</td>
                                <td>{course.name}</td>
                                <td>{course.duration}</td>
                                <td>₹ {course.amount}</td>
                                <td className="d-flex gap-2">
                                    <button className="btn btn-success btn-sm" onClick={() => handleEdit(course)}>
                                        <CIcon icon={cilPen} />
                                    </button>
                                    <button className="btn btn-danger btn-sm" onClick={() => handleDelete(course.id)}>
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
                        <li key={i} className={`page-item ${currentPage === i ? "active" : ""}`}>
                            <button className="page-link" onClick={() => setCurrentPage(i)}>
                                {i + 1}
                            </button>
                        </li>
                    ))}
                </ul>
            )}

        </div>
    );
}
