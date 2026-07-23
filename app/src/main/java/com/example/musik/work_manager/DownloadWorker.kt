package com.example.musik.work_manager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.musik.R
import com.example.musik.data.datastore.DataStoreManager
import com.example.musik.ui.misc.ytdlp.YtDlp
import kotlinx.coroutines.flow.first

class DownloadWorker(
	appContext: Context,
	params: WorkerParameters,
	private val ytDlp: YtDlp,
	private val dataStoreManager: DataStoreManager
) : CoroutineWorker(appContext, params) {
	companion object {
		const val WORK_NAME = "yt_dlp_download_work"

		const val KEY_LINK = "link"
		const val KEY_DOWNLOAD_LOCATION = "download_location"

		const val KEY_FAILURE_REASON = "failure_reason"
		const val REASON_OUTDATED_YTDLP = "outdated_ytdlp"
		const val REASON_VIDEO_UNAVAILABLE = "video_unavailable"
		const val REASON_CANCELLED = "cancelled"
		const val REASON_ERROR = "error"

		private const val NOTIFICATION_CHANNEL_ID = "yt_dlp_download_channel"
		private const val NOTIFICATION_ID = 1840

		fun createNotificationChannel(context: Context) {
			val channel = NotificationChannel(
				NOTIFICATION_CHANNEL_ID,
				context.getString(R.string.downloadworker_channel_name),
				NotificationManager.IMPORTANCE_LOW
			)
			val notificationManager = context.getSystemService(NotificationManager::class.java)

			notificationManager?.createNotificationChannel(channel)
		}
	}

	override suspend fun doWork(): Result {
		setForeground(createForegroundInfo())

		val link = inputData.getString(KEY_LINK)
		val downloadLocation = inputData.getString(KEY_DOWNLOAD_LOCATION)

		if (link.isNullOrBlank() || downloadLocation.isNullOrBlank()) {
			return Result.failure(
				workDataOf(KEY_FAILURE_REASON to REASON_ERROR)
			)
		}

		val doConvertMp3 = dataStoreManager.doConvertMp3.first()

		val downloadResult = ytDlp.startDownload(
			doConvertMp3 = doConvertMp3,
			downloadLocationStr = downloadLocation,
			link = link
		)

		return when (downloadResult) {
			YtDlp.DownloadResult.Success -> Result.success()
			YtDlp.DownloadResult.OutdatedYtDlp -> Result.failure(
				workDataOf(KEY_FAILURE_REASON to REASON_OUTDATED_YTDLP)
			)
			YtDlp.DownloadResult.VideoUnavailable -> Result.failure(
				workDataOf(KEY_FAILURE_REASON to REASON_VIDEO_UNAVAILABLE)
			)
			YtDlp.DownloadResult.Cancelled -> Result.failure(
				workDataOf(KEY_FAILURE_REASON to REASON_CANCELLED)
			)
			YtDlp.DownloadResult.Error -> Result.failure(
				workDataOf(KEY_FAILURE_REASON to REASON_ERROR)
			)
		}
	}

	private fun createForegroundInfo(): ForegroundInfo {
		val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
			.setContentTitle(applicationContext.getString(R.string.downloadworker_notif_title))
			.setContentText(applicationContext.getString(R.string.downloadworker_notif_text))
			.setSmallIcon(R.drawable.musik_pixel_icon)
			.setOngoing(true)
			.setOnlyAlertOnce(true)
			.build()

		return ForegroundInfo(
			NOTIFICATION_ID,
			notification,
			ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
		)
	}
}