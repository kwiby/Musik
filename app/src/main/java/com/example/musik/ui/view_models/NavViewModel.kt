package com.example.musik.ui.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class Tabs {
	ALL_MUSIC,
	PLAYLISTS,
	STATS
}

class NavViewModel : ViewModel() {
	val entryPointScreen: Tabs = Tabs.ALL_MUSIC
	var curTab by mutableStateOf(entryPointScreen)
		private set

	fun navTo(newTab: Tabs) {
		curTab = newTab
	}
}