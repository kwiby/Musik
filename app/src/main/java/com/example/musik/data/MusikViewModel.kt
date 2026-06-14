package com.example.musik.data

import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class MusikViewModel : ViewModel() {
	val allMusicScreenInt = 0
	val playlistsScreenInt = 1
	val statsScreenInt = 2
	val addSongsScreenInt = 3

	val entryPointScreenInt = allMusicScreenInt
	var currentScreen by mutableIntStateOf(entryPointScreenInt)
		private set

	fun navTo(newScreen: Int) {
		currentScreen = newScreen
	}
}