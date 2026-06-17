package com.example.musik.ui.screens.all_music.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.musik.R

@Composable
fun LoadingIndicator() {
	Spacer(modifier = Modifier.height(dimensionResource(R.dimen.x_large_padding)))

	Box(
		contentAlignment = Alignment.TopCenter,
		modifier = Modifier.fillMaxSize()
	) {
		CircularProgressIndicator(color = MaterialTheme.colorScheme.onSecondary)
	}
}