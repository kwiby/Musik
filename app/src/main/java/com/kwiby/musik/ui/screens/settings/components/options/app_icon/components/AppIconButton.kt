package com.kwiby.musik.ui.screens.settings.components.options.app_icon.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import com.kwiby.musik.R
import com.kwiby.musik.ui.view_models.SettingsViewModel
import kotlinx.coroutines.launch

@Composable
fun AppIconButton(
	settingsViewModel: SettingsViewModel,
	alias: String
) {
	val dataStoreAppIcon by settingsViewModel.dataStoreAppIcon.collectAsStateWithLifecycle()
	val context = LocalContext.current

	val appIconRes = when (alias) {
		"Default" -> R.drawable.musik_pixel_icon
		"Black" -> R.drawable.musik_pixel_icon_black
		"Blue" -> R.drawable.musik_pixel_icon_blue
		"Green" -> R.drawable.musik_pixel_icon_green
		"Orange" -> R.drawable.musik_pixel_icon_orange
		"Pink" -> R.drawable.musik_pixel_icon_pink
		"Purple" -> R.drawable.musik_pixel_icon_purple
		"Red" -> R.drawable.musik_pixel_icon_red
		"White" -> R.drawable.musik_pixel_icon_white
		else -> R.drawable.musik_pixel_icon
	}

	CompositionLocalProvider(LocalRippleConfiguration provides null) {
		Column(
			modifier = Modifier
				.clip(MaterialTheme.shapes.small)
				.clickable {
					settingsViewModel.viewModelScope.launch {
						settingsViewModel.switchAppIcon(context, alias)
					}
				},
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Image(
				painter = painterResource(id = appIconRes),
				contentDescription = alias,
				modifier = Modifier
					.size(dimensionResource(R.dimen.radiobutton_image_size))
					.clip(MaterialTheme.shapes.medium)
			)

			Spacer(Modifier.height(dimensionResource(R.dimen.small_padding)))

			RadioButton(
				selected = dataStoreAppIcon == alias,
				onClick = null,
				colors = RadioButtonDefaults.colors(
					unselectedColor = MaterialTheme.colorScheme.outline,
					selectedColor = MaterialTheme.colorScheme.outline
				)
			)
		}
	}
}