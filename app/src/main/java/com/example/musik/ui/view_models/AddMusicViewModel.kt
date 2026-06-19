package com.example.musik.ui.view_models

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musik.data.misc.fetchAudioFiles
import com.example.musik.data.models.MusicDetails
import com.example.musik.data.repository.AudioFileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddMusicViewModel(
	application: Application,
	private val audioFileRepo: AudioFileRepository
): AndroidViewModel(application) {
	val searchQuery = MutableStateFlow("")

	private val _allAudioFiles = MutableStateFlow<List<MusicDetails>>(emptyList())
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
	val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

	private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
	val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()


	private fun clearSearchQuery() {
		searchQuery.value = ""
	}

	private fun clearSelection() {
		_selectedIds.value = emptySet()
	}

	private var loadJob: Job? = null
	private fun loadAudioFiles() {
		_isLoading.value = true
		loadJob?.cancel()

		loadJob = viewModelScope.launch(Dispatchers.IO) {
			try {
				_allAudioFiles.value = fetchAudioFiles(getApplication()).map {
					it.toMusicDetails()
				}
			} finally {
				_isLoading.value = false
			}
		}
	}

	suspend fun addSelectedMusic() {
		val selectedMusic = audioFiles.value.filter { it.id in _selectedIds.value }
		withContext(Dispatchers.IO) {
			audioFileRepo.insertMultipleAudioFiles(selectedMusic.map { it.toAudioFile() })
		}

		clearSelection()
	}


	fun onSearchQueryChange(query: String) {
		searchQuery.value = query
	}

	fun toggleSelection(id: Long) {
		_selectedIds.update { current ->
			if (id in current) {
				current - id
			} else {
				current + id
			}
		}
	}

	fun refreshButton() {
		clearSelection()
		clearSearchQuery()
		loadAudioFiles()
	}

	fun musicAddingSetup() {
		clearSelection()
		clearSearchQuery()
		loadAudioFiles()
	}
}