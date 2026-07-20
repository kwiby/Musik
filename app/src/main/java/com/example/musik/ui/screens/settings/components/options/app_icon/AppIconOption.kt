package com.example.musik.ui.screens.settings.components.options.app_icon

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.musik.R
import com.example.musik.ui.screens.settings.components.options.OptionHeader

@Composable
fun AppIconOption() {
	OptionHeader(stringResource(R.string.settings_header_app_icon))
	Spacer(Modifier.height(dimensionResource(R.dimen.option_header_bottom_padding)))
}