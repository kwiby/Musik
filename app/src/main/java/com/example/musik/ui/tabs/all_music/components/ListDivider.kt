package com.example.musik.ui.tabs.all_music.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import com.example.musik.R
import com.example.musik.data.models.MusicDetails

@Composable
fun ListDivider(index: Int, list: List<MusicDetails>) {
	if (index < list.size - 1) {
		Box(modifier = Modifier.fillMaxWidth()) {
			HorizontalDivider(
				thickness = dimensionResource(R.dimen.horizontal_divider_thickness),
				color = Color.DarkGray,
				modifier = Modifier
					.fillMaxWidth(0.785f)
					.align(Alignment.CenterEnd)
					.padding(
						horizontal = dimensionResource(R.dimen.horizontal_divider_padding),
						vertical = dimensionResource(R.dimen.vertical_divider_padding)
					)
			)
		}
	}
}