package com.kwiby.musik.ui.floating_ui.dialogs.sleep_timer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogWindowProvider
import com.kwiby.musik.R
import com.kwiby.musik.ui.view_models.SleepTimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTimerDialog(
	sleepTimerViewModel: SleepTimerViewModel,
	onDismiss: () -> Unit
) {
	val radioOptions = listOf(
		stringResource(R.string.time_15_mins),
		stringResource(R.string.time_30_mins),
		stringResource(R.string.time_1_hour),
		stringResource(R.string.time_2_hours),
		stringResource(R.string.time_4_hours),
		stringResource(R.string.time_8_hours)
	)
	var selectedOption by remember { mutableStateOf(radioOptions[0]) }


	BasicAlertDialog(onDismissRequest = onDismiss) {
		val view = LocalView.current
		SideEffect {
			(view.parent as? DialogWindowProvider)?.window?.setDimAmount(0.4f)
		}

		Box(
			modifier = Modifier
				.fillMaxWidth()
				.clickable(
					interactionSource = null,
					indication = null
				) { onDismiss() }
				.padding(horizontal = dimensionResource(R.dimen.sleep_timer_dialog_outer_horizontal_padding)),
			contentAlignment = Alignment.Center
		) {
			Surface(
				modifier = Modifier.clickable(
					interactionSource = null,
					indication = null
				) {},
				shape = MaterialTheme.shapes.medium,
				color = MaterialTheme.colorScheme.secondary,
				shadowElevation = dimensionResource(R.dimen.x_small_padding)
			) {
				Column(
					modifier = Modifier.padding(
						top = dimensionResource(R.dimen.medium_padding),
						start = dimensionResource(R.dimen.medium_padding),
						end = dimensionResource(R.dimen.medium_padding)
					)
				) {
					Text(
						text = stringResource(R.string.start_sleep_timer),
						color = MaterialTheme.colorScheme.onSecondary,
						style = MaterialTheme.typography.bodyLarge
					)

					Spacer(Modifier.height(dimensionResource(R.dimen.small_padding)))

					Column {
						radioOptions.forEach { text ->
							val isSelected = text == selectedOption
							val onClick = { selectedOption = text }

							Row(
								modifier = Modifier
									.fillMaxWidth()
									.height(dimensionResource(R.dimen.sleep_timer_radio_button_height))
									.selectable(
										selected = isSelected,
										interactionSource = null,
										indication = null,
										onClick = onClick
									),
								verticalAlignment = Alignment.CenterVertically
							) {
								Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))

								RadioButton(
									selected = isSelected,
									onClick = null,
									colors = RadioButtonDefaults.colors(
										unselectedColor = MaterialTheme.colorScheme.outline,
										selectedColor = MaterialTheme.colorScheme.outline
									)
								)

								Spacer(Modifier.width(dimensionResource(R.dimen.sleep_timer_component_gap)))

								Text(
									text = text,
									color = MaterialTheme.colorScheme.onSecondary,
									style = MaterialTheme.typography.bodyLarge
								)
							}
						}
					}

					Spacer(Modifier.height(dimensionResource(R.dimen.xx_small_padding)))

					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.End
					) {
						TextButton(
							onClick = {
								onDismiss()
							},
							shape = MaterialTheme.shapes.large
						) {
							Text(
								text = stringResource(R.string.playlist_dialog_cancel),
								color = MaterialTheme.colorScheme.outline,
								style = MaterialTheme.typography.bodyLarge
							)
						}

						TextButton(
							onClick = {
								when (selectedOption) {
									radioOptions[0] -> sleepTimerViewModel.setSleepTimer(900_000) // 15 minutes
									radioOptions[1] -> sleepTimerViewModel.setSleepTimer(1_800_000) // 30 minutes
									radioOptions[2] -> sleepTimerViewModel.setSleepTimer(3_600_000) // 1 hour
									radioOptions[3] -> sleepTimerViewModel.setSleepTimer(7_200_000) // 2 hours
									radioOptions[4] -> sleepTimerViewModel.setSleepTimer(14_400_000) // 4 hours
									radioOptions[5] -> sleepTimerViewModel.setSleepTimer(28_800_000) // 8 hours
								}

								onDismiss()
							},
							shape = MaterialTheme.shapes.large
						) {
							Text(
								text = stringResource(R.string.playlist_dialog_confirm),
								color = MaterialTheme.colorScheme.outline,
								style = MaterialTheme.typography.bodyLarge
							)
						}
					}
				}
			}
		}
	}
}