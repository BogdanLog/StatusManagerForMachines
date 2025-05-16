package data.remote.retrofit

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import data.prefs.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

class TokenPrefs(context: Context) {
    private val dataStore = context.dataStore
    private val tokenKey = stringPreferencesKey("auth_token")

    val tokenFlow: Flow<String?> = dataStore.data.map { it[tokenKey] }

    suspend fun save(token: String) {
        dataStore.edit { it[tokenKey] = token }
    }

    suspend fun clear() {
        dataStore.edit { it.remove(tokenKey) }
    }

    val token: String?
        get() = runBlocking { tokenFlow.firstOrNull() }
}