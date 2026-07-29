package com.kwiby.musik.ui.view_models

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwiby.musik.data.data_classes.MusicStats
import com.kwiby.musik.data.repositories.music_stats.OfflineMusicStatsRepository
import com.kwiby.musik.ui.misc.formatDuration
import kotlinx.coroutines.launch

class StatsViewModel(
	private val musicStatsRepo: OfflineMusicStatsRepository
) : ViewModel() {
	private val playCountIdentifier = "Play count"
	private val listenTimeIdentifier = "Listen time"
	private var playCountStats by mutableStateOf<List<MusicStats>>(emptyList())
	private var listenTimeStats by mutableStateOf<List<MusicStats>>(emptyList())

	var isLoading by mutableStateOf(true)
		private set
	var refreshTrigger by mutableStateOf(false)
		private set

	var selectedOrderRule by mutableStateOf(playCountIdentifier)
		private set
	val selectedStat by derivedStateOf {
		when (selectedOrderRule) {
			playCountIdentifier -> playCountStats
			listenTimeIdentifier -> listenTimeStats
			else -> playCountStats
		}
	}

	var overallPlayCount by mutableStateOf("0")
		private set
	var overallListenTime by mutableStateOf("0:00:00")
		private set

	fun switchSortingRuleButton() {
		selectedOrderRule = when (selectedOrderRule) {
			playCountIdentifier -> listenTimeIdentifier
			listenTimeIdentifier -> playCountIdentifier
			else -> playCountIdentifier
		}
	}

	fun refreshButton() {
		resetStatsTab()
		refreshTrigger = !refreshTrigger
	}

	fun resetStatsTab() {
		viewModelScope.launch {
			isLoading = true

			overallPlayCount = (musicStatsRepo.getOverallPlayCount() ?: 0).toString()
			overallListenTime = musicStatsRepo.getOverallListenTime()?.formatDuration() ?: "0:00:00"

			playCountStats = musicStatsRepo.getStatsOrderedByPlayCountDESC()
			listenTimeStats = musicStatsRepo.getStatsOrderedByListenTimeDESC()

			isLoading = false
		}
	}
}