package com.kwiby.musik.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.kwiby.musik.R
import com.kwiby.musik.ui.components.CustomIconButton
import com.kwiby.musik.ui.components.LoadingIndicator
import com.kwiby.musik.ui.components.verticalScrollbar
import com.kwiby.musik.ui.screens.settings.components.options.app_icon.AppIconOption
import com.kwiby.musik.ui.screens.settings.components.options.downloading.DownloadingOption
import com.kwiby.musik.ui.screens.settings.components.options.entry_tab.EntryTabOption
import com.kwiby.musik.ui.screens.settings.components.options.theme.ThemeOption
import com.kwiby.musik.ui.screens.settings.components.options.update_musik.UpdateMusikOption
import com.kwiby.musik.ui.screens.settings.components.options.update_ytdlp.UpdateYtDlpOption
import com.kwiby.musik.ui.view_models.NavViewModel
import com.kwiby.musik.ui.view_models.Screen
import com.kwiby.musik.ui.view_models.SettingsViewModel

@Composable
fun SettingsScreen(
	settingsViewModel: SettingsViewModel,
	navViewModel: NavViewModel
) {
	val dataStoreYtDlpVersion by settingsViewModel.dataStoreYtDlpVersion.collectAsStateWithLifecycle()
	val dataStoreDoConvertMp3 by settingsViewModel.dataStoreDoConvertMp3.collectAsStateWithLifecycle()
	val dataStoreAppIcon by settingsViewModel.dataStoreAppIcon.collectAsStateWithLifecycle()
	val dataStoreThemeMode by settingsViewModel.dataStoreThemeMode.collectAsStateWithLifecycle()
	val dataStoreThemeStyle by settingsViewModel.dataStoreThemeStyle.collectAsStateWithLifecycle()

	val scrollState = rememberScrollState()
	val hasScrolled by remember {
		derivedStateOf { scrollState.value > 0 }
	}
	val surfaceColour by animateColorAsState(
		targetValue = if (hasScrolled) {
			MaterialTheme.colorScheme.secondary
		} else {
			MaterialTheme.colorScheme.background
		},
		animationSpec = tween(durationMillis = 250),
		label = "settings_surface_colour"
	)

	BackHandler(true) {
		navViewModel.navToScreen(Screen.Main)
	}

	Surface(
		color = surfaceColour,
		modifier = Modifier.fillMaxSize()
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Spacer(Modifier.height(dimensionResource(R.dimen.screen_back_button_top_padding)))

			Row(
				horizontalArrangement = Arrangement.Start,
				verticalAlignment = Alignment.CenterVertically,
				modifier = Modifier.fillMaxWidth()
			) {
				Spacer(Modifier.width(dimensionResource(R.dimen.medium_padding)))

				// --===--  Back Button  --===--
				CustomIconButton(
					iconImageVector = Icons.AutoMirrored.Rounded.ArrowBack,
					contentDescription = stringResource(R.string.back_button)
				) {
					navViewModel.navToScreen(Screen.Main)
				}

				Spacer(Modifier.width(dimensionResource(R.dimen.screen_back_button_right_padding)))

				// --===--  Settings Screen Title  --===--
				Text(
					text = stringResource(R.string.settings_title),
					color = MaterialTheme.colorScheme.onSecondary,
					style = MaterialTheme.typography.headlineLarge
				)
			}

			Spacer(Modifier.height(dimensionResource(R.dimen.screen_top_section_bottom_padding)))

			// --===--  Settings Options  --===--
			Box(
				modifier = Modifier
					.weight(1f)
					.fillMaxWidth()
					.background(MaterialTheme.colorScheme.background)
					.navigationBarsPadding()
					.verticalScrollbar(scrollState)
			) {
				Column(
					modifier = Modifier
						.fillMaxSize()
						.verticalScroll(scrollState)
						.padding(horizontal = dimensionResource(R.dimen.screen_options_horizontal_padding)),
					horizontalAlignment = Alignment.Start
				) {
					Spacer(Modifier.height(dimensionResource(R.dimen.screen_options_top_padding)))

					if (dataStoreYtDlpVersion == null
						|| dataStoreDoConvertMp3 == null
						|| dataStoreAppIcon == null
						|| dataStoreThemeMode == null
						|| dataStoreThemeStyle == null) {
						LoadingIndicator()
					} else {
						EntryTabOption(navViewModel)
						ThemeOption(settingsViewModel)
						AppIconOption(settingsViewModel)
						DownloadingOption(settingsViewModel)
						UpdateYtDlpOption(settingsViewModel)
						UpdateMusikOption()
					}
				}
			}
		}
	}
}