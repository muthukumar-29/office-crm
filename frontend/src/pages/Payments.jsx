// src/pages/Payments.jsx — NEW
import React, { useEffect, useState, useCallback } from 'react'
import { recordPayment, getPaymentsByDateRange, getPaymentsByAllocation } from '../api/services/paymentService'
import { getAllAllocations } from '../api/services/allocationService'
import { Preloader, CrmPagination, CrmModal, PageHeader, Field, Badge, EmptyState } from '../components/common/ui'
import { usePagination } from '../utils/usePagination'
import Toast from '../utils/toast'

const TODAY = new Date().toISOString().slice(0, 10)
const MODES = ['CASH', 'UPI', 'BANK_TRANSFER', 'CHEQUE', 'ONLINE']
const EMPTY = { allocationId: '', amount: '', paymentMode: 'CASH', paymentDate: TODAY, transactionRef: '', remarks: '' }
const modeColor = m => m === 'UPI' ? 'info' : m === 'CASH' ? 'success' : m === 'BANK_TRANSFER' ? 'purple' : 'muted'

export default function Payments() {
  const [allocations, setAllocations] = useState([])
  const [payments, setPayments]       = useState([])
  const [loading, setLoading]         = useState(false)
  const [showModal, setShowModal]     = useState(false)
  const [form, setForm]               = useState(EMPTY)
  const [selectedAlloc, setSelectedAlloc] = useState(null)
  const [dateFilter, setDateFilter]   = useState({ start: TODAY.slice(0, 7) + '-01', end: TODAY })
  const { page, setPage, totalPages, pageData } = usePagination(payments, 10)

  const fetchAllocations = useCallback(async () => {
    try {
      const res = await getAllAllocations()
      const active = (res.data?.data || res.data || []).filter(a => a.allocationStatus === 'ACTIVE')
      setAllocations(active)
    } catch { Toast.fire({ icon: 'error', title: 'Failed to load allocations' }) }
  }, [])

  const fetchPayments = useCallback(async () => {
    if (!dateFilter.start || !dateFilter.end) return
    setLoading(true)
    try {
      const res = await getPaymentsByDateRange(dateFilter.start, dateFilter.end)
      setPayments(res.data?.data || res.data || [])
    } catch { Toast.fire({ icon: 'error', title: 'Failed to load payments' }) }
    finally { setLoading(false) }
  }, [dateFilter])

  useEffect(() => { fetchAllocations(); fetchPayments() }, [fetchAllocations, fetchPayments])

  const ch = e => {
    const { name, value } = e.target
    setForm(p => ({ ...p, [name]: value }))
    if (name === 'allocationId') {
      setSelectedAlloc(allocations.find(a => String(a.id) === String(value)) || null)
    }
  }

  const handleSave = async () => {
    if (!form.allocationId || !form.amount || !form.paymentDate) {
      Toast.fire({ icon: 'warning', title: 'Allocation, Amount and Date required' }); return
    }
    setLoading(true)
    const payload = { allocationId: Number(form.allocationId), amount: Number(form.amount), paymentMode: form.paymentMode, paymentDate: form.paymentDate, transactionRef: form.transactionRef || null, remarks: form.remarks || null }
    try {
      await recordPayment(payload)
      Toast.fire({ icon: 'success', title: '✓ Payment recorded. Finance entry auto-created.' })
      setShowModal(false); setForm(EMPTY); setSelectedAlloc(null)
      fetchAllocations(); fetchPayments()
    } catch (err) { Toast.fire({ icon: 'error', title: err.response?.data?.message || 'Failed' }) }
    finally { setLoading(false) }
  }

  return (
    <div>
      <Preloader show={loading} />
      <PageHeader title="Payments" subtitle="Record and track student payments"
        actions={<button className="btn-crm-primary" onClick={() => setShowModal(true)}>+ Record Payment</button>} />

      {/* Date filter */}
      <div className="card mb-3">
        <div className="card-body" style={{ padding: '0.75rem 1.25rem' }}>
          <div className="d-flex gap-2 align-items-center flex-wrap">
            <span className="crm-label mb-0">From</span>
            <input className="crm-input" type="date" style={{ width: 160 }} value={dateFilter.start} onChange={e => setDateFilter(p => ({ ...p, start: e.target.value }))} />
            <span className="crm-label mb-0">To</span>
            <input className="crm-input" type="date" style={{ width: 160 }} value={dateFilter.end} onChange={e => setDateFilter(p => ({ ...p, end: e.target.value }))} />
            <button className="btn-crm-primary" onClick={fetchPayments}>Filter</button>
            <span style={{ marginLeft: 'auto', fontSize: '0.8rem', color: 'var(--text-muted)' }}>{payments.length} records</span>
          </div>
        </div>
      </div>

      <div className="card">
        <div style={{ overflowX: 'auto' }}>
          <table className="crm-table">
            <thead><tr><th>#</th><th>Receipt</th><th>Student</th><th>Amount</th><th>Mode</th><th>Date</th><th>Reference</th><th>Remarks</th></tr></thead>
            <tbody>
              {pageData.length === 0
                ? <EmptyState message="No payments in selected date range" />
                : pageData.map((p, i) => (
                  <tr key={p.id}>
                    <td style={{ color: 'var(--text-muted)' }}>{page * 10 + i + 1}</td>
                    <td><code style={{ fontSize: '0.72rem', color: 'var(--brand-primary)' }}>{p.receiptNumber}</code></td>
                    <td style={{ fontWeight: 500, color: 'var(--text-primary)' }}>{p.allocation?.student?.name}</td>
                    <td style={{ fontWeight: 600, color: '#10b981' }}>₹ {p.amount}</td>
                    <td><Badge type={modeColor(p.paymentMode)}>{p.paymentMode}</Badge></td>
                    <td>{p.paymentDate}</td>
                    <td style={{ fontSize: '0.78rem' }}>{p.transactionRef || '—'}</td>
                    <td style={{ fontSize: '0.78rem' }}>{p.remarks || '—'}</td>
                  </tr>
                ))}
            </tbody>
          </table>
        </div>
        <div style={{ padding: '0.75rem 1.25rem', borderTop: '1px solid var(--border-subtle)' }}>
          <CrmPagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
      </div>

      <CrmModal show={showModal} onClose={() => { setShowModal(false); setForm(EMPTY); setSelectedAlloc(null) }} title="Record Payment" size="modal-lg"
        footer={<><button className="btn-crm-ghost" onClick={() => setShowModal(false)}>Cancel</button><button className="btn-crm-primary" disabled={loading} onClick={handleSave}>Record</button></>}>
        <div className="row g-3">
          <div className="col-12">
            <Field label="Allocation (Active only) *">
              <select className="crm-input" name="allocationId" value={form.allocationId} onChange={ch}>
                <option value="">-- Select Allocation --</option>
                {allocations.map(a => (
                  <option key={a.id} value={a.id}>
                    {a.student?.name} — {a.category}: {a.course?.name || a.intern?.title || a.project?.title} (Balance: ₹{a.balanceDue})
                  </option>
                ))}
              </select>
            </Field>
          </div>
          {selectedAlloc && (
            <div className="col-12">
              <div style={{ background: 'rgba(14,165,233,0.08)', border: '1px solid rgba(14,165,233,0.2)', borderRadius: '8px', padding: '0.65rem 1rem', fontSize: '0.82rem', color: 'var(--text-secondary)', display: 'flex', gap: '1.5rem' }}>
                <span><strong style={{ color: 'var(--text-primary)' }}>Total:</strong> ₹{selectedAlloc.totalFee}</span>
                <span><strong style={{ color: 'var(--text-primary)' }}>Paid:</strong> ₹{selectedAlloc.amountPaid}</span>
                <span><strong style={{ color: '#ef4444' }}>Balance:</strong> ₹{selectedAlloc.balanceDue}</span>
              </div>
            </div>
          )}
          <div className="col-md-6">
            <Field label="Amount (₹) *">
              <input className="crm-input" type="number" name="amount" value={form.amount} onChange={ch} max={selectedAlloc?.balanceDue} />
            </Field>
          </div>
          <div className="col-md-6">
            <Field label="Payment Mode *">
              <select className="crm-input" name="paymentMode" value={form.paymentMode} onChange={ch}>
                {MODES.map(m => <option key={m} value={m}>{m}</option>)}
              </select>
            </Field>
          </div>
          <div className="col-md-6"><Field label="Payment Date *"><input className="crm-input" type="date" name="paymentDate" value={form.paymentDate} onChange={ch} /></Field></div>
          <div className="col-md-6"><Field label="Transaction Ref / UPI ID"><input className="crm-input" name="transactionRef" value={form.transactionRef} onChange={ch} /></Field></div>
          <div className="col-12"><Field label="Remarks"><input className="crm-input" name="remarks" value={form.remarks} onChange={ch} /></Field></div>
        </div>
      </CrmModal>
    </div>
  )
}
