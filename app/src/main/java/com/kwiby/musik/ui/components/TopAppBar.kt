package com.kwiby.musik.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kwiby.musik.BuildConfig
import com.kwiby.musik.R
import com.kwiby.musik.ui.misc.formatDuration
import com.kwiby.musik.ui.view_models.NavViewModel
import com.kwiby.musik.ui.view_models.Screen
import com.kwiby.musik.ui.view_models.SleepTimerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusikTopAppBar(
	navViewModel: NavViewModel?,
	sleepTimerViewModel: SleepTimerViewModel?,
	onSleepTimerClick: (() -> Unit)?
) {
	val isNotCrashScreen = navViewModel != null
			&& sleepTimerViewModel != null
			&& onSleepTimerClick != null
	val isSleepTimerSet = sleepTimerViewModel?.isSleepTimerSet?.collectAsStateWithLifecycle()?.value
		?: false
	val remainingMs = (sleepTimerViewModel?.remainingMs?.collectAsStateWithLifecycle()?.value
		?: 0L).formatDuration()

	TopAppBar(
		title = {
			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxWidth()
			) {
				Row(
					verticalAlignment = Alignment.Bottom,
				) {
					Text(
						text = stringResource(R.string.app_name),
						style = MaterialTheme.typography.titleLarge,
						color = MaterialTheme.colorScheme.onPrimary,
						modifier = Modifier.alignByBaseline()
					)

					Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.xx_small_padding)))

					Text(
						text = BuildConfig.VERSION_NAME,
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.surface,
						modifier = Modifier.alignByBaseline()
					)
				}

				if (isNotCrashScreen) {
					Row {
						// --===-- Sleep Timer --===--
						if (isSleepTimerSet) {
							Surface(
								shape = MaterialTheme.shapes.extraLarge,
								color = MaterialTheme.colorScheme.secondary,
								shadowElevation = dimensionResource(R.dimen.x_small_padding)
							) {
								Row(
									verticalAlignment = Alignment.CenterVertically
								) {
									Spacer(Modifier.width(dimensionResource(R.dimen.sleep_timer_left_padding)))

									Text(
										text = remainingMs,
										modifier = Modifier.width(dimensionResource(R.dimen.sleep_timer_width)),
										color = MaterialTheme.colorScheme.onSecondary,
										style = MaterialTheme.typography.bodyLarge
									)

									CustomIconButton(
										iconImageVector = Icons.Rounded.Close,
										contentDescription = stringResource(R.string.sleep_timer_button),
										size = dimensionResource(R.dimen.medium_padding),
										colour = MaterialTheme.colorScheme.onSecondary
									) {
										sleepTimerViewModel.stopSleepTimer()
									}
								}
							}
						} else {
							CustomIconButton(
								iconImageVector = Icons.Rounded.Timer,
								contentDescription = stringResource(R.string.sleep_timer_button),
								colour = MaterialTheme.colorScheme.onSurfaceVariant
							) {
								onSleepTimerClick()
							}
						}

						// --===-- Settings Button --===--
						CustomIconButton(
							iconImageVector =  Icons.Rounded.Settings,
							contentDescription =  stringResource(R.string.settings_button),
							colour =  MaterialTheme.colorScheme.onSurfaceVariant
						) {
							navViewModel.navToScreen(Screen.Settings)
						}
					}
				}
			}
		},
		colors = TopAppBarDefaults.topAppBarColors(
			containerColor = MaterialTheme.colorScheme.background
		),
		windowInsets = WindowInsets(
			top = 150,
			bottom = 50,
			left = 50,
			right = 50
		),
	)
}