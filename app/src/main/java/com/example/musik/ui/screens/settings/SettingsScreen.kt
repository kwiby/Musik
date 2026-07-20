package com.example.musik.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.musik.R
import com.example.musik.ui.components.CustomIconButton
import com.example.musik.ui.screens.settings.components.options.app_icon.AppIconOption
import com.example.musik.ui.screens.settings.components.options.entry_tab.EntryTabOption
import com.example.musik.ui.screens.settings.components.options.theme.ThemeOption
import com.example.musik.ui.screens.settings.components.options.update_ytdlp.UpdateYtDlpOption
import com.example.musik.ui.view_models.NavViewModel
import com.example.musik.ui.view_models.Screen
import com.example.musik.ui.view_models.SettingsViewModel

@Composable
fun SettingsScreen(
	settingsViewModel: SettingsViewModel,
	navViewModel: NavViewModel
) {
	BackHandler(true) {
		navViewModel.navToScreen(Screen.MAIN)
	}

	Surface(
		color = MaterialTheme.colorScheme.background,
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
					.padding(horizontal = dimensionResource(R.dimen.settings_options_horizontal_padding)),
				horizontalAlignment = Alignment.Start
			) {
				// --===--  Change Entry Tab  --===--
				EntryTabOption(navViewModel)
				Spacer(Modifier.height(dimensionResource(R.dimen.settings_option_section_vertical_padding)))

				// --===--  Change Theme  --===--
				ThemeOption()
				Spacer(Modifier.height(dimensionResource(R.dimen.settings_option_section_vertical_padding)))

				// --===--  Change App Icon  --===--
				AppIconOption()
				Spacer(Modifier.height(dimensionResource(R.dimen.settings_option_section_vertical_padding)))

				// --===--  Update Yt-Dlp  --===--
				UpdateYtDlpOption(settingsViewModel)
				Spacer(Modifier.height(dimensionResource(R.dimen.settings_option_section_vertical_padding)))
			}
		}
	}
}