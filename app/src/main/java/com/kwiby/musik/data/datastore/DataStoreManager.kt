package com.kwiby.musik.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(
	private val appContext: Context
) {
	companion object {
		val DOWNLOAD_LOCATION_KEY = stringPreferencesKey("download_location")
		val ENTRY_TAB_KEY = stringPreferencesKey("entry_tab")
		val THEME_MODE_KEY = stringPreferencesKey("theme_mode")
		val THEME_STYLE_KEY = stringPreferencesKey("theme_style")
		val APP_ICON_KEY = stringPreferencesKey("app_icon")
		val DO_CONVERT_MP3_KEY = booleanPreferencesKey("do_convert_mp3")
		val YTDLP_VERSION_KEY = stringPreferencesKey("ytdlp_version")
	}

	// --===--  Download Location  --===--
	val downloadLocation: Flow<String> = appContext.dataStore.data.map { prefs ->
		prefs[DOWNLOAD_LOCATION_KEY] ?: ""
	}
	suspend fun setDownloadLocation(path: String) {
		appContext.dataStore.edit { prefs ->
			prefs[DOWNLOAD_LOCATION_KEY] = path
		}
	}

	// --===--  Theme Mode  --===--
	val themeMode: Flow<String> = appContext.dataStore.data.map { prefs ->
		prefs[THEME_MODE_KEY] ?: "DARK"
	}
	suspend fun setThemeMode(newThemeMode: String) {
		appContext.dataStore.edit { prefs ->
			prefs[THEME_MODE_KEY] = newThemeMode
		}
	}

	// --===--  Theme Style  --===--
	val themeStyle: Flow<String> = appContext.dataStore.data.map { prefs ->
		prefs[THEME_STYLE_KEY] ?: "NIGHT"
	}
	suspend fun setThemeStyle(newThemeStyle: String) {
		appContext.dataStore.edit { prefs ->
			prefs[THEME_STYLE_KEY] = newThemeStyle
		}
	}

	// --===--  Entry Tab  --===--
	val entryTab: Flow<String> = appContext.dataStore.data.map { prefs ->
		prefs[ENTRY_TAB_KEY] ?: "all_music"
	}
	suspend fun setEntryTab(newTab: String) {
		appContext.dataStore.edit { prefs ->
			prefs[ENTRY_TAB_KEY] = newTab
		}
	}

	// --===--  App Icon  --===--
	val appIcon: Flow<String> = appContext.dataStore.data.map { prefs ->
		prefs[APP_ICON_KEY] ?: "Default"
	}
	suspend fun setAppIcon(newAppIcon: String) {
		appContext.dataStore.edit { prefs ->
			prefs[APP_ICON_KEY] = newAppIcon
		}
	}

	// --===--  Do Convert Mp3  --===--
	val doConvertMp3: Flow<Boolean> = appContext.dataStore.data.map { prefs ->
		prefs[DO_CONVERT_MP3_KEY] ?: false
	}
	suspend fun setDoConvertMp3(newBool: Boolean) {
		appContext.dataStore.edit { prefs ->
			prefs[DO_CONVERT_MP3_KEY] = newBool
		}
	}

	// --===--  YtDlp Version  --===--
	val ytDlpVersion: Flow<String> = appContext.dataStore.data.map { prefs ->
		prefs[YTDLP_VERSION_KEY] ?: "UNKNOWN"
	}
	suspend fun setYtDlpVersion(newVersion: String) {
		appContext.dataStore.edit { prefs ->
			prefs[YTDLP_VERSION_KEY] = newVersion
		}
	}
}