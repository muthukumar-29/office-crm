// src/pages/Finance.jsx — NEW
import React, { useEffect, useState, useCallback } from 'react'
import { recordTransaction, getAllTransactions, getFinanceSummary } from '../api/services/financeService'
import { getAllCategories } from '../api/services/categoryService'
import { Preloader, CrmPagination, CrmModal, PageHeader, Field, Badge, EmptyState, StatCard } from '../components/common/ui'
import { usePagination } from '../utils/usePagination'
import Toast from '../utils/toast'

const TODAY = new Date().toISOString().slice(0, 10)
const MONTH_START = new Date(new Date().getFullYear(), new Date().getMonth(), 2).toISOString().slice(0, 10)
const MODES = ['CASH', 'UPI', 'BANK_TRANSFER', 'CHEQUE', 'ONLINE']
const FALLBACK_INC = ['Student Fee', 'Workshop', 'Consultation', 'Office Project', 'Other Income']
const FALLBACK_EXP = ['Salary', 'Rent', 'Utilities', 'Equipment', 'Marketing', 'Travel', 'Miscellaneous']
const EMPTY = { type: 'EXPENSE', amount: '', category: '', description: '', paymentMode: 'CASH', transactionDate: TODAY, referenceNo: '', notes: '' }

export default function Finance() {
  const [txns, setTxns]               = useState([])
  const [summary, setSummary]         = useState(null)
  const [loading, setLoading]         = useState(false)
  const [showModal, setShowModal]     = useState(false)
  const [form, setForm]               = useState(EMPTY)
  const [typeFilter, setTypeFilter]   = useState('')
  const [dateFilter, setDateFilter]   = useState({ start: MONTH_START, end: TODAY })
  const [dynCategories, setDynCategories] = useState([])

  const filtered = typeFilter ? txns.filter(t => t.type === typeFilter) : txns
  const { page, setPage, totalPages, pageData } = usePagination(filtered, 12)

  const fetchAll = useCallback(async () => {
    setLoading(true)
    try {
      const [tRes, sRes, cRes] = await Promise.all([
        getAllTransactions({ start: dateFilter.start, end: dateFilter.end }),
        getFinanceSummary(dateFilter.start, dateFilter.end),
        getAllCategories(),
      ])
      setTxns(tRes.data?.data || tRes.data || [])
      setSummary(sRes.data?.data || sRes.data)
      const cats = cRes.data?.data || cRes.data || []
      if (cats.length > 0) setDynCategories(cats.map(c => c.name))
    } catch { Toast.fire({ icon: 'error', title: 'Failed to load finance data' }) }
    finally { setLoading(false) }
  }, [dateFilter])

  useEffect(() => { fetchAll() }, [fetchAll])

  const ch = e => setForm(p => ({ ...p, [e.target.name]: e.target.value }))

  const handleSave = async () => {
    if (!form.amount || !form.category || !form.transactionDate) {
      Toast.fire({ icon: 'warning', title: 'Amount, Category and Date required' }); return
    }
    setLoading(true)
    const payload = { type: form.type, amount: Number(form.amount), category: form.category, description: form.description || null, paymentMode: form.paymentMode || null, transactionDate: form.transactionDate, referenceNo: form.referenceNo || null, notes: form.notes || null }
    try {
      await recordTransaction(payload)
      Toast.fire({ icon: 'success', title: 'Transaction recorded' })
      setShowModal(false); setForm(EMPTY); fetchAll()
    } catch (err) { Toast.fire({ icon: 'error', title: err.response?.data?.message || 'Failed' }) }
    finally { setLoading(false) }
  }

  const cats = dynCategories.length > 0
    ? dynCategories
    : (form.type === 'INCOME' ? FALLBACK_INC : FALLBACK_EXP)

  return (
    <div>
      <Preloader show={loading} />
      <PageHeader title="Finance" subtitle="Track income and expenses"
        actions={<button className="btn-crm-primary" onClick={() => setShowModal(true)}>+ Record Transaction</button>} />

      {/* Summary */}
      {summary && (
        <div className="row g-3 mb-4">
          <div className="col-md-4">
            <StatCard label="Total Income" value={`₹${Number(summary.totalIncome||0).toLocaleString('en-IN')}`} icon="⬆" color="green" />
          </div>
          <div className="col-md-4">
            <StatCard label="Total Expense" value={`₹${Number(summary.totalExpense||0).toLocaleString('en-IN')}`} icon="⬇" color="red" />
          </div>
          <div className="col-md-4">
            <StatCard label="Net Balance" value={`₹${Number(summary.netBalance||0).toLocaleString('en-IN')}`} icon="⚖" color={Number(summary.netBalance) >= 0 ? 'blue' : 'amber'} />
          </div>
        </div>
      )}

      {/* Filter */}
      <div className="card mb-3">
        <div className="card-body" style={{ padding: '0.75rem 1.25rem' }}>
          <div className="d-flex gap-2 align-items-center flex-wrap">
            <input className="crm-input" type="date" style={{ width: 160 }} value={dateFilter.start} onChange={e => setDateFilter(p => ({ ...p, start: e.target.value }))} />
            <span className="text-muted-crm text-sm">to</span>
            <input className="crm-input" type="date" style={{ width: 160 }} value={dateFilter.end} onChange={e => setDateFilter(p => ({ ...p, end: e.target.value }))} />
            <button className="btn-crm-primary" onClick={fetchAll}>Apply</button>
            <select className="crm-input" style={{ width: 150, marginLeft: 'auto' }} value={typeFilter} onChange={e => { setTypeFilter(e.target.value); setPage(0) }}>
              <option value="">All Types</option>
              <option value="INCOME">Income</option>
              <option value="EXPENSE">Expense</option>
            </select>
          </div>
        </div>
      </div>

      <div className="card">
        <div style={{ overflowX: 'auto' }}>
          <table className="crm-table">
            <thead><tr><th>#</th><th>Type</th><th>Category</th><th>Description</th><th>Amount</th><th>Mode</th><th>Date</th><th>Ref</th></tr></thead>
            <tbody>
              {pageData.length === 0 ? <EmptyState message="No transactions in selected range" /> :
                pageData.map((t, i) => (
                  <tr key={t.id}>
                    <td style={{ color: 'var(--text-muted)' }}>{page * 12 + i + 1}</td>
                    <td><Badge type={t.type === 'INCOME' ? 'success' : 'danger'}>{t.type}</Badge></td>
                    <td style={{ fontWeight: 500, color: 'var(--text-primary)' }}>{t.category}</td>
                    <td style={{ fontSize: '0.8rem' }}>{t.description || '—'}</td>
                    <td style={{ fontWeight: 600, color: t.type === 'INCOME' ? '#10b981' : '#ef4444' }}>
                      {t.type === 'INCOME' ? '+' : '-'} ₹{Number(t.amount).toLocaleString('en-IN')}
                    </td>
                    <td>{t.paymentMode ? <Badge type="muted">{t.paymentMode}</Badge> : '—'}</td>
                    <td>{t.transactionDate}</td>
                    <td style={{ fontSize: '0.75rem' }}>{t.referenceNo || '—'}</td>
                  </tr>
                ))}
            </tbody>
          </table>
        </div>
        <div style={{ padding: '0.75rem 1.25rem', borderTop: '1px solid var(--border-subtle)' }}>
          <CrmPagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
      </div>

      <CrmModal show={showModal} onClose={() => setShowModal(false)} title="Record Transaction" size="modal-lg"
        footer={<><button className="btn-crm-ghost" onClick={() => setShowModal(false)}>Cancel</button><button className="btn-crm-primary" disabled={loading} onClick={handleSave}>Save</button></>}>
        {/* Type toggle */}
        <div className="d-flex gap-2 mb-3">
          {['INCOME', 'EXPENSE'].map(t => (
            <button key={t} onClick={() => setForm(p => ({ ...p, type: t, category: '' }))}
              style={{ flex: 1, padding: '0.55rem', borderRadius: '8px', border: `1px solid ${form.type === t ? (t === 'INCOME' ? '#10b981' : '#ef4444') : 'var(--border-medium)'}`, background: form.type === t ? (t === 'INCOME' ? 'rgba(16,185,129,0.12)' : 'rgba(239,68,68,0.12)') : 'transparent', color: form.type === t ? (t === 'INCOME' ? '#10b981' : '#ef4444') : 'var(--text-secondary)', fontWeight: 600, cursor: 'pointer', fontSize: '0.875rem' }}>
              {t === 'INCOME' ? '⬆ Income' : '⬇ Expense'}
            </button>
          ))}
        </div>
        <div className="row g-3">
          <div className="col-md-6"><Field label="Amount (₹) *"><input className="crm-input" type="number" name="amount" value={form.amount} onChange={ch} /></Field></div>
          <div className="col-md-6">
            <Field label="Category *">
              <select className="crm-input" name="category" value={form.category} onChange={ch}>
                <option value="">-- Select Category --</option>
                {cats.map(c => <option key={c} value={c}>{c}</option>)}
              </select>
            </Field>
          </div>
          <div className="col-md-6"><Field label="Date *"><input className="crm-input" type="date" name="transactionDate" value={form.transactionDate} onChange={ch} /></Field></div>
          <div className="col-md-6">
            <Field label="Payment Mode">
              <select className="crm-input" name="paymentMode" value={form.paymentMode} onChange={ch}>
                {MODES.map(m => <option key={m} value={m}>{m}</option>)}
              </select>
            </Field>
          </div>
          <div className="col-md-6"><Field label="Reference No"><input className="crm-input" name="referenceNo" value={form.referenceNo} onChange={ch} /></Field></div>
          <div className="col-md-6"><Field label="Description"><input className="crm-input" name="description" value={form.description} onChange={ch} /></Field></div>
          <div className="col-12"><Field label="Notes"><textarea className="crm-input" name="notes" rows={2} value={form.notes} onChange={ch} /></Field></div>
        </div>
      </CrmModal>
    </div>
  )
}
