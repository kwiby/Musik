package com.kwiby.musik.ui

import android.content.Context
import com.kwiby.musik.data.databases.music_list.MusicListDatabase
import com.kwiby.musik.data.datastore.DataStoreManager
import com.kwiby.musik.data.repositories.music_list.OfflineMusicListRepository
import com.kwiby.musik.data.repositories.music_stats.OfflineMusicStatsRepository
import com.kwiby.musik.data.repositories.playlists.OfflinePlaylistsRepository
import com.kwiby.musik.ui.misc.ytdlp.YtDlp

interface AppContainer {
	val musicListRepo: OfflineMusicListRepository
	val musicStatsRepo: OfflineMusicStatsRepository
	val playlistsRepo: OfflinePlaylistsRepository
	val dataStoreManager: DataStoreManager
	val ytDlp: YtDlp
}

class AppDataContainer(private val context: Context) : AppContainer {
	override val musicListRepo: OfflineMusicListRepository by lazy {
		OfflineMusicListRepository(
			MusicListDatabase.getDatabase(context.applicationContext).musicListDao()
		)
	}

	override val musicStatsRepo: OfflineMusicStatsRepository by lazy {
		OfflineMusicStatsRepository(
			MusicListDatabase.getDatabase(context.applicationContext).musicStatsDao()
		)
	}

	override val playlistsRepo: OfflinePlaylistsRepository by lazy {
		OfflinePlaylistsRepository(
			MusicListDatabase.getDatabase(context.applicationContext).playlistDao()
		)
	}

	override val dataStoreManager: DataStoreManager by lazy {
		DataStoreManager(context.applicationContext)
	}

	override val ytDlp: YtDlp by lazy {
		YtDlp(context.applicationContext)
	}
}