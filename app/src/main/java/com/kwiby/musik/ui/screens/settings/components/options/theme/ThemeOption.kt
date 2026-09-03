package com.kwiby.musik.ui.screens.settings.components.options.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kwiby.musik.R
import com.kwiby.musik.ui.screens.components.OptionHeader
import com.kwiby.musik.ui.screens.components.OptionSwitch
import com.kwiby.musik.ui.screens.settings.components.options.theme.components.ThemeButton
import com.kwiby.musik.ui.theme.ThemeStyle
import com.kwiby.musik.ui.view_models.SettingsViewModel

@Composable
fun ThemeOption(
	settingsViewModel: SettingsViewModel
) {
	val scrollState = rememberScrollState()
	val dataStoreThemeMode by settingsViewModel.dataStoreThemeMode.collectAsStateWithLifecycle()

	OptionHeader(stringResource(R.string.settings_header_theme))
	Spacer(Modifier.height(dimensionResource(R.dimen.option_header_bottom_padding)))

	Column {
		// --===--  Theme Mode  --===--
		Row(
			modifier = Modifier.clickable(
				interactionSource = null,
				indication = null
			) {
				settingsViewModel.switchThemeMode()
			},
			verticalAlignment = Alignment.CenterVertically
		) {
			Column(
				modifier = Modifier.weight(1f)
			) {
				Text(
					text = stringResource(R.string.settings_theme_option_mode_title),
					color = MaterialTheme.colorScheme.onSecondary,
					style = MaterialTheme.typography.labelLarge
				)

				Text(
					text = stringResource(R.string.settings_theme_option_mode_title_desc),
					color = MaterialTheme.colorScheme.onSurfaceVariant,
					style = MaterialTheme.typography.labelMedium
				)
			}

			Spacer(Modifier.width(dimensionResource(R.dimen.medium_padding)))

			OptionSwitch(dataStoreThemeMode == "DARK")
		}

		Spacer(Modifier.height(dimensionResource(R.dimen.settings_theme_option_vertical_padding)))

		// --===--  Theme Style Buttons  --===--
		Row(
			modifier = Modifier
				.horizontalScroll(scrollState)
		) {
			ThemeButton(settingsViewModel, ThemeStyle.NIGHT)
			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))
			ThemeButton(settingsViewModel, ThemeStyle.ICE)
			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))
			ThemeButton(settingsViewModel, ThemeStyle.LIME)
			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))
			ThemeButton(settingsViewModel, ThemeStyle.SUNSET)
			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))
			ThemeButton(settingsViewModel, ThemeStyle.LEMON)
			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))
			ThemeButton(settingsViewModel, ThemeStyle.FLAMINGO)
			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))
			ThemeButton(settingsViewModel, ThemeStyle.GRAPE)
			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))
			ThemeButton(settingsViewModel, ThemeStyle.APPLE)
		}
	}

	Spacer(Modifier.height(dimensionResource(R.dimen.screen_option_section_vertical_padding)))
}