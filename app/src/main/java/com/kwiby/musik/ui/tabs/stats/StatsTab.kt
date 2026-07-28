package com.kwiby.musik.ui.tabs.stats

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kwiby.musik.R
import com.kwiby.musik.ui.components.CustomIconButton
import com.kwiby.musik.ui.components.LoadingIndicator
import com.kwiby.musik.ui.tabs.stats.components.StatsOverviewContainer
import com.kwiby.musik.ui.view_models.StatsViewModel
import com.kwiby.musik.ui.view_models.ViewModelProvider

@Composable
fun StatsTab(
	statsViewModel: StatsViewModel = viewModel(factory = ViewModelProvider.Factory)
) {
	val isLoading = statsViewModel.isLoading

	LaunchedEffect(Unit) {
		statsViewModel.resetStatsTab()
	}

	Column {
		Spacer(Modifier.height(dimensionResource(R.dimen.tabs_buttons_padding)))

		// ---===---  All Buttons  ---===---
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			// ---===---  Change Sorting Rule Button  ---===---
			Row {
				Spacer(modifier = Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))

				CustomIconButton(
					iconImageVector = Icons.Rounded.SwapVert,
					contentDescription = stringResource(R.string.back_button)
				) {
					statsViewModel.switchSortingRuleButton()
				}
			}

			// ---===---  Refresh Button  ---===---
			Row {
				CustomIconButton(
					iconImageVector = Icons.Rounded.Refresh,
					contentDescription = stringResource(R.string.back_button)
				) {
					statsViewModel.refreshButton()
				}

				Spacer(Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))
			}
		}

		Spacer(Modifier.height(dimensionResource(R.dimen.xx_small_padding)))

		// --===--  Stats Overview  --===--
		Crossfade(
			targetState = isLoading,
			label = "stats_loading"
		) { doLoading ->
			if (doLoading) {
				LoadingIndicator()
			} else {
				Column {
					Row(
						modifier = Modifier
							.fillMaxWidth()
							.padding(horizontal = dimensionResource(R.dimen.stats_container_horizontal_padding)),
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						// --===--  Total Play Count  --===--
						StatsOverviewContainer(
							stringResource(R.string.stats_total_play_count),
							statsViewModel.overallPlayCount,
							Modifier.weight(1f)
						)

						Spacer(Modifier.width(dimensionResource(R.dimen.stats_container_gap)))

						// --===--  Total Listen Time  --===--
						StatsOverviewContainer(
							stringResource(R.string.stats_total_listen_time),
							statsViewModel.overallListenTime,
							Modifier.weight(1f)
						)
					}

					Spacer(Modifier.height(dimensionResource(R.dimen.buttons_vertical_padding)))

					// --===--  Stats List  --===--
					Surface(
						modifier = Modifier
							.fillMaxSize()
							.padding(
								start = dimensionResource(R.dimen.stats_container_horizontal_padding),
								end = dimensionResource(R.dimen.stats_container_horizontal_padding),
								bottom = dimensionResource(R.dimen.stats_bottom_padding)
							),
						shape = MaterialTheme.shapes.small,
						color = MaterialTheme.colorScheme.secondaryContainer
					) {
						Column {

						}
					}
				}
			}
		}
	}
}