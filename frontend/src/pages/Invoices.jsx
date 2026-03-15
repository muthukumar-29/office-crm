// src/pages/Invoices.jsx — NEW
import React, { useEffect, useState, useCallback } from 'react'
import { createInvoice, getAllInvoices, downloadInvoice } from '../api/services/invoiceService'
import { getAllAllocations } from '../api/services/allocationService'
import { Preloader, CrmPagination, CrmModal, PageHeader, Field, Badge, EmptyState } from '../components/common/ui'
import { usePagination } from '../utils/usePagination'
import Toast from '../utils/toast'

const TODAY = new Date().toISOString().slice(0, 10)
const EMPTY_ITEM = { description: '', quantity: 1, unitPrice: '' }

export default function Invoices() {
  const [invoices,    setInvoices]    = useState([])
  const [allocations, setAllocations] = useState([])
  const [loading,     setLoading]     = useState(false)
  const [showModal,   setShowModal]   = useState(false)
  const [mode,        setMode]        = useState('allocation')
  const [allocId,     setAllocId]     = useState('')
  const [client,      setClient]      = useState({ name: '', email: '', phone: '', address: '' })
  const [invoiceDate, setInvoiceDate] = useState(TODAY)
  const [dueDate,     setDueDate]     = useState('')
  const [items,       setItems]       = useState([{ ...EMPTY_ITEM }])
  const [discount,    setDiscount]    = useState('')
  const [taxPct,      setTaxPct]      = useState('')
  const [notes,       setNotes]       = useState('')
  const { page, setPage, totalPages, pageData } = usePagination(invoices, 10)

  const fetchAll = useCallback(async () => {
    setLoading(true)
    try {
      const [iRes, aRes] = await Promise.all([getAllInvoices(), getAllAllocations()])
      setInvoices(iRes.data?.data || iRes.data || [])
      const eligible = (aRes.data?.data || aRes.data || []).filter(a => a.paymentStatus === 'PAID' && !a.invoiceGenerated)
      setAllocations(eligible)
    } catch { Toast.fire({ icon: 'error', title: 'Failed to load data' }) }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { fetchAll() }, [fetchAll])

  const addItem    = () => setItems(p => [...p, { ...EMPTY_ITEM }])
  const removeItem = (i) => setItems(p => p.filter((_, idx) => idx !== i))
  const updateItem = (i, f, v) => setItems(p => p.map((it, idx) => idx === i ? { ...it, [f]: v } : it))

  const subtotal   = items.reduce((s, it) => s + (Number(it.quantity || 0) * Number(it.unitPrice || 0)), 0)
  const discAmt    = Number(discount || 0)
  const taxAmt     = (subtotal - discAmt) * Number(taxPct || 0) / 100
  const total      = subtotal - discAmt + taxAmt

  const handleSave = async () => {
    if (items.some(it => !it.description || !it.unitPrice)) { Toast.fire({ icon: 'warning', title: 'All items need description and price' }); return }
    if (mode === 'allocation' && !allocId) { Toast.fire({ icon: 'warning', title: 'Select allocation' }); return }
    if (mode === 'manual' && !client.name) { Toast.fire({ icon: 'warning', title: 'Client name required' }); return }
    setLoading(true)
    const payload = {
      ...(mode === 'allocation' ? { allocationId: Number(allocId) } : { clientName: client.name, clientEmail: client.email, clientPhone: client.phone, clientAddress: client.address }),
      invoiceDate, dueDate: dueDate || null, discount: discAmt, taxPercent: Number(taxPct || 0), notes: notes || null,
      items: items.map(it => ({ description: it.description, quantity: Number(it.quantity), unitPrice: Number(it.unitPrice) })),
    }
    try {
      await createInvoice(payload)
      Toast.fire({ icon: 'success', title: '🧾 Invoice created!' })
      setShowModal(false); resetForm(); fetchAll()
    } catch (err) { Toast.fire({ icon: 'error', title: err.response?.data?.message || 'Failed' }) }
    finally { setLoading(false) }
  }

  const handleDownload = async (inv) => {
    setLoading(true)
    try {
      const res = await downloadInvoice(inv.id)
      const url = window.URL.createObjectURL(new Blob([res.data], { type: 'application/pdf' }))
      const a = document.createElement('a'); a.href = url; a.download = `invoice_${inv.invoiceNumber}.pdf`; a.click()
      window.URL.revokeObjectURL(url)
    } catch { Toast.fire({ icon: 'error', title: 'Download failed' }) }
    finally { setLoading(false) }
  }

  const resetForm = () => {
    setMode('allocation'); setAllocId(''); setClient({ name: '', email: '', phone: '', address: '' })
    setInvoiceDate(TODAY); setDueDate(''); setItems([{ ...EMPTY_ITEM }]); setDiscount(''); setTaxPct(''); setNotes('')
  }

  return (
    <div>
      <Preloader show={loading} />
      <PageHeader title="Invoices" subtitle={`${invoices.length} invoices`}
        actions={<button className="btn-crm-primary" onClick={() => setShowModal(true)}>+ Create Invoice</button>} />

      <div className="card">
        <div style={{ overflowX: 'auto' }}>
          <table className="crm-table">
            <thead><tr><th>#</th><th>Invoice No</th><th>Client</th><th>Date</th><th>Total</th><th>Paid</th><th>Balance</th><th>Status</th><th>PDF</th></tr></thead>
            <tbody>
              {pageData.length === 0 ? <EmptyState message="No invoices yet" /> :
                pageData.map((inv, i) => (
                  <tr key={inv.id}>
                    <td style={{ color: 'var(--text-muted)' }}>{page * 10 + i + 1}</td>
                    <td><code style={{ fontSize: '0.72rem', color: 'var(--brand-primary)' }}>{inv.invoiceNumber}</code></td>
                    <td style={{ fontWeight: 500, color: 'var(--text-primary)' }}>{inv.clientName}</td>
                    <td>{inv.invoiceDate}</td>
                    <td style={{ fontWeight: 600 }}>₹{Number(inv.totalAmount).toLocaleString('en-IN')}</td>
                    <td style={{ color: '#10b981' }}>₹{Number(inv.amountPaid).toLocaleString('en-IN')}</td>
                    <td style={{ color: Number(inv.balanceDue) > 0 ? '#ef4444' : '#10b981' }}>₹{Number(inv.balanceDue).toLocaleString('en-IN')}</td>
                    <td><Badge type={inv.status === 'PAID' ? 'success' : 'warning'}>{inv.status}</Badge></td>
                    <td><button className="btn-crm-icon info" onClick={() => handleDownload(inv)}>⬇</button></td>
                  </tr>
                ))}
            </tbody>
          </table>
        </div>
        <div style={{ padding: '0.75rem 1.25rem', borderTop: '1px solid var(--border-subtle)' }}>
          <CrmPagination page={page} totalPages={totalPages} onPageChange={setPage} />
        </div>
      </div>

      <CrmModal show={showModal} onClose={() => { setShowModal(false); resetForm() }} title="Create Invoice" size="modal-xl"
        footer={<><button className="btn-crm-ghost" onClick={() => { setShowModal(false); resetForm() }}>Cancel</button><button className="btn-crm-primary" disabled={loading} onClick={handleSave}>Create Invoice</button></>}>
        {/* Mode */}
        <div className="d-flex gap-2 mb-3">
          {[['allocation', '🎓 Student Allocation'], ['manual', '✍ Manual Invoice']].map(([m, label]) => (
            <button key={m} onClick={() => setMode(m)}
              style={{ flex: 1, padding: '0.55rem', borderRadius: '8px', border: `1px solid ${mode === m ? 'var(--brand-primary)' : 'var(--border-medium)'}`, background: mode === m ? 'var(--brand-glow)' : 'transparent', color: mode === m ? 'var(--brand-primary)' : 'var(--text-secondary)', fontWeight: 600, cursor: 'pointer', fontSize: '0.875rem' }}>
              {label}
            </button>
          ))}
        </div>

        {mode === 'allocation' ? (
          <div className="mb-3">
            <Field label="Fully-paid Allocation *">
              <select className="crm-input" value={allocId} onChange={e => setAllocId(e.target.value)}>
                <option value="">-- Select Allocation --</option>
                {allocations.map(a => <option key={a.id} value={a.id}>{a.student?.name} — {a.category}: {a.course?.name || a.intern?.title || a.project?.title} (₹{a.amountPaid})</option>)}
              </select>
            </Field>
          </div>
        ) : (
          <div className="row g-3 mb-3">
            {[['Client Name *', 'name'], ['Email', 'email'], ['Phone', 'phone'], ['Address', 'address']].map(([l, k]) => (
              <div key={k} className="col-md-6">
                <Field label={l}><input className="crm-input" value={client[k]} onChange={e => setClient(p => ({ ...p, [k]: e.target.value }))} /></Field>
              </div>
            ))}
          </div>
        )}

        <div className="row g-3 mb-3">
          <div className="col-md-4"><Field label="Invoice Date *"><input className="crm-input" type="date" value={invoiceDate} onChange={e => setInvoiceDate(e.target.value)} /></Field></div>
          <div className="col-md-4"><Field label="Due Date"><input className="crm-input" type="date" value={dueDate} onChange={e => setDueDate(e.target.value)} /></Field></div>
        </div>

        {/* Items */}
        <div className="crm-label mb-2">Line Items</div>
        <div style={{ overflowX: 'auto', marginBottom: '0.75rem' }}>
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr style={{ borderBottom: '1px solid var(--border-subtle)' }}>
                {['Description', 'Qty', 'Unit Price', 'Total', ''].map(h => (
                  <th key={h} style={{ padding: '0.5rem 0.5rem', fontSize: '0.7rem', textTransform: 'uppercase', letterSpacing: '0.06em', color: 'var(--text-muted)', textAlign: 'left', fontWeight: 600 }}>{h}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {items.map((it, i) => (
                <tr key={i} style={{ borderBottom: '1px solid var(--border-subtle)' }}>
                  <td style={{ padding: '0.4rem 0.5rem' }}><input className="crm-input" value={it.description} onChange={e => updateItem(i, 'description', e.target.value)} /></td>
                  <td style={{ padding: '0.4rem 0.5rem', width: 80 }}><input className="crm-input" type="number" value={it.quantity} onChange={e => updateItem(i, 'quantity', e.target.value)} /></td>
                  <td style={{ padding: '0.4rem 0.5rem', width: 130 }}><input className="crm-input" type="number" value={it.unitPrice} onChange={e => updateItem(i, 'unitPrice', e.target.value)} /></td>
                  <td style={{ padding: '0.4rem 0.5rem', width: 120, fontWeight: 600, color: 'var(--text-primary)' }}>₹ {(Number(it.quantity||0) * Number(it.unitPrice||0)).toFixed(2)}</td>
                  <td style={{ padding: '0.4rem 0.5rem', width: 40 }}><button className="btn-crm-icon danger" onClick={() => removeItem(i)} disabled={items.length === 1}>✕</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <button className="btn-crm-ghost" style={{ fontSize: '0.8rem' }} onClick={addItem}>+ Add Item</button>

        {/* Totals */}
        <div style={{ marginTop: '1rem', display: 'flex', justifyContent: 'flex-end' }}>
          <div style={{ width: 260 }}>
            {[['Subtotal', `₹ ${subtotal.toFixed(2)}`]].map(([l, v]) => (
              <div key={l} style={{ display: 'flex', justifyContent: 'space-between', padding: '0.3rem 0', fontSize: '0.85rem' }}>
                <span style={{ color: 'var(--text-muted)' }}>{l}</span><span style={{ color: 'var(--text-secondary)' }}>{v}</span>
              </div>
            ))}
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.3rem 0', gap: '0.5rem' }}>
              <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Discount (₹)</span>
              <input className="crm-input" type="number" style={{ width: 90, padding: '0.3rem 0.5rem' }} value={discount} onChange={e => setDiscount(e.target.value)} />
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', padding: '0.3rem 0', gap: '0.5rem' }}>
              <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Tax (%)</span>
              <input className="crm-input" type="number" style={{ width: 90, padding: '0.3rem 0.5rem' }} value={taxPct} onChange={e => setTaxPct(e.target.value)} />
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', padding: '0.5rem 0', borderTop: '1px solid var(--border-subtle)', marginTop: '0.25rem', fontWeight: 700, color: 'var(--brand-primary)', fontSize: '1rem' }}>
              <span>Total</span><span>₹ {total.toFixed(2)}</span>
            </div>
          </div>
        </div>
        <div className="mt-2"><Field label="Notes"><textarea className="crm-input" rows={2} value={notes} onChange={e => setNotes(e.target.value)} /></Field></div>
      </CrmModal>
    </div>
  )
}
