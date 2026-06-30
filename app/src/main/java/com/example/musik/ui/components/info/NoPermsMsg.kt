package com.example.musik.ui.components.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.musik.R
import com.example.musik.data.misc.openPermissionsSettings
import com.example.musik.ui.components.CustomIconButton

@Composable
fun NoPermsMsg() {
	Column(
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier.offset(y = dimensionResource(R.dimen.main_container_no_permissions_offset))
	) {
		Text(
			text = stringResource(R.string.no_permissions_msg),
			style = MaterialTheme.typography.titleSmall,
			color = MaterialTheme.colorScheme.onSecondary
		)

		val context = LocalContext.current
		CustomIconButton(
			iconImageVector = Icons.Rounded.Settings,
			contentDescription = stringResource(R.string.settings_button)
		) {
			openPermissionsSettings(context)
		}
	}
}