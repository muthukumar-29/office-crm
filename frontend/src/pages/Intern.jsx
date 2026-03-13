import React, { useEffect, useState } from 'react';
import {
  createIntern,
  getInternsByPage,
  updateIntern,
  deleteIntern
} from '../api/services/internService';

import { getAllDomains } from '../api/services/domainService';

import Preloader from '../components/common/Preloader';

import CIcon from '@coreui/icons-react'
import { cilTrash, cilPen } from '@coreui/icons';
import Swal from 'sweetalert2';
import Toast from "../utils/toast";

export default function Intern() {

  const [loading, setLoading] = useState(false);

  const [interns, setInterns] = useState([]);
  const [domains, setDomains] = useState([]);
  const [showModal, setShowModal] = useState(false);

  const [title, setTitle] = useState('');
  const [domainId, setDomainId] = useState('');
  const [duration, setDuration] = useState('');
  const [amount, setAmount] = useState('');

  const [editId, setEditId] = useState(null);

  // Pagination
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const pageSize = 5;

  useEffect(() => {
    fetchInterns(currentPage);
    fetchDomains();
  }, [currentPage]);

  // 🔹 Fetch Interns (Paginated)
  const fetchInterns = async (page) => {
    setLoading(true);
    try {
      const res = await getInternsByPage(page, pageSize);
      setInterns(res.data.content);
      setTotalPages(res.data.totalPages);

    } catch (err) {
      Toast.fire({ icon: 'error', title: 'Failed to fetch interns' });
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // 🔹 Fetch Domains for Dropdown
  const fetchDomains = async () => {
    setLoading(true);
    try {
      const res = await getAllDomains();
      setDomains(res.data);
    } catch (err) {
      Toast.fire({ icon: 'error', title: 'Failed to load domains' });
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // 🔹 ADD / UPDATE
  const handleSave = async () => {
    if (!title || !domainId || !duration || !amount) {
      Toast.fire({
        icon: 'warning',
        title: 'All fields are required'
      });
      return;
    }

    const payload = {
      title,
      domain: { id: domainId },   // 👈 Important for ManyToOne
      duration,
      amount
    };

    try {
      if (editId) {
        await updateIntern(editId, payload);
        Toast.fire({ icon: 'success', title: 'Intern updated successfully' });
      } else {
        await createIntern(payload);
        Toast.fire({ icon: 'success', title: 'Intern added successfully' });
      }

      resetForm();
      fetchInterns(currentPage);

    } catch (err) {
      Toast.fire({ icon: 'error', title: 'Failed to save intern' });
      console.error(err);
    }
  };

  // 🔹 EDIT
  const handleEdit = (intern) => {
    setEditId(intern.id);
    setTitle(intern.title);
    setDomainId(intern.domain?.id || '');
    setDuration(intern.duration);
    setAmount(intern.amount);
    setShowModal(true);
  };

  // 🔹 DELETE
  const handleDelete = async (id) => {
    const result = await Swal.fire({
      title: "Are you sure?",
      text: "This intern record will be permanently deleted!",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#d33",
      cancelButtonColor: "#3085d6",
      confirmButtonText: "Yes, delete it!"
    });

    if (!result.isConfirmed) return;

    try {
      await deleteIntern(id);
      Toast.fire({ icon: 'success', title: 'Intern deleted' });
      fetchInterns(currentPage);
    } catch (err) {
      Toast.fire({ icon: 'error', title: 'Failed to delete intern' });
      console.error(err);
    }
  };

  const resetForm = () => {
    setTitle('');
    setDomainId('');
    setDuration('');
    setAmount('');
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
          onClick={() => { resetForm(); setShowModal(true); }}
        >
          + Add Intern
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
            <div className="modal-dialog" onClick={(e) => e.stopPropagation()}>
              <div className="modal-content">

                <div className="modal-header">
                  <h5 className="modal-title">
                    {editId ? "Edit Intern" : "Add Intern"}
                  </h5>
                  <button className="btn-close" onClick={resetForm}></button>
                </div>

                <div className="modal-body">

                  <div className="mb-3">
                    <label className="form-label">Title</label>
                    <input
                      type="text"
                      className="form-control"
                      value={title}
                      onChange={(e) => setTitle(e.target.value)}
                    />
                  </div>

                  {/* Domain Dropdown */}
                  <div className="mb-3">
                    <label className="form-label">Domain</label>
                    <select
                      className="form-select"
                      value={domainId}
                      onChange={(e) => setDomainId(e.target.value)}
                    >
                      <option value="">-- Select Domain --</option>
                      {domains.map((domain) => (
                        <option key={domain.id} value={domain.id}>
                          {domain.title}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="mb-3">
                    <label className="form-label">Duration</label>
                    <input
                      type="text"
                      className="form-control"
                      value={duration}
                      onChange={(e) => setDuration(e.target.value)}
                      placeholder="e.g. 3 Months"
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
            <th>Title</th>
            <th>Domain</th>
            <th>Duration</th>
            <th>Amount</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          {interns.length === 0 ? (
            <tr>
              <td colSpan="6" className="text-center">No interns found</td>
            </tr>
          ) : (
            interns.map((intern, index) => (
              <tr key={intern.id}>
                <td>{currentPage * pageSize + index + 1}</td>
                <td>{intern.title}</td>
                <td>{intern.domain?.title}</td>
                <td>{intern.duration}</td>
                <td>₹ {intern.amount}</td>
                <td className='d-flex gap-2'>
                  <button
                    className='btn btn-success btn-sm'
                    onClick={() => handleEdit(intern)}
                  >
                    <CIcon icon={cilPen} />
                  </button>
                  <button
                    className='btn btn-danger btn-sm'
                    onClick={() => handleDelete(intern.id)}
                  >
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
        <nav>
          <ul className="pagination">
            {Array.from({ length: totalPages }, (_, i) => (
              <li key={i} className={`page-item ${currentPage === i ? 'active' : ''}`}>
                <button className="page-link" onClick={() => setCurrentPage(i)}>
                  {i + 1}
                </button>
              </li>
            ))}
          </ul>
        </nav>
      )}

    </div>
  );
}
