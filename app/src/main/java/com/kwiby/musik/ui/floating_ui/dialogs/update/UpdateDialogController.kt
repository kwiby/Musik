package com.kwiby.musik.ui.floating_ui.dialogs.update

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kwiby.musik.ui.view_models.UpdateViewModel

private const val LOG_TAG = "UpdateDialogController"

@Composable
fun UpdateDialogController(
	updateViewModel: UpdateViewModel
) {
	val state by updateViewModel.updateState.collectAsStateWithLifecycle()

	when (val s = state) {
		is UpdateViewModel.UpdateState.Available -> {
			UpdateDialog(
				updateInfo = s.info,
				downloadProgress = null,
				onUpdate = { updateViewModel.startDownload(s.info) },
				onDismiss = { updateViewModel.dismiss() }
			)
		}
		is UpdateViewModel.UpdateState.Downloading -> {
			UpdateDialog(
				updateInfo = s.info,
				downloadProgress = s.progress,
				onUpdate = {},
				onDismiss = {}
			)
		}
		is UpdateViewModel.UpdateState.ReadyToInstall -> {
			LaunchedEffect(s.file) {
				updateViewModel.startInstall(s.file)
			}
		}
		is UpdateViewModel.UpdateState.Error -> Log.e(LOG_TAG, "Update error: ${s.msg}")
		UpdateViewModel.UpdateState.Idle,
		UpdateViewModel.UpdateState.Checking,
		UpdateViewModel.UpdateState.UpToDate -> Log.i(LOG_TAG, "Musik is already up to date")
	}
}