import React, { useEffect, useState } from "react";
import {
  createStudent,
  getStudentsByPage,
  updateStudent,
  deleteStudent
} from "../api/services/studentService";

import Preloader from "../components/common/Preloader";

import Swal from "sweetalert2";
import Toast from "../utils/toast";
import CIcon from "@coreui/icons-react";
import { cilTrash, cilPen } from "@coreui/icons";

export default function Students() {

  const [loading, setLoading] = useState(false);

  const [students, setStudents] = useState([]);
  const [showModal, setShowModal] = useState(false);

  const [form, setForm] = useState({
    name: "",
    collegeName: "",
    rollNumber: "",
    phoneNumber: "",
    email: ""
  });

  const [editId, setEditId] = useState(null);

  // Pagination
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const size = 5;

  useEffect(() => {
    loadStudents(page);
  }, [page]);

  const loadStudents = async (page) => {
    setLoading(true)
    try {
      const res = await getStudentsByPage(page, size);
      setStudents(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      Toast.fire({ icon: "error", title: "Failed to load students" });
    } finally {
      setLoading(false)
    }
  };

  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  // 🔹 ADD / UPDATE
  const handleSave = async () => {

    if (!form.name || !form.email) {
      Toast.fire({ icon: "warning", title: "Name & Email are required" });
      return;
    }

    try {
      if (editId) {
        await updateStudent(editId, form);
        Toast.fire({ icon: "success", title: "Student updated" });
      } else {
        await createStudent(form);
        Toast.fire({ icon: "success", title: "Student created" });
      }

      resetForm();
      loadStudents(page);

    } catch (err) {
      Toast.fire({ icon: "error", title: "Save failed" });
    }
  };

  // 🔹 EDIT
  const handleEdit = (student) => {
    setEditId(student.id);
    setForm({
      name: student.name || "",
      collegeName: student.collegeName || "",
      rollNumber: student.rollNumber || "",
      phoneNumber: student.phoneNumber || "",
      email: student.email || ""
    });
    setShowModal(true);
  };

  // 🔹 DELETE
  const handleDelete = async (id) => {
    const result = await Swal.fire({
      title: "Are you sure?",
      text: "This student will be permanently deleted!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Yes, delete!"
    });

    if (!result.isConfirmed) return;

    try {
      await deleteStudent(id);
      Toast.fire({ icon: "success", title: "Student deleted" });
      loadStudents(page);
    } catch (err) {
      Toast.fire({ icon: "error", title: "Delete failed" });
    }
  };

  const resetForm = () => {
    setForm({
      name: "",
      collegeName: "",
      rollNumber: "",
      phoneNumber: "",
      email: ""
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
          + Add Student
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
                    {editId ? "Edit Student" : "Add Student"}
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
                      <label>College Name</label>
                      <input name="collegeName" className="form-control" value={form.collegeName} onChange={handleChange} />
                    </div>

                    <div className="col-md-6">
                      <label>Roll Number</label>
                      <input name="rollNumber" className="form-control" value={form.rollNumber} onChange={handleChange} />
                    </div>

                    <div className="col-md-6">
                      <label>Phone Number</label>
                      <input name="phoneNumber" className="form-control" value={form.phoneNumber} onChange={handleChange} />
                    </div>

                    <div className="col-md-6">
                      <label>Email</label>
                      <input name="email" type="email" className="form-control" value={form.email} onChange={handleChange} />
                    </div>

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
            <th>Student ID</th>
            <th>Name</th>
            <th>College</th>
            <th>Roll No</th>
            <th>Email</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {students.length === 0 ? (
            <tr>
              <td colSpan="7" className="text-center">No students found</td>
            </tr>
          ) : (
            students.map((student, i) => (
              <tr key={student.id}>
                <td>{page * size + i + 1}</td>
                <td>{student.studentId}</td>
                <td>{student.name}</td>
                <td>{student.collegeName}</td>
                <td>{student.rollNumber}</td>
                <td>{student.email}</td>
                <td className="d-flex gap-2">
                  <button className="btn btn-success btn-sm" onClick={() => handleEdit(student)}>
                    <CIcon icon={cilPen} />
                  </button>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDelete(student.id)}>
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
