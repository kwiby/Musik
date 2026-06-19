package com.example.musik.ui.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musik.data.models.AudioFile
import com.example.musik.data.models.MusicDetails
import com.example.musik.data.repository.AudioFileRepository
import com.example.musik.ui.misc.formatDuration
import com.example.musik.ui.misc.unformatDuration
import com.example.musik.ui.models.MusicUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MusicEntryViewModel(private val audioFileRepo: AudioFileRepository): ViewModel() {
	var musicUiState by mutableStateOf(MusicUiState())
		private set

	val audioFileCount: StateFlow<Int> = audioFileRepo
		.getAudioFileCountStream()
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = 0
		)

	private fun validateInput(uiState: MusicDetails = musicUiState.musicDetails): Boolean {
		return with(uiState) {
			contentUri.isNotBlank()
		}
	}

	suspend fun saveAudioFile() {
		if (validateInput()) {
			audioFileRepo.insertAudioFile(musicUiState.musicDetails.toAudioFile())
		}
	}

	fun updateUiState(musicDetails: MusicDetails) {
		musicUiState = MusicUiState(musicDetails = musicDetails, isEntryValid = validateInput(musicDetails))
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

// AudioFile --> MusicUiState
fun AudioFile.toMusicUiState(isEntryValid: Boolean = false): MusicUiState = MusicUiState(
	musicDetails = this.toMusicDetails(),
	isEntryValid = isEntryValid
)