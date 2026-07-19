package com.example.musik.ui.screens.settings.components.options

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun OptionHeader(
	text: String
) {
	Text(
		text = text,
		color = MaterialTheme.colorScheme.outline,
		style = MaterialTheme.typography.headlineMedium
	)
}