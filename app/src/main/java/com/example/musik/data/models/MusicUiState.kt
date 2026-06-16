package com.example.musik.data.models

data class MusicUiState(
	val musicDetails: MusicDetails = MusicDetails(),
	val isEntryValid: Boolean = false
)
