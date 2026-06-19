package com.example.musik.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musik.data.models.AudioFile
import com.example.musik.data.models.MusicDetails
import com.example.musik.data.repository.AudioFileRepository
import com.example.musik.ui.misc.formatDuration
import com.example.musik.ui.misc.unformatDuration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MusicListViewModel(audioFileRepo: AudioFileRepository): ViewModel() {
	sealed interface MusicUiState {
		data object Loading: MusicUiState
		data object Empty: MusicUiState
		data class Success(val musicList: List<MusicDetails>): MusicUiState
	}

	val uiState: StateFlow<MusicUiState> = audioFileRepo
		.getAllAudioFilesStream()
		.map<List<AudioFile>, MusicUiState> { audioFiles ->
			if (audioFiles.isEmpty()) {
				MusicUiState.Empty
			} else {
				MusicUiState.Success(audioFiles.map { it.toMusicDetails() })
			}
		}
		.onStart { emit(MusicUiState.Loading) }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = MusicUiState.Loading
		)

	private val _selectedIds = MutableStateFlow<Set<Long>>(emptySet())
	val selectedIds: StateFlow<Set<Long>> = _selectedIds.asStateFlow()


	private fun clearSelection() {
		_selectedIds.value = emptySet()
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

	fun moveMusicButton() {
		clearSelection()
	}

	fun addingButton() {
		clearSelection()
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