package com.example.musik.ui.view_models

import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.musik.data.data_classes.AudioFile
import com.example.musik.data.data_classes.MusicDetails
import com.example.musik.data.repositories.audio_file.AudioFileRepository
import com.example.musik.ui.misc.formatDuration
import com.example.musik.ui.misc.unformatDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicListViewModel(
	private val audioFileRepo: AudioFileRepository
) : ViewModel() {
	private val _queue = mutableListOf<MusicDetails>()
	private var _previousQueueForSync: List<MusicDetails> = emptyList()
	private var _queueBeforeMove: List<MusicDetails> = emptyList()
	private val _manualQueue = MutableStateFlow<List<MusicDetails>?>(null)

	sealed interface MusicUiState {
		data object Loading: MusicUiState
		data object Empty: MusicUiState
		data class Success(val musicList: List<MusicDetails>): MusicUiState
	}

	val uiState: StateFlow<MusicUiState> = combine(
		audioFileRepo.getAllAudioFilesStream(), _manualQueue
	) { musicList, manualQueue ->
		if (musicList.isEmpty()) {
			_queue.clear()
			MusicUiState.Empty
		} else {
			val newMusicDetails = musicList.map { it.toMusicDetails() }
			val newIds = newMusicDetails.map { it.id }
			val curIds = _queue.map { it.id }

			newMusicDetails.filter { it.id !in curIds }.forEach { _queue.add(it) }
			_queue.removeAll { it.id !in newIds }

			if (manualQueue != null) {
				_manualQueue.value = null
			}

			MusicUiState.Success(manualQueue ?: _queue.toList())
		}
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = MusicUiState.Loading
	)

	val queueSyncEvent: StateFlow<List<MusicDetails>?> = uiState
		.map { state ->
			val newQueue = if (state is MusicUiState.Success) {
				state.musicList
			} else {
				emptyList()
			}

			if (newQueue != _previousQueueForSync) {
				_previousQueueForSync = newQueue
				newQueue
			} else {
				_previousQueueForSync = newQueue
				null // null = don't sync
			}
		}
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)


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

	private fun revertQueueToBeforeMove() {
		if (_queueBeforeMove.isEmpty()) {
			return
		} else {
			_queue.clear()
			_queueBeforeMove.forEach { _queue.add(it) }
			_manualQueue.value = _queue.toList()

			_queueBeforeMove = emptyList()
		}
	}

	suspend fun removeMusicButton(playbackViewModel: PlaybackViewModel) {
		val selectedMusic = _selectedIds.value

		playbackViewModel.removeFromQueue(selectedMusic)
		withContext(Dispatchers.IO) {
			audioFileRepo.deleteMultipleAudioFilesById(selectedMusic)
		}

		resetMusicList()
	}

	fun onMove(fromIndex: Int, toIndex: Int) {
		val list = _queue.toList().toMutableList()
		val moved = list.removeAt(fromIndex)
		list.add(toIndex, moved)

		_queue.clear()
		list.forEach { _queue.add(it) }

		_manualQueue.value = _queue.toList()
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

	fun enterMoveModeButton() {
		_queueBeforeMove = _queue.toList()

		clearSelection()
		setMoveMode(true)
	}

	fun confirmMoveButton(playbackViewModel: PlaybackViewModel) {
		val currentUiState = uiState.value
		if (currentUiState !is MusicUiState.Success) {
			setMoveMode(false)
			return
		}

		val queue = _queue.toList()

		playbackViewModel.setQueue(queue.map { it.toMediaItem() })
		_queueBeforeMove = emptyList()

		setMoveMode(false)

		viewModelScope.launch(Dispatchers.IO) {
			audioFileRepo.updateMultipleOrderPos(queue.map { it.id })
		}
	}

	fun exitMoveModeButton() {
		revertQueueToBeforeMove()
		setMoveMode(false)
	}

	fun addToPlaylistButton() {
		resetMusicList()
	}

	fun addMusicButton(onAddMusicButtonClick: () -> Unit) {
		resetMusicList()
		onAddMusicButtonClick()
	}

	fun addYtMusicButton(onAddYtMusicButtonClick: () -> Unit) {
		resetMusicList()
		onAddYtMusicButtonClick()
	}

	fun handleBack() {
		resetMusicList()
	}

	fun resetMusicList() {
		clearSelection()
		if (_isInMoveMode.value) {
			revertQueueToBeforeMove()
		}
		setMoveMode(false)
	}
}

// MusicDetails --> MediaItem
fun MusicDetails.toMediaItem(): MediaItem {
	return MediaItem.Builder()
		.setMediaId(id.toString())
		.setUri(contentUri.toUri())
		.setMediaMetadata(
			MediaMetadata.Builder()
				.setTitle(title)
				.setArtist(artist)
				.setDurationMs(duration.unformatDuration())
				.setArtworkUri(albumArtUri.toUri())
				.build()
		).build()
}

// MusicDetails --> AudioFile
fun MusicDetails.toAudioFile(): AudioFile = AudioFile(
	id = id,
	contentUri = contentUri,
	albumArtUri = albumArtUri,
	title = title,
	artist = artist,
	duration = duration.unformatDuration(),
	orderPos = orderPos
)

// AudioFile --> MusicDetails
fun AudioFile.toMusicDetails(): MusicDetails = MusicDetails(
	id = id,
	contentUri = contentUri,
	albumArtUri = albumArtUri,
	title = title,
	artist = artist,
	duration = duration.formatDuration(),
	orderPos = orderPos
)