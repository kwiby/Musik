package com.example.musik.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.musik.R

@Composable
fun CustomIconButton(
	iconImageVector: ImageVector,
	contentDescription: String,
	size: Dp = 24.dp,
	colour: Color = MaterialTheme.colorScheme.onSecondary,
	onClick: () -> Unit
) {
	CompositionLocalProvider(
		LocalContentColor provides MaterialTheme.colorScheme.primary,
		LocalMinimumInteractiveComponentSize provides dimensionResource(R.dimen.zero)
	) {
		IconButton(
			colors = IconButtonDefaults.iconButtonColors(
				contentColor = colour
			),
			onClick = onClick,
			//modifier = Modifier.size(40.dp)
		) {
			Icon(
				iconImageVector,
				contentDescription = contentDescription,
				modifier = Modifier.size(size)
			)
		}
	}
}