package com.example.musik.ui.view_models

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musik.data.datastore.DataStoreManager
import com.example.musik.data.repositories.audio_file.AudioFileRepository
import com.example.musik.ui.misc.FolderManager
import com.example.musik.ui.misc.YtDlp
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddYtMusicViewModel(
	private val dataStoreManager: DataStoreManager,
	private val audioFileRepo: AudioFileRepository,
	private val ytDlp: YtDlp
) : ViewModel() {
	private val _hasValidFolderPerms = MutableStateFlow<Boolean?>(null)
	val hasValidFolderPerms = _hasValidFolderPerms.asStateFlow()

	private val _isLinkValid = MutableStateFlow(false)
	val isLinkValid = _isLinkValid.asStateFlow()

	private val _ytLink = MutableStateFlow("")
	val ytLink = _ytLink.asStateFlow()

	val downloadLocation = dataStoreManager.downloadLocation.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5_000),
		initialValue = null
	)


	suspend fun checkValidLink(): Boolean {
		if (_ytLink.value.isBlank()) {
			_isLinkValid.value = false

			return false
		} else {
			val result = ytDlp.checkValidLink(_ytLink.value)
			_isLinkValid.value = result

			return result
		}
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

	fun getDataStoreManager(): DataStoreManager {
		return dataStoreManager
	}

	fun resetAddYtMusic() {
		_isLinkValid.value = false
		_ytLink.value = ""
	}
}


// --===--  ConnectivityManager Stuff  --===--
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

fun ConnectivityManager.observeConnectivity(): Flow<Boolean> = callbackFlow {
	trySend(isConnected())

	val networkCallback = object : ConnectivityManager.NetworkCallback() {
		override fun onAvailable(network: Network) {
			trySend(isConnected())
		}

		override fun onLost(network: Network) {
			trySend(isConnected())
		}

		override fun onCapabilitiesChanged(
			network: Network,
			networkCapabilities: NetworkCapabilities
		) {
			trySend(isConnected())
		}

		override fun onUnavailable() {
			trySend(isConnected())
		}
	}

	val request = NetworkRequest.Builder()
		.addCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
		.build()
	registerNetworkCallback(request, networkCallback)

	/*
	 * If the above doesn't work all the time for live updating the state of the internet connection,
	 * then use the below line of code, which should (hopefully) work:
	 *
	 * registerDefaultNetworkCallback(networkCallback)
	 */

	awaitClose {
		unregisterNetworkCallback(networkCallback)
	}
}.distinctUntilChanged()