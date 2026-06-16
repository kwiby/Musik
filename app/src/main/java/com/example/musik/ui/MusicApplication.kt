package com.example.musik.ui

import android.app.Application
import com.example.musik.data.misc.AppContainer
import com.example.musik.data.misc.AppDataContainer

class MusicApplication : Application() {
	lateinit var container: AppContainer
	override fun onCreate() {
		super.onCreate()
		container = AppDataContainer(this)
	}
}