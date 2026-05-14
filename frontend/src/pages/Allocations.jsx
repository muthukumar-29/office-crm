// src/pages/Allocations.jsx
import React, { useEffect, useState, useCallback } from 'react'
import {
  getAllAllocations, createAllocation, updateAllocationStatus, getCatalogItems, updateAllocationAssign
} from '../api/services/allocationService'
import { getAllStudents } from '../api/services/studentService'
import { getAllDomains } from '../api/services/domainService'
import { getAllUsers } from '../api/services/userService'
import { Preloader, CrmPagination, CrmModal, PageHeader, Field, Badge, EmptyState } from '../components/common/ui'
import { usePagination } from '../utils/usePagination'
import Toast from '../utils/toast'
import { useAuth } from '../context/AuthContext'

const CATEGORIES = ['PROJECT', 'INTERN', 'COURSE']
const STATUS_OPTS = {
  PROJECT: ['NOT_STARTED','IN_PROGRESS','REVIEW_PENDING','DELIVERED','COMPLETED','CANCELLED'],
  INTERN:  ['ONGOING','COMPLETED','TERMINATED'],
  COURSE:  ['ENROLLED','IN_PROGRESS','COMPLETED','DROPPED']
}
const ALLOC_OPTS = ['ACTIVE','COMPLETED','DROPPED','ON_HOLD']
const payBadge   = s => s==='PAID'?'success':s==='PARTIAL'?'warning':s==='PENDING'?'muted':'danger'
const allocBadge = s => s==='ACTIVE'?'success':s==='COMPLETED'?'info':s==='DROPPED'?'danger':'warning'
const catBadge   = s => s==='PROJECT'?'info':s==='INTERN'?'success':'purple'

const EMPTY = { studentId:'', category:'', domainId:'', itemId:'', startDate:'', endDate:'',
                classStartTime:'', classEndTime:'', assignedEmployeeId:'', totalFee:'', notes:'' }

