package com.example.musik.ui.view_models

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.musik.data.data_classes.VideoInfo
import com.example.musik.data.datastore.DataStoreManager
import com.example.musik.data.repositories.audio_file.AudioFileRepository
import com.example.musik.ui.misc.folder_manager.FolderManager
import com.example.musik.ui.misc.ytdlp.YtDlp
import com.example.musik.work_manager.DownloadWorker
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddYtMusicViewModel(
	application: Application,
	dataStoreManager: DataStoreManager,
	private val ytDlp: YtDlp,
	private val audioFileRepo: AudioFileRepository
) : AndroidViewModel(application) {
	sealed interface DownloaderUiState {
		data object Empty: DownloaderUiState // No actions executed yet
		data object Loading: DownloaderUiState // Loading in progress
		data object Downloading: DownloaderUiState // Download in progress
		data object InvalidLink: DownloaderUiState // Inputted yt video link is invalid
		data object OutdatedYtDlp: DownloaderUiState // Outdated YtDlp binary version
		data object Success: DownloaderUiState // Downloading completed successfully
		data object Error: DownloaderUiState // Unexpected errors
	}

	private val workManager = WorkManager.getInstance(application)

	private val _uiState = MutableStateFlow<DownloaderUiState>(DownloaderUiState.Empty)
	val uiState: StateFlow<DownloaderUiState> = _uiState.asStateFlow()

	private val _hasValidFolderPerms = MutableStateFlow<Boolean?>(null)
	val hasValidFolderPerms = _hasValidFolderPerms.asStateFlow()

	private val _ytLink = MutableStateFlow("")
	val ytLink = _ytLink.asStateFlow()

	val videoInfo: StateFlow<VideoInfo?> = ytDlp.videoInfo

	val downloadLocation = dataStoreManager.downloadLocation.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = null
	)

	val downloadPercent: StateFlow<Float> = ytDlp.downloadPercent
	val downloadSpeed: StateFlow<String> = ytDlp.downloadSpeed
	val eta: StateFlow<String> = ytDlp.eta


	private fun applyWorkInfo(workInfo: WorkInfo) {
		when (workInfo.state) {
			WorkInfo.State.ENQUEUED -> _uiState.value = DownloaderUiState.Loading
			WorkInfo.State.RUNNING -> _uiState.value = DownloaderUiState.Downloading
			WorkInfo.State.SUCCEEDED -> _uiState.value = DownloaderUiState.Success
			WorkInfo.State.CANCELLED -> {
				if (!isProcessing()) {
					_uiState.value = DownloaderUiState.Empty
				}
			}
			WorkInfo.State.FAILED -> {
				_uiState.value = when (workInfo.outputData.getString(DownloadWorker.KEY_FAILURE_REASON)) {
					DownloadWorker.REASON_OUTDATED_YTDLP -> DownloaderUiState.OutdatedYtDlp
					DownloadWorker.REASON_VIDEO_UNAVAILABLE -> DownloaderUiState.InvalidLink
					DownloadWorker.REASON_CANCELLED -> DownloaderUiState.Empty
					else -> DownloaderUiState.Error
				}
			}
			WorkInfo.State.BLOCKED -> {}
		}
	}

	private fun enqueueDownload(link: String) {
		check(downloadLocation.value != null)

		val request = OneTimeWorkRequestBuilder<DownloadWorker>()
			.setConstraints(
				Constraints.Builder()
					.setRequiredNetworkType(NetworkType.CONNECTED)
					.build()
			)
			.setInputData(
				workDataOf(
					DownloadWorker.KEY_LINK to link,
					DownloadWorker.KEY_DOWNLOAD_LOCATION to downloadLocation.value
				)
			)
			.build()

		workManager.enqueueUniqueWork(
			DownloadWorker.WORK_NAME,
			ExistingWorkPolicy.REPLACE,
			request
		)
	}

	fun checkValidLink(): Boolean {
		return ytDlp.checkValidLink(_ytLink.value)
	}

	fun isProcessing(): Boolean {
		return _uiState.value == DownloaderUiState.Loading
				|| _uiState.value == DownloaderUiState.Downloading
	}

	fun startDownloadButton() {
		if (!_ytLink.value.isBlank()) {
			val link = _ytLink.value
			_uiState.value = DownloaderUiState.Loading

			viewModelScope.launch {
				val result = checkValidLink()
				if (result) {
					_uiState.value = DownloaderUiState.Downloading
					enqueueDownload(link)
				} else {
					_uiState.value = DownloaderUiState.InvalidLink
				}
			}
		}
	}

	fun stopDownloadButton() {
		ytDlp.stopDownload()
		workManager.cancelUniqueWork(DownloadWorker.WORK_NAME)
		_uiState.value = DownloaderUiState.Empty
	}

	fun checkFolderPerms(folderManager: FolderManager) {
		viewModelScope.launch {
			val downloadLocation = downloadLocation.value
			val isValid = folderManager.hasValidPerms(downloadLocation)

			_hasValidFolderPerms.value = isValid
			if (!isValid && !downloadLocation.isNullOrEmpty()) {
				folderManager.resetDownloadLocation()
			}
		}
	}

	fun onYouTubeLinkChange(newLink: String) {
		_ytLink.value = newLink
	}

	fun resetAddYtMusic() {
		_ytLink.value = ""
		if (!isProcessing()) {
			_uiState.value = DownloaderUiState.Empty
		}
	}


	init {
		workManager.getWorkInfosForUniqueWorkLiveData(DownloadWorker.WORK_NAME)
			.observeForever { workInfos ->
				val workInfo = workInfos?.firstOrNull() ?: return@observeForever
				applyWorkInfo(workInfo)
			}
	}
}


// --===--  ConnectivityManager Stuff  --===--
/*
fun ConnectivityManager.isConnected(): Boolean {
	val network = this.activeNetwork ?: return false
	val capabilities = this.getNetworkCapabilities(network) ?: return false

	val hasInternetCapability =
		capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
				&& capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
	val hasTransportCapability =
		capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
				|| capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
				|| capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
				|| capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)


	return hasInternetCapability && hasTransportCapability
}
 */

fun ConnectivityManager.observeConnectivity(): Flow<Boolean> = callbackFlow {
	val networkCallback = object : ConnectivityManager.NetworkCallback() {
		private fun isConnected(capabilities: NetworkCapabilities?): Boolean {
			return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
					&& capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
		}

		override fun onAvailable(network: Network) {
			trySend(true)
		}

		override fun onLost(network: Network) {
			trySend(false)
		}

		override fun onCapabilitiesChanged(
			network: Network,
			networkCapabilities: NetworkCapabilities
		) {
			trySend(isConnected(networkCapabilities))
		}

		override fun onUnavailable() {
			trySend(false)
		}
	}

	registerDefaultNetworkCallback(networkCallback)

	awaitClose {
		unregisterNetworkCallback(networkCallback)
	}
}.distinctUntilChanged()