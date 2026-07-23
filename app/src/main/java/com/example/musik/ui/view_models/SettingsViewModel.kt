package com.example.musik.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musik.data.datastore.DataStoreManager
import com.example.musik.ui.misc.ytdlp.YtDlp
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
	private val dataStoreManager: DataStoreManager,
	private val ytDlp: YtDlp
) : ViewModel() {
	val dataStoreDoConvertMp3: StateFlow<Boolean?> = dataStoreManager.doConvertMp3
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = null
		)
	val dataStoreYtDlpVersion: StateFlow<String?> = dataStoreManager.ytDlpVersion
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = null
		)
	val ytDlpVersion: StateFlow<String> = ytDlp.ytDlpVersion

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