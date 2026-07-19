package com.example.musik.ui.view_models

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.musik.ui.MusikApplication

object ViewModelProvider {
	val Factory = viewModelFactory {
		// --===--  NavViewModel  --===--
		initializer {
			NavViewModel(
				musikApplication().container.dataStoreManager
			)
		}

		// --===--  MusicListViewModel  --===--
		initializer {
			MusicListViewModel(
				musikApplication().container.audioFileRepository
			)
		}

		// --===--  PlayBackViewModel  --===--
		initializer {
			PlaybackViewModel(
				musikApplication()
			)
		}

		// --===--  AddMusicViewModel  --===--
		initializer {
			AddMusicViewModel(
				musikApplication(),
				musikApplication().container.audioFileRepository
			)
		}

		// --===--  AddYtMusicViewModel  --===--
		initializer {
			AddYtMusicViewModel(
				musikApplication(),
				musikApplication().container.dataStoreManager,
				musikApplication().container.audioFileRepository,
				musikApplication().container.ytDlp
			)
		}
	}
}

fun CreationExtras.musikApplication(): MusikApplication =
	(this[AndroidViewModelFactory.APPLICATION_KEY] as MusikApplication)