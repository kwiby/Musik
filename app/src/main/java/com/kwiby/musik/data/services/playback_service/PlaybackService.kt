package com.kwiby.musik.data.services.playback_service

import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.OptIn
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.kwiby.musik.MainActivity
import com.kwiby.musik.data.services.playback_service.components.PlaybackServiceHolder
import com.kwiby.musik.data.services.playback_service.components.PlaybackStatsTracker
import com.kwiby.musik.data.services.playback_service.components.SleepTimerController
import com.kwiby.musik.ui.MusikApplication
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.time.Duration.Companion.milliseconds

class PlaybackService: MediaSessionService() {
	private var mediaSession: MediaSession? = null
	private lateinit var player: ExoPlayer

	private lateinit var statsTracker: PlaybackStatsTracker
	lateinit var sleepTimerController: SleepTimerController
	private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

	override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
		return mediaSession
	}

	@OptIn(UnstableApi::class)
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
			skipSilenceEnabled = false // TODO: Allow user to toggle(?)
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

		statsTracker = PlaybackStatsTracker(
			player,
			serviceScope,
			(application as MusikApplication).container.musicStatsRepo
		)
		player.addListener(statsTracker)

		sleepTimerController = SleepTimerController(player, serviceScope)
		PlaybackServiceHolder.attach(this)
	}

	override fun onDestroy() {
		PlaybackServiceHolder.detach(this)
		sleepTimerController.release()

		runBlocking {
			withTimeoutOrNull(1000.milliseconds) {
				statsTracker.flush()
			}
			statsTracker.reset()
		}

		mediaSession?.run {
			if (player.isCommandAvailable(Player.COMMAND_STOP)) {
				player.release()
			}

			release()
			mediaSession = null
		}

		serviceScope.cancel()
		super.onDestroy()
	}

	override fun onTaskRemoved(rootIntent: Intent?) {
		sleepTimerController.stopSleepTimer()

		/*
		val player = mediaSession?.player
		if (player != null && !player.playWhenReady) {
			stopSelf()
		}
		 */
		runBlocking {
			withTimeoutOrNull(1000.milliseconds) {
				statsTracker.flush()
			}
			statsTracker.reset()
		}
		stopSelf()

		super.onTaskRemoved(rootIntent)
	}
}