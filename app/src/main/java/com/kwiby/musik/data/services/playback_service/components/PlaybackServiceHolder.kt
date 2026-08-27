package com.kwiby.musik.data.services.playback_service.components

import com.kwiby.musik.data.services.playback_service.PlaybackService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object PlaybackServiceHolder {
	private val _service = MutableStateFlow<PlaybackService?>(null)
	val service: StateFlow<PlaybackService?> = _service.asStateFlow()

	fun attach(service: PlaybackService) {
		_service.value = service
	}

	fun detach(service: PlaybackService) {
		if (_service.value === service) {
			_service.value = null
		}
	}
}