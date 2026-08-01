package com.kwiby.musik.ui.components

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun customTextSelectionColours(): TextSelectionColors {
	return TextSelectionColors(
		handleColor = MaterialTheme.colorScheme.outline,
		backgroundColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)
	)
}