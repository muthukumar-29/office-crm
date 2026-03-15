import { useState, useMemo } from 'react'
export function usePagination(data = [], ps = 10) {
  const [page, setPage] = useState(0)
  const total = Math.ceil(data.length / ps)
  const pageData = useMemo(() => data.slice(page * ps, page * ps + ps), [data, page, ps])
  return { page, setPage, totalPages: total, pageData, reset: () => setPage(0) }
}
