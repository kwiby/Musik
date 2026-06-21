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

	private val _audioFiles = MutableStateFlow<List<MusicDetails>>(emptyList())
	val audioFiles: StateFlow<List<MusicDetails>> = searchQuery
		.combine(_audioFiles) { query, files ->
			if (query.isBlank()) {
				files
			} else {
				files.filter {
					it.title.contains(query, ignoreCase = true)
				}
			}
		}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = emptyList())

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
		loadJob?.cancel()
		_isLoading.value = true

		loadJob = viewModelScope.launch(Dispatchers.IO) {
			try {
				 val files = fetchAudioFiles(getApplication()).map {
					it.toMusicDetails()
				}

				_audioFiles.value = files
			} finally {
				_isLoading.value = false
			}
		}
	}

	suspend fun addSelectedMusic() {
		val selectedMusic = _audioFiles.value.filter { it.id in _selectedIds.value }
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