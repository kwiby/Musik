package com.example.musik.ui.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.musik.data.models.AudioFile
import com.example.musik.data.models.MusicDetails
import com.example.musik.data.repository.AudioFileRepository
import com.example.musik.ui.misc.formatDuration
import com.example.musik.ui.misc.unformatDuration
import com.example.musik.ui.models.MusicUiState

class MusicEntryViewModel(private val audioFileRepo: AudioFileRepository): ViewModel() {
	var musicUiState by mutableStateOf(MusicUiState())
		private set

	fun updateUiState(musicDetails: MusicDetails) {
		musicUiState =
			MusicUiState(musicDetails = musicDetails, isEntryValid = validateInput(musicDetails))
	}

	private fun validateInput(uiState: MusicDetails = musicUiState.musicDetails): Boolean {
		return with(uiState) {
			filePath.isNotBlank() && title.isNotBlank() && artist.isNotBlank() && duration.isNotBlank()
		}
	}

	suspend fun saveAudioFile() {
		if (validateInput()) {
			audioFileRepo.insertAudioFile(musicUiState.musicDetails.toAudioFile())
		}
	}
}

// MusicDetails --> AudioFile
fun MusicDetails.toAudioFile(): AudioFile = AudioFile(
	id = id,
	filePath = filePath,
	title = title,
	artist = artist,
	duration = duration.unformatDuration()
)

// AudioFile --> MusicUiState
fun AudioFile.toMusicUiState(isEntryValid: Boolean = false): MusicUiState = MusicUiState(
	musicDetails = this.toMusicDetails(),
	isEntryValid = isEntryValid
)

// AudioFile --> MusicDetails
fun AudioFile.toMusicDetails(): MusicDetails = MusicDetails(
	id = id,
	filePath = filePath,
	title = title,
	artist = artist,
	duration = duration.formatDuration()
)