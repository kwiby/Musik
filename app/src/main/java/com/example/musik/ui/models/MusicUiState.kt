package com.example.musik.ui.models

import com.example.musik.data.models.MusicDetails

data class MusicUiState(
	val musicDetails: MusicDetails = MusicDetails(),
	val isEntryValid: Boolean = false
)