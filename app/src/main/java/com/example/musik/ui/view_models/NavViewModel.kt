package com.example.musik.ui.view_models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musik.data.datastore.DataStoreManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

class NavViewModel(
	private val dataStoreManager: DataStoreManager
) : ViewModel() {
	// --===--  Tab  --===--
	private val _entryTab = MutableStateFlow(Tab.ALL_MUSIC)
	val entryTab: StateFlow<Tab> = _entryTab.asStateFlow()

	var curTab by mutableStateOf(entryTab.value)
		private set

	private var job: Job? = null
	fun setEntryTab(newTab: Tab) {
		_entryTab.value = newTab

		job?.cancel()
		job = viewModelScope.launch {
			dataStoreManager.setEntryTab(
				when (newTab) {
					Tab.ALL_MUSIC -> "all_music"
					Tab.PLAYLISTS -> "playlists"
					Tab.STATS -> "stats"
				}
			)
		}
	}

	fun navToTab(newTab: Tab) {
		curTab = newTab
	}

	// --===--  Screen  --===--
	var curScreen by mutableStateOf(Screen.MAIN)
		private set

	fun navToScreen(newScreen: Screen) {
		curScreen = newScreen
	}

	// --==--  Init  --==--
	private val _isReady = MutableStateFlow(false)
	val isReady: StateFlow<Boolean> = _isReady.asStateFlow()

	init {
		viewModelScope.launch {
			val entryTab = when (dataStoreManager.entryTab.first()) {
				"all_music" -> Tab.ALL_MUSIC
				"playlists" -> Tab.PLAYLISTS
				"stats" -> Tab.STATS
				else -> Tab.ALL_MUSIC
			}

			_entryTab.value = entryTab
			curTab = entryTab

			_isReady.value = true
		}
	}
}