package com.kwiby.musik.ui.view_models

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwiby.musik.data.services.playback_service.components.PlaybackServiceHolder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn

class SleepTimerViewModel: ViewModel() {
	@OptIn(ExperimentalCoroutinesApi::class)
	val remainingMs = PlaybackServiceHolder.service
		.flatMapLatest { service ->
			service?.sleepTimerController?.remainingMs ?: MutableStateFlow(0L)
		}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = 0L
		)

	@OptIn(ExperimentalCoroutinesApi::class)
	val isSleepTimerSet = PlaybackServiceHolder.service
		.flatMapLatest { service ->
			service?.sleepTimerController?.isSleepTimerSet ?: MutableStateFlow(false)
		}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = false
		)

	fun setSleepTimer(durationMs: Long) {
		PlaybackServiceHolder.service.value?.sleepTimerController?.setSleepTimer(durationMs)
	}

	fun stopSleepTimer() {
		PlaybackServiceHolder.service.value?.sleepTimerController?.stopSleepTimer()
	}
}