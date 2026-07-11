package com.example.musik.ui

import android.app.Application
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.musik.data.misc.AppContainer
import com.example.musik.data.misc.AppDataContainer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MusikApplication : Application() {
	lateinit var container: AppContainer

	override fun onCreate() {
		super.onCreate()
		container = AppDataContainer(this)

		if (!Python.isStarted()) {
			Python.start(AndroidPlatform(this))
		}

		CoroutineScope(Dispatchers.IO).launch {
			runCatching {
				Python.getInstance().getModule("ytdlp").callAttr("warm_up")
			}
		}
	}
}