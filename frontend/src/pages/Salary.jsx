// src/pages/Salary.jsx — REPLACE
import React, { useEffect, useState, useCallback } from 'react'
import { getAllSalaries, createSalary, markSalaryPaid, getPayslipHtml } from '../api/services/salaryService'
import { getAllUsers } from '../api/services/userService'
import { Preloader, CrmPagination, CrmModal, PageHeader, Field, Badge, EmptyState, StatCard } from '../components/common/ui'
import { usePagination } from '../utils/usePagination'
import Toast from '../utils/toast'
import Swal from 'sweetalert2'
import { useAuth } from '../context/AuthContext'

const MODES = ['BANK_TRANSFER', 'CASH', 'CHEQUE', 'UPI']
const MONTHS = [
  { label: 'January', value: '01' }, { label: 'February', value: '02' },
  { label: 'March', value: '03' }, { label: 'April', value: '04' },
  { label: 'May', value: '05' }, { label: 'June', value: '06' },
  { label: 'July', value: '07' }, { label: 'August', value: '08' },
  { label: 'September', value: '09' }, { label: 'October', value: '10' },
  { label: 'November', value: '11' }, { label: 'December', value: '12' },
]
const YEARS = Array.from({ length: 5 }, (_, i) => String(new Date().getFullYear() - i))

const now = new Date()
const CUR_YEAR = String(now.getFullYear())
const CUR_MONTH = String(now.getMonth() + 1).padStart(2, '0')

const EMPTY = {
  employeeId: '', payMonth: `${CUR_YEAR}-${CUR_MONTH}`,
  basicSalary: '', hra: '', transportAllowance: '',
  otherAllowance: '', bonus: '', pfDeduction: '', taxDeduction: '', otherDeduction: '',
  paymentMode: 'BANK_TRANSFER', transactionRef: '', notes: ''
}

const statusBadge = s => s === 'PAID' ? 'success' : s === 'PENDING' ? 'warning' : 'danger'

