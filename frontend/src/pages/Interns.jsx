// src/pages/Interns.jsx — REPLACE Intern.jsx
import React, { useEffect, useState, useCallback } from 'react'
import { getAllInterns, createIntern, updateIntern, deleteIntern } from '../api/services/internService'
import { getAllDomains } from '../api/services/domainService'
import { Preloader, CrmPagination, CrmModal, PageHeader, Field, EmptyState } from '../components/common/ui'
import { usePagination } from '../utils/usePagination'
import Toast from '../utils/toast'
import Swal from 'sweetalert2'

const EMPTY = { title: '', duration: '', amount: '', domainId: '' }

export default function Interns() {
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
      const [iRes, dRes] = await Promise.all([getAllInterns(), getAllDomains()])
      setAll(iRes.data?.data || iRes.data || [])
      setDomains(dRes.data?.data || dRes.data || [])
    } catch { Toast.fire({ icon: 'error', title: 'Failed to load data' }) }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { fetchAll() }, [fetchAll])

  const ch = e => setForm({ ...form, [e.target.name]: e.target.value })

  const handleSave = async () => {
    if (!form.title || !form.domainId) { Toast.fire({ icon: 'warning', title: 'Title and Domain required' }); return }
    setLoading(true)
    const payload = { title: form.title, duration: form.duration, amount: form.amount, domain: { id: Number(form.domainId) } }
    try {
      if (editId) { await updateIntern(editId, payload); Toast.fire({ icon: 'success', title: 'Updated' }) }
      else        { await createIntern(payload);          Toast.fire({ icon: 'success', title: 'Created' }) }
      reset_form(); fetchAll()
    } catch (err) { Toast.fire({ icon: 'error', title: err.response?.data?.message || 'Save failed' }) }
    finally { setLoading(false) }
  }

  const handleDelete = async (id) => {
    const ok = await Swal.fire({ title: 'Delete internship?', icon: 'warning', showCancelButton: true, confirmButtonColor: '#ef4444', confirmButtonText: 'Delete', background: 'var(--surface-2)', color: 'var(--text-primary)' })
    if (!ok.isConfirmed) return
    try { await deleteIntern(id); Toast.fire({ icon: 'success', title: 'Deleted' }); fetchAll() }
    catch { Toast.fire({ icon: 'error', title: 'Delete failed' }) }
  }

  const reset_form = () => { setForm(EMPTY); setEditId(null); setShowModal(false) }
  const openEdit   = (r) => { setEditId(r.id); setForm({ title: r.title||'', duration: r.duration||'', amount: r.amount||'', domainId: r.domain?.id||'' }); setShowModal(true) }

  return (
    <div>
      <Preloader show={loading} />
      <PageHeader title="Internship Programs" subtitle={`${all.length} programs`}
        actions={<button className="btn-crm-primary" onClick={() => { reset_form(); setShowModal(true) }}>+ Add Internship</button>} />
      <div className="card">
        <div style={{ overflowX: 'auto' }}>
          <table className="crm-table">
            <thead><tr><th>#</th><th>Title</th><th>Domain</th><th>Duration</th><th>Amount</th><th>Actions</th></tr></thead>
            <tbody>
              {pageData.length === 0 ? <EmptyState /> :
                pageData.map((r, i) => (
                  <tr key={r.id}>
                    <td style={{ color: 'var(--text-muted)' }}>{page * 10 + i + 1}</td>
                    <td style={{ fontWeight: 500, color: 'var(--text-primary)' }}>{r.title}</td>
                    <td>{r.domain?.title || r.domain?.name || '—'}</td>
                    <td>{r.duration}</td>
                    <td>₹ {r.amount}</td>
                    <td><div className="d-flex gap-1">
                      <button className="btn-crm-icon success" onClick={() => openEdit(r)}>✎</button>
                      <button className="btn-crm-icon danger" onClick={() => handleDelete(r.id)}>✕</button>
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

      <CrmModal show={showModal} onClose={reset_form} title={editId ? 'Edit Internship' : 'Add Internship'} size="modal-md"
        footer={<><button className="btn-crm-ghost" onClick={reset_form}>Cancel</button><button className="btn-crm-primary" disabled={loading} onClick={handleSave}>{editId ? 'Update' : 'Save'}</button></>}>
        <div className="row g-3">
          <div className="col-12"><Field label="Title *"><input className="crm-input" name="title" value={form.title} onChange={ch} /></Field></div>
          <div className="col-md-6">
            <Field label="Domain *">
              <select className="crm-input" name="domainId" value={form.domainId} onChange={ch}>
                <option value="">-- Select Domain --</option>
                {domains.map(d => <option key={d.id} value={d.id}>{d.title || d.name}</option>)}
              </select>
            </Field>
          </div>
          <div className="col-md-6"><Field label="Duration"><input className="crm-input" name="duration" placeholder="e.g. 3 Months" value={form.duration} onChange={ch} /></Field></div>
          <div className="col-md-6"><Field label="Amount (₹)"><input className="crm-input" type="number" name="amount" value={form.amount} onChange={ch} /></Field></div>
        </div>
      </CrmModal>
    </div>
  )
}
