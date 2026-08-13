package com.kwiby.musik.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.Dp
import com.kwiby.musik.R

@Composable
fun ListDivider(
	widthFraction: Float = 0.8f,
	horizontalPadding: Dp = dimensionResource(R.dimen.horizontal_divider_padding)
) {
	Box(modifier = Modifier.fillMaxWidth()) {
		HorizontalDivider(
			thickness = dimensionResource(R.dimen.horizontal_divider_thickness),
			color = Color.DarkGray,
			modifier = Modifier
				.fillMaxWidth(widthFraction)
				.align(Alignment.CenterEnd)
				.padding(
					horizontal = horizontalPadding,
					vertical = dimensionResource(R.dimen.vertical_divider_padding)
				)
		)
	}
}