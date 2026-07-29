package com.kwiby.musik.ui.tabs.stats.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.kwiby.musik.R

@Composable
fun StatsOverviewContainer(
	titleText: String,
	valueText: String,
	modifier: Modifier = Modifier
) {
	Surface(
		modifier = modifier
			.height(dimensionResource(R.dimen.stats_overview_container_height)),
		shape = MaterialTheme.shapes.small,
		color = MaterialTheme.colorScheme.secondaryContainer
	) {
		Column(
			modifier = Modifier.padding(dimensionResource(R.dimen.medium_padding)),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Text(
				text = titleText,
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.labelLarge,
				textAlign = TextAlign.Center
			)

			Text(
				text = valueText,
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.labelLarge.copy(
					fontSize = 23.sp
				),
				textAlign = TextAlign.Center
			)
		}
	}
}