package com.example.musik.playback

import android.app.PendingIntent
import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.musik.MainActivity

class PlaybackService: MediaSessionService() {
	private var mediaSession: MediaSession? = null
	private lateinit var player: ExoPlayer

	override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
		return mediaSession
	}

	override fun onCreate() {
		super.onCreate()

		player = ExoPlayer.Builder(this).build().apply {
			setAudioAttributes(
				AudioAttributes.Builder()
					.setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
					.setUsage(C.USAGE_MEDIA)
					.build(),
				true
			)
			setHandleAudioBecomingNoisy(true)
			setWakeMode(C.WAKE_MODE_LOCAL)
		}

		val pendingIntent = PendingIntent.getActivity(
			this,
			0,
			Intent(this, MainActivity::class.java),
			PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
		)

		mediaSession = MediaSession.Builder(this, player)
			.setSessionActivity(pendingIntent)
			.build()
	}

	override fun onDestroy() {
		mediaSession?.run {
			if (player.isCommandAvailable(androidx.media3.common.Player.COMMAND_STOP)) {
				player.release()
			}

			release()
			mediaSession = null
		}

		super.onDestroy()
	}

	override fun onTaskRemoved(rootIntent: Intent?) {
		super.onTaskRemoved(rootIntent)
		stopSelf()
	}
}