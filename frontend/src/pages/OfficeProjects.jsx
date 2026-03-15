// src/pages/OfficeProjects.jsx — NEW
import React, { useEffect, useState, useCallback } from 'react'
import { getAllOfficeProjects, createOfficeProject, updateOfficeProject, updateOfficeStatus } from '../api/services/officeProjectService'
import { getAllUsers } from '../api/services/userService'
import { Preloader, CrmPagination, CrmModal, PageHeader, Field, Badge, EmptyState } from '../components/common/ui'
import { usePagination } from '../utils/usePagination'
import Toast from '../utils/toast'

const STATUSES = ['PLANNING','IN_PROGRESS','TESTING','DEPLOYED','ON_HOLD','COMPLETED','CANCELLED']
const statusColor = s => ({ PLANNING:'muted', IN_PROGRESS:'info', TESTING:'warning', DEPLOYED:'success', COMPLETED:'success', ON_HOLD:'warning', CANCELLED:'danger' })[s] || 'muted'
const EMPTY = { name: '', description: '', clientName: '', startDate: '', expectedEndDate: '', budget: '', techStack: '' }

export default function OfficeProjects() {
  const [all, setAll]             = useState([])
  const [users, setUsers]         = useState([])
  const [loading, setLoading]     = useState(false)
  const [showModal, setShowModal] = useState(false)
  const [showStatus, setShowStatus] = useState(false)
  const [editId, setEditId]       = useState(null)
  const [form, setForm]           = useState(EMPTY)
  const [statusTarget, setStatusTarget] = useState({ id: null, status: '' })
  const [filterStatus, setFilterStatus] = useState('')
  const filtered = filterStatus ? all.filter(p => p.status === filterStatus) : all
  const { page, setPage, totalPages, pageData } = usePagination(filtered, 10)

  const fetchAll = useCallback(async () => {
    setLoading(true)
    try {
      const [pRes, uRes] = await Promise.all([getAllOfficeProjects(), getAllUsers()])
      setAll(pRes.data?.data || pRes.data || [])
      setUsers(uRes.data?.data || uRes.data || [])
    } catch { Toast.fire({ icon: 'error', title: 'Failed to load data' }) }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { fetchAll() }, [fetchAll])

  const ch = e => setForm(p => ({ ...p, [e.target.name]: e.target.value }))

  const handleSave = async () => {
    if (!form.name) { Toast.fire({ icon: 'warning', title: 'Project name required' }); return }
    setLoading(true)
    const payload = { name: form.name, description: form.description || null, clientName: form.clientName || null, startDate: form.startDate || null, expectedEndDate: form.expectedEndDate || null, budget: form.budget ? Number(form.budget) : null, techStack: form.techStack || null }
    try {
      if (editId) { await updateOfficeProject(editId, payload); Toast.fire({ icon: 'success', title: 'Updated' }) }
      else        { await createOfficeProject(payload);          Toast.fire({ icon: 'success', title: 'Project created' }) }
      reset_form(); fetchAll()
    } catch (err) { Toast.fire({ icon: 'error', title: err.response?.data?.message || 'Save failed' }) }
    finally { setLoading(false) }
  }

  const saveStatus = async () => {
    setLoading(true)
    try {
      await updateOfficeStatus(statusTarget.id, statusTarget.status)
      Toast.fire({ icon: 'success', title: 'Status updated' })
      setShowStatus(false); fetchAll()
    } catch (err) { Toast.fire({ icon: 'error', title: err.response?.data?.message || 'Failed' }) }
    finally { setLoading(false) }
  }

  const reset_form = () => { setForm(EMPTY); setEditId(null); setShowModal(false) }
  const openEdit = (p) => { setEditId(p.id); setForm({ name: p.name||'', description: p.description||'', clientName: p.clientName||'', startDate: p.startDate||'', expectedEndDate: p.expectedEndDate||'', budget: p.budget||'', techStack: p.techStack||'' }); setShowModal(true) }

  return (
    <div>
      <Preloader show={loading} />
      <PageHeader title="Office Projects" subtitle={`${all.length} live projects`}
        actions={
          <>
            <select className="crm-input" style={{ width: 170 }} value={filterStatus} onChange={e => { setFilterStatus(e.target.value); setPage(0) }}>
              <option value="">All Statuses</option>
              {STATUSES.map(s => <option key={s} value={s}>{s}</option>)}
            </select>
            <button className="btn-crm-primary" onClick={() => { reset_form(); setShowModal(true) }}>+ New Project</button>
          </>
        }
      />

      <div className="card">
        <div style={{ overflowX: 'auto' }}>
          <table className="crm-table">
            <thead><tr><th>#</th><th>Project</th><th>Client</th><th>Budget</th><th>Start</th><th>Expected End</th><th>Tech Stack</th><th>Status</th><th>Actions</th></tr></thead>
            <tbody>
              {pageData.length === 0 ? <EmptyState message="No office projects found" /> :
                pageData.map((p, i) => (
                  <tr key={p.id}>
                    <td style={{ color: 'var(--text-muted)' }}>{page * 10 + i + 1}</td>
                    <td>
                      <div style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{p.name}</div>
                      {p.description && <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)', maxWidth: 220, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{p.description}</div>}
                    </td>
                    <td>{p.clientName || '—'}</td>
                    <td>{p.budget ? `₹${Number(p.budget).toLocaleString('en-IN')}` : '—'}</td>
                    <td>{p.startDate || '—'}</td>
                    <td>{p.expectedEndDate || '—'}</td>
                    <td style={{ fontSize: '0.78rem' }}>{p.techStack ? <code style={{ color: 'var(--brand-primary)', fontSize: '0.72rem' }}>{p.techStack}</code> : '—'}</td>
                    <td><Badge type={statusColor(p.status)}>{p.status || 'PLANNING'}</Badge></td>
                    <td>
                      <div className="d-flex gap-1">
                        <button className="btn-crm-icon success" title="Edit" onClick={() => openEdit(p)}>✎</button>
                        <button className="btn-crm-icon info" title="Update Status" onClick={() => { setStatusTarget({ id: p.id, status: p.status || 'PLANNING' }); setShowStatus(true) }}>⚙</button>
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

      {/* Create / Edit Modal */}
      <CrmModal show={showModal} onClose={reset_form} title={editId ? 'Edit Project' : 'New Office Project'} size="modal-lg"
        footer={<><button className="btn-crm-ghost" onClick={reset_form}>Cancel</button><button className="btn-crm-primary" disabled={loading} onClick={handleSave}>{editId ? 'Update' : 'Create'}</button></>}>
        <div className="row g-3">
          <div className="col-12"><Field label="Project Name *"><input className="crm-input" name="name" value={form.name} onChange={ch} /></Field></div>
          <div className="col-md-6"><Field label="Client Name"><input className="crm-input" name="clientName" value={form.clientName} onChange={ch} /></Field></div>
          <div className="col-md-6"><Field label="Budget (₹)"><input className="crm-input" type="number" name="budget" value={form.budget} onChange={ch} /></Field></div>
          <div className="col-md-6"><Field label="Start Date"><input className="crm-input" type="date" name="startDate" value={form.startDate} onChange={ch} /></Field></div>
          <div className="col-md-6"><Field label="Expected End Date"><input className="crm-input" type="date" name="expectedEndDate" value={form.expectedEndDate} onChange={ch} /></Field></div>
          <div className="col-12"><Field label="Tech Stack"><input className="crm-input" name="techStack" placeholder="e.g. React, Spring Boot, PostgreSQL" value={form.techStack} onChange={ch} /></Field></div>
          <div className="col-12"><Field label="Description"><textarea className="crm-input" name="description" rows={3} value={form.description} onChange={ch} /></Field></div>
        </div>
      </CrmModal>

      {/* Status Modal */}
      <CrmModal show={showStatus} onClose={() => setShowStatus(false)} title="Update Project Status" size="modal-sm"
        footer={<><button className="btn-crm-ghost" onClick={() => setShowStatus(false)}>Cancel</button><button className="btn-crm-primary" disabled={loading} onClick={saveStatus}>Update</button></>}>
        <Field label="New Status">
          <select className="crm-input" value={statusTarget.status} onChange={e => setStatusTarget(p => ({ ...p, status: e.target.value }))}>
            {STATUSES.map(s => <option key={s} value={s}>{s}</option>)}
          </select>
        </Field>
      </CrmModal>
    </div>
  )
}
