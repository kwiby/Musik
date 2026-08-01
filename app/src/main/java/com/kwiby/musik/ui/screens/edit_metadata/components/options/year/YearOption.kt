package com.kwiby.musik.ui.screens.edit_metadata.components.options.year

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.kwiby.musik.R
import com.kwiby.musik.ui.screens.components.OptionHeader
import com.kwiby.musik.ui.screens.edit_metadata.components.MetadataField
import com.kwiby.musik.ui.view_models.EditMetadataViewModel

@Composable
fun YearOption(
	editMetadataViewModel: EditMetadataViewModel
) {
	OptionHeader(stringResource(R.string.edit_metadata_year))
	Spacer(Modifier.height(dimensionResource(R.dimen.option_header_bottom_padding)))

	MetadataField(
		query = editMetadataViewModel.yearQuery.value,
		onValueChange = { editMetadataViewModel.yearQuery.value = it },
		placeholderText = stringResource(R.string.edit_metadata_placeholder_year)
	)

	Spacer(Modifier.height(dimensionResource(R.dimen.screen_option_section_vertical_padding)))
}