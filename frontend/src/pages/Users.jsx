// src/pages/Users.jsx — REPLACE
import React, { useEffect, useState, useCallback } from 'react'
import { getAllUsers, createUser, updateUser, deleteUser } from '../api/services/userService'
import { Preloader, CrmPagination, CrmModal, PageHeader, Field, Badge, EmptyState } from '../components/common/ui'
import { usePagination } from '../utils/usePagination'
import Toast from '../utils/toast'
import Swal from 'sweetalert2'
import { useAuth } from '../context/AuthContext'

const ROLES = ['SUPER_ADMIN', 'ADMIN', 'SUB_ADMIN', 'EMPLOYEE']
const EMP_TYPES = ['FULL_TIME', 'PART_TIME']
const EMPTY = { name: '', email: '', phone: '', role: 'EMPLOYEE', employmentType: 'FULL_TIME', position: '', dateOfJoining: '', password: '' }
const roleColor = r => r === 'SUPER_ADMIN' || r === 'ADMIN' ? 'danger' : r === 'SUB_ADMIN' ? 'info' : 'muted'

export default function Users() {
  const { isAdmin } = useAuth()
  const [all, setAll]         = useState([])
  const [loading, setLoading] = useState(false)
  const [showModal, setShowModal] = useState(false)
  const [editId, setEditId]   = useState(null)
  const [form, setForm]       = useState(EMPTY)
  const { page, setPage, totalPages, pageData } = usePagination(all, 10)

  const fetchAll = useCallback(async () => {
    setLoading(true)
    try { const res = await getAllUsers(); setAll(res.data?.data || res.data || []) }
    catch { Toast.fire({ icon: 'error', title: 'Failed to load users' }) }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { fetchAll() }, [fetchAll])

  const ch = e => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSave = async () => {
    if (!form.name || !form.email) { Toast.fire({ icon: 'warning', title: 'Name and Email required' }); return }
    if (!editId && !form.password) { Toast.fire({ icon: 'warning', title: 'Password required' }); return }
    setLoading(true)
    try {
      if (editId) {
        const { password, ...rest } = form
        await updateUser(editId, rest)
        Toast.fire({ icon: 'success', title: 'User updated' })
      } else {
        await createUser(form)
        Toast.fire({ icon: 'success', title: 'User created' })
      }
      reset_form(); fetchAll()
    } catch (err) { Toast.fire({ icon: 'error', title: err.response?.data?.message || 'Save failed' }) }
    finally { setLoading(false) }
  }

  const handleDelete = async (id) => {
    const ok = await Swal.fire({ title: 'Delete user?', icon: 'warning', showCancelButton: true, confirmButtonColor: '#ef4444', confirmButtonText: 'Delete', background: 'var(--surface-2)', color: 'var(--text-primary)' })
    if (!ok.isConfirmed) return
    try { await deleteUser(id); Toast.fire({ icon: 'success', title: 'Deleted' }); fetchAll() }
    catch { Toast.fire({ icon: 'error', title: 'Delete failed' }) }
  }

  const reset_form = () => { setForm(EMPTY); setEditId(null); setShowModal(false) }
  const openEdit   = (u) => { setEditId(u.id); setForm({ name: u.name||'', email: u.email||'', phone: u.phone||'', role: u.role||'EMPLOYEE', employmentType: u.employmentType||'FULL_TIME', position: u.position||'', dateOfJoining: u.dateOfJoining||'', password: '' }); setShowModal(true) }

  return (
    <div>
      <Preloader show={loading} />
      <PageHeader
        title="Staff Users"
        subtitle={`${all.length} staff members`}
        actions={isAdmin() && <button className="btn-crm-primary" onClick={() => { reset_form(); setShowModal(true) }}>+ Add User</button>}
      />

      <div className="card">
        <div style={{ overflowX: 'auto' }}>
          <table className="crm-table">
            <thead>
              <tr><th>#</th><th>Name</th><th>Email</th><th>Phone</th><th>Role</th><th>Position</th><th>Type</th>{isAdmin() && <th>Actions</th>}</tr>
            </thead>
            <tbody>
              {pageData.length === 0 ? <EmptyState message="No users found" /> :
                pageData.map((u, i) => (
                  <tr key={u.id}>
                    <td style={{ color: 'var(--text-muted)' }}>{page * 10 + i + 1}</td>
                    <td>
                      <div style={{ fontWeight: 500, color: 'var(--text-primary)' }}>{u.name}</div>
                      <div className="text-xs text-muted-crm">{u.userId}</div>
                    </td>
                    <td>{u.email}</td>
                    <td>{u.phone || '—'}</td>
                    <td><Badge type={roleColor(u.role)}>{u.role}</Badge></td>
                    <td>{u.position || '—'}</td>
                    <td><Badge type="muted">{u.employmentType || '—'}</Badge></td>
                    {isAdmin() && (
                      <td>
                        <div className="d-flex gap-1">
                          <button className="btn-crm-icon success" onClick={() => openEdit(u)}>✎</button>
                          <button className="btn-crm-icon danger" onClick={() => handleDelete(u.id)}>✕</button>
                        </div>
                      </td>
                    )}
                  </tr>
                ))}
            </tbody>
          </table>
        </div>
        <div style={{ padding: '0.75rem 1.25rem', borderTop: '1px solid var(--border-subtle)' }}>
          <CrmPagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
      </div>

      <CrmModal show={showModal} onClose={reset_form} title={editId ? 'Edit User' : 'Add User'} size="modal-lg"
        footer={<>
          <button className="btn-crm-ghost" onClick={reset_form}>Cancel</button>
          <button className="btn-crm-primary" disabled={loading} onClick={handleSave}>{editId ? 'Update' : 'Create'}</button>
        </>}>
        <div className="row g-3">
          {[{ l: 'Full Name *', n: 'name' }, { l: 'Email *', n: 'email', t: 'email' }, { l: 'Phone', n: 'phone' }, { l: 'Position', n: 'position' }, { l: 'Date of Joining', n: 'dateOfJoining', t: 'date' }].map(({ l, n, t = 'text' }) => (
            <div key={n} className="col-md-6">
              <Field label={l}><input className="crm-input" type={t} name={n} value={form[n]} onChange={ch} disabled={editId && n === 'email'} /></Field>
            </div>
          ))}
          <div className="col-md-6">
            <Field label="Role">
              <select className="crm-input" name="role" value={form.role} onChange={ch}>
                {ROLES.map(r => <option key={r} value={r}>{r}</option>)}
              </select>
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Employment Type">
              <select className="crm-input" name="employmentType" value={form.employmentType} onChange={ch}>
                {EMP_TYPES.map(t => <option key={t} value={t}>{t}</option>)}
              </select>
            </Field>
          </div>
          {!editId && (
            <div className="col-md-6">
              <Field label="Password *"><input className="crm-input" type="password" name="password" value={form.password} onChange={ch} /></Field>
            </div>
          )}
        </div>
      </CrmModal>
    </div>
  )
}
