// src/pages/Courses.jsx — REPLACE Course.jsx
import React, { useEffect, useState, useCallback } from 'react'
import { getAllCourses, createCourse, updateCourse, deleteCourse } from '../api/services/courseService'
import { getAllDomains } from '../api/services/domainService'
import { Preloader, CrmPagination, CrmModal, PageHeader, Field, EmptyState } from '../components/common/ui'
import { usePagination } from '../utils/usePagination'
import Toast from '../utils/toast'
import Swal from 'sweetalert2'

const EMPTY = { courseId: '', name: '', duration: '', amount: '', domainId: '' }

function genId(name) {
  if (!name || name.length < 2) return ''
  return `CO-${name.substring(0, 2).toUpperCase()}-${Math.floor(1000 + Math.random() * 9000)}`
}

export default function Courses() {
  const [all, setAll]           = useState([])
  const [domains, setDomains]   = useState([])
  const [loading, setLoading]   = useState(false)
  const [showModal, setShowModal] = useState(false)
  const [editId, setEditId]     = useState(null)
  const [form, setForm]         = useState(EMPTY)
  const { page, setPage, totalPages, pageData } = usePagination(all, 10)

  const fetchAll = useCallback(async () => {
    setLoading(true)
    try {
      const [cRes, dRes] = await Promise.all([getAllCourses(), getAllDomains()])
      setAll(cRes.data?.data || cRes.data || [])
      setDomains(dRes.data?.data || dRes.data || [])
    } catch { Toast.fire({ icon: 'error', title: 'Failed to load data' }) }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { fetchAll() }, [fetchAll])

  const ch = e => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSave = async () => {
    if (!form.name || !form.domainId) { Toast.fire({ icon: 'warning', title: 'Name and Domain required' }); return }
    setLoading(true)
    const payload = { courseId: form.courseId, name: form.name, duration: form.duration, amount: form.amount, domain: { id: Number(form.domainId) } }
    try {
      if (editId) { await updateCourse(editId, payload); Toast.fire({ icon: 'success', title: 'Updated' }) }
      else        { await createCourse(payload);          Toast.fire({ icon: 'success', title: 'Created' }) }
      reset_form(); fetchAll()
    } catch (err) { Toast.fire({ icon: 'error', title: err.response?.data?.message || 'Save failed' }) }
    finally { setLoading(false) }
  }

  const handleDelete = async (id) => {
    const ok = await Swal.fire({ title: 'Delete course?', icon: 'warning', showCancelButton: true, confirmButtonColor: '#ef4444', confirmButtonText: 'Delete', background: 'var(--surface-2)', color: 'var(--text-primary)' })
    if (!ok.isConfirmed) return
    try { await deleteCourse(id); Toast.fire({ icon: 'success', title: 'Deleted' }); fetchAll() }
    catch { Toast.fire({ icon: 'error', title: 'Delete failed' }) }
  }

  const reset_form = () => { setForm(EMPTY); setEditId(null); setShowModal(false) }
  const openEdit   = (c) => { setEditId(c.id); setForm({ courseId: c.courseId||'', name: c.name||'', duration: c.duration||'', amount: c.amount||'', domainId: c.domain?.id||'' }); setShowModal(true) }

  return (
    <div>
      <Preloader show={loading} />
      <PageHeader title="Courses" subtitle={`${all.length} courses`}
        actions={<button className="btn-crm-primary" onClick={() => { reset_form(); setShowModal(true) }}>+ Add Course</button>} />
      <div className="card">
        <div style={{ overflowX: 'auto' }}>
          <table className="crm-table">
            <thead><tr><th>#</th><th>Course ID</th><th>Name</th><th>Domain</th><th>Duration</th><th>Amount</th><th>Actions</th></tr></thead>
            <tbody>
              {pageData.length === 0 ? <EmptyState /> :
                pageData.map((c, i) => (
                  <tr key={c.id}>
                    <td style={{ color: 'var(--text-muted)' }}>{page * 10 + i + 1}</td>
                    <td><code style={{ fontSize: '0.75rem', color: 'var(--brand-primary)' }}>{c.courseId}</code></td>
                    <td style={{ fontWeight: 500, color: 'var(--text-primary)' }}>{c.name}</td>
                    <td>{c.domain?.title || c.domain?.name || '—'}</td>
                    <td>{c.duration}</td>
                    <td>₹ {c.amount}</td>
                    <td><div className="d-flex gap-1">
                      <button className="btn-crm-icon success" onClick={() => openEdit(c)}>✎</button>
                      <button className="btn-crm-icon danger" onClick={() => handleDelete(c.id)}>✕</button>
                    </div></td>
                  </tr>
                ))}
            </tbody>
          </table>
        </div>
        <div style={{ padding: '0.75rem 1.25rem', borderTop: '1px solid var(--border-subtle)' }}>
          <CrmPagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
      </div>

      <CrmModal show={showModal} onClose={reset_form} title={editId ? 'Edit Course' : 'Add Course'} size="modal-md"
        footer={<><button className="btn-crm-ghost" onClick={reset_form}>Cancel</button><button className="btn-crm-primary" disabled={loading} onClick={handleSave}>{editId ? 'Update' : 'Save'}</button></>}>
        <div className="row g-3">
          <div className="col-12">
            <Field label="Course ID (auto-generated)"><input className="crm-input" readOnly value={form.courseId} /></Field>
          </div>
          <div className="col-12">
            <Field label="Course Name *">
              <input className="crm-input" name="name" value={form.name} onChange={e => { ch(e); if (!editId) setForm(p => ({ ...p, courseId: genId(e.target.value), name: e.target.value })) }} />
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Domain *">
              <select className="crm-input" name="domainId" value={form.domainId} onChange={ch}>
                <option value="">-- Select Domain --</option>
                {domains.map(d => <option key={d.id} value={d.id}>{d.title || d.name}</option>)}
              </select>
            </Field>
          </div>
          <div className="col-md-6"><Field label="Duration"><input className="crm-input" name="duration" placeholder="e.g. 6 Months" value={form.duration} onChange={ch} /></Field></div>
          <div className="col-md-6"><Field label="Amount (₹)"><input className="crm-input" type="number" name="amount" value={form.amount} onChange={ch} /></Field></div>
        </div>
      </CrmModal>
    </div>
  )
}
