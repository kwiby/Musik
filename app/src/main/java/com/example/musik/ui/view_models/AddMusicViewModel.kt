package com.example.musik.ui.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musik.data.misc.fetchAudioFiles
import com.example.musik.data.models.MusicDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddMusicViewModel(application: Application): AndroidViewModel(application) {
	private val _audioFiles = MutableStateFlow<List<MusicDetails>>(emptyList())
	val audioFiles: StateFlow<List<MusicDetails>> = _audioFiles

	private val _isLoading = MutableStateFlow(false)
	val isLoading: StateFlow<Boolean> = _isLoading

	init {
		loadAudioFiles()
	}

	fun loadAudioFiles() {
		viewModelScope.launch {
			_isLoading.value = true
			_audioFiles.value = fetchAudioFiles(getApplication()).map { it.toMusicDetails() }
			_isLoading.value = false
		}
	}
}