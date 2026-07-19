package com.example.musik.ui.screens.settings.components.options

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.musik.R
import com.example.musik.ui.screens.settings.components.options.entry_tab.EntryTabOption
import com.example.musik.ui.view_models.NavViewModel

@Composable
fun SettingsOptions(
	navViewModel: NavViewModel
) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(horizontal = dimensionResource(R.dimen.settings_options_horizontal_padding)),
		horizontalAlignment = Alignment.Start
	) {
		// --===--  Change Entry Tab  --===--
		EntryTabOption(navViewModel)

		// --===--  Change Theme  --===--
		OptionHeader(stringResource(R.string.settings_header_theme))

		// --===--  Change App Icon  --===--
		OptionHeader(stringResource(R.string.settings_header_app_icon))

		// --===--  Update Yt-Dlp  --===--
		OptionHeader(stringResource(R.string.settings_header_update_ytdlp))
	}
}