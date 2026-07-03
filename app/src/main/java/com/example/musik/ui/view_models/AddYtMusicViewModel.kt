package com.example.musik.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musik.data.datastore.DataStoreManager
import com.example.musik.data.repositories.audio_file.AudioFileRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class AddYtMusicViewModel(
	private val dataStoreManager: DataStoreManager,
	private val audioFileRepo: AudioFileRepository
) : ViewModel() {
	val downloadLocation = dataStoreManager.downloadLocation.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = ""
	)
}