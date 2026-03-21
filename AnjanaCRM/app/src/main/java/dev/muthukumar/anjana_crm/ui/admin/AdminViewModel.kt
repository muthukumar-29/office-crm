package dev.muthukumar.anjana_crm.ui.admin

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.muthukumar.anjana_crm.data.api.ApiClient
import dev.muthukumar.anjana_crm.data.model.*
import dev.muthukumar.anjana_crm.data.store.TokenStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class AdminState(
    val loading: Boolean = false,
    val error: String? = null,
    val allocations: List<Allocation> = emptyList(),
    val students: List<Student> = emptyList(),
    val certificates: List<Certificate> = emptyList(),
    val salaries: List<Salary> = emptyList(),
    val invoices: List<Invoice> = emptyList(),
    val financeSummary: FinanceSummary? = null,
    val transactions: List<FinanceTransaction> = emptyList(),
    val users: List<User> = emptyList()
)

class AdminViewModel(app: Application) : AndroidViewModel(app) {
    private val store = TokenStore(app)
    private val api   = ApiClient.service

    private val _state = MutableStateFlow(AdminState())
    val state    = _state.asStateFlow()
    val userName = store.name
    val role     = store.role

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val alloc = api.getAllocations()
                val stud  = api.getStudents()
                val certs = api.getCertificates()
                val sal   = api.getAllSalaries()
                val inv   = api.getInvoices()
                val fin   = api.getFinanceSummary()
                val users = api.getUsers()
                val trans = try { api.getTransactions() } catch (_: Exception) { null }
                _state.update {
                    it.copy(
                        loading        = false,
                        allocations    = alloc.body()?.data ?: emptyList(),
                        students       = stud.body() ?: emptyList(),
                        certificates   = certs.body()?.data ?: emptyList(),
                        salaries       = sal.body() ?: emptyList(),
                        invoices       = inv.body()?.data ?: emptyList(),
                        financeSummary = fin.body()?.data,
                        transactions   = trans?.body()?.data ?: emptyList(),
                        users          = users.body() ?: emptyList()
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    // ── Allocations ───────────────────────────────────────────
    fun updateAllocationStatus(id: Long, statusMap: Map<String, String>) {
        viewModelScope.launch {
            try { api.updateAllocationStatus(id, statusMap); loadAll() }
            catch (_: Exception) {}
        }
    }

    // ── Certificates ──────────────────────────────────────────
    fun issueCertificate(allocationId: Long, grade: String?) {
        viewModelScope.launch {
            try {
                val body = buildMap<String, Any> {
                    put("allocationId", allocationId)
                    if (grade != null) put("grade", grade)
                }
                api.issueCertificate(body); loadAll()
            } catch (_: Exception) {}
        }
    }

    // ── Salary ────────────────────────────────────────────────
    fun markSalaryPaid(id: Long) {
        viewModelScope.launch {
            try { api.markSalaryPaid(id); loadAll() }
            catch (_: Exception) {}
        }
    }

    fun createSalary(request: SalaryRequest) {
        viewModelScope.launch {
            try { api.createSalary(request); loadAll() }
            catch (_: Exception) {}
        }
    }

    // ── Invoices ──────────────────────────────────────────────
    fun createInvoice(body: Map<String, Any>) {
        viewModelScope.launch {
            try { api.createInvoice(body); loadAll() }
            catch (_: Exception) {}
        }
    }

    fun markInvoicePaid(id: Long) {
        viewModelScope.launch {
            try { api.markInvoicePaid(id); loadAll() }
            catch (_: Exception) {}
        }
    }

    // ── Finance transactions ──────────────────────────────────
    fun createTransaction(body: Map<String, Any>) {
        viewModelScope.launch {
            try { api.createTransaction(body); loadAll() }
            catch (_: Exception) {}
        }
    }

    fun updateTransaction(id: Long, body: Map<String, Any>) {
        viewModelScope.launch {
            try { api.updateTransaction(id, body); loadAll() }
            catch (_: Exception) {}
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            try { api.deleteTransaction(id); loadAll() }
            catch (_: Exception) {}
        }
    }
}