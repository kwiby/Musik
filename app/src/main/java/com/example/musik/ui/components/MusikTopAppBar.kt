package com.example.musik.ui.components

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.musik.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusikTopAppBar(modifier: Modifier = Modifier) {
	TopAppBar(
		title = {
			Text(
				text = stringResource(R.string.app_name),
				style = MaterialTheme.typography.titleLarge,
				color = MaterialTheme.colorScheme.onPrimary
			)
		},
		colors = TopAppBarDefaults.topAppBarColors(
			containerColor = MaterialTheme.colorScheme.background
		),
		windowInsets = WindowInsets(top = 150, bottom = 50, left = 50),
	)
}