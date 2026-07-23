package com.example.musik.ui

import android.app.Application
import android.util.Log
import com.example.musik.crash_handling.CrashHandler
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException

class MusikApplication : Application() {
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
	}
}