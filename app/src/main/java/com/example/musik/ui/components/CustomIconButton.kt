package com.example.musik.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun CustomIconButton(
	onClick: () -> Unit,
	iconImageVector: ImageVector,
	contentDescription: String
) {
	CompositionLocalProvider(
		LocalContentColor provides MaterialTheme.colorScheme.primary
	) {
		IconButton(
			colors = IconButtonDefaults.iconButtonColors(
				contentColor = MaterialTheme.colorScheme.onSecondary
			),

			onClick = onClick
		) {
			Icon(
				iconImageVector,
				contentDescription = contentDescription
			)
		}
	}
}