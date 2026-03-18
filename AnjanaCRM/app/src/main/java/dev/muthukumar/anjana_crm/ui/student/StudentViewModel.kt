package dev.muthukumar.anjana_crm.ui.student

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.muthukumar.anjana_crm.data.api.ApiClient
import dev.muthukumar.anjana_crm.data.model.*
import dev.muthukumar.anjana_crm.data.store.TokenStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StudentState(
    val loading: Boolean = false,
    val error: String? = null,
    val allocations: List<Allocation> = emptyList(),
    val certificates: List<Certificate> = emptyList(),
    val payments: List<Payment> = emptyList(),
    val tickets: List<Ticket> = emptyList(),
    val ticketSubmitted: Boolean = false
)

class StudentViewModel(app: Application) : AndroidViewModel(app) {
    private val store = TokenStore(app)
    private val api   = ApiClient.service

    private val _state = MutableStateFlow(StudentState())
    val state    = _state.asStateFlow()
    val userName = store.name

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val alloc = api.getAllocations()
                val certs = api.getCertificates()
                val today = java.time.LocalDate.now()
                val start = today.minusYears(1).toString()
                val pay   = api.getPayments(start, today.toString())
                val tick  = try { api.getTickets() } catch (_: Exception) { null }

                _state.update {
                    it.copy(
                        loading      = false,
                        allocations  = alloc.body()?.data ?: emptyList(),
                        certificates = certs.body()?.data ?: emptyList(),
                        payments     = pay.body()?.data ?: emptyList(),
                        tickets      = tick?.body()?.data ?: emptyList()
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun submitTicket(subject: String, message: String) {
        viewModelScope.launch {
            try {
                api.submitTicket(TicketRequest(subject, message))
                _state.update { it.copy(ticketSubmitted = true) }
                loadAll()
            } catch (_: Exception) {}
        }
    }

    fun resetTicketFlag() = _state.update { it.copy(ticketSubmitted = false) }
}
