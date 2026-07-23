package com.example.musik.ui.screens.settings.components.options

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.musik.R

@Composable
fun OptionHeader(
	text: String
) {
	Row(
		verticalAlignment = Alignment.CenterVertically
	) {
		Text(
			text = text,
			color = MaterialTheme.colorScheme.outline,
			style = MaterialTheme.typography.headlineMedium
		)

		Spacer(Modifier.width(dimensionResource(R.dimen.option_header_divider_horizontal_padding)))

		HorizontalDivider(
			modifier = Modifier.weight(1f),
			thickness = dimensionResource(R.dimen.option_divider_thickness),
			color = MaterialTheme.colorScheme.secondary
		)
	}
}