package com.example.musik.ui.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musik.data.misc.fetchAudioFiles
import com.example.musik.data.models.MusicDetails
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddMusicViewModel(application: Application): AndroidViewModel(application) {
	private val _allAudioFiles = MutableStateFlow<List<MusicDetails>>(emptyList())

	val searchQuery = MutableStateFlow("")
	val audioFiles: StateFlow<List<MusicDetails>> = searchQuery
		.combine(_allAudioFiles) { query, files ->
			if (query.isBlank()) {
				files
			} else {
				files.filter {
					it.title.contains(query, ignoreCase = true)
				}
			}
		}
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

	private val _isLoading = MutableStateFlow(false)
	val isLoading: StateFlow<Boolean> = _isLoading

	init {
		loadAudioFiles()
	}

	fun onSearchQueryChange(query: String) {
		searchQuery.value = query
	}

	fun clearSearchQuery() {
		searchQuery.value = ""
	}

	fun loadAudioFiles() {
		viewModelScope.launch {
			_isLoading.value = true
			_allAudioFiles.value = fetchAudioFiles(getApplication()).map { it.toMusicDetails() }
			_isLoading.value = false
		}
	}
}