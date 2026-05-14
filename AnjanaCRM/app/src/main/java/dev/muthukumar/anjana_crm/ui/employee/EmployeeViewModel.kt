package dev.muthukumar.anjana_crm.ui.employee

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.muthukumar.anjana_crm.data.api.ApiClient
import dev.muthukumar.anjana_crm.data.model.Allocation
import dev.muthukumar.anjana_crm.data.model.Salary
import dev.muthukumar.anjana_crm.data.store.TokenStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class EmployeeState(
    val loading: Boolean = false,
    val error: String? = null,
    val allocations: List<Allocation> = emptyList(),
    val salaries: List<Salary> = emptyList()
)

class EmployeeViewModel(app: Application) : AndroidViewModel(app) {
    private val store = TokenStore(app)
    private val api   = ApiClient.service

    private val _state = MutableStateFlow(EmployeeState())
    val state = _state.asStateFlow()

    val userName = store.name
    val role     = store.role

    init { loadAll() }

    fun loadAll() {
        viewModelScope.launch {
            _state.update { it.copy(loading = true, error = null) }
            try {
                val alloc = api.getAllocations()
                val sal   = api.getAllSalaries()
                _state.update {
                    it.copy(
                        loading     = false,
                        allocations = alloc.body()?.data ?: emptyList(),
                        salaries    = sal.body()?.data ?: emptyList()
                    )
                }
            } catch (e: Exception) {
                _state.update { it.copy(loading = false, error = e.message) }
            }
        }
    }

    fun updateWorkStatus(allocationId: Long, category: String, newStatus: String) {
        viewModelScope.launch {
            val key = when (category.uppercase()) {
                "PROJECT" -> "projectStatus"
                "INTERN"  -> "internStatus"
                else      -> "courseStatus"
            }
            try {
                api.updateAllocationStatus(allocationId, mapOf(key to newStatus))
                loadAll()
            } catch (_: Exception) {}
        }
    }
}
