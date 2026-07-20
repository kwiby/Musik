package com.example.musik.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musik.ui.misc.ytdlp.YtDlp
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
	private val ytDlp: YtDlp
) : ViewModel() {
	fun updateYtDlp(channel: YoutubeDL.UpdateChannel) {
		viewModelScope.launch {
			ytDlp.updateYtDlp(channel)
		}
	}

	fun getYtDlpVersionStateFlow(): StateFlow<String> {
		return ytDlp.ytDlpVersion
	}
}