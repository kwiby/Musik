package com.example.musik.work_manager

import android.content.Context
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.musik.data.datastore.DataStoreManager
import com.example.musik.ui.misc.ytdlp.YtDlp

class DownloadWorkerFactory(
	private val ytDlp: YtDlp,
	private val dataStoreManager: DataStoreManager
) : WorkerFactory() {
	override fun createWorker(
		appContext: Context,
		workerClassName: String,
		workerParameters: WorkerParameters
	) = when (workerClassName) {
		DownloadWorker::class.java.name -> DownloadWorker(
			appContext,
			workerParameters,
			ytDlp,
			dataStoreManager
		)
		else -> null // Fall back to default factory for any other workers
	}
}