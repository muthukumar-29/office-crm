package dev.muthukumar.anjana_crm.data.store

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("anjana_crm_prefs")

class TokenStore(private val context: Context) {

    companion object {
        val KEY_TOKEN  = stringPreferencesKey("jwt_token")
        val KEY_ROLE   = stringPreferencesKey("user_role")
        val KEY_NAME   = stringPreferencesKey("user_name")
        val KEY_EMAIL  = stringPreferencesKey("user_email")
        val KEY_ID     = stringPreferencesKey("user_id")
    }

    val token: Flow<String?> = context.dataStore.data.map { it[KEY_TOKEN] }
    val role:  Flow<String?> = context.dataStore.data.map { it[KEY_ROLE] }
    val name:  Flow<String?> = context.dataStore.data.map { it[KEY_NAME] }

    suspend fun save(token: String, role: String, name: String, email: String, id: String) {
        context.dataStore.edit {
            it[KEY_TOKEN] = token
            it[KEY_ROLE]  = role
            it[KEY_NAME]  = name
            it[KEY_EMAIL] = email
            it[KEY_ID]    = id
        }
    }

    suspend fun clear() {
        context.dataStore.edit { it.clear() }
    }
}
