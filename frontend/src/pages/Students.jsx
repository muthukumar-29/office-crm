// src/pages/Students.jsx — REPLACE
import React, { useEffect, useState, useCallback } from 'react'
import { getAllStudents, createStudent, updateStudent, deleteStudent, searchStudents } from '../api/services/studentService'
import { Preloader, CrmPagination, CrmModal, PageHeader, Field, Input, EmptyState } from '../components/common/ui'
import { usePagination } from '../utils/usePagination'
import Toast from '../utils/toast'
import Swal from 'sweetalert2'

const EMPTY = { name: '', rollNo: '', email: '', phone: '', collegeName: '', department: '', year: '', address: '', guardianName: '', guardianPhone: '' }

export default function Students() {
  const [all, setAll]           = useState([])
  const [loading, setLoading]   = useState(false)
  const [showModal, setShowModal] = useState(false)
  const [editId, setEditId]     = useState(null)
  const [form, setForm]         = useState(EMPTY)
  const [search, setSearch]     = useState('')
  const { page, setPage, totalPages, pageData, reset } = usePagination(all, 10)

  const fetchAll = useCallback(async () => {
    setLoading(true)
    try {
      const res = await getAllStudents()
      setAll(res.data?.data || res.data || []); reset()
    } catch { Toast.fire({ icon: 'error', title: 'Failed to load students' }) }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { fetchAll() }, [fetchAll])

  const doSearch = async () => {
    if (!search.trim()) { fetchAll(); return }
    setLoading(true)
    try { const res = await searchStudents(search); setAll(res.data?.data || res.data || []); reset() }
    catch { Toast.fire({ icon: 'error', title: 'Search failed' }) }
    finally { setLoading(false) }
  }

  const ch = e => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSave = async () => {
    if (!form.name || !form.email) { Toast.fire({ icon: 'warning', title: 'Name and Email required' }); return }
    setLoading(true)
    try {
      if (editId) { await updateStudent(editId, form); Toast.fire({ icon: 'success', title: 'Student updated' }) }
      else        { await createStudent(form);          Toast.fire({ icon: 'success', title: 'Student created' }) }
      reset_form(); fetchAll()
    } catch (err) { Toast.fire({ icon: 'error', title: err.response?.data?.message || 'Save failed' }) }
    finally { setLoading(false) }
  }

  const handleDelete = async (id) => {
    const ok = await Swal.fire({ title: 'Delete student?', text: 'This action cannot be undone.', icon: 'warning', showCancelButton: true, confirmButtonColor: '#ef4444', confirmButtonText: 'Delete', background: 'var(--surface-2)', color: 'var(--text-primary)' })
    if (!ok.isConfirmed) return
    try { await deleteStudent(id); Toast.fire({ icon: 'success', title: 'Deleted' }); fetchAll() }
    catch { Toast.fire({ icon: 'error', title: 'Delete failed' }) }
  }

  const reset_form = () => { setForm(EMPTY); setEditId(null); setShowModal(false) }
  const openEdit   = (s) => { setEditId(s.id); setForm({ name: s.name||'', rollNo: s.rollNo||'', email: s.email||'', phone: s.phone||'', collegeName: s.collegeName||'', department: s.department||'', year: s.year||'', address: s.address||'', guardianName: s.guardianName||'', guardianPhone: s.guardianPhone||'' }); setShowModal(true) }

  const FIELDS = [
    { l: 'Full Name *', n: 'name' }, { l: 'Roll No', n: 'rollNo' },
    { l: 'Email *', n: 'email', t: 'email' }, { l: 'Phone', n: 'phone' },
    { l: 'College Name', n: 'collegeName' }, { l: 'Department', n: 'department' },
    { l: 'Year', n: 'year' }, { l: 'Address', n: 'address' },
    { l: 'Guardian Name', n: 'guardianName' }, { l: 'Guardian Phone', n: 'guardianPhone' },
  ]

  return (
    <div>
      <Preloader show={loading} />
      <PageHeader
        title="Students"
        subtitle={`${all.length} students registered`}
        actions={
          <button className="btn-crm-primary" onClick={() => { reset_form(); setShowModal(true) }}>+ Add Student</button>
        }
      />

      {/* Search */}
      <div className="card mb-3">
        <div className="card-body" style={{ padding: '0.75rem 1.25rem' }}>
          <div className="d-flex gap-2">
            <input className="crm-input" style={{ maxWidth: 340 }} placeholder="Search by name, email or roll no…"
              value={search} onChange={e => setSearch(e.target.value)} onKeyDown={e => e.key === 'Enter' && doSearch()} />
            <button className="btn-crm-primary" onClick={doSearch}>Search</button>
            {search && <button className="btn-crm-ghost" onClick={() => { setSearch(''); fetchAll() }}>Clear</button>}
          </div>
        </div>
      </div>

      {/* Table */}
      <div className="card">
        <div className="card-body" style={{ padding: 0 }}>
          <table className="crm-table">
            <thead>
              <tr>
                <th>#</th><th>Name</th><th>Roll No</th><th>College</th><th>Email</th><th>Phone</th><th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {pageData.length === 0 ? <EmptyState message="No students found" /> :
                pageData.map((s, i) => (
                  <tr key={s.id}>
                    <td style={{ color: 'var(--text-muted)' }}>{page * 10 + i + 1}</td>
                    <td>
                      <div style={{ fontWeight: 500, color: 'var(--text-primary)' }}>{s.name}</div>
                      <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>{s.studentId}</div>
                    </td>
                    <td>{s.rollNo || '—'}</td>
                    <td>{s.collegeName}</td>
                    <td>{s.email}</td>
                    <td>{s.phone}</td>
                    <td>
                      <div className="d-flex gap-1">
                        <button className="btn-crm-icon success" title="Edit" onClick={() => openEdit(s)}>✎</button>
                        <button className="btn-crm-icon danger" title="Delete" onClick={() => handleDelete(s.id)}>✕</button>
                      </div>
                    </td>
                  </tr>
                ))}
            </tbody>
          </table>
        </div>
        <div style={{ padding: '0.75rem 1.25rem', borderTop: '1px solid var(--border-subtle)' }}>
          <CrmPagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
      </div>

      {/* Modal */}
      <CrmModal show={showModal} onClose={reset_form} title={editId ? 'Edit Student' : 'Add Student'} size="modal-lg"
        footer={<>
          <button className="btn-crm-ghost" onClick={reset_form}>Cancel</button>
          <button className="btn-crm-primary" disabled={loading} onClick={handleSave}>{editId ? 'Update' : 'Save'}</button>
        </>}>
        <div className="row g-3">
          {FIELDS.map(({ l, n, t = 'text' }) => (
            <div key={n} className="col-md-6">
              <Field label={l}>
                <input className="crm-input" type={t} name={n} value={form[n]} onChange={ch} />
              </Field>
            </div>
          ))}
        </div>
      </CrmModal>
    </div>
  )
}
