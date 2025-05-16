package data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val PREFS_NAME = "user_prefs"

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFS_NAME)

class UserPrefs(context: Context) {
    private val ds = context.dataStore
    private val keyLogin = stringPreferencesKey("current_login")

    val currentLogin: Flow<String?> = ds.data.map { prefs ->
        prefs[keyLogin]
    }

    suspend fun saveLogin(login: String) {
        ds.edit { prefs ->
            prefs[keyLogin] = login
        }
    }

    suspend fun clearLogin() {
        ds.edit { prefs ->
            prefs.remove(keyLogin)
        }
    }
}