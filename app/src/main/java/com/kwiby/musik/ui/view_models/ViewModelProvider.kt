package com.kwiby.musik.ui.view_models

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kwiby.musik.ui.MusikApplication

object ViewModelProvider {
	val Factory = viewModelFactory {
		// --===--  NavViewModel  --===--
		initializer {
			NavViewModel(
				musikApplication().container.dataStoreManager
			)
		}

		// --===--  PlayBackViewModel  --===--
		initializer {
			PlaybackViewModel(
				musikApplication()
			)
		}

		// --===--  StatsViewModel  --===--
		initializer {
			StatsViewModel(
				musikApplication().container.musicStatsRepo
			)
		}

		// --===--  SettingsViewModel  --===--
		initializer {
			SettingsViewModel(
				musikApplication().container.dataStoreManager,
				musikApplication().container.ytDlp
			)
		}

		// --===--  EditMetadataViewModel  --===--
		initializer {
			EditMetadataViewModel(
				musikApplication().container.musicListRepo
			)
		}

		// --===--  MusicListViewModel  --===--
		initializer {
			MusicListViewModel(
				musikApplication().container.musicListRepo
			)
		}

		// --===--  AddMusicViewModel  --===--
		initializer {
			AddMusicViewModel(
				musikApplication(),
				musikApplication().container.musicListRepo
			)
		}

		// --===--  AddYtMusicViewModel  --===--
		initializer {
			AddYtMusicViewModel(
				musikApplication(),
				musikApplication().container.dataStoreManager,
				musikApplication().container.ytDlp,
				musikApplication().container.musicListRepo
			)
		}
	}
}

fun CreationExtras.musikApplication(): MusikApplication =
	(this[AndroidViewModelFactory.APPLICATION_KEY] as MusikApplication)