package com.example.musik.ui.screens.settings.components.options.update_musik

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.musik.R
import com.example.musik.ui.screens.settings.components.options.OptionHeader

@Composable
fun UpdateMusikOption() {
	OptionHeader(stringResource(R.string.settings_header_update_musik))
	Spacer(Modifier.height(dimensionResource(R.dimen.option_header_bottom_padding)))

	Spacer(Modifier.height(dimensionResource(R.dimen.settings_option_section_vertical_padding)))
}