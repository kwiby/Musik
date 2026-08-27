package com.kwiby.musik.ui.view_models

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.kwiby.musik.ui.MusikApplication

object ViewModelProvider {
	val Factory = viewModelFactory {
		// --===-- UpdateViewModel --==--
		initializer {
			UpdateViewModel(
				application = musikApplication(),
				updateChecker = musikApplication().container.updateChecker,
				apkInstaller = musikApplication().container.apkInstaller
			)
		}

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

		// --===--  SleepTimerViewModel  --===--
		initializer {
			SleepTimerViewModel()
		}

		// --===--  SettingsViewModel  --===--
		initializer {
			SettingsViewModel(
				dataStoreManager = musikApplication().container.dataStoreManager,
				ytDlp = musikApplication().container.ytDlp
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

		// --===--  EditMetadataViewModel  --===--
		initializer {
			EditMetadataViewModel(
				application = musikApplication(),
				dataStoreManager = musikApplication().container.dataStoreManager,
				musicListRepo = musikApplication().container.musicListRepo
			)
		}

		// --===--  StatsViewModel  --===--
		initializer {
			StatsViewModel(
				musicStatsRepo = musikApplication().container.musicStatsRepo
			)
		}

		// --===--  PlaylistsViewModel  --===--
		initializer {
			PlaylistsViewModel(
				playlistsRepo = musikApplication().container.playlistsRepo,
				musicListRepo = musikApplication().container.musicListRepo
			)
		}
	}
}

fun CreationExtras.musikApplication(): MusikApplication =
	(this[AndroidViewModelFactory.APPLICATION_KEY] as MusikApplication)