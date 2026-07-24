package com.example.musik.ui.view_models

import android.app.Application
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.musik.data.data_classes.AudioFile
import com.example.musik.data.data_classes.VideoInfo
import com.example.musik.data.datastore.DataStoreManager
import com.example.musik.data.repositories.audio_file.AudioFileRepository
import com.example.musik.ui.misc.folder_manager.FolderManager
import com.example.musik.ui.misc.ytdlp.YtDlp
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddYtMusicViewModel(
	application: Application,
	private val dataStoreManager: DataStoreManager,
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

	private var downloadJob: Job? = null

	val videoInfo: StateFlow<VideoInfo?> = ytDlp.videoInfo

	private val _uiState = MutableStateFlow<DownloaderUiState>(DownloaderUiState.Empty)
	val uiState: StateFlow<DownloaderUiState> = _uiState.asStateFlow()

	private val _hasValidFolderPerms = MutableStateFlow<Boolean?>(null)
	val hasValidFolderPerms = _hasValidFolderPerms.asStateFlow()

	private val _ytLink = MutableStateFlow("")
	val ytLink = _ytLink.asStateFlow()

	val downloadLocation = dataStoreManager.downloadLocation.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = null
	)

	val downloadPercent: StateFlow<Float> = ytDlp.downloadPercent
	val downloadSpeed: StateFlow<String> = ytDlp.downloadSpeed
	val eta: StateFlow<String> = ytDlp.eta


	private suspend fun startDownload(link: String) {
		/*
		 * This function should only be called when the download location is already selected,
		 * hence the non-null value
		 */
		check(downloadLocation.value != null)

		val downloadResult: YtDlp.DownloadResult = ytDlp.startDownload(
			dataStoreManager.doConvertMp3.first(),
			downloadLocation.value!!,
			link
		)

		when (downloadResult) {
			is YtDlp.DownloadResult.Success -> {
				addDownloadedMusic(
					id = downloadResult.id,
					contentUri = downloadResult.contentUri,
					albumArtUri = downloadResult.albumArtUri,
					title = downloadResult.title,
					artist = downloadResult.artist,
					duration = downloadResult.duration,
				)

				_uiState.value = DownloaderUiState.Success
			}
			YtDlp.DownloadResult.OutdatedYtDlp -> _uiState.value = DownloaderUiState.OutdatedYtDlp
			YtDlp.DownloadResult.VideoUnavailable -> _uiState.value = DownloaderUiState.InvalidLink
			YtDlp.DownloadResult.Cancelled -> {} // State already change to Empty in stopDownload()
			YtDlp.DownloadResult.Error -> _uiState.value = DownloaderUiState.Error
		}
	}

	private fun stopDownload() {
		ytDlp.stopDownload()

		downloadJob?.cancel()
		downloadJob = null

		_uiState.value = DownloaderUiState.Empty
	}

	private fun addDownloadedMusic(
		id: Long,
		contentUri: Uri,
		albumArtUri: Uri,
		title: String,
		artist: String,
		duration: Long
	) {
		viewModelScope.launch {
			audioFileRepo.insertAudioFile(
				AudioFile(
					id = id,
					contentUri = contentUri.toString(),
					albumArtUri = albumArtUri.toString(),
					title = title,
					artist = artist,
					duration = duration
				).copy(
					// 0-indexed, so the next orderPos is just the total count of db items
					orderPos = audioFileRepo.getAudioFileCount()
				)
			)
		}
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

			downloadJob = viewModelScope.launch {
				val result = checkValidLink()
				if (result) {
					_uiState.value = DownloaderUiState.Downloading
					startDownload(link)
				} else {
					_uiState.value = DownloaderUiState.InvalidLink
				}
			}
		}
	}

	fun stopDownloadButton() {
		stopDownload()
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