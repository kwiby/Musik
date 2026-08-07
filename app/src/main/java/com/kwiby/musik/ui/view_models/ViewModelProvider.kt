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
				dataStoreManager = musikApplication().container.dataStoreManager
			)
		}

		// --===--  PlayBackViewModel  --===--
		initializer {
			PlaybackViewModel(
				application = musikApplication()
			)
		}

		// --===--  StatsViewModel  --===--
		initializer {
			StatsViewModel(
				musicStatsRepo = musikApplication().container.musicStatsRepo
			)
		}

		// --===--  SettingsViewModel  --===--
		initializer {
			SettingsViewModel(
				dataStoreManager = musikApplication().container.dataStoreManager,
				ytDlp = musikApplication().container.ytDlp
			)
		}

		// --===--  EditMetadataViewModel  --===--
		initializer {
			EditMetadataViewModel(
				application = musikApplication(),
				dataStoreManager = musikApplication().container.dataStoreManager,
				musicListRepo = musikApplication().container.musicListRepo
			)
		}

		// --===--  MusicListViewModel  --===--
		initializer {
			MusicListViewModel(
				musicListRepo = musikApplication().container.musicListRepo
			)
		}

		// --===--  AddMusicViewModel  --===--
		initializer {
			AddMusicViewModel(
				application = musikApplication(),
				musicListRepo = musikApplication().container.musicListRepo
			)
		}

		// --===--  AddYtMusicViewModel  --===--
		initializer {
			AddYtMusicViewModel(
				application = musikApplication(),
				dataStoreManager = musikApplication().container.dataStoreManager,
				ytDlp = musikApplication ().container.ytDlp,
				musicListRepo = musikApplication().container.musicListRepo
			)
		}
	}
}

fun CreationExtras.musikApplication(): MusikApplication =
	(this[AndroidViewModelFactory.APPLICATION_KEY] as MusikApplication)