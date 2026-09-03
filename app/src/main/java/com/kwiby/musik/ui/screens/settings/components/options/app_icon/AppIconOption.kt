package com.kwiby.musik.ui.screens.settings.components.options.app_icon

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.kwiby.musik.R
import com.kwiby.musik.ui.screens.components.OptionHeader
import com.kwiby.musik.ui.screens.settings.components.options.app_icon.components.AppIconButton
import com.kwiby.musik.ui.view_models.SettingsViewModel

@Composable
fun AppIconOption(
	settingsViewModel: SettingsViewModel
) {
	val scrollState = rememberScrollState()


	OptionHeader(stringResource(R.string.settings_header_app_icon))
	Spacer(Modifier.height(dimensionResource(R.dimen.option_header_bottom_padding)))

	Column {
		// --===--  Restart Description  --===--
		Text(
			text = stringResource(R.string.settings_app_icon_option_restart_desc),
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			style = MaterialTheme.typography.labelMedium
		)

		Spacer(Modifier.height(dimensionResource(R.dimen.settings_app_icon_option_vertical_padding)))

		// --===--  App Icon Buttons  --===--
		Row(
			modifier = Modifier
				.horizontalScroll(scrollState)
		) {
			AppIconButton(settingsViewModel, "Default")
			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))
			AppIconButton(settingsViewModel, "Blue")
			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))
			AppIconButton(settingsViewModel, "Green")
			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))
			AppIconButton(settingsViewModel, "Orange")
			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))
			AppIconButton(settingsViewModel, "Yellow")
			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))
			AppIconButton(settingsViewModel, "Pink")
			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))
			AppIconButton(settingsViewModel, "Purple")
			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))
			AppIconButton(settingsViewModel, "Red")
			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))
			AppIconButton(settingsViewModel, "Black")
			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))
			AppIconButton(settingsViewModel, "White")
		}
	}

	Spacer(Modifier.height(dimensionResource(R.dimen.screen_option_section_vertical_padding)))
}