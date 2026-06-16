package com.example.musik.data.misc

import android.content.Context
import com.example.musik.data.db.AudioFileDatabase
import com.example.musik.data.repository.AudioFileRepository
import com.example.musik.data.repository.OfflineAudioFileRepository

interface AppContainer {
	val audioFileRepository: AudioFileRepository
}

class AppDataContainer(private val context: Context) : AppContainer {
	override val audioFileRepository: AudioFileRepository by lazy {
		OfflineAudioFileRepository(AudioFileDatabase.getDatabase(context).audioFileDao())
	}
}