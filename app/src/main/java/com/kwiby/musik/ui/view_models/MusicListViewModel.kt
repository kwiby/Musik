package com.kwiby.musik.ui.view_models

import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.kwiby.musik.data.data_classes.AudioFile
import com.kwiby.musik.data.data_classes.MusicDetails
import com.kwiby.musik.data.repositories.music_list.OfflineMusicListRepository
import com.kwiby.musik.ui.misc.formatDuration
import com.kwiby.musik.ui.misc.unformatDuration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MusicListViewModel(
	private val musicListRepo: OfflineMusicListRepository
) : ViewModel() {
	private val _queue = MutableStateFlow<List<MusicDetails>>(emptyList())
	private var _queueBeforeMove: List<MusicDetails> = emptyList()

	sealed interface MusicUiState {
		data object Loading: MusicUiState
		data object Empty: MusicUiState
		data class Success(val musicList: List<MusicDetails>): MusicUiState
	}

	val queueSyncEvent: StateFlow<List<MusicDetails>?> = _queue
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)


	private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
	val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()

	val isInSelectionMode: StateFlow<Boolean> = _selectedIds
		.map { it.isNotEmpty() }
		.stateIn(viewModelScope, SharingStarted.Eagerly, false)

	private val _isInMoveMode = MutableStateFlow(false)
	val isInMoveMode: StateFlow<Boolean> = _isInMoveMode.asStateFlow()

	var isInEditMetadataMode = mutableStateOf(false)
		private set

	val uiState: StateFlow<MusicUiState> = combine(
		musicListRepo.getAllAudioFilesStream(), _queue, _isInMoveMode
	) { repoList, currentQueue, inMoveMode ->
		if (repoList.isEmpty()) {
			_queue.value = emptyList()
			return@combine MusicUiState.Empty
		}
		if (inMoveMode) {
			return@combine MusicUiState.Success(currentQueue)
		}

		val newMusicDetails = repoList.map { it.toMusicDetails() }
		val newIds = newMusicDetails.map { it.id }.toSet()
		val curIds = currentQueue.map { it.id }.toSet()

		if (newIds != curIds) {
			val keptSongs = currentQueue.filter { it.id in newIds }
			val addedSongs = newMusicDetails.filter { it.id !in curIds }
			val updatedQueue = keptSongs + addedSongs
			_queue.value = updatedQueue

			return@combine MusicUiState.Success(updatedQueue)
		} else {
			return@combine MusicUiState.Success(currentQueue)
		}
	}.flowOn(Dispatchers.Default).stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = MusicUiState.Loading
	)


	private fun clearSelection() {
		_selectedIds.value = emptySet()
	}

	private fun setMoveMode(bool: Boolean) {
		_isInMoveMode.value = bool
	}

	private fun setEditMetadataMode(bool: Boolean) {
		isInEditMetadataMode.value = bool
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
			_queue.value = _queueBeforeMove
			_queueBeforeMove = emptyList()
		}
	}

	suspend fun removeMusicButton(playbackViewModel: PlaybackViewModel) {
		val selectedMusic = _selectedIds.value

		playbackViewModel.removeFromQueue(selectedMusic)
		withContext(Dispatchers.IO) {
			musicListRepo.deleteMultipleAudioFilesById(selectedMusic)
		}

		resetMusicList()
	}

	suspend fun deleteTracksByIds(ids: Set<Long>) {
		withContext(Dispatchers.IO) {
			musicListRepo.deleteMultipleAudioFilesById(ids)
		}

		_queue.value = _queue.value.filterNot { it.id in ids }
	}

	fun setQueueOrder(newOrder: List<MusicDetails>) {
		_queue.value = newOrder
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

	fun confirmMoveButton(playbackViewModel: PlaybackViewModel) {
		val currentUiState = uiState.value
		if (currentUiState !is MusicUiState.Success) {
			setMoveMode(false)
			return
		}

		val queue = _queue.value

		playbackViewModel.setQueue(queue.map { it.toMediaItem() })
		_queueBeforeMove = emptyList()

		setMoveMode(false)

		viewModelScope.launch(Dispatchers.IO) {
			musicListRepo.updateMultipleOrderPos(queue.map { it.id })
		}
	}

	fun enterMoveModeButton() {
		clearSelection()
		setEditMetadataMode(false)

		_queueBeforeMove = _queue.value
		setMoveMode(true)
	}

	fun exitMoveModeButton() {
		revertQueueToBeforeMove()
		setMoveMode(false)
	}

	fun enterEditMetadataModeButton() {
		clearSelection()
		setMoveMode(false)

		setEditMetadataMode(true)
	}

	fun exitEditMetadataModeButton() {
		setEditMetadataMode(false)
	}

	fun editMetadataButton(id: Long) {

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
		setEditMetadataMode(false)

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
				.setDurationMs(durationMs.unformatDuration())
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
	durationMs = durationMs.unformatDuration(),
	orderPos = orderPos
)

// AudioFile --> MusicDetails
fun AudioFile.toMusicDetails(): MusicDetails = MusicDetails(
	id = id,
	contentUri = contentUri,
	albumArtUri = albumArtUri,
	title = title,
	artist = artist,
	durationMs = durationMs.formatDuration(),
	orderPos = orderPos
)