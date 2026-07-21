package com.example.musik.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musik.data.datastore.DataStoreManager
import com.example.musik.ui.misc.ytdlp.YtDlp
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
	private val dataStoreManager: DataStoreManager,
	private val ytDlp: YtDlp
) : ViewModel() {
	val dataStoreYtDlpVersion: Flow<String> = dataStoreManager.ytDlpVersion
	val ytDlpVersion: StateFlow<String> = ytDlp.ytDlpVersion

	fun updateYtDlp(channel: YoutubeDL.UpdateChannel) {
		viewModelScope.launch {
			ytDlp.updateYtDlp(
				channel,
				dataStoreManager
			)
		}
	}
}