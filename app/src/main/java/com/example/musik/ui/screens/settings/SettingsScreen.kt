package com.example.musik.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.musik.R
import com.example.musik.ui.components.CustomIconButton
import com.example.musik.ui.components.LoadingIndicator
import com.example.musik.ui.screens.settings.components.options.app_icon.AppIconOption
import com.example.musik.ui.screens.settings.components.options.downloading.DownloadingOption
import com.example.musik.ui.screens.settings.components.options.entry_tab.EntryTabOption
import com.example.musik.ui.screens.settings.components.options.theme.ThemeOption
import com.example.musik.ui.screens.settings.components.options.update_musik.UpdateMusikOption
import com.example.musik.ui.screens.settings.components.options.update_ytdlp.UpdateYtDlpOption
import com.example.musik.ui.view_models.NavViewModel
import com.example.musik.ui.view_models.Screen
import com.example.musik.ui.view_models.SettingsViewModel

@Composable
fun SettingsScreen(
	settingsViewModel: SettingsViewModel,
	navViewModel: NavViewModel
) {
	val dataStoreYtDlpVersion by settingsViewModel.dataStoreYtDlpVersion.collectAsStateWithLifecycle()
	val dataStoreDoConvertMp3 by settingsViewModel.dataStoreDoConvertMp3.collectAsStateWithLifecycle()

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
		navViewModel.navToScreen(Screen.MAIN)
	}

	Surface(
		color = surfaceColour,
		modifier = Modifier.fillMaxSize()
	) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Spacer(Modifier.height(dimensionResource(R.dimen.settings_back_button_top_padding)))

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
					navViewModel.navToScreen(Screen.MAIN)
				}

				Spacer(Modifier.width(dimensionResource(R.dimen.settings_back_button_right_padding)))

				// --===--  Settings Screen Title  --===--
				Text(
					text = stringResource(R.string.settings_title),
					color = MaterialTheme.colorScheme.onSecondary,
					style = MaterialTheme.typography.headlineLarge
				)
			}

			Spacer(Modifier.height(dimensionResource(R.dimen.settings_top_section_bottom_padding)))

			// --===--  Settings Options  --===--
			Column(
				modifier = Modifier
					.fillMaxSize()
					.background(MaterialTheme.colorScheme.background)
					.padding(horizontal = dimensionResource(R.dimen.settings_options_horizontal_padding))
					.verticalScroll(scrollState),
				horizontalAlignment = Alignment.Start
			) {
				Spacer(Modifier.height(dimensionResource(R.dimen.settings_options_top_padding)))

				if (dataStoreYtDlpVersion == null || dataStoreDoConvertMp3 == null) {
					LoadingIndicator()
				} else {
					EntryTabOption(navViewModel)
					ThemeOption()
					AppIconOption()
					DownloadingOption(settingsViewModel)
					UpdateYtDlpOption(settingsViewModel)
					UpdateMusikOption()
				}

				Spacer(Modifier.height(dimensionResource(R.dimen.settings_options_bottom_padding)))
			}
		}
	}
}