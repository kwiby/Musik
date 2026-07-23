package com.example.musik.ui

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import com.example.musik.crash_handling.CrashHandler
import com.example.musik.work_manager.DownloadWorker
import com.example.musik.work_manager.DownloadWorkerFactory
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException

class MusikApplication : Application(), Configuration.Provider {
	lateinit var container: AppContainer

	override fun onCreate() {
		super.onCreate()
		container = AppDataContainer(this)

		Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this))

		try {
			YoutubeDL.getInstance().init(this)
			FFmpeg.getInstance().init(this)
		} catch (e: YoutubeDLException) {
			Log.e("MusikApplication", "Failed to initialize youtubedl-android", e)
		}

		DownloadWorker.createNotificationChannel(this)
	}

	override val workManagerConfiguration: Configuration get() = Configuration.Builder()
			.setWorkerFactory(DownloadWorkerFactory(container.ytDlp, container.dataStoreManager)).build()
}