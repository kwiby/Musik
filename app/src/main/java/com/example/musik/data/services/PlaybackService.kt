package com.example.musik.data.services

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class PlaybackService: MediaSessionService() {
	private var mediaSession: MediaSession? = null

	override fun onGetSession(controllerInfo: MediaSession.ControllerInfo) = mediaSession

	override fun onCreate() {
		super.onCreate()

		val player = ExoPlayer.Builder(this)
			.setAudioAttributes(AudioAttributes.DEFAULT, /*handleAudioFocus =*/ true)
			.setHandleAudioBecomingNoisy(true)
			.setWakeMode(C.WAKE_MODE_LOCAL)
			.build()

		mediaSession = MediaSession.Builder(this, player)
			.setSessionActivity(
				PendingIntent.getActivity(
					this, 0,
					Intent(this, com.example.musik.MainActivity::class.java),
					PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
				)
			)
			.build()
	}

	override fun onDestroy() {
		mediaSession?.run {
			player.release()
			release()
			mediaSession = null
		}

		super.onDestroy()
	}

	override fun onTaskRemoved(rootIntent: Intent?) {
		super.onTaskRemoved(rootIntent)

		mediaSession?.run {
			player.stop()
			player.release()
			release()
			mediaSession = null
		}

		stopSelf()
	}
}