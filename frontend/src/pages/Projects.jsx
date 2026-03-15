// src/pages/Projects.jsx — REPLACE Project.jsx
import React, { useEffect, useState, useCallback } from 'react'
import { getAllProjects, createProject, updateProject, deleteProject } from '../api/services/projectService'
import { getAllDomains } from '../api/services/domainService'
import { Preloader, CrmPagination, CrmModal, PageHeader, Field, EmptyState } from '../components/common/ui'
import { usePagination } from '../utils/usePagination'
import Toast from '../utils/toast'
import Swal from 'sweetalert2'

const EMPTY = { title: '', amount: '', domainId: '' }

export default function Projects() {
  const [all, setAll]             = useState([])
  const [domains, setDomains]     = useState([])
  const [loading, setLoading]     = useState(false)
  const [showModal, setShowModal] = useState(false)
  const [editId, setEditId]       = useState(null)
  const [form, setForm]           = useState(EMPTY)
  const { page, setPage, totalPages, pageData } = usePagination(all, 10)

  const fetchAll = useCallback(async () => {
    setLoading(true)
    try {
      const [pRes, dRes] = await Promise.all([getAllProjects(), getAllDomains()])
      setAll(pRes.data?.data || pRes.data || [])
      setDomains(dRes.data?.data || dRes.data || [])
    } catch { Toast.fire({ icon: 'error', title: 'Failed to load data' }) }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { fetchAll() }, [fetchAll])

  const ch = e => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSave = async () => {
    if (!form.title || !form.domainId) { Toast.fire({ icon: 'warning', title: 'Title and Domain required' }); return }
    setLoading(true)
    const payload = { title: form.title, amount: form.amount, domain: { id: Number(form.domainId) } }
    try {
      if (editId) { await updateProject(editId, payload); Toast.fire({ icon: 'success', title: 'Updated' }) }
      else        { await createProject(payload);          Toast.fire({ icon: 'success', title: 'Created' }) }
      reset_form(); fetchAll()
    } catch (err) { Toast.fire({ icon: 'error', title: err.response?.data?.message || 'Save failed' }) }
    finally { setLoading(false) }
  }

  const handleDelete = async (id) => {
    const ok = await Swal.fire({ title: 'Delete project?', icon: 'warning', showCancelButton: true, confirmButtonColor: '#ef4444', confirmButtonText: 'Delete', background: 'var(--surface-2)', color: 'var(--text-primary)' })
    if (!ok.isConfirmed) return
    try { await deleteProject(id); Toast.fire({ icon: 'success', title: 'Deleted' }); fetchAll() }
    catch { Toast.fire({ icon: 'error', title: 'Delete failed' }) }
  }

  const reset_form = () => { setForm(EMPTY); setEditId(null); setShowModal(false) }
  const openEdit   = (p) => { setEditId(p.id); setForm({ title: p.title||'', amount: p.amount||'', domainId: p.domain?.id||'' }); setShowModal(true) }

  return (
    <div>
      <Preloader show={loading} />
      <PageHeader title="Student Projects" subtitle={`${all.length} project types`}
        actions={<button className="btn-crm-primary" onClick={() => { reset_form(); setShowModal(true) }}>+ Add Project</button>} />
      <div className="card">
        <div style={{ overflowX: 'auto' }}>
          <table className="crm-table">
            <thead><tr><th>#</th><th>Title</th><th>Domain</th><th>Amount</th><th>Actions</th></tr></thead>
            <tbody>
              {pageData.length === 0 ? <EmptyState /> :
                pageData.map((p, i) => (
                  <tr key={p.id}>
                    <td style={{ color: 'var(--text-muted)' }}>{page * 10 + i + 1}</td>
                    <td style={{ fontWeight: 500, color: 'var(--text-primary)' }}>{p.title}</td>
                    <td>{p.domain?.title || p.domain?.name || '—'}</td>
                    <td>₹ {p.amount}</td>
                    <td><div className="d-flex gap-1">
                      <button className="btn-crm-icon success" onClick={() => openEdit(p)}>✎</button>
                      <button className="btn-crm-icon danger" onClick={() => handleDelete(p.id)}>✕</button>
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

      <CrmModal show={showModal} onClose={reset_form} title={editId ? 'Edit Project' : 'Add Project'} size="modal-md"
        footer={<><button className="btn-crm-ghost" onClick={reset_form}>Cancel</button><button className="btn-crm-primary" disabled={loading} onClick={handleSave}>{editId ? 'Update' : 'Save'}</button></>}>
        <div className="row g-3">
          <div className="col-12"><Field label="Project Title *"><input className="crm-input" name="title" value={form.title} onChange={ch} /></Field></div>
          <div className="col-md-6">
            <Field label="Domain *">
              <select className="crm-input" name="domainId" value={form.domainId} onChange={ch}>
                <option value="">-- Select Domain --</option>
                {domains.map(d => <option key={d.id} value={d.id}>{d.title || d.name}</option>)}
              </select>
            </Field>
          </div>
          <div className="col-md-6"><Field label="Amount (₹)"><input className="crm-input" type="number" name="amount" value={form.amount} onChange={ch} /></Field></div>
        </div>
      </CrmModal>
    </div>
  )
}
