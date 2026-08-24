package com.kwiby.musik.ui.view_models

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.kwiby.musik.R
import com.kwiby.musik.data.data_classes.updating.UpdateInfo
import com.kwiby.musik.data.updating.ApkInstaller
import com.kwiby.musik.data.updating.UpdateChecker
import com.kwiby.musik.ui.MusikApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

class UpdateViewModel(
	application: MusikApplication,
	private val updateChecker: UpdateChecker,
	private val apkInstaller: ApkInstaller
) : AndroidViewModel(application) {
	sealed class UpdateState {
		object Idle: UpdateState()
		object Checking: UpdateState()
		object UpToDate: UpdateState()
		data class Available(val info: UpdateInfo): UpdateState()
		data class Downloading(val info: UpdateInfo, val progress: Float): UpdateState()
		data class ReadyToInstall(val file: File): UpdateState()
		data class Error(val msg: String): UpdateState()
	}

	private val _updateState = MutableStateFlow<UpdateState>(UpdateState.Idle)
	val updateState: StateFlow<UpdateState> = _updateState.asStateFlow()


	private fun emitToast(text: String) {
		Toast.makeText(getApplication<MusikApplication>(), text, Toast.LENGTH_SHORT).show()
	}

	fun checkForUpdates(isManual: Boolean) {
		viewModelScope.launch {
			_updateState.value = UpdateState.Checking
			if (isManual) {
				emitToast(getApplication<MusikApplication>().getString(R.string.checking_for_updates))
			}

			val info = updateChecker.checkForUpdates(doForce = isManual)
			 when {
				info != null -> _updateState.value = UpdateState.Available(info)
				isManual -> {
					_updateState.value = UpdateState.UpToDate
					emitToast(getApplication<MusikApplication>().getString(R.string.app_already_up_to_date))
				}
				else -> _updateState.value = UpdateState.Idle
			}
		}
	}

	fun startDownload(info: UpdateInfo) {
		viewModelScope.launch {
			try {
				val file = apkInstaller.downloadApk(info.apkUrl) { progress ->
					_updateState.value = UpdateState.Downloading(info, progress)
				}
				_updateState.value = UpdateState.ReadyToInstall(file)
			} catch (e: Exception) {
				_updateState.value = UpdateState.Error(e.message ?: "Download failed")
			}
		}
	}

	fun startInstall(file: File) {
		apkInstaller.installApk(file)
	}

	fun dismiss() {
		_updateState.value = UpdateState.Idle
	}

	fun cleanupOldApks(context: Context) {
		val downloadDir = File(context.cacheDir, "apk")
		downloadDir.listFiles() { file -> file.extension == "apk" }?.forEach { it.delete() }
	}
}