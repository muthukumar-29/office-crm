import React, { useEffect, useState } from "react";
import {
  createAllocation,
  getAllocationsByPage,
  updateAllocation,
  deleteAllocation
} from "../api/services/AllocationService";

import { getAllCategories } from "../api/services/categoryService";
import { getAllStudents } from "../api/services/studentService";
import { getAllDomains } from "../api/services/domainService";

import { getInternsByDomain } from "../api/services/internService";
import { getCoursesByDomain } from "../api/services/CourseService";
import { getProjectsByDomain } from "../api/services/projectService";

import CIcon from "@coreui/icons-react";
import { cilTrash, cilPen } from "@coreui/icons";
import Swal from "sweetalert2";
import Toast from "../utils/toast";
import Preloader from "../components/common/Preloader";

export default function Allocation() {

  const [allocations, setAllocations] = useState([]);
  const [categories, setCategories] = useState([]);
  const [students, setStudents] = useState([]);
  const [domains, setDomains] = useState([]);
  const [references, setReferences] = useState([]);   // Intern / Course / Project list

  const [showModal, setShowModal] = useState(false);
  const [loading, setLoading] = useState(false);

  // 🔹 Single Object State
  const [form, setForm] = useState({
    categoryId: "",
    studentId: "",
    domainId: "",
    referenceType: "",   // INTERN / COURSE / PROJECT
    referenceId: "",
    amount: ""
  });

  const [editId, setEditId] = useState(null);

  // Pagination
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 5;

  useEffect(() => {
    fetchAllocations(currentPage);
    fetchDropdowns();
  }, [currentPage]);

  // 🔹 Common Change Handler
  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm(prev => ({
      ...prev,
      [name]: value
    }));
  };

  // 🔹 Fetch Allocations
  const fetchAllocations = async (page) => {
    setLoading(true);
    try {
      const res = await getAllocationsByPage(page, pageSize);
      setAllocations(res.data.content);
      setTotalPages(res.data.totalPages);
    } catch (err) {
      Toast.fire({ icon: "error", title: "Failed to fetch allocations" });
    } finally {
      setLoading(false);
    }
  };

  // 🔹 Fetch Dropdown Data
  const fetchDropdowns = async () => {
    try {
      const [catRes, stuRes, domRes] = await Promise.all([
        getAllCategories(),
        getAllStudents(),
        getAllDomains()
      ]);

      setCategories(catRes.data);
      setStudents(stuRes.data);
      setDomains(domRes.data);
    } catch (err) {
      Toast.fire({ icon: "error", title: "Failed to load dropdown data" });
    }
  };

  // 🔹 When Reference Type Changes
  const handleReferenceTypeChange = (e) => {
    const value = e.target.value;

    setForm(prev => ({
      ...prev,
      referenceType: value,
      referenceId: ""   // reset reference
    }));

    setReferences([]); // clear old references
  };

  // 🔹 When Domain Changes → Auto Load References
  const handleDomainChange = async (e) => {
    const domainId = e.target.value;

    setForm(prev => ({
      ...prev,
      domainId,
      referenceId: ""
    }));

    if (!domainId || !form.referenceType) {
      setReferences([]);
      return;
    }

    try {
      let res;

      if (form.referenceType === "INTERN") {
        res = await getInternsByDomain(domainId);
      } else if (form.referenceType === "COURSE") {
        res = await getCoursesByDomain(domainId);
      } else if (form.referenceType === "PROJECT") {
        res = await getProjectsByDomain(domainId);
      }

      setReferences(res.data);

    } catch (err) {
      Toast.fire({ icon: "error", title: "Failed to load references" });
    }
  };

  // 🔹 Create / Update
  const handleSave = async () => {
    const { categoryId, studentId, domainId, referenceType, referenceId, amount } = form;

    if (!categoryId || !studentId || !domainId || !referenceType || !referenceId || !amount) {
      Toast.fire({ icon: "warning", title: "All fields are required" });
      return;
    }

    setLoading(true);

    const payload = {
      category: { id: categoryId },
      student: { id: studentId },
      domain: { id: domainId },
      referenceId,
      amount
    };

    try {
      if (editId) {
        await updateAllocation(editId, payload);
        Toast.fire({ icon: "success", title: "Allocation updated" });
      } else {
        await createAllocation(payload);
        Toast.fire({ icon: "success", title: "Allocation created" });
      }

      resetForm();
      fetchAllocations(currentPage);

    } catch (err) {
      Toast.fire({ icon: "error", title: "Failed to save allocation" });
    } finally {
      setLoading(false);
    }
  };

  // 🔹 Edit
  const handleEdit = (alloc) => {
    setEditId(alloc.id);

    setForm({
      categoryId: alloc.category?.id || "",
      studentId: alloc.student?.id || "",
      domainId: alloc.domain?.id || "",
      referenceType: "",          // user must reselect type
      referenceId: alloc.referenceId || "",
      amount: alloc.amount || ""
    });

    setReferences([]);
    setShowModal(true);
  };

  // 🔹 Delete
  const handleDelete = async (id) => {
    const result = await Swal.fire({
      title: "Are you sure?",
      text: "This allocation will be permanently deleted!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Yes, delete it!"
    });

    if (!result.isConfirmed) return;

    setLoading(true);

    try {
      await deleteAllocation(id);
      Toast.fire({ icon: "success", title: "Allocation deleted" });
      fetchAllocations(currentPage);
    } catch (err) {
      Toast.fire({ icon: "error", title: "Delete failed" });
    } finally {
      setLoading(false);
    }
  };

  const resetForm = () => {
    setForm({
      categoryId: "",
      studentId: "",
      domainId: "",
      referenceType: "",
      referenceId: "",
      amount: ""
    });
    setReferences([]);
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
          + Add Allocation
        </button>
      </div>

      {/* Modal */}
      {showModal && (
        <>
          {/* Backdrop */}
          <div className="modal-backdrop fade show" onClick={resetForm}></div>

          {/* Modal */}
          <div className="modal fade show d-block" tabIndex="-1" onClick={resetForm}>
            <div className="modal-dialog modal-lg" onClick={(e) => e.stopPropagation()}>
              <div className="modal-content">

                <div className="modal-header">
                  <h5 className="modal-title">
                    {editId ? "Edit Allocation" : "Add Allocation"}
                  </h5>
                  <button className="btn-close" onClick={resetForm}></button>
                </div>

                <div className="modal-body">
                  <div className="row g-3">

                    {/* Category */}
                    <div className="col-md-6">
                      <label>Category</label>
                      <select
                        className="form-select"
                        name="categoryId"
                        value={form.categoryId}
                        onChange={handleChange}
                      >
                        <option value="">-- Select Category --</option>
                        {categories.map(c => (
                          <option key={c.id} value={c.id}>{c.title}</option>
                        ))}
                      </select>
                    </div>

                    {/* Student */}
                    <div className="col-md-6">
                      <label>Student</label>
                      <select
                        className="form-select"
                        name="studentId"
                        value={form.studentId}
                        onChange={handleChange}
                      >
                        <option value="">-- Select Student --</option>
                        {students.map(s => (
                          <option key={s.id} value={s.id}>{s.name}</option>
                        ))}
                      </select>
                    </div>

                    {/* Reference Type */}
                    <div className="col-md-6">
                      <label>Reference Type</label>
                      <select
                        className="form-select"
                        value={form.referenceType}
                        onChange={handleReferenceTypeChange}
                      >
                        <option value="">-- Select Type --</option>
                        <option value="INTERN">Intern</option>
                        <option value="COURSE">Course</option>
                        <option value="PROJECT">Project</option>
                      </select>
                    </div>

                    {/* Domain */}
                    <div className="col-md-6">
                      <label>Domain</label>
                      <select
                        className="form-select"
                        value={form.domainId}
                        onChange={handleDomainChange}
                      >
                        <option value="">-- Select Domain --</option>
                        {domains.map(d => (
                          <option key={d.id} value={d.id}>{d.title}</option>
                        ))}
                      </select>
                    </div>

                    {/* Reference Dropdown */}
                    <div className="col-md-6">
                      <label>Reference</label>
                      <select
                        className="form-select"
                        name="referenceId"
                        value={form.referenceId}
                        onChange={handleChange}
                        disabled={references.length === 0}
                      >
                        <option value="">-- Select Reference --</option>
                        {references.map(ref => (
                          <option key={ref.id} value={ref.id}>
                            {form.referenceType === "INTERN" && ref.title}
                            {form.referenceType === "COURSE" && ref.name}
                            {form.referenceType === "PROJECT" && ref.title}
                          </option>
                        ))}
                      </select>
                    </div>

                    {/* Amount */}
                    <div className="col-md-6">
                      <label>Amount</label>
                      <input
                        type="number"
                        className="form-control"
                        name="amount"
                        value={form.amount}
                        onChange={handleChange}
                      />
                    </div>

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
            <th>Category</th>
            <th>Student</th>
            <th>Domain</th>
            <th>Reference ID</th>
            <th>Amount</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {allocations.length === 0 ? (
            <tr>
              <td colSpan="7" className="text-center">No allocations found</td>
            </tr>
          ) : (
            allocations.map((a, index) => (
              <tr key={a.id}>
                <td>{currentPage * pageSize + index + 1}</td>
                <td>{a.category?.title}</td>
                <td>{a.student?.name}</td>
                <td>{a.domain?.title}</td>
                <td>{a.referenceId}</td>
                <td>₹ {a.amount}</td>
                <td className="d-flex gap-2">
                  <button className="btn btn-success btn-sm" onClick={() => handleEdit(a)}>
                    <CIcon icon={cilPen} />
                  </button>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDelete(a.id)}>
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
