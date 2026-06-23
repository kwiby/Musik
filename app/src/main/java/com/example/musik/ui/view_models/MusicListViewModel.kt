package com.example.musik.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musik.data.models.AudioFile
import com.example.musik.data.models.MusicDetails
import com.example.musik.data.repository.AudioFileRepository
import com.example.musik.ui.misc.formatDuration
import com.example.musik.ui.misc.unformatDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

class MusicListViewModel(private val audioFileRepo: AudioFileRepository): ViewModel() {
	sealed interface MusicUiState {
		data object Loading: MusicUiState
		data object Empty: MusicUiState
		data class Success(val musicList: List<MusicDetails>): MusicUiState
	}

	/*
	Have a variable to hold the CircularDoublyLinkedList, then for the uiState value, upon success,
	iterate through the CircularDoublyLinkedList to map each index to the corresponding MusicDetail
	held within the CircularDoublyLinkedList.

	Furthermore, to handle adding/removing from the CircularDoublyLinkedList, implement separate
	functions for them.
	 */
	val uiState: StateFlow<MusicUiState> = audioFileRepo
		.getAllAudioFilesStream()
		.map<List<AudioFile>, MusicUiState> { musicList ->
			if (musicList.isEmpty()) {
				MusicUiState.Empty
			} else {
				MusicUiState.Success(musicList.map { it.toMusicDetails() })
			}
		}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = MusicUiState.Loading
		)

	private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
	val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

	val isInSelectionMode: StateFlow<Boolean> = _selectedIds
		.map { it.isNotEmpty() }
		.stateIn(viewModelScope, SharingStarted.Eagerly, false)

	private val _isInMoveMode = MutableStateFlow(false)
	val isInMoveMode: StateFlow<Boolean> = _isInMoveMode.asStateFlow()


	private fun clearSelection() {
		_selectedIds.value = emptySet()
	}

	private fun disableMoveMode() {
		_isInMoveMode.value = false
	}

	private fun updateSelection(id: Long) {
		_selectedIds.update { current ->
			if (id in current) {
				current - id
			} else {
				current + id
			}
		}
	}

	suspend fun removeMusicButton(
		currentMusicId: String?,
		removeCurrentMusicFunction: () -> Unit
	) {
		val selectedMusic = _selectedIds.value

		if (currentMusicId != null && currentMusicId.toLong() in selectedMusic) {
			removeCurrentMusicFunction()
		}

		withContext(Dispatchers.IO) {
			audioFileRepo.deleteMultipleAudioFilesById(selectedMusic)
		}

		resetMusicList()
	}

	fun onMove(fromIndex: Int, toIndex: Int) {

	}

	fun addingButton(onAddMusicButtonClick: () -> Unit) {
		resetMusicList()

		onAddMusicButtonClick()
	}

	fun handleTap(id: Long, onPlayMusic: () -> Unit) {
		if (isInSelectionMode.value) {
			updateSelection(id)
		} else {
			onPlayMusic()
		}
	}


	fun handleHold(id: Long) {
		updateSelection(id)
	}

	fun moveMusicButton() {
		resetMusicList()
	}

	fun confirmMoveButton() {
		disableMoveMode()
	}

	fun addToPlaylistButton() {
		resetMusicList()
	}

	fun addYtMusicButton() {
		resetMusicList()
	}

	fun handleBack() {
		resetMusicList()
	}

	fun resetMusicList() {
		clearSelection()
		disableMoveMode()
	}
}

// MusicDetails --> AudioFile
fun MusicDetails.toAudioFile(): AudioFile = AudioFile(
	id = id,
	contentUri = contentUri,
	albumArtUri = albumArtUri,
	title = title,
	artist = artist,
	duration = duration.unformatDuration()
)

// AudioFile --> MusicDetails
fun AudioFile.toMusicDetails(): MusicDetails = MusicDetails(
	id = id,
	contentUri = contentUri,
	albumArtUri = albumArtUri,
	title = title,
	artist = artist,
	duration = duration.formatDuration()
)