export default function Allocations() {
  const { isAdmin } = useAuth()
  const [all, setAll]               = useState([])
  const [students, setStudents]     = useState([])
  const [domains, setDomains]       = useState([])
  const [employees, setEmployees]   = useState([])
  const [items, setItems]           = useState([])
  const [loading, setLoading]       = useState(false)
  const [showModal, setShowModal]   = useState(false)
  const [showStatus, setShowStatus] = useState(false)
  const [showAssign, setShowAssign] = useState(false)
  const [form, setForm]             = useState(EMPTY)
  const [statusForm, setStatusForm] = useState({ id:null, category:'', allocationStatus:'', specificStatus:'' })
  const [assignForm, setAssignForm] = useState({ id:null, assignedEmployeeId:'', classStartTime:'', classEndTime:'', startDate:'', endDate:'', notes:'' })
  const [filterCat, setFilterCat]   = useState('')

  const filtered = filterCat ? all.filter(a => a.category === filterCat) : all
  const { page, setPage, totalPages, pageData } = usePagination(filtered, 10)

  const fetchAll = useCallback(async () => {
    setLoading(true)
    try {
      const [aRes, sRes, dRes, uRes] = await Promise.all([
        getAllAllocations(), getAllStudents(), getAllDomains(), getAllUsers()
      ])
      setAll(aRes.data?.data || aRes.data || [])
      setStudents(sRes.data?.data || sRes.data || [])
      setDomains(dRes.data?.data || dRes.data || [])
      setEmployees((uRes.data?.data || uRes.data || []))
    } catch { Toast.fire({ icon:'error', title:'Failed to load data' }) }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { fetchAll() }, [fetchAll])

  const handleFormChange = async (e) => {
    const { name, value } = e.target
    const updated = { ...form, [name]: value }
    if (name === 'category')  { updated.domainId = ''; updated.itemId = ''; setItems([]) }
    if (name === 'domainId' && updated.category && value) {
      try {
        const res = await getCatalogItems(updated.category, value)
        setItems(res.data?.data || res.data || [])
      } catch { Toast.fire({ icon:'error', title:'Failed to load items' }) }
      updated.itemId = ''
    }
    setForm(updated)
  }

  const handleSave = async () => {
    if (!form.studentId||!form.category||!form.domainId||!form.itemId||!form.startDate) {
      Toast.fire({ icon:'warning', title:'Student, Category, Domain, Item and Start Date required' }); return
    }
    setLoading(true)
    const payload = {
      studentId: Number(form.studentId), category: form.category,
      startDate: form.startDate, endDate: form.endDate || null,
      assignedEmployeeId: form.assignedEmployeeId ? Number(form.assignedEmployeeId) : null,
      classStartTime: form.classStartTime || null,
      classEndTime:   form.classEndTime   || null,
      totalFee: form.totalFee ? Number(form.totalFee) : null,
      notes: form.notes || null,
      ...(form.category==='COURSE'  && { courseId:         Number(form.itemId) }),
      ...(form.category==='INTERN'  && { internProgramId:  Number(form.itemId) }),
      ...(form.category==='PROJECT' && { studentProjectId: Number(form.itemId) }),
    }
    try {
      await createAllocation(payload)
      Toast.fire({ icon:'success', title:'Allocated successfully' })
      setShowModal(false); setForm(EMPTY); setItems([]); fetchAll()
    } catch (err) { Toast.fire({ icon:'error', title: err.response?.data?.message||'Failed' }) }
    finally { setLoading(false) }
  }

  const openStatus = (a) => {
    const sp = a.category==='PROJECT' ? a.projectStatus : a.category==='INTERN' ? a.internStatus : a.courseStatus
    setStatusForm({ id:a.id, category:a.category, allocationStatus:a.allocationStatus||'', specificStatus:sp||'' })
    setShowStatus(true)
  }

  const saveStatus = async () => {
    const { id, category, allocationStatus, specificStatus } = statusForm
    const payload = { allocationStatus: allocationStatus || null }
    if (category==='PROJECT') payload.projectStatus = specificStatus || null
    if (category==='INTERN')  payload.internStatus  = specificStatus || null
    if (category==='COURSE')  payload.courseStatus  = specificStatus || null
    setLoading(true)
    try {
      await updateAllocationStatus(id, payload)
      Toast.fire({ icon:'success', title:'Status updated' })
      setShowStatus(false); fetchAll()
    } catch (err) { Toast.fire({ icon:'error', title: err.response?.data?.message||'Failed' }) }
    finally { setLoading(false) }
  }

  const openAssign = (a) => {
    setAssignForm({
      id: a.id,
      assignedEmployeeId: a.assignedEmployee?.id || '',
      classStartTime: a.classStartTime || '',
      classEndTime:   a.classEndTime   || '',
      startDate:      a.startDate      || '',
      endDate:        a.endDate        || '',
      notes:          a.notes          || '',
    })
    setShowAssign(true)
  }

  const saveAssign = async () => {
    setLoading(true)
    try {
      await updateAllocationAssign(assignForm.id, {
        assignedEmployeeId: assignForm.assignedEmployeeId ? Number(assignForm.assignedEmployeeId) : null,
        classStartTime: assignForm.classStartTime || null,
        classEndTime:   assignForm.classEndTime   || null,
        startDate:      assignForm.startDate      || null,
        endDate:        assignForm.endDate        || null,
        notes:          assignForm.notes          || null,
      })
      Toast.fire({ icon:'success', title:'Assignment updated' })
      setShowAssign(false); fetchAll()
    } catch (err) { Toast.fire({ icon:'error', title: err.response?.data?.message||'Failed' }) }
    finally { setLoading(false) }
  }

  const getItemLabel = (it) => form.category==='COURSE' ? it.name : it.title
  const showTiming   = (cat) => cat==='COURSE' || cat==='INTERN'

  return (
    <div>
      <Preloader show={loading} />
      <PageHeader
        title="Allocations"
        subtitle={`${all.length} total`}
        actions={<>
          <select className="crm-input" style={{ width:160 }} value={filterCat} onChange={e => { setFilterCat(e.target.value); setPage(0) }}>
            <option value="">All Categories</option>
            {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
          </select>
          {isAdmin() && <button className="btn-crm-primary" onClick={() => { setForm(EMPTY); setItems([]); setShowModal(true) }}>+ Allocate</button>}
        </>}
      />

      <div className="card">
        <div style={{ overflowX:'auto' }}>
          <table className="crm-table">
            <thead>
              <tr>
                <th>#</th><th>Student</th><th>Category</th><th>Program</th>
                <th>Mentor</th><th>Timing</th>
                <th>Fee</th><th>Paid</th><th>Balance</th><th>Payment</th><th>Status</th>
                {isAdmin() && <th>Actions</th>}
              </tr>
            </thead>
            <tbody>
              {pageData.length === 0 ? <EmptyState message="No allocations found" /> :
                pageData.map((a, i) => (
                  <tr key={a.id}>
                    <td style={{ color:'var(--text-muted)' }}>{page*10+i+1}</td>
                    <td>
                      <div style={{ fontWeight:500, color:'var(--text-primary)', fontSize:'0.875rem' }}>{a.student?.name}</div>
                      <div className="text-xs text-muted-crm">{a.student?.rollNo}</div>
                    </td>
                    <td><Badge type={catBadge(a.category)}>{a.category}</Badge></td>
                    <td style={{ fontSize:'0.8rem' }}>
                      <div>{a.course?.name || a.intern?.title || a.project?.title || '—'}</div>
                      <div className="text-xs text-muted-crm">{a.course?.domain?.name || a.intern?.domain?.name || a.project?.domain?.name}</div>
                    </td>
                    <td style={{ fontSize:'0.8rem' }}>
                      {a.assignedEmployee
                        ? <><div style={{ color:'var(--text-primary)', fontWeight:500 }}>{a.assignedEmployee.name}</div>
                            <div className="text-xs text-muted-crm">{a.assignedEmployee.position}</div></>
                        : <span className="text-muted-crm">—</span>}
                    </td>
                    <td style={{ fontSize:'0.75rem', color:'var(--text-secondary)' }}>
                      {a.startDate && <div>📅 {a.startDate} {a.endDate ? `→ ${a.endDate}` : ''}</div>}
                      {a.classStartTime && <div>⏰ {a.classStartTime} – {a.classEndTime}</div>}
                      {!a.startDate && !a.classStartTime && <span className="text-muted-crm">—</span>}
                    </td>
                    <td>₹{a.totalFee ?? '—'}</td>
                    <td style={{ color:'#10b981' }}>₹{a.amountPaid ?? 0}</td>
                    <td style={{ color: Number(a.balanceDue)>0 ? '#ef4444' : '#10b981' }}>₹{a.balanceDue ?? '—'}</td>
                    <td><Badge type={payBadge(a.paymentStatus)}>{a.paymentStatus}</Badge></td>
                    <td>
                      <Badge type={allocBadge(a.allocationStatus)}>{a.allocationStatus}</Badge>
                      <div className="text-xs text-muted-crm mt-1">{a.projectStatus||a.internStatus||a.courseStatus}</div>
                    </td>
                    {isAdmin() && (
                      <td>
                        <div className="d-flex gap-1">
                          <button className="btn-crm-icon info" title="Assign Mentor / Timing" onClick={() => openAssign(a)}>👤</button>
                          <button className="btn-crm-icon success" title="Update Status" onClick={() => openStatus(a)}>⚙</button>
                        </div>
                      </td>
                    )}
                  </tr>
                ))}
            </tbody>
          </table>
        </div>
        <div style={{ padding:'0.75rem 1.25rem', borderTop:'1px solid var(--border-subtle)' }}>
          <CrmPagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
      </div>

      {/* ── Allocate Modal ─────────────────────────────────────────────────── */}
      <CrmModal show={showModal} onClose={() => setShowModal(false)} title="New Allocation" size="modal-lg"
        footer={<>
          <button className="btn-crm-ghost" onClick={() => setShowModal(false)}>Cancel</button>
          <button className="btn-crm-primary" disabled={loading} onClick={handleSave}>Allocate</button>
        </>}>
        <div className="row g-3">
          <div className="col-md-6">
            <Field label="Category *">
              <select className="crm-input" name="category" value={form.category} onChange={handleFormChange}>
                <option value="">-- Select --</option>
                {CATEGORIES.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Student *">
              <select className="crm-input" name="studentId" value={form.studentId} onChange={handleFormChange}>
                <option value="">-- Select Student --</option>
                {students.map(s => <option key={s.id} value={s.id}>{s.name} {s.rollNo?`(${s.rollNo})`:''}</option>)}
              </select>
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Domain *">
              <select className="crm-input" name="domainId" value={form.domainId} onChange={handleFormChange} disabled={!form.category}>
                <option value="">-- Select Domain --</option>
                {domains.map(d => <option key={d.id} value={d.id}>{d.title||d.name}</option>)}
              </select>
            </Field>
          </div>
          <div className="col-md-6">
            <Field label={`${form.category||'Item'} *`}>
              <select className="crm-input" name="itemId" value={form.itemId} onChange={handleFormChange} disabled={items.length===0}>
                <option value="">-- Select --</option>
                {items.map(it => <option key={it.id} value={it.id}>{getItemLabel(it)} — ₹{it.amount}</option>)}
              </select>
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Assign Mentor / Project Head">
              <select className="crm-input" name="assignedEmployeeId" value={form.assignedEmployeeId} onChange={handleFormChange}>
                <option value="">-- No assignment --</option>
                {employees.map(e => <option key={e.id} value={e.id}>{e.name} ({e.role})</option>)}
              </select>
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Fee Override (₹)">
              <input className="crm-input" type="number" name="totalFee" placeholder="Leave blank for default" value={form.totalFee} onChange={handleFormChange} />
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Start Date *">
              <input className="crm-input" type="date" name="startDate" value={form.startDate} onChange={handleFormChange} />
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="End Date">
              <input className="crm-input" type="date" name="endDate" value={form.endDate} onChange={handleFormChange} />
            </Field>
          </div>
          {showTiming(form.category) && <>
            <div className="col-md-6">
              <Field label="Class Start Time (daily)">
                <input className="crm-input" type="time" name="classStartTime" value={form.classStartTime} onChange={handleFormChange} />
              </Field>
            </div>
            <div className="col-md-6">
              <Field label="Class End Time (daily)">
                <input className="crm-input" type="time" name="classEndTime" value={form.classEndTime} onChange={handleFormChange} />
              </Field>
            </div>
          </>}
          <div className="col-12">
            <Field label="Notes">
              <textarea className="crm-input" name="notes" rows={2} value={form.notes} onChange={handleFormChange} />
            </Field>
          </div>
        </div>
      </CrmModal>

      {/* ── Assign Employee / Timing Modal ─────────────────────────────────── */}
      <CrmModal show={showAssign} onClose={() => setShowAssign(false)} title="Assign Mentor & Timing" size="modal-md"
        footer={<>
          <button className="btn-crm-ghost" onClick={() => setShowAssign(false)}>Cancel</button>
          <button className="btn-crm-primary" disabled={loading} onClick={saveAssign}>Save</button>
        </>}>
        <div className="row g-3">
          <div className="col-12">
            <Field label="Mentor / Project Head">
              <select className="crm-input" value={assignForm.assignedEmployeeId} onChange={e => setAssignForm(p => ({ ...p, assignedEmployeeId: e.target.value }))}>
                <option value="">-- No assignment --</option>
                {employees.map(e => <option key={e.id} value={e.id}>{e.name} — {e.position||e.role}</option>)}
              </select>
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Start Date">
              <input className="crm-input" type="date" value={assignForm.startDate} onChange={e => setAssignForm(p => ({ ...p, startDate: e.target.value }))} />
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="End Date">
              <input className="crm-input" type="date" value={assignForm.endDate} onChange={e => setAssignForm(p => ({ ...p, endDate: e.target.value }))} />
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Class Start Time">
              <input className="crm-input" type="time" value={assignForm.classStartTime} onChange={e => setAssignForm(p => ({ ...p, classStartTime: e.target.value }))} />
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Class End Time">
              <input className="crm-input" type="time" value={assignForm.classEndTime} onChange={e => setAssignForm(p => ({ ...p, classEndTime: e.target.value }))} />
            </Field>
          </div>
          <div className="col-12">
            <Field label="Notes">
              <textarea className="crm-input" rows={2} value={assignForm.notes} onChange={e => setAssignForm(p => ({ ...p, notes: e.target.value }))} />
            </Field>
          </div>
        </div>
      </CrmModal>

      {/* ── Status Modal ───────────────────────────────────────────────────── */}
      <CrmModal show={showStatus} onClose={() => setShowStatus(false)} title="Update Status" size="modal-sm"
        footer={<>
          <button className="btn-crm-ghost" onClick={() => setShowStatus(false)}>Cancel</button>
          <button className="btn-crm-primary" disabled={loading} onClick={saveStatus}>Update</button>
        </>}>
        <Field label="Allocation Status">
          <select className="crm-input" value={statusForm.allocationStatus} onChange={e => setStatusForm(p => ({ ...p, allocationStatus: e.target.value }))}>
            <option value="">-- No change --</option>
            {ALLOC_OPTS.map(s => <option key={s} value={s}>{s}</option>)}
          </select>
        </Field>
        <div style={{ marginTop:'1rem' }}>
          <Field label={`${statusForm.category} Status`}>
            <select className="crm-input" value={statusForm.specificStatus} onChange={e => setStatusForm(p => ({ ...p, specificStatus: e.target.value }))}>
              <option value="">-- No change --</option>
              {(STATUS_OPTS[statusForm.category]||[]).map(s => <option key={s} value={s}>{s}</option>)}
            </select>
          </Field>
        </div>
      </CrmModal>
    </div>
  )
}
