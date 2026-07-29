package com.kwiby.musik.ui.screens.settings.components.options

import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun OptionSwitch(
	checked: Boolean,
) {
	CompositionLocalProvider(LocalRippleConfiguration provides null) {
		Switch(
			checked = checked,
			onCheckedChange = null,
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