package com.example.musik.ui.screens.settings.components.options.downloading

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musik.R
import com.example.musik.ui.screens.settings.components.options.OptionHeader
import com.example.musik.ui.view_models.SettingsViewModel

@Composable
fun DownloadingOption(
	settingsViewModel: SettingsViewModel
) {
	val doConvertMp3 by settingsViewModel.dataStoreDoConvertMp3.collectAsStateWithLifecycle()

	OptionHeader(stringResource(R.string.settings_header_downloading))
	Spacer(Modifier.height(dimensionResource(R.dimen.option_header_bottom_padding)))

	Row {
		Column(
			modifier = Modifier.weight(1f)
		) {
			Text(
				text = stringResource(R.string.settings_downloading_option_title),
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.labelLarge
			)

			Text(
				text = stringResource(R.string.settings_downloading_option_description),
				color = MaterialTheme.colorScheme.onSurfaceVariant,
				style = MaterialTheme.typography.labelMedium
			)
		}

		Spacer(Modifier.width(dimensionResource(R.dimen.medium_padding)))

		CompositionLocalProvider(LocalRippleConfiguration provides null) {
			Switch(
				checked = doConvertMp3 ?: false,
				onCheckedChange = { settingsViewModel.toggleDoConvertMp3() },
				colors = SwitchDefaults.colors(
					uncheckedThumbColor = MaterialTheme.colorScheme.outline,
					uncheckedBorderColor = MaterialTheme.colorScheme.outline,
					uncheckedTrackColor = MaterialTheme.colorScheme.onSurface,
					checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
					checkedBorderColor = MaterialTheme.colorScheme.outline,
					checkedTrackColor = MaterialTheme.colorScheme.outline
				)
			)
		}
	}

	Spacer(Modifier.height(dimensionResource(R.dimen.settings_option_section_vertical_padding)))
}