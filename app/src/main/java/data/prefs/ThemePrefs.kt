package data.prefs

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.Flow

sealed class ThemeOption(val key: String) {
    data object Light : ThemeOption("light")
    data object Dark : ThemeOption("dark")
    data object System : ThemeOption("system")
    companion object {
        fun fromKey(key: String?): ThemeOption = when (key) {
            Light.key -> Light
            Dark.key -> Dark
            else -> System
        }
    }
}

private const val PREFS_NAME = "theme_prefs"
val Context.themeDataStore by preferencesDataStore(name = PREFS_NAME)

class ThemePrefs(context: Context) {
    private val ds = context.themeDataStore
    private val themeKey = stringPreferencesKey("theme_option")

    val themeOptionFlow: Flow<ThemeOption> = ds.data
        .map { prefs -> ThemeOption.fromKey(prefs[themeKey]) }

    suspend fun saveTheme(option: ThemeOption) {
        ds.edit { prefs ->
            prefs[themeKey] = option.key
        }
    }
}
