package com.kwiby.musik.ui

import android.app.Application
import android.util.Log
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import com.kwiby.musik.crash_handling.CrashHandler
import com.kwiby.musik.data.coil.ArtworkFetcher
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException

private const val LOG_TAG = "MusikApplication"

class MusikApplication : Application(), SingletonImageLoader.Factory {
	lateinit var container: AppContainer

	override fun onCreate() {
		super.onCreate()
		container = AppDataContainer(this)

		Thread.setDefaultUncaughtExceptionHandler(CrashHandler(this))

		try {
			YoutubeDL.getInstance().init(this)
			FFmpeg.getInstance().init(this)
		} catch (e: YoutubeDLException) {
			Log.e(LOG_TAG, "Failed to initialize youtubedl-android", e)
		}
	}

	override fun newImageLoader(context: PlatformContext): ImageLoader {
		Log.d("debug", "Building custom ImageLoader")
		val loader = ImageLoader.Builder(context)
			.components {
				add(ArtworkFetcher.Factory(context))
			}
			.build()
		Log.d("debug", "Fetcher factories: ${loader.components.fetcherFactories}")
		Log.d("debug", "ImageLoader built: $loader, components=${loader.components}")
		return loader
	}
}