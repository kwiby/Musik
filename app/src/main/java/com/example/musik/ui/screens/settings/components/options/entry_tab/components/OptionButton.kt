package com.example.musik.ui.screens.settings.components.options.entry_tab.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import com.example.musik.R

@Composable
fun OptionButton(
	text: String,
	isSelected: Boolean,
	modifier: Modifier = Modifier,
	onClick: () -> Unit = {}
) {
	val interactionSource = remember { MutableInteractionSource() }

	Surface(
		modifier = modifier
			.height(dimensionResource(R.dimen.option_button_height))
			.padding(horizontal = dimensionResource(R.dimen.option_button_horizontal_padding))
			.clickable(
				interactionSource = interactionSource,
				indication = null
			) {
				onClick()
			},
		shape = MaterialTheme.shapes.medium,
		color = if (isSelected) {
			MaterialTheme.colorScheme.outlineVariant
		} else {
			MaterialTheme.colorScheme.secondaryContainer
		},
		border = BorderStroke(
			dimensionResource(R.dimen.option_button_border_thickness),
			MaterialTheme.colorScheme.onSurfaceVariant
		),
		shadowElevation = dimensionResource(R.dimen.option_button_shadow_elevation)
	) {
		Box(
			contentAlignment = Alignment.Center
		) {
			Text(
				text = text,
				color = Color.White,
				style = MaterialTheme.typography.bodyLarge,
				overflow = TextOverflow.Ellipsis,
				maxLines = 1
			)
		}
	}
}