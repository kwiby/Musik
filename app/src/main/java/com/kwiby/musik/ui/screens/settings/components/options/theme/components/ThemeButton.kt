package com.kwiby.musik.ui.screens.settings.components.options.theme.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.dimensionResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kwiby.musik.R
import com.kwiby.musik.ui.theme.AppTheme
import com.kwiby.musik.ui.theme.LocalAppTheme
import com.kwiby.musik.ui.theme.ThemeMode
import com.kwiby.musik.ui.theme.ThemeStyle
import com.kwiby.musik.ui.theme.colorScheme
import com.kwiby.musik.ui.view_models.SettingsViewModel

@Composable
fun ThemeButton(
	settingsViewModel: SettingsViewModel,
	themeStyle: ThemeStyle
) {
	val currentTheme = LocalAppTheme.current

	val dataStoreThemeMode by settingsViewModel.dataStoreThemeMode.collectAsStateWithLifecycle()
	val appTheme = AppTheme(
		mode = if (dataStoreThemeMode == "DARK") {
			ThemeMode.DARK
		} else {
			ThemeMode.LIGHT
		},
		style = themeStyle
	)

	CompositionLocalProvider(LocalRippleConfiguration provides null) {
		Column(
			modifier = Modifier
				.clip(MaterialTheme.shapes.small)
				.clickable {
					settingsViewModel.switchThemeStyle(themeStyle)
				},
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Box(
				modifier = Modifier
					.size(dimensionResource(R.dimen.radiobutton_image_size))
					.border(
						width = dimensionResource(R.dimen.settings_theme_button_border_width),
						color = MaterialTheme.colorScheme.outline,
						shape = MaterialTheme.shapes.medium
					)
					.clip(MaterialTheme.shapes.medium)
					.drawBehind {
						drawRect(appTheme.colorScheme().secondary)

						val path = Path().apply {
							moveTo(0f, 0f)
							lineTo(size.width, 0f)
							lineTo(0f, size.height)
							close()
						}
						drawPath(path, appTheme.colorScheme().outline)
					}
			)

			Spacer(Modifier.height(dimensionResource(R.dimen.small_padding)))

			RadioButton(
				selected = currentTheme.style == appTheme.style,
				onClick = null,
				colors = RadioButtonDefaults.colors(
					unselectedColor = MaterialTheme.colorScheme.outline,
					selectedColor = MaterialTheme.colorScheme.outline
				)
			)
		}
	}
}