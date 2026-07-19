package com.example.musik.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "user_prefs")

class DataStoreManager(
	context: Context
) {
	private val appContext = context.applicationContext

	companion object {
		val DOWNLOAD_LOCATION_KEY = stringPreferencesKey("download_location")
		val ENTRY_TAB = stringPreferencesKey("entry_tab")
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
		prefs[ENTRY_TAB] ?: "all_music"
	}
	suspend fun setEntryTab(newTab: String) {
		appContext.dataStore.edit { prefs ->
			prefs[ENTRY_TAB] = newTab
		}
	}
}