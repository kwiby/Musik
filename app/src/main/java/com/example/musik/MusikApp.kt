package com.example.musik

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musik.data.MusikViewModel
import com.example.musik.data.RequestPermissions
import com.example.musik.data.rememberPermissionHandler
import com.example.musik.ui.components.MainContainer
import com.example.musik.ui.components.MusikTopAppBar
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MusikApp(
	viewModel: MusikViewModel = viewModel()
) {
	val permissionStatus = rememberPermissionHandler()
	if (!permissionStatus.status.isGranted) {
		RequestPermissions(permissionStatus)
	}

	Scaffold(
		containerColor = MaterialTheme.colorScheme.background,
		topBar = { MusikTopAppBar() }
	) { innerPadding ->
		MainContainer(
			viewModel = viewModel,
			modifier = Modifier.padding(innerPadding)
		)
	}
}