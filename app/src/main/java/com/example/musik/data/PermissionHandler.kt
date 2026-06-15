package com.example.musik.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberPermissionHandler(): PermissionState {
	val permissionStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android Version >= 13
		rememberPermissionState(android.Manifest.permission.READ_MEDIA_AUDIO)
	} else { // Android Version < 13
		rememberPermissionState(android.Manifest.permission.READ_EXTERNAL_STORAGE)
	}

	return permissionStatus
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
// Omit the 'trigger' argument if requesting permissions at launch.
fun RequestPermissions(permissionStatus: PermissionState, trigger: Any = Unit) {
	LaunchedEffect(trigger) {
		if (!permissionStatus.status.isGranted) {
			permissionStatus.launchPermissionRequest()
		}
	}
}

fun openPermissionsSettings(context: Context) {
	val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
		data = Uri.fromParts("package", context.packageName, null)
	}

	context.startActivity(intent)
}