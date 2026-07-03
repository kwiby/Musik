package com.example.musik.ui.view_models

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musik.data.misc.fetchAudioFiles
import com.example.musik.data.data_classes.MusicDetails
import com.example.musik.data.repositories.audio_file.AudioFileRepository
import com.example.musik.ui.MusikApplication
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
	application: MusikApplication,
	private val audioFileRepo: AudioFileRepository
) : AndroidViewModel(application) {
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
			val curCount = audioFileRepo.getAudioFileCount()
			audioFileRepo.insertMultipleAudioFiles(
				selectedMusic.mapIndexed { index, music ->
					music.toAudioFile().copy(orderPos = curCount + index)
				}
			)

			/*
			Above code is to add the orderPos value when adding audio files. Originally, orderPos
			would only be set when music reordering confirm button was pressed. If the above feature
			is not needed, consider switching to the below code (as of writing this, the DB should
			return the list in insertion order, as all orderPos values are 0 WITH the bottom code):

			audioFileRepo.insertMultipleAudioFiles(selectedMusic.map { it.toAudioFile() })
			 */
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
		resetMusicAdding()
	}

	fun resetMusicAdding() {
		clearSelection()
		clearSearchQuery()
		loadAudioFiles()
	}
}