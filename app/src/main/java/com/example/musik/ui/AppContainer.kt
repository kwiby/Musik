package com.example.musik.ui

import android.content.Context
import com.example.musik.data.datastore.DataStoreManager
import com.example.musik.data.db.AudioFileDatabase
import com.example.musik.data.repositories.audio_file.AudioFileRepository
import com.example.musik.data.repositories.audio_file.OfflineAudioFileRepository
import com.example.musik.ui.misc.ytdlp.YtDlp

//import com.example.musik.ui.misc.YtDlp.YtDlp

interface AppContainer {
	val audioFileRepository: AudioFileRepository
	val dataStoreManager: DataStoreManager
	val ytDlp: YtDlp
}

class AppDataContainer(private val context: Context) : AppContainer {
	override val audioFileRepository: AudioFileRepository by lazy {
		OfflineAudioFileRepository(AudioFileDatabase.getDatabase(context.applicationContext).audioFileDao())
	}

	override val dataStoreManager: DataStoreManager by lazy {
		DataStoreManager(context.applicationContext)
	}

	override val ytDlp: YtDlp by lazy {
		YtDlp(context.applicationContext)
	}
}