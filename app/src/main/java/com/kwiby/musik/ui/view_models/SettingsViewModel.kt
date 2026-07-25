package com.kwiby.musik.ui.view_models

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwiby.musik.data.datastore.DataStoreManager
import com.kwiby.musik.ui.misc.ytdlp.YtDlp
import com.kwiby.musik.ui.theme.ThemeStyle
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val LOG_TAG = "SettingsViewModel"

class SettingsViewModel(
	private val dataStoreManager: DataStoreManager,
	private val ytDlp: YtDlp
) : ViewModel() {
	fun <T> Flow<T>.stateInViewModel(): StateFlow<T?> =
		stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

	val dataStoreThemeMode: StateFlow<String?> = dataStoreManager.themeMode.stateInViewModel()
	val dataStoreThemeStyle: StateFlow<String?> = dataStoreManager.themeStyle.stateInViewModel()
	val dataStoreAppIcon: StateFlow<String?> = dataStoreManager.appIcon.stateInViewModel()
	val dataStoreDoConvertMp3: StateFlow<Boolean?> = dataStoreManager.doConvertMp3.stateInViewModel()
	val dataStoreYtDlpVersion: StateFlow<String?> = dataStoreManager.ytDlpVersion.stateInViewModel()
	val ytDlpVersion: StateFlow<String> = ytDlp.ytDlpVersion


	suspend fun switchAppIcon(context: Context, newAlias: String) {
		val validAliases = setOf(
			"Default", "Black", "Blue",
			"Green", "Orange", "Pink",
			"Purple", "Red", "White"
		)
		if (!validAliases.contains(newAlias)) {
			Log.e(LOG_TAG, "Invalid newAlias")
			return
		}

		withContext(Dispatchers.IO) {
			val currentAlias = dataStoreManager.appIcon.first()
			if (currentAlias == newAlias) {
				return@withContext
			}
			dataStoreManager.setAppIcon(newAlias)

			val packageManager = context.packageManager
			val packageName = context.packageName
			packageManager.setComponentEnabledSetting(
				ComponentName(context, "$packageName.${newAlias}Icon"),
				PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
				PackageManager.DONT_KILL_APP
			)
			packageManager.setComponentEnabledSetting(
				ComponentName(context, "$packageName.${currentAlias}Icon"),
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
				PackageManager.DONT_KILL_APP
			)
		}
	}

	fun switchThemeMode() {
		viewModelScope.launch {
			if (dataStoreThemeMode.first() == "DARK") {
				dataStoreManager.setThemeMode("LIGHT")
			} else {
				dataStoreManager.setThemeMode("DARK")
			}
		}
	}

	fun switchThemeStyle(newThemeStyle: ThemeStyle) {
		viewModelScope.launch {
			if (newThemeStyle.name == dataStoreThemeStyle.first()) {
				return@launch
			} else {
				dataStoreManager.setThemeStyle(newThemeStyle.name)
			}
		}
	}

	fun updateYtDlp(channel: YoutubeDL.UpdateChannel) {
		viewModelScope.launch {
			ytDlp.updateYtDlp(
				channel,
				dataStoreManager
			)
		}
	}

	fun toggleDoConvertMp3() {
		viewModelScope.launch {
			// This function should only be called after dataStoreDoConvertMp3 != null, hence the !!
			val curBool = dataStoreDoConvertMp3.first()!!
			dataStoreManager.setDoConvertMp3(!curBool)
		}
	}
}