package com.example.musik.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musik.data.misc.CircularDoublyLinkedList
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
	private val _queue: CircularDoublyLinkedList = CircularDoublyLinkedList()

	sealed interface MusicUiState {
		data object Loading: MusicUiState
		data object Empty: MusicUiState
		data class Success(val musicList: List<MusicDetails>): MusicUiState
	}

	val uiState: StateFlow<MusicUiState> = audioFileRepo
		.getAllAudioFilesStream()
		.map<List<AudioFile>, MusicUiState> { musicList ->
			if (musicList.isEmpty()) {
				MusicUiState.Empty
			} else {
				val newMusicDetails = musicList.map { it.toMusicDetails() }
				val curMusicDetails = _queue.toList()

				/*
				Filters the DB list such that only values that are NOT in the CDLL are kept, which
				are then added to the end of the CDLL iteratively

				EX:
					DB Emits: [A, B, C, D]  =>  'D' was added to the DB
					CDLL Contains: [A, B, C]  =>  'D' has NOT yet been added to the CDLL
					Filter Keeps: [D]  =>  As such, 'D' is the only filtered item
					Final CDLL: [A, B, C, D]  =>  Finally, 'D' is added to the CDLL
				 */
				newMusicDetails.filter { it !in curMusicDetails }.forEach { _queue.addEnd(it) }

				/*
				Filters the DB list such that only values that ARE in the CDLL but are NOT in the DB
				are kept, which are then removed from the CDLL iteratively

				EX:
					DB Emits: [A, C, D]  =>  'B' was deleted from the DB
					CDLL Contains: [A, B, C, D]  =>  'B' has NOT yet been deleted from the CDLL
					Filter Keeps: [B]  =>  As such, 'B' is the only filtered item
					Final CDLL: [A, C, D]  =>  Finally, 'B' is removed from the CDLL
				 */
				curMusicDetails.filter { it !in newMusicDetails }.forEach { _queue.remove(it) }

				MusicUiState.Success(_queue.toList())
			}
		}.stateIn(
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

	private fun setMoveMode(bool: Boolean) {
		_isInMoveMode.value = bool
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
		clearSelection()
		setMoveMode(true)
	}

	fun confirmMoveButton() {
		setMoveMode(false)
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
		setMoveMode(false)
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