import React, { useEffect, useState } from "react";
import {
  createProject,
  getProjectsByPage,
  updateProject,
  deleteProject
} from "../api/services/ProjectService";

import { getAllDomains } from "../api/services/domainService";

import CIcon from "@coreui/icons-react";
import { cilTrash, cilPen } from "@coreui/icons";
import Swal from "sweetalert2";
import Toast from "../utils/toast";
import Preloader from "../components/common/Preloader";

export default function Project() {

  const [projects, setProjects] = useState([]);
  const [domains, setDomains] = useState([]);
  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(false);

  const [title, setTitle] = useState("");
  const [domainId, setDomainId] = useState("");
  const [amount, setAmount] = useState("");
  const [editId, setEditId] = useState(null);

  // Pagination
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 5;

  useEffect(() => {
    fetchProjects(currentPage);
    fetchDomains();
  }, [currentPage]);

  // 🔹 Fetch projects
  const fetchProjects = async (page) => {
    setLoading(true);
    try {
      const res = await getProjectsByPage(page, pageSize);
      setProjects(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      Toast.fire({ icon: "error", title: "Failed to fetch projects" });
    } finally {
      setLoading(false);
    }
  };

  // 🔹 Fetch domains
  const fetchDomains = async () => {
    try {
      const res = await getAllDomains();
      setDomains(res.data);
    } catch (err) {
      Toast.fire({ icon: "error", title: "Failed to load domains" });
    }
  };

  // 🔹 Create / Update
  const handleSave = async () => {
    if (!title || !domainId || !amount) {
      Toast.fire({ icon: "warning", title: "All fields are required" });
      return;
    }

    setLoading(true);

    const payload = {
      title,
      domain: { id: domainId },
      amount
    };

    try {
      if (editId) {
        await updateProject(editId, payload);
        Toast.fire({ icon: "success", title: "Project updated" });
      } else {
        await createProject(payload);
        Toast.fire({ icon: "success", title: "Project created" });
      }

      resetForm();
      fetchProjects(currentPage);

    } catch (err) {
      Toast.fire({ icon: "error", title: "Failed to save project" });
    } finally {
      setLoading(false);
    }
  };

  // 🔹 Edit
  const handleEdit = (project) => {
    setEditId(project.id);
    setTitle(project.title);
    setDomainId(project.domain?.id || "");
    setAmount(project.amount);
    setShowModal(true);
  };

  // 🔹 Delete
  const handleDelete = async (id) => {
    const result = await Swal.fire({
      title: "Are you sure?",
      text: "This project will be permanently deleted!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      confirmButtonText: "Yes, delete it!"
    });

    if (!result.isConfirmed) return;

    setLoading(true);

    try {
      await deleteProject(id);
      Toast.fire({ icon: "success", title: "Project deleted" });
      fetchProjects(currentPage);
    } catch (err) {
      Toast.fire({ icon: "error", title: "Delete failed" });
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setTitle("");
    setDomainId("");
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
          + Add Project
        </button>
      </div>

      {/* Modal */}
      {showModal && (
        <>
          {/* Backdrop */}
          <div
            className="modal-backdrop fade show"
            onClick={resetForm}
          ></div>

          {/* Modal */}
          <div
            className="modal fade show d-block"
            tabIndex="-1"
            onClick={resetForm}
          >
            <div
              className="modal-dialog"
              onClick={(e) => e.stopPropagation()}
            >
              <div className="modal-content">

                <div className="modal-header">
                  <h5 className="modal-title">
                    {editId ? "Edit Project" : "Add Project"}
                  </h5>
                  <button className="btn-close" onClick={resetForm}></button>
                </div>

                <div className="modal-body">

                  <div className="mb-3">
                    <label className="form-label">Title</label>
                    <input
                      className="form-control"
                      value={title}
                      onChange={(e) => setTitle(e.target.value)}
                    />
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Domain</label>
                    <select
                      className="form-select"
                      value={domainId}
                      onChange={(e) => setDomainId(e.target.value)}
                    >
                      <option value="">-- Select Domain --</option>
                      {domains.map((d) => (
                        <option key={d.id} value={d.id}>
                          {d.title}
                        </option>
                      ))}
                    </select>
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
                  <button className="btn btn-secondary" onClick={resetForm}>
                    Cancel
                  </button>
                  <button
                    className="btn btn-primary"
                    disabled={loading}
                    onClick={handleSave}
                  >
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
            <th>Title</th>
            <th>Domain</th>
            <th>Amount</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {projects.length === 0 ? (
            <tr>
              <td colSpan="5" className="text-center">No projects found</td>
            </tr>
          ) : (
            projects.map((project, index) => (
              <tr key={project.id}>
                <td>{currentPage * pageSize + index + 1}</td>
                <td>{project.title}</td>
                <td>{project.domain?.title}</td>
                <td>₹ {project.amount}</td>
                <td className="d-flex gap-2">
                  <button className="btn btn-success btn-sm" onClick={() => handleEdit(project)}>
                    <CIcon icon={cilPen} />
                  </button>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDelete(project.id)}>
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
