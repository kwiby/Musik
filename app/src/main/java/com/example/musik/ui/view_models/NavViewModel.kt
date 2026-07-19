package com.example.musik.ui.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

enum class Screen {
	MAIN,
	SETTINGS,
	PLAYER
}
enum class Tab {
	ALL_MUSIC,
	PLAYLISTS,
	STATS
}

class NavViewModel : ViewModel() {
	val entryPointTab: Tab = Tab.ALL_MUSIC
	var curTab by mutableStateOf(entryPointTab)
		private set

	var curScreen by mutableStateOf(Screen.MAIN)
		private set

	fun navToTab(newTab: Tab) {
		curTab = newTab
	}

	fun navToScreen(newScreen: Screen) {
		curScreen = newScreen
	}
}