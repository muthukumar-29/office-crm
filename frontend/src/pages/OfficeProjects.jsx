// src/pages/OfficeProjects.jsx
import React, { useEffect, useState, useCallback } from 'react'
import {
  getAllOfficeProjects, createOfficeProject, updateOfficeProject,
  updateOfficeStatus, addProjectMember, removeProjectMember,
} from '../api/services/officeProjectService'
import { getAllUsers } from '../api/services/userService'
import { Preloader, CrmPagination, CrmModal, PageHeader, Field, Badge, EmptyState } from '../components/common/ui'
import { usePagination } from '../utils/usePagination'
import Toast from '../utils/toast'
import { useAuth } from '../context/AuthContext'

const STATUSES = ['PLANNING', 'IN_PROGRESS', 'TESTING', 'DEPLOYED', 'ON_HOLD', 'COMPLETED', 'CANCELLED']
const statusColor = s => ({
  PLANNING: 'muted', IN_PROGRESS: 'info', TESTING: 'warning',
  DEPLOYED: 'success', COMPLETED: 'success', ON_HOLD: 'warning', CANCELLED: 'danger',
})[s] || 'muted'

const EMPTY = {
  name: '', description: '', clientName: '', clientContact: '',
  startDate: '', deadline: '', contractValue: '', amountReceived: '',
  techStack: '', projectManagerId: '', notes: '',
}

