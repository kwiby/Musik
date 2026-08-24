package com.kwiby.musik.ui.screens.settings.components.options.update_musik

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.kwiby.musik.BuildConfig
import com.kwiby.musik.R
import com.kwiby.musik.ui.screens.components.OptionButton
import com.kwiby.musik.ui.screens.components.OptionHeader
import com.kwiby.musik.ui.view_models.SettingsViewModel
import com.kwiby.musik.ui.view_models.UpdateViewModel

@Composable
fun UpdateMusikOption(
	settingsViewModel: SettingsViewModel,
	updateViewModel: UpdateViewModel
) {
	val context = LocalContext.current

	OptionHeader(stringResource(R.string.settings_header_update_musik))
	Spacer(Modifier.height(dimensionResource(R.dimen.option_header_bottom_padding)))

	Column {
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

		Spacer(Modifier.height(dimensionResource(R.dimen.medium_padding)))

		// --===-- Buttons --===--
		Row {
			OptionButton(
				stringResource(R.string.update_app_option_check),
				modifier = Modifier.weight(1f),
				startPadding = dimensionResource(R.dimen.zero),
				enableRippleAnimation = true
			) {
				updateViewModel.checkForUpdates(isManual = true)
			}

			OptionButton(
				stringResource(R.string.update_app_option_github),
				modifier = Modifier.weight(1f),
				endPadding = dimensionResource(R.dimen.zero),
				enableRippleAnimation = true
			) {
				settingsViewModel.openGitHubPage(context)
			}
		}
	}

	Spacer(Modifier.height(dimensionResource(R.dimen.screen_option_section_vertical_padding)))
}