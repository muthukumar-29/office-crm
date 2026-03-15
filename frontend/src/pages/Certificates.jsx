// src/pages/Certificates.jsx — NEW
import React, { useEffect, useState, useCallback } from 'react'
import { previewCertificate, issueCertificate, getAllCertificates, downloadCertificate } from '../api/services/certificateService'
import { getAllAllocations } from '../api/services/allocationService'
import { Preloader, CrmPagination, CrmModal, PageHeader, Field, Badge, EmptyState } from '../components/common/ui'
import { usePagination } from '../utils/usePagination'
import Toast from '../utils/toast'

export default function Certificates() {
  const [certs, setCerts]         = useState([])
  const [allocations, setAllocs]  = useState([])
  const [loading, setLoading]     = useState(false)
  const [showModal, setShowModal] = useState(false)
  const [preview, setPreview]     = useState(null)
  const [form, setForm]           = useState({ allocationId: '', grade: '', remarks: '', issuedDate: '' })
  const { page, setPage, totalPages, pageData } = usePagination(certs, 10)

  const fetchAll = useCallback(async () => {
    setLoading(true)
    try {
      const [cRes, aRes] = await Promise.all([getAllCertificates(), getAllAllocations()])
      setCerts(cRes.data?.data || cRes.data || [])
      const eligible = (aRes.data?.data || aRes.data || []).filter(a => a.allocationStatus === 'COMPLETED' && !a.certificateIssued)
      setAllocs(eligible)
    } catch { Toast.fire({ icon: 'error', title: 'Failed to load data' }) }
    finally { setLoading(false) }
  }, [])

  useEffect(() => { fetchAll() }, [fetchAll])

  const handlePreview = async () => {
    if (!form.allocationId) { Toast.fire({ icon: 'warning', title: 'Select an allocation' }); return }
    setLoading(true)
    try { const res = await previewCertificate(form.allocationId); setPreview(res.data?.data || res.data) }
    catch (err) { Toast.fire({ icon: 'error', title: err.response?.data?.message || 'Preview failed' }) }
    finally { setLoading(false) }
  }

  const handleIssue = async () => {
    if (!form.allocationId || !preview) { Toast.fire({ icon: 'warning', title: 'Preview first, then issue' }); return }
    setLoading(true)
    const payload = { allocationId: Number(form.allocationId), grade: form.grade || null, remarks: form.remarks || null, issuedDate: form.issuedDate || null }
    try {
      await issueCertificate(payload)
      Toast.fire({ icon: 'success', title: '🎓 Certificate issued!' })
      setShowModal(false); setPreview(null); setForm({ allocationId: '', grade: '', remarks: '', issuedDate: '' })
      fetchAll()
    } catch (err) { Toast.fire({ icon: 'error', title: err.response?.data?.message || 'Issue failed' }) }
    finally { setLoading(false) }
  }

  const handleDownload = async (cert) => {
    setLoading(true)
    try {
      const res = await downloadCertificate(cert.allocationId || cert.allocation?.id)
      const url = window.URL.createObjectURL(new Blob([res.data], { type: 'application/pdf' }))
      const a = document.createElement('a'); a.href = url; a.download = `cert_${cert.certificateNumber}.pdf`; a.click()
      window.URL.revokeObjectURL(url)
    } catch { Toast.fire({ icon: 'error', title: 'Download failed' }) }
    finally { setLoading(false) }
  }

  const INFO = [
    ['Student', 'studentName'], ['Roll No', 'rollNo'], ['College', 'collegeName'],
    ['Department', 'department'], ['Domain', 'domainName'], ['Program', 'programTitle'],
    ['Category', 'category'], ['Start', 'startDate'], ['End', 'endDate'],
  ]

  return (
    <div>
      <Preloader show={loading} />
      <PageHeader title="Certificates" subtitle={`${certs.length} issued`}
        actions={<button className="btn-crm-primary" onClick={() => setShowModal(true)}>🎓 Issue Certificate</button>} />

      <div className="card">
        <div style={{ overflowX: 'auto' }}>
          <table className="crm-table">
            <thead><tr><th>#</th><th>Cert No</th><th>Student</th><th>Program</th><th>Domain</th><th>Grade</th><th>Issued</th><th>PDF</th></tr></thead>
            <tbody>
              {pageData.length === 0 ? <EmptyState message="No certificates issued yet" /> :
                pageData.map((c, i) => (
                  <tr key={c.id}>
                    <td style={{ color: 'var(--text-muted)' }}>{page * 10 + i + 1}</td>
                    <td><code style={{ fontSize: '0.72rem', color: 'var(--brand-primary)' }}>{c.certificateNumber}</code></td>
                    <td>
                      <div style={{ fontWeight: 500, color: 'var(--text-primary)' }}>{c.studentName}</div>
                      <div className="text-xs text-muted-crm">{c.collegeName}</div>
                    </td>
                    <td>{c.programTitle}</td>
                    <td>{c.domainName}</td>
                    <td>{c.grade ? <Badge type="success">{c.grade}</Badge> : '—'}</td>
                    <td>{c.issuedDate}</td>
                    <td>
                      <button className="btn-crm-icon info" title="Download PDF" onClick={() => handleDownload(c)}>⬇</button>
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

      <CrmModal show={showModal} onClose={() => { setShowModal(false); setPreview(null) }} title="Issue Certificate" size="modal-lg"
        footer={<>
          <button className="btn-crm-ghost" onClick={() => { setShowModal(false); setPreview(null) }}>Cancel</button>
          <button className="btn-crm-ghost" onClick={handlePreview}>Preview Details</button>
          <button className="btn-crm-primary" disabled={loading || !preview} onClick={handleIssue}>Issue Certificate</button>
        </>}>
        <div className="row g-3 mb-3">
          <div className="col-12">
            <Field label="Completed Allocation (COMPLETED status required) *">
              <select className="crm-input" value={form.allocationId} onChange={e => { setForm(p => ({ ...p, allocationId: e.target.value })); setPreview(null) }}>
                <option value="">-- Select Allocation --</option>
                {allocations.map(a => <option key={a.id} value={a.id}>{a.student?.name} — {a.category}: {a.course?.name || a.intern?.title || a.project?.title}</option>)}
              </select>
            </Field>
          </div>
        </div>

        {preview && (
          <div style={{ background: 'var(--surface-1)', border: '1px solid var(--brand-primary)', borderRadius: '10px', padding: '1.25rem', marginBottom: '1rem' }}>
            <div style={{ fontSize: '0.7rem', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.08em', color: 'var(--brand-primary)', marginBottom: '0.75rem' }}>Certificate Preview</div>
            <div className="row g-2">
              {INFO.map(([label, key]) => preview[key] && (
                <div key={key} className="col-md-4">
                  <div className="text-xs text-muted-crm">{label}</div>
                  <div style={{ fontSize: '0.82rem', color: 'var(--text-primary)', fontWeight: 500 }}>{preview[key]}</div>
                </div>
              ))}
            </div>
            <div className="divider" />
            <div className="row g-3">
              <div className="col-md-4"><Field label="Grade"><input className="crm-input" placeholder="A, B, Distinction…" value={form.grade} onChange={e => setForm(p => ({ ...p, grade: e.target.value }))} /></Field></div>
              <div className="col-md-4"><Field label="Issue Date"><input className="crm-input" type="date" value={form.issuedDate} onChange={e => setForm(p => ({ ...p, issuedDate: e.target.value }))} /></Field></div>
              <div className="col-md-12"><Field label="Remarks"><input className="crm-input" placeholder="Optional remarks" value={form.remarks} onChange={e => setForm(p => ({ ...p, remarks: e.target.value }))} /></Field></div>
            </div>
          </div>
        )}

        {!preview && form.allocationId && (
          <div style={{ textAlign: 'center', padding: '2rem', color: 'var(--text-muted)', fontSize: '0.85rem' }}>
            Click "Preview Details" to load student information before issuing
          </div>
        )}
      </CrmModal>
    </div>
  )
}
