// ─────────────────────────────────────────────────────────────
// SessionManager.kt
// ─────────────────────────────────────────────────────────────
package dev.muthukumar.anjana_crm.domain

import dev.muthukumar.anjana_crm.data.store.TokenStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class SessionManager(private val store: TokenStore) {

    // Synchronous read used only by AuthInterceptor at request time
    fun getTokenSync(): String? = runBlocking { store.token.first() }

    fun isAdmin(role: String?)   = role in listOf("SUPER_ADMIN", "ADMIN")
    fun isEmployee(role: String?) = role == "EMPLOYEE" || role == "SUB_ADMIN"
    fun isStudent(role: String?)  = role == "STUDENT"

    fun getRole(): String? = runBlocking { store.role.first() }
    suspend fun getRoleOnce()    = store.role.first()
    suspend fun clear()          = store.clear()

    fun getNavigationDestination(): String {
        val role = getRole() ?: return "login"
        return when {
            role.contains("ADMIN") -> "admin"
            role == "STUDENT"      -> "student"
            else                   -> "employee"
        }
    }
}