export default function Salary() {
  const { isAdmin } = useAuth()
  const [all, setAll] = useState([])
  const [employees, setEmployees] = useState([])
  const [loading, setLoading] = useState(false)
  const [showModal, setShowModal] = useState(false)
  const [form, setForm] = useState(EMPTY)
  const [selMonth, setSelMonth] = useState(CUR_MONTH)
  const [selYear, setSelYear] = useState(CUR_YEAR)
  const [filterEmp, setFilterEmp] = useState('')

  const filtered = filterEmp ? all.filter(s => String(s.employee?.id) === filterEmp) : all
  const { page, setPage, totalPages, pageData } = usePagination(filtered, 10)

  const fetchAll = useCallback(async () => {
    setLoading(true)
    try {
      const [sRes, uRes] = await Promise.all([getAllSalaries(), getAllUsers()])
      setAll(sRes.data?.data || sRes.data || [])
      setEmployees(uRes.data?.data || uRes.data || [])
    } catch { Toast.fire({ icon: 'error', title: 'Failed to load salary data' }) }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { fetchAll() }, [fetchAll])

  // Keep payMonth in sync whenever selYear or selMonth changes
  useEffect(() => {
    setForm(p => ({ ...p, payMonth: `${selYear}-${selMonth}` }))
  }, [selYear, selMonth])

  const ch = e => setForm(p => ({ ...p, [e.target.name]: e.target.value }))

  // Live totals
  const gross = ['basicSalary', 'hra', 'transportAllowance', 'otherAllowance', 'bonus']
    .reduce((s, k) => s + Number(form[k] || 0), 0)
  const deductions = ['pfDeduction', 'taxDeduction', 'otherDeduction']
    .reduce((s, k) => s + Number(form[k] || 0), 0)
  const net = gross - deductions

  const handleSave = async () => {
    // Detailed validation with specific messages
    if (!form.employeeId) {
      Toast.fire({ icon: 'warning', title: 'Please select an employee' }); return
    }
    if (!form.payMonth || form.payMonth === '-') {
      Toast.fire({ icon: 'warning', title: 'Please select pay month and year' }); return
    }
    if (!form.basicSalary || Number(form.basicSalary) <= 0) {
      Toast.fire({ icon: 'warning', title: 'Basic salary must be greater than 0' }); return
    }

    setLoading(true)
    const payload = {
      employeeId: Number(form.employeeId),
      payMonth: form.payMonth,
      basicSalary: Number(form.basicSalary),
      hra: Number(form.hra || 0),
      transportAllowance: Number(form.transportAllowance || 0),
      otherAllowance: Number(form.otherAllowance || 0),
      bonus: Number(form.bonus || 0),
      pfDeduction: Number(form.pfDeduction || 0),
      taxDeduction: Number(form.taxDeduction || 0),
      otherDeduction: Number(form.otherDeduction || 0),
      paymentMode: form.paymentMode,
      transactionRef: form.transactionRef || null,
      notes: form.notes || null,
    }

    try {
      await createSalary(payload)
      Toast.fire({ icon: 'success', title: 'Salary record created' })
      setShowModal(false)
      setForm(EMPTY)
      fetchAll()
    } catch (err) {
      const msg = err.response?.data?.message || err.response?.data || 'Failed to save salary'
      Toast.fire({ icon: 'error', title: msg })
    } finally { setLoading(false) }
  }

  const handlePay = async (id) => {
    const ok = await Swal.fire({
      title: 'Mark as Paid?', text: 'This will also create a finance expense entry.',
      icon: 'question', showCancelButton: true, confirmButtonColor: '#10b981',
      confirmButtonText: 'Yes, Mark Paid', background: 'var(--surface-2)', color: 'var(--text-primary)'
    })
    if (!ok.isConfirmed) return
    setLoading(true)
    try {
      await markSalaryPaid(id)
      Toast.fire({ icon: 'success', title: '✓ Salary marked as PAID. Finance expense recorded.' })
      fetchAll()
    } catch (err) { Toast.fire({ icon: 'error', title: err.response?.data?.message || 'Failed' }) }
    finally { setLoading(false) }
  }

  const handlePayslip = async (id) => {
    setLoading(true)
    try {
      const res = await getPayslipHtml(id)
      const win = window.open('', '_blank')
      win.document.write(res.data)
      win.document.close()
      setTimeout(() => win.print(), 800)
    } catch { Toast.fire({ icon: 'error', title: 'Failed to load payslip' }) }
    finally { setLoading(false) }
  }

  const paid = all.filter(s => s.status === 'PAID').reduce((s, r) => s + Number(r.netSalary || 0), 0)
  const pending = all.filter(s => s.status === 'PENDING').reduce((s, r) => s + Number(r.netSalary || 0), 0)

  const openModal = () => {
    const y = CUR_YEAR
    const m = CUR_MONTH
    setSelYear(y)
    setSelMonth(m)
    setForm({ ...EMPTY, payMonth: `${y}-${m}` })
    setShowModal(true)
  }

  return (
    <div>
      <Preloader show={loading} />
      <PageHeader title="Salary & Payroll" subtitle={`${all.length} records`}
        actions={isAdmin() && <button className="btn-crm-primary" onClick={openModal}>+ Generate Salary</button>}
      />

      {/* Stats */}
      <div className="row g-3 mb-4">
        <div className="col-md-4">
          <StatCard label="Total Paid" value={`₹${Number(paid).toLocaleString('en-IN')}`} icon="✅" color="green" sub="Disbursed" />
        </div>
        <div className="col-md-4">
          <StatCard label="Pending Payout" value={`₹${Number(pending).toLocaleString('en-IN')}`} icon="⏳" color="amber" sub="Awaiting payment" />
        </div>
        <div className="col-md-4">
          <StatCard label="Total Records" value={all.length} icon="📋" color="blue" sub="Salary entries" />
        </div>
      </div>

      {/* Filter */}
      <div className="card mb-3">
        <div className="card-body" style={{ padding: '0.75rem 1.25rem' }}>
          <div className="d-flex gap-2 align-items-center flex-wrap">
            <select className="crm-input" style={{ width: 220 }} value={filterEmp} onChange={e => { setFilterEmp(e.target.value); setPage(0) }}>
              <option value="">All Employees</option>
              {employees.map(e => <option key={e.id} value={e.id}>{e.name}</option>)}
            </select>
            <span style={{ marginLeft: 'auto', fontSize: '0.8rem', color: 'var(--text-muted)' }}>{filtered.length} records</span>
          </div>
        </div>
      </div>

      <div className="card">
        <div style={{ overflowX: 'auto' }}>
          <table className="crm-table">
            <thead>
              <tr>
                <th>#</th><th>Employee</th><th>Pay Month</th><th>Basic</th>
                <th>Gross</th><th>Deductions</th><th>Net Pay</th>
                <th>Mode</th><th>Status</th><th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {pageData.length === 0 ? <EmptyState message="No salary records" /> :
                pageData.map((s, i) => (
                  <tr key={s.id}>
                    <td style={{ color: 'var(--text-muted)' }}>{page * 10 + i + 1}</td>
                    <td>
                      <div style={{ fontWeight: 500, color: 'var(--text-primary)' }}>{s.employee?.name}</div>
                      <div className="text-xs text-muted-crm">{s.employee?.position || s.employee?.role}</div>
                    </td>
                    <td style={{ fontWeight: 500 }}>{s.payMonth}</td>
                    <td>₹{Number(s.basicSalary || 0).toLocaleString('en-IN')}</td>
                    <td style={{ color: '#10b981' }}>₹{Number(s.grossSalary || 0).toLocaleString('en-IN')}</td>
                    <td style={{ color: '#ef4444' }}>₹{Number((s.pfDeduction || 0) + (s.taxDeduction || 0) + (s.otherDeduction || 0)).toLocaleString('en-IN')}</td>
                    <td style={{ fontWeight: 700, color: 'var(--brand-primary)', fontSize: '1rem' }}>
                      ₹{Number(s.netSalary || 0).toLocaleString('en-IN')}
                    </td>
                    <td>{s.paymentMode ? <Badge type="muted">{s.paymentMode}</Badge> : '—'}</td>
                    <td><Badge type={statusBadge(s.status)}>{s.status}</Badge></td>
                    <td>
                      <div className="d-flex gap-1">
                        <button className="btn-crm-icon info" title="View Payslip" onClick={() => handlePayslip(s.id)}>🖨</button>
                        {isAdmin() && s.status === 'PENDING' &&
                          <button className="btn-crm-icon success" title="Mark Paid" onClick={() => handlePay(s.id)}>💳</button>}
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

      {/* Generate Salary Modal */}
      <CrmModal show={showModal} onClose={() => setShowModal(false)} title="Generate Salary" size="modal-lg"
        footer={<>
          <button className="btn-crm-ghost" onClick={() => setShowModal(false)}>Cancel</button>
          <button className="btn-crm-primary" disabled={loading} onClick={handleSave}>Generate</button>
        </>}>
        <div className="row g-3">
          <div className="col-md-6">
            <Field label="Employee *">
              <select className="crm-input" name="employeeId" value={form.employeeId} onChange={ch}>
                <option value="">-- Select Employee --</option>
                {employees.map(e => <option key={e.id} value={e.id}>{e.name} — {e.position || e.role}</option>)}
              </select>
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Pay Month *">
              <div className="d-flex gap-2">
                <select className="crm-input" value={selYear} onChange={e => setSelYear(e.target.value)}>
                  {YEARS.map(y => <option key={y} value={y}>{y}</option>)}
                </select>
                <select className="crm-input" value={selMonth} onChange={e => setSelMonth(e.target.value)}>
                  {MONTHS.map(m => <option key={m.value} value={m.value}>{m.label}</option>)}
                </select>
              </div>
              <div style={{ fontSize: '0.72rem', color: 'var(--brand-primary)', marginTop: '4px' }}>
                Will be saved as: <strong>{form.payMonth}</strong>
              </div>
            </Field>
          </div>

          <div className="col-12">
            <div style={{ fontSize: '0.7rem', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.08em', color: '#10b981', marginBottom: '0.25rem' }}>Earnings</div>
            <div style={{ height: '1px', background: 'rgba(16,185,129,0.2)' }} />
          </div>

          <div className="col-md-4"><Field label="Basic Salary *"><input className="crm-input" type="number" name="basicSalary" placeholder="Enter basic salary" value={form.basicSalary} onChange={ch} /></Field></div>
          <div className="col-md-4"><Field label="HRA"><input className="crm-input" type="number" name="hra" placeholder="0" value={form.hra} onChange={ch} /></Field></div>
          <div className="col-md-4"><Field label="Transport Allowance"><input className="crm-input" type="number" name="transportAllowance" placeholder="0" value={form.transportAllowance} onChange={ch} /></Field></div>
          <div className="col-md-4"><Field label="Other Allowance"><input className="crm-input" type="number" name="otherAllowance" placeholder="0" value={form.otherAllowance} onChange={ch} /></Field></div>
          <div className="col-md-4"><Field label="Bonus"><input className="crm-input" type="number" name="bonus" placeholder="0" value={form.bonus} onChange={ch} /></Field></div>

          <div className="col-12">
            <div style={{ fontSize: '0.7rem', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.08em', color: '#ef4444', marginBottom: '0.25rem' }}>Deductions</div>
            <div style={{ height: '1px', background: 'rgba(239,68,68,0.2)' }} />
          </div>
          <div className="col-md-4"><Field label="PF Deduction"><input className="crm-input" type="number" name="pfDeduction" placeholder="0" value={form.pfDeduction} onChange={ch} /></Field></div>
          <div className="col-md-4"><Field label="Tax (TDS)"><input className="crm-input" type="number" name="taxDeduction" placeholder="0" value={form.taxDeduction} onChange={ch} /></Field></div>
          <div className="col-md-4"><Field label="Other Deduction"><input className="crm-input" type="number" name="otherDeduction" placeholder="0" value={form.otherDeduction} onChange={ch} /></Field></div>

          {/* Live summary */}
          <div className="col-12">
            <div style={{ background: 'linear-gradient(135deg,#0f172a,#1e3a5f)', borderRadius: '10px', padding: '1rem 1.5rem', display: 'flex', gap: '3rem', alignItems: 'center' }}>
              <div>
                <div style={{ fontSize: '0.7rem', color: '#94a3b8', textTransform: 'uppercase' }}>Gross</div>
                <div style={{ fontSize: '1.1rem', fontWeight: 700, color: '#e2e8f0' }}>₹{gross.toLocaleString('en-IN')}</div>
              </div>
              <div>
                <div style={{ fontSize: '0.7rem', color: '#94a3b8', textTransform: 'uppercase' }}>Deductions</div>
                <div style={{ fontSize: '1.1rem', fontWeight: 700, color: '#ef4444' }}>₹{deductions.toLocaleString('en-IN')}</div>
              </div>
              <div style={{ marginLeft: 'auto' }}>
                <div style={{ fontSize: '0.7rem', color: '#94a3b8', textTransform: 'uppercase' }}>Net Pay</div>
                <div style={{ fontSize: '1.5rem', fontWeight: 800, color: '#38bdf8' }}>₹{net.toLocaleString('en-IN')}</div>
              </div>
            </div>
          </div>

          <div className="col-md-6">
            <Field label="Payment Mode">
              <select className="crm-input" name="paymentMode" value={form.paymentMode} onChange={ch}>
                {MODES.map(m => <option key={m} value={m}>{m}</option>)}
              </select>
            </Field>
          </div>
          <div className="col-md-6"><Field label="Transaction Ref"><input className="crm-input" name="transactionRef" value={form.transactionRef} onChange={ch} /></Field></div>
          <div className="col-12"><Field label="Notes"><textarea className="crm-input" name="notes" rows={2} value={form.notes} onChange={ch} /></Field></div>
        </div>
      </CrmModal>
    </div>
  )
}