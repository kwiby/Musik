package com.example.musik.data.datastore

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
		val YTDLP_VERSION_KEY = stringPreferencesKey("ytdlp_version")
		val DO_CONVERT_MP3_KEY = booleanPreferencesKey("do_convert_mp3")
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

	// --===--  Entry Tab  --===--
	val entryTab: Flow<String> = appContext.dataStore.data.map { prefs ->
		prefs[ENTRY_TAB_KEY] ?: "all_music"
	}
	suspend fun setEntryTab(newTab: String) {
		appContext.dataStore.edit { prefs ->
			prefs[ENTRY_TAB_KEY] = newTab
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

	// --===--  Do Convert Mp3  --===--
	val doConvertMp3: Flow<Boolean> = appContext.dataStore.data.map { prefs ->
		prefs[DO_CONVERT_MP3_KEY] ?: false
	}
	suspend fun setDoConvertMp3(newBool: Boolean) {
		appContext.dataStore.edit { prefs ->
			prefs[DO_CONVERT_MP3_KEY] = newBool
		}
	}
}