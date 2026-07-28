package com.kwiby.musik.ui.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwiby.musik.data.data_classes.AudioFile
import com.kwiby.musik.data.repositories.music_stats.OfflineMusicStatsRepository
import com.kwiby.musik.ui.misc.formatDuration
import kotlinx.coroutines.launch

class StatsViewModel(
	private val musicStatsRepo: OfflineMusicStatsRepository
) : ViewModel() {
	var isLoading by mutableStateOf(true)
		private set

	var playCountStats by mutableStateOf<List<AudioFile>>(emptyList())
		private set
	var listenTimeStats by mutableStateOf<List<AudioFile>>(emptyList())
		private set

	var overallPlayCount by mutableStateOf("0")
		private set
	var overallListenTime by mutableStateOf("0:00:00")
		private set

	fun switchSortingRuleButton() {

	}

	fun refreshButton() {
		viewModelScope.launch {
			isLoading = true

			overallPlayCount = (musicStatsRepo.getOverallPlayCount() ?: 0).toString()
			overallListenTime = musicStatsRepo.getOverallListenTime()?.formatDuration() ?: "0:00:00"

			playCountStats = musicStatsRepo.getStatsOrderedByPlayCountDESC()
			listenTimeStats = musicStatsRepo.getStatsOrderedByListenTimeDESC()

			isLoading = false
		}
	}

	fun resetStatsTab() {
		refreshButton()
	}
}