package com.kwiby.musik.ui.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kwiby.musik.data.data_classes.MusicDetails
import com.kwiby.musik.data.misc.fetchAudioFiles
import com.kwiby.musik.data.repositories.music_list.OfflineMusicListRepository
import com.kwiby.musik.ui.MusikApplication
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
	private val musicListRepo: OfflineMusicListRepository
) : AndroidViewModel(application) {
	val searchQuery = MutableStateFlow("")

	private val _audioFiles = MutableStateFlow<List<MusicDetails>>(emptyList())
	val audioFiles: StateFlow<List<MusicDetails>> = searchQuery
		.combine(_audioFiles) { query, files ->
			val trimmedQuery = query.trim()

			if (trimmedQuery.isBlank()) {
				files
			} else {
				files.filter {
					it.title.contains(trimmedQuery, ignoreCase = true)
				}
			}
		}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = emptyList())

	private val _isLoading = MutableStateFlow(false)
	val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

	private val _selectedIds = MutableStateFlow<List<Long>>(emptyList())
	val selectedIds: StateFlow<List<Long>> = _selectedIds.asStateFlow()

	var refreshTrigger by mutableStateOf(false)
		private set


	private fun clearSearchQuery() {
		searchQuery.value = ""
	}

	private fun clearSelection() {
		_selectedIds.value = emptyList()
	}

	private var loadJob: Job? = null
	fun loadAudioFiles() {
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
		val musicByIdMap = _audioFiles.value.associateBy { it.id }
		val selectedMusic = _selectedIds.value.mapNotNull { musicByIdMap[it] }

		withContext(Dispatchers.IO) {
			val curCount = musicListRepo.getAudioFileCount()
			musicListRepo.insertMultipleAudioFiles(
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
		refreshTrigger = !refreshTrigger
	}

	fun resetMusicAdding() {
		clearSelection()
		clearSearchQuery()
	}
}