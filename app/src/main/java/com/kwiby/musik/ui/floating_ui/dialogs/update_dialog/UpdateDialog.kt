package com.kwiby.musik.ui.floating_ui.dialogs.update_dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.window.DialogWindowProvider
import com.kwiby.musik.BuildConfig
import com.kwiby.musik.R
import com.kwiby.musik.data.data_classes.updating.UpdateInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateDialog(
	updateInfo: UpdateInfo,
	downloadProgress: Float?,
	onUpdate: () -> Unit,
	onDismiss: () -> Unit
) {
	BasicAlertDialog(onDismissRequest = onDismiss) {
		val view = LocalView.current
		SideEffect {
			(view.parent as? DialogWindowProvider)?.window?.setDimAmount(0.4f)
		}

		Surface(
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
					text = stringResource(R.string.update_available),
					color = MaterialTheme.colorScheme.onSecondary,
					style = MaterialTheme.typography.bodyLarge
				)

				Spacer(Modifier.height(dimensionResource(R.dimen.medium_padding)))

				// --===-- Current Version --===--
				Row {
					Text(
						text = stringResource(R.string.settings_update_option_cur_version) + " ",
						color = MaterialTheme.colorScheme.onSecondary,
						style = MaterialTheme.typography.labelLarge.copy(
							fontWeight = FontWeight.W400
						)
					)
					Text(
						text = BuildConfig.VERSION_NAME,
						color = MaterialTheme.colorScheme.onSecondary,
						style = MaterialTheme.typography.labelLarge
					)
				}

				Spacer(Modifier.height(dimensionResource(R.dimen.x_small_padding)))

				// --===-- Latest Version --===--
				Row {
					Text(
						text = stringResource(R.string.settings_update_option_latest_version) + " ",
						color = MaterialTheme.colorScheme.onSecondary,
						style = MaterialTheme.typography.labelLarge.copy(
							fontWeight = FontWeight.W400
						)
					)
					Text(
						text = updateInfo.versionName,
						color = MaterialTheme.colorScheme.onSecondary,
						style = MaterialTheme.typography.labelLarge
					)
				}

				Spacer(Modifier.height(dimensionResource(R.dimen.small_padding)))

				// --===-- Changelog --===--
				Surface(
					modifier = Modifier.fillMaxWidth(),
					shape = MaterialTheme.shapes.small,
					color = MaterialTheme.colorScheme.background
				) {
					Text(
						text = updateInfo.changelog,
						modifier = Modifier.padding(
							vertical = dimensionResource(R.dimen.x_small_padding),
							horizontal = dimensionResource(R.dimen.small_padding)
						),
						color = MaterialTheme.colorScheme.onSecondary,
						style = MaterialTheme.typography.labelLarge
					)
				}

				// --===-- Progress Indicator --===--
				if (downloadProgress != null) {
					Spacer(Modifier.height(dimensionResource(R.dimen.medium_padding)))

					Column {
						Row(
							modifier = Modifier.fillMaxWidth(),
							horizontalArrangement = Arrangement.SpaceBetween
						) {
							// --===-- Downloading Text --===--
							Text(
								text = stringResource(R.string.update_downloading),
								color = MaterialTheme.colorScheme.onSecondary,
								style = MaterialTheme.typography.labelLarge.copy(
									fontWeight = FontWeight.W400
								)
							)

							// --===-- Percent Text --===--
							Text(
								text = "${"%.1f".format(downloadProgress * 100f)}%",
								color = MaterialTheme.colorScheme.onSecondary,
								style = MaterialTheme.typography.labelLarge.copy(
									fontWeight = FontWeight.W400
								)
							)
						}

						Spacer(Modifier.height(dimensionResource(R.dimen.small_padding)))

						LinearProgressIndicator(
							progress = { downloadProgress },
							modifier = Modifier
								.fillMaxWidth()
								.height(dimensionResource(R.dimen.small_padding)),
							color = MaterialTheme.colorScheme.outline,
							trackColor = MaterialTheme.colorScheme.onSurface,
							gapSize = dimensionResource(R.dimen.xx_small_padding),
							drawStopIndicator = {}
						)
					}

					Spacer(Modifier.height(dimensionResource(R.dimen.medium_padding)))
				} else {
					Spacer(Modifier.height(dimensionResource(R.dimen.xx_small_padding)))

					// --===-- Buttons --===--
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
								text = stringResource(R.string.update_dialog_later),
								color = MaterialTheme.colorScheme.outline,
								style = MaterialTheme.typography.bodyLarge
							)
						}

						TextButton(
							onClick = {
								onUpdate()
								onDismiss()
							},
							shape = MaterialTheme.shapes.large
						) {
							Text(
								text = stringResource(R.string.update_dialog_update),
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