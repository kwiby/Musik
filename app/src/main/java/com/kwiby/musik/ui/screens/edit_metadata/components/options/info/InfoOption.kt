package com.kwiby.musik.ui.screens.edit_metadata.components.options.info

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.kwiby.musik.R
import com.kwiby.musik.ui.misc.formatDuration
import com.kwiby.musik.ui.screens.components.OptionHeader
import com.kwiby.musik.ui.view_models.EditMetadataViewModel
import java.math.RoundingMode

@Composable
fun InfoOption(
	editMetadataViewModel: EditMetadataViewModel
) {
	val metadata = editMetadataViewModel.metadata.value

	OptionHeader(stringResource(R.string.edit_metadata_info))
	Spacer(Modifier.height(dimensionResource(R.dimen.option_header_bottom_padding)))

	Column {
		// --===--  File Path  --===--
		Row {
			Text(
				text = stringResource(R.string.edit_metadata_file_path) + " ",
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.labelLarge.copy(
					fontWeight = FontWeight.W400
				)
			)

			Text(
				text = metadata?.filePath
					?: stringResource(R.string.edit_metadata_unknown_file_path),
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.labelLarge
			)
		}

		Spacer(Modifier.height(dimensionResource(R.dimen.x_small_padding)))

		// --===--  File Size  --===--
		Row {
			Text(
				text = stringResource(R.string.edit_metadata_file_size) + " ",
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.labelLarge.copy(
					fontWeight = FontWeight.W400
				)
			)

			Text(
				text = metadata?.fileSizeMB
					?: stringResource(R.string.edit_metadata_unknown_file_size),
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.labelLarge
			)
		}

		Spacer(Modifier.height(dimensionResource(R.dimen.x_small_padding)))

		// --===--  Duration  --===--
		Row {
			Text(
				text = stringResource(R.string.edit_metadata_duration) + " ",
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.labelLarge.copy(
					fontWeight = FontWeight.W400
				)
			)

			Text(
				text = metadata?.durationMs?.toLong()?.formatDuration()
					?: stringResource(R.string.edit_metadata_unknown_duration),
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.labelLarge
			)
		}

		Spacer(Modifier.height(dimensionResource(R.dimen.x_small_padding)))

		// --===--  Bit Rate --===--
		Row {
			Text(
				text = stringResource(R.string.edit_metadata_bit_rate) + " ",
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.labelLarge.copy(
					fontWeight = FontWeight.W400
				)
			)

			Text(
				text = if (metadata?.bitRate != null) {
					"${metadata.bitRate.toInt() / 1000} kbps"
				} else {
					stringResource(R.string.edit_metadata_unknown_bit_rate)
				},
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.labelLarge
			)
		}

		Spacer(Modifier.height(dimensionResource(R.dimen.x_small_padding)))

		// --===--  Sample Rate  --===--
		Row {
			Text(
				text = stringResource(R.string.edit_metadata_sample_rate) + " ",
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.labelLarge.copy(
					fontWeight = FontWeight.W400
				)
			)

			Text(
				text = if (metadata?.sampleRate != null) {
					"${metadata.sampleRate.toBigDecimal().divide(1000.toBigDecimal(), 
						3, 
						RoundingMode.HALF_UP)} kHz"
				} else {
					stringResource(R.string.edit_metadata_unknown_sample_rate)
				},
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.labelLarge
			)
		}

		Spacer(Modifier.height(dimensionResource(R.dimen.x_small_padding)))

		// --===--  Mime Type  --===--
		Row {
			Text(
				text = stringResource(R.string.edit_metadata_mime_type) + " ",
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.labelLarge.copy(
					fontWeight = FontWeight.W400
				)
			)

			Text(
				text = metadata?.mimeType
					?: stringResource(R.string.edit_metadata_unknown_mime_type),
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.labelLarge
			)
		}
	}

	Spacer(Modifier.height(dimensionResource(R.dimen.screen_option_section_vertical_padding)))
}