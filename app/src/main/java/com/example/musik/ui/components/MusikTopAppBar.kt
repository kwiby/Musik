package com.example.musik.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.musik.BuildConfig
import com.example.musik.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MusikTopAppBar() {
	TopAppBar(
		title = {
			Row(
				horizontalArrangement = Arrangement.SpaceBetween,
				modifier = Modifier.fillMaxWidth()
			) {
				Row(
					verticalAlignment = Alignment.Bottom,
				) {
					Text(
						text = stringResource(R.string.app_name),
						style = MaterialTheme.typography.titleLarge,
						color = MaterialTheme.colorScheme.onPrimary,
						modifier = Modifier.alignByBaseline()
					)

					Spacer(modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.xx_small_padding)))

					Text(
						text = "${BuildConfig.VERSION_NAME}.${BuildConfig.VERSION_CODE}",
						style = MaterialTheme.typography.bodySmall,
						color = MaterialTheme.colorScheme.surface,
						modifier = Modifier.alignByBaseline()
					)
				}

				CustomIconButton(
					Icons.Rounded.Settings,
					stringResource(R.string.settings_button),
					MaterialTheme.colorScheme.onSurfaceVariant
				) {

				}


			}
		},
		colors = TopAppBarDefaults.topAppBarColors(
			containerColor = MaterialTheme.colorScheme.background
		),
		windowInsets = WindowInsets(top = 150, bottom = 50, left = 50, right = 50),
	)
}