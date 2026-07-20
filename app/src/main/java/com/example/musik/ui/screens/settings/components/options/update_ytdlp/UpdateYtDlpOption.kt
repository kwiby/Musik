package com.example.musik.ui.screens.settings.components.options.update_ytdlp

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musik.R
import com.example.musik.ui.screens.settings.components.options.OptionButton
import com.example.musik.ui.screens.settings.components.options.OptionHeader
import com.example.musik.ui.view_models.SettingsViewModel
import com.yausername.youtubedl_android.YoutubeDL

@Composable
fun UpdateYtDlpOption(
	settingsViewModel: SettingsViewModel
) {
	val dataStoreYtDlpVersion by settingsViewModel.getDataStoreManagerYtDlpVersion().collectAsStateWithLifecycle(initialValue = "")
	val ytDlpVersion by settingsViewModel.getYtDlpVersionStateFlow().collectAsStateWithLifecycle()

	OptionHeader(stringResource(R.string.settings_header_update_ytdlp))
	Spacer(Modifier.height(dimensionResource(R.dimen.option_header_bottom_padding)))

	Column{
		// --===--  Current Version  --===--
		Row {
			Text(
				text = stringResource(R.string.settings_update_ytdlp_option_cur_version),
				style = MaterialTheme.typography.labelLarge.copy(
					color = MaterialTheme.colorScheme.onSecondary,
					fontWeight = FontWeight.W400
				)
			)

			Spacer(Modifier.width(dimensionResource(R.dimen.small_padding)))

			Text(
				text = if (ytDlpVersion == "UNKNOWN") {
					dataStoreYtDlpVersion
				} else {
					ytDlpVersion
				},
				style = MaterialTheme.typography.labelLarge.copy(
					color = MaterialTheme.colorScheme.onSecondary
				)
			)
		}

		Spacer(Modifier.height(dimensionResource(R.dimen.settings_update_ytdlp_option_buttons_top_padding)))

		// --===--  Channels Description  --===--
		Text(
			text = stringResource(R.string.settings_update_ytdlp_option_description),
			style = MaterialTheme.typography.labelMedium.copy(
				color = MaterialTheme.colorScheme.onSurfaceVariant
			)
		)

		Spacer(Modifier.height(dimensionResource(R.dimen.settings_update_ytdlp_option_description_bottom_padding)))

		// --===--  Buttons  --===--
		Row {
			// --===--  Stable Channel  --===--
			OptionButton(
				text = stringResource(R.string.settings_update_ytdlp_option_stable),
				modifier = Modifier.weight(1f),
				enableRippleAnimation = true
			) {
				settingsViewModel.updateYtDlp(YoutubeDL.UpdateChannel.STABLE)
			}

			// --===--  Nightly Channel  --===--
			OptionButton(
				text = stringResource(R.string.settings_update_ytdlp_option_nightly),
				modifier = Modifier.weight(1f),
				enableRippleAnimation = true
			) {
				settingsViewModel.updateYtDlp(YoutubeDL.UpdateChannel.NIGHTLY)
			}

			// --===--  Master Channel  --===--
			OptionButton(
				text = stringResource(R.string.settings_update_ytdlp_option_master),
				modifier = Modifier.weight(1f),
				enableRippleAnimation = true
			) {
				settingsViewModel.updateYtDlp(YoutubeDL.UpdateChannel.MASTER)
			}
		}
	}
}