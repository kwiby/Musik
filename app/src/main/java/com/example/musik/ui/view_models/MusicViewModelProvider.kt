package com.example.musik.ui.view_models

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.musik.ui.misc.MusicApplication

object MusicViewModelProvider {
	val Factory = viewModelFactory {
		initializer {
			MusicEntryViewModel(musicApplication().container.audioFileRepository)
		}
	}
}

fun CreationExtras.musicApplication(): MusicApplication =
	(this[AndroidViewModelFactory.APPLICATION_KEY] as MusicApplication)