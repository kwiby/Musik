package com.example.musik.ui.screens.settings

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.example.musik.ui.view_models.NavViewModel
import com.example.musik.ui.view_models.Screen

@Composable
fun SettingsScreen(
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
			Spacer(Modifier.height(dimensionResource(R.dimen.settings_screen_back_button_top_padding)))

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
		}
	}
}