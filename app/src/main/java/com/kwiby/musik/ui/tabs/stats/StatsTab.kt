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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.SwapVert
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kwiby.musik.R
import com.kwiby.musik.ui.components.CustomIconButton
import com.kwiby.musik.ui.components.LoadingIndicator
import com.kwiby.musik.ui.components.verticalScrollbar
import com.kwiby.musik.ui.tabs.stats.components.StatsListItem
import com.kwiby.musik.ui.tabs.stats.components.StatsOverviewContainer
import com.kwiby.musik.ui.tabs.stats.components.info.NoMusicStatsMsg
import com.kwiby.musik.ui.view_models.StatsViewModel
import com.kwiby.musik.ui.view_models.ViewModelProvider

@Composable
fun StatsTab(
	statsViewModel: StatsViewModel = viewModel(factory = ViewModelProvider.Factory)
) {
	val lazyListState = rememberLazyListState()
	val isLoading = statsViewModel.isLoading

	LaunchedEffect(Unit) {
		statsViewModel.resetStatsTab()
	}
	LaunchedEffect(statsViewModel.selectedOrderRule, statsViewModel.refreshTrigger) {
		if (lazyListState.firstVisibleItemIndex != 0) {
			lazyListState.scrollToItem(0)
		}
	}

	Column {
		Spacer(Modifier.height(dimensionResource(R.dimen.tabs_buttons_padding)))

		// ---===---  All Buttons  ---===---
		Row(
			modifier = Modifier.fillMaxWidth(),
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			// ---===---  Change Sorting Rule Button  ---===---
			Row(
				verticalAlignment = Alignment.CenterVertically
			) {
				Spacer(modifier = Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))

				CustomIconButton(
					iconImageVector = Icons.Rounded.SwapVert,
					contentDescription = stringResource(R.string.back_button)
				) {
					statsViewModel.switchSortingRuleButton()
				}

				Spacer(modifier = Modifier.width(dimensionResource(R.dimen.small_padding)))

				Text(
					text = stringResource(R.string.stats_sorting_by_text),
					color = MaterialTheme.colorScheme.onSecondary,
					style = MaterialTheme.typography.labelLarge.copy(
						fontWeight = FontWeight.W400
					)
				)

				Spacer(Modifier.width(dimensionResource(R.dimen.x_small_padding)))

				Text(
					text = statsViewModel.selectedOrderRule,
					color = MaterialTheme.colorScheme.onSecondary,
					style = MaterialTheme.typography.labelLarge
				)
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
						if (statsViewModel.selectedStat.isEmpty()) {
							NoMusicStatsMsg()
						} else {
							Column(
								modifier = Modifier.padding(dimensionResource(R.dimen.small_padding))
							) {
								// --==--  Labels  --==--
								Row(
									modifier = Modifier.fillMaxWidth(),
									horizontalArrangement = Arrangement.SpaceBetween
								) {
									// --===--  Music Details  --===--
									Text(
										text = "Music Details",
										color = MaterialTheme.colorScheme.onSecondary,
										style = MaterialTheme.typography.bodyLarge.copy(
											fontSize = 10.sp,
											fontWeight = FontWeight.W500
										),
										textAlign = TextAlign.Center
									)

									Row {
										// --===--  Music Play Count  --===--
										Text(
											text = "Play Count",
											modifier = Modifier
												.width(dimensionResource(R.dimen.stats_play_count_width)),
											color = MaterialTheme.colorScheme.onSecondary,
											style = MaterialTheme.typography.bodyLarge.copy(
												fontSize = 10.sp,
												fontWeight = FontWeight.W500
											),
											textAlign = TextAlign.Center
										)

										Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))

										// --===--  Music Listen Time  --===--
										Text(
											text = "Listen Time",
											modifier = Modifier
												.width(dimensionResource(R.dimen.stats_listen_time_width)),
											color = MaterialTheme.colorScheme.onSecondary,
											style = MaterialTheme.typography.bodyLarge.copy(
												fontSize = 10.sp,
												fontWeight = FontWeight.W500
											),
											textAlign = TextAlign.Center
										)
									}
								}

								Spacer(Modifier.height(dimensionResource(R.dimen.small_padding)))

								LazyColumn(
									state = lazyListState,
									modifier = Modifier
										.fillMaxSize()
										.verticalScrollbar(lazyListState)
								) {
									items(
										count = statsViewModel.selectedStat.size,
										key = { statsViewModel.selectedStat[it].id }
									) { index ->
										StatsListItem(statsViewModel.selectedStat[index])
									}
								}
							}
						}
					}
				}
			}
		}
	}
}