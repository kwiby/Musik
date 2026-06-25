package com.example.musik

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musik.data.misc.RequestPermissions
import com.example.musik.data.misc.rememberPermissionHandler
import com.example.musik.ui.components.MainContainer
import com.example.musik.ui.components.MusikTopAppBar
import com.example.musik.ui.view_models.NavViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MusikApp(
	navViewModel: NavViewModel = viewModel()
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
			navViewModel = navViewModel,
			modifier = Modifier.padding(innerPadding)
		)
	}
}