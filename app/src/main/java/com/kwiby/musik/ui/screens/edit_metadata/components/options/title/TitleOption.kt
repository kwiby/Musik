package com.kwiby.musik.ui.screens.edit_metadata.components.options.title

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.kwiby.musik.R
import com.kwiby.musik.ui.screens.components.OptionHeader

@Composable
fun TitleOption() {
	OptionHeader(stringResource(R.string.edit_metadata_title))
	Spacer(Modifier.height(dimensionResource(R.dimen.option_header_bottom_padding)))

	Spacer(Modifier.height(dimensionResource(R.dimen.screen_option_section_vertical_padding)))
}