export default function OfficeProjects() {
  const { isAdmin } = useAuth()
  const [all, setAll]               = useState([])
  const [users, setUsers]           = useState([])
  const [loading, setLoading]       = useState(false)

  // Create / Edit modal
  const [showModal, setShowModal]   = useState(false)
  const [editId, setEditId]         = useState(null)
  const [form, setForm]             = useState(EMPTY)

  // Status modal
  const [showStatus, setShowStatus] = useState(false)
  const [statusTarget, setStatusTarget] = useState({ id: null, status: '' })

  // Members modal
  const [showMembers, setShowMembers]   = useState(false)
  const [selectedProject, setSelectedProject] = useState(null)
  const [memberForm, setMemberForm]     = useState({ userId: '', role: '' })

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
    const payload = {
      name:            form.name,
      description:     form.description     || null,
      clientName:      form.clientName      || null,
      clientContact:   form.clientContact   || null,
      techStack:       form.techStack       || null,
      notes:           form.notes           || null,
      startDate:       form.startDate       || null,
      deadline:        form.deadline        || null,
      contractValue:   form.contractValue   ? Number(form.contractValue)   : null,
      amountReceived:  form.amountReceived  ? Number(form.amountReceived)  : null,
      projectManagerId: form.projectManagerId ? Number(form.projectManagerId) : null,
    }
    try {
      if (editId) {
        await updateOfficeProject(editId, payload)
        Toast.fire({ icon: 'success', title: 'Project updated' })
      } else {
        await createOfficeProject(payload)
        Toast.fire({ icon: 'success', title: 'Project created' })
      }
      resetForm(); fetchAll()
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

  const handleAddMember = async () => {
    if (!memberForm.userId) { Toast.fire({ icon: 'warning', title: 'Select a user' }); return }
    setLoading(true)
    try {
      await addProjectMember(selectedProject.id, {
        userId:     Number(memberForm.userId),
        role:       memberForm.role || null,
        joinedDate: new Date().toISOString().slice(0, 10),
      })
      Toast.fire({ icon: 'success', title: 'Member added' })
      setMemberForm({ userId: '', role: '' })
      // refresh selected project
      const res = await getAllOfficeProjects()
      const updated = (res.data?.data || res.data || [])
      setAll(updated)
      setSelectedProject(updated.find(p => p.id === selectedProject.id) || selectedProject)
    } catch (err) { Toast.fire({ icon: 'error', title: err.response?.data?.message || 'Failed to add member' }) }
    finally { setLoading(false) }
  }

  const handleRemoveMember = async (userId) => {
    setLoading(true)
    try {
      await removeProjectMember(selectedProject.id, userId)
      Toast.fire({ icon: 'success', title: 'Member removed' })
      const res = await getAllOfficeProjects()
      const updated = (res.data?.data || res.data || [])
      setAll(updated)
      setSelectedProject(updated.find(p => p.id === selectedProject.id) || selectedProject)
    } catch (err) { Toast.fire({ icon: 'error', title: err.response?.data?.message || 'Failed to remove member' }) }
    finally { setLoading(false) }
  }

  const resetForm = () => { setForm(EMPTY); setEditId(null); setShowModal(false) }

  const openEdit = (p) => {
    setEditId(p.id)
    setForm({
      name:             p.name            || '',
      description:      p.description     || '',
      clientName:       p.clientName      || '',
      clientContact:    p.clientContact   || '',
      startDate:        p.startDate       || '',
      deadline:         p.deadline        || '',
      contractValue:    p.contractValue   || '',
      amountReceived:   p.amountReceived  || '',
      techStack:        p.techStack       || '',
      projectManagerId: p.projectManager?.id || '',
      notes:            p.notes           || '',
    })
    setShowModal(true)
  }

  const openMembers = (p) => {
    setSelectedProject(p)
    setMemberForm({ userId: '', role: '' })
    setShowMembers(true)
  }

  const activeMembers = (p) => (p.members || []).filter(m => m.isActive !== false)
  const balanceDue    = (p) => {
    const cv = Number(p.contractValue || 0)
    const ar = Number(p.amountReceived || 0)
    return cv > 0 ? cv - ar : null
  }

  return (
    <div>
      <Preloader show={loading} />
      <PageHeader
        title="Office Projects"
        subtitle={`${all.length} project${all.length !== 1 ? 's' : ''}`}
        actions={
          <>
            <select className="crm-input" style={{ width: 170 }} value={filterStatus} onChange={e => { setFilterStatus(e.target.value); setPage(0) }}>
              <option value="">All Statuses</option>
              {STATUSES.map(s => <option key={s} value={s}>{s.replace('_', ' ')}</option>)}
            </select>
            {isAdmin() && (
              <button className="btn-crm-primary" onClick={() => { resetForm(); setShowModal(true) }}>
                + New Project
              </button>
            )}
          </>
        }
      />

      {/* ── Table ────────────────────────────────────────────────────────── */}
      <div className="card">
        <div style={{ overflowX: 'auto' }}>
          <table className="crm-table">
            <thead>
              <tr>
                <th>#</th>
                <th>Project</th>
                <th>Client</th>
                <th>Contract</th>
                <th>Received</th>
                <th>Balance</th>
                <th>Timeline</th>
                <th>Tech Stack</th>
                <th>Manager</th>
                <th>Team</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {pageData.length === 0
                ? <EmptyState message="No office projects found" />
                : pageData.map((p, i) => {
                    const bal = balanceDue(p)
                    const members = activeMembers(p)
                    return (
                      <tr key={p.id}>
                        <td style={{ color: 'var(--text-muted)' }}>{page * 10 + i + 1}</td>

                        {/* Project name + description */}
                        <td style={{ minWidth: 160 }}>
                          <div style={{ fontWeight: 600, color: 'var(--text-primary)' }}>{p.name}</div>
                          {p.description && (
                            <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)', maxWidth: 200, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                              {p.description}
                            </div>
                          )}
                        </td>

                        {/* Client */}
                        <td style={{ fontSize: '0.82rem' }}>
                          <div>{p.clientName || '—'}</div>
                          {p.clientContact && <div className="text-xs text-muted-crm">{p.clientContact}</div>}
                        </td>

                        {/* Contract value */}
                        <td style={{ fontWeight: 500 }}>
                          {p.contractValue ? `₹${Number(p.contractValue).toLocaleString('en-IN')}` : '—'}
                        </td>

                        {/* Amount received */}
                        <td style={{ color: '#059669', fontWeight: 500 }}>
                          {p.amountReceived ? `₹${Number(p.amountReceived).toLocaleString('en-IN')}` : '—'}
                        </td>

                        {/* Balance */}
                        <td style={{ color: bal !== null ? (bal > 0 ? '#dc2626' : '#059669') : 'var(--text-muted)', fontWeight: 500 }}>
                          {bal !== null ? `₹${Number(bal).toLocaleString('en-IN')}` : '—'}
                        </td>

                        {/* Timeline */}
                        <td style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>
                          {p.startDate && <div>📅 {p.startDate}</div>}
                          {p.deadline  && <div style={{ color: '#d97706' }}>⏳ {p.deadline}</div>}
                          {p.actualCompletionDate && <div style={{ color: '#059669' }}>✅ {p.actualCompletionDate}</div>}
                          {!p.startDate && !p.deadline && <span className="text-muted-crm">—</span>}
                        </td>

                        {/* Tech stack */}
                        <td style={{ fontSize: '0.75rem' }}>
                          {p.techStack
                            ? <code style={{ color: 'var(--brand-primary)', fontSize: '0.72rem', background: 'rgba(14,165,233,0.08)', padding: '2px 6px', borderRadius: 4 }}>{p.techStack}</code>
                            : '—'}
                        </td>

                        {/* Project manager */}
                        <td style={{ fontSize: '0.82rem' }}>
                          {p.projectManager
                            ? <><div style={{ fontWeight: 500, color: 'var(--text-primary)' }}>{p.projectManager.name}</div>
                                <div className="text-xs text-muted-crm">{p.projectManager.role}</div></>
                            : <span className="text-muted-crm">—</span>}
                        </td>

                        {/* Team members */}
                        <td>
                          <button
                            className="btn-crm-icon info"
                            title="Manage team members"
                            onClick={() => openMembers(p)}
                            style={{ width: 'auto', padding: '0 0.5rem', gap: '0.25rem', display: 'flex', alignItems: 'center' }}
                          >
                            👥 {members.length > 0 && <span style={{ fontSize: '0.72rem', fontWeight: 600 }}>{members.length}</span>}
                          </button>
                        </td>

                        {/* Status */}
                        <td><Badge type={statusColor(p.status)}>{(p.status || 'PLANNING').replace('_', ' ')}</Badge></td>

                        {/* Actions */}
                        <td>
                          <div className="d-flex gap-1">
                            {isAdmin() && (
                              <>
                                <button className="btn-crm-icon success" title="Edit project" onClick={() => openEdit(p)}>✎</button>
                                <button className="btn-crm-icon info" title="Update status" onClick={() => { setStatusTarget({ id: p.id, status: p.status || 'PLANNING' }); setShowStatus(true) }}>⚙</button>
                              </>
                            )}
                          </div>
                        </td>
                      </tr>
                    )
                  })}
            </tbody>
          </table>
        </div>
        <div style={{ padding: '0.75rem 1.25rem', borderTop: '1px solid var(--border-subtle)' }}>
          <CrmPagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
      </div>

      {/* ── Create / Edit Modal ───────────────────────────────────────────── */}
      <CrmModal
        show={showModal}
        onClose={resetForm}
        title={editId ? 'Edit Office Project' : 'New Office Project'}
        size="modal-lg"
        footer={
          <>
            <button className="btn-crm-ghost" onClick={resetForm}>Cancel</button>
            <button className="btn-crm-primary" disabled={loading} onClick={handleSave}>
              {editId ? 'Update' : 'Create'}
            </button>
          </>
        }
      >
        <div className="row g-3">
          <div className="col-12">
            <Field label="Project Name *">
              <input className="crm-input" name="name" value={form.name} onChange={ch} placeholder="e.g. E-Commerce Platform" />
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Client Name">
              <input className="crm-input" name="clientName" value={form.clientName} onChange={ch} />
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Client Contact">
              <input className="crm-input" name="clientContact" value={form.clientContact} onChange={ch} placeholder="Phone / Email" />
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Contract Value (₹)">
              <input className="crm-input" type="number" name="contractValue" value={form.contractValue} onChange={ch} />
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Amount Received (₹)">
              <input className="crm-input" type="number" name="amountReceived" value={form.amountReceived} onChange={ch} />
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Start Date">
              <input className="crm-input" type="date" name="startDate" value={form.startDate} onChange={ch} />
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Deadline">
              <input className="crm-input" type="date" name="deadline" value={form.deadline} onChange={ch} />
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Project Manager">
              <select className="crm-input" name="projectManagerId" value={form.projectManagerId} onChange={ch}>
                <option value="">-- No manager assigned --</option>
                {users.map(u => (
                  <option key={u.id} value={u.id}>{u.name} ({u.role})</option>
                ))}
              </select>
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Tech Stack">
              <input className="crm-input" name="techStack" value={form.techStack} onChange={ch} placeholder="React, Spring Boot, PostgreSQL" />
            </Field>
          </div>
          <div className="col-12">
            <Field label="Description">
              <textarea className="crm-input" name="description" rows={3} value={form.description} onChange={ch} />
            </Field>
          </div>
          <div className="col-12">
            <Field label="Notes">
              <textarea className="crm-input" name="notes" rows={2} value={form.notes} onChange={ch} />
            </Field>
          </div>
        </div>
      </CrmModal>

      {/* ── Status Modal ─────────────────────────────────────────────────── */}
      <CrmModal
        show={showStatus}
        onClose={() => setShowStatus(false)}
        title="Update Project Status"
        size="modal-sm"
        footer={
          <>
            <button className="btn-crm-ghost" onClick={() => setShowStatus(false)}>Cancel</button>
            <button className="btn-crm-primary" disabled={loading} onClick={saveStatus}>Update</button>
          </>
        }
      >
        <Field label="New Status">
          <select className="crm-input" value={statusTarget.status} onChange={e => setStatusTarget(p => ({ ...p, status: e.target.value }))}>
            {STATUSES.map(s => <option key={s} value={s}>{s.replace('_', ' ')}</option>)}
          </select>
        </Field>
      </CrmModal>

      {/* ── Team Members Modal ───────────────────────────────────────────── */}
      <CrmModal
        show={showMembers}
        onClose={() => setShowMembers(false)}
        title={`Team — ${selectedProject?.name || ''}`}
        size="modal-md"
        footer={<button className="btn-crm-ghost" onClick={() => setShowMembers(false)}>Close</button>}
      >
        {/* Current members list */}
        <div style={{ marginBottom: '1.25rem' }}>
          <div className="crm-label" style={{ marginBottom: '0.5rem' }}>Current Team</div>
          {activeMembers(selectedProject || {}).length === 0
            ? <p style={{ fontSize: '0.82rem', color: 'var(--text-muted)', margin: 0 }}>No members yet.</p>
            : activeMembers(selectedProject || {}).map(m => (
              <div key={m.id} style={{
                display: 'flex', alignItems: 'center', justifyContent: 'space-between',
                padding: '0.55rem 0.75rem', borderRadius: 8,
                border: '1px solid var(--border-subtle)', marginBottom: '0.4rem',
                background: 'var(--surface-1)',
              }}>
                <div>
                  <div style={{ fontWeight: 500, fontSize: '0.875rem', color: 'var(--text-primary)' }}>
                    {m.user?.name || `User #${m.user?.id}`}
                  </div>
                  <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>
                    {m.role || m.user?.role || 'Team Member'}
                    {m.joinedDate && ` · joined ${m.joinedDate}`}
                  </div>
                </div>
                {isAdmin() && (
                  <button
                    className="btn-crm-icon danger"
                    title="Remove member"
                    onClick={() => handleRemoveMember(m.user?.id)}
                  >✕</button>
                )}
              </div>
            ))
          }
        </div>

        {/* Add member form (admin only) */}
        {isAdmin() && (
          <div style={{ borderTop: '1px solid var(--border-subtle)', paddingTop: '1rem' }}>
            <div className="crm-label" style={{ marginBottom: '0.5rem' }}>Add Member</div>
            <div className="row g-2">
              <div className="col-md-6">
                <Field label="User">
                  <select className="crm-input" value={memberForm.userId} onChange={e => setMemberForm(p => ({ ...p, userId: e.target.value }))}>
                    <option value="">-- Select user --</option>
                    {users
                      .filter(u => !activeMembers(selectedProject || {}).some(m => m.user?.id === u.id))
                      .map(u => <option key={u.id} value={u.id}>{u.name} ({u.role})</option>)
                    }
                  </select>
                </Field>
              </div>
              <div className="col-md-6">
                <Field label="Role in Project">
                  <input className="crm-input" placeholder="e.g. Developer, Tester" value={memberForm.role} onChange={e => setMemberForm(p => ({ ...p, role: e.target.value }))} />
                </Field>
              </div>
              <div className="col-12">
                <button className="btn-crm-primary" disabled={loading || !memberForm.userId} onClick={handleAddMember}>
                  + Add to Team
                </button>
              </div>
            </div>
          </div>
        )}
      </CrmModal>
    </div>
  )
}
