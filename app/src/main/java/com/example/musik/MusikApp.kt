package com.example.musik

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.musik.data.MusikViewModel
import com.example.musik.ui.components.MainContainer
import com.example.musik.ui.components.MusikTopAppBar

@Composable
fun MusikApp(
	viewModel: MusikViewModel = viewModel()
) {
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