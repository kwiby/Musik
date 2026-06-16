package com.example.musik.ui.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class Screen {
	ALL_MUSIC,
	PLAYLISTS,
	STATS
}

class NavViewModel : ViewModel() {
	val entryPointScreen: Screen = Screen.ALL_MUSIC
	var curScreen by mutableStateOf(entryPointScreen)
		private set

	fun navTo(newScreen: Screen) {
		curScreen = newScreen
	}
}