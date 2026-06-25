package com.example.musik.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.sp
import com.example.musik.R
import com.example.musik.ui.theme.SourGummy

@Composable
fun TabButton(
	label: String,
	isSelected: Boolean,
	onClick: () -> Unit
) {
	val height = if (isSelected) {
		dimensionResource(R.dimen.tab_selected_height)
	} else {
		dimensionResource(R.dimen.tab_unselected_height)
	}

	val interactionSource = remember { MutableInteractionSource() }
	val tabRoundedCornerShape = RoundedCornerShape(
		topStart = dimensionResource(R.dimen.tab_top_corners_radius),
		topEnd = dimensionResource(R.dimen.tab_top_corners_radius)
	)
	Surface(
		shape = tabRoundedCornerShape,
		color = MaterialTheme.colorScheme.secondary,
		modifier = Modifier
			.width(dimensionResource(R.dimen.tab_width))
			.height(height)
			.dropShadow(
				shape = tabRoundedCornerShape,
				shadow = Shadow(
					color = Color.Black.copy(alpha = 0.2f),
					offset = DpOffset(dimensionResource(R.dimen.zero), dimensionResource(R.dimen.tabs_shadow_offset)),
					radius = dimensionResource(R.dimen.tabs_shadow_radius),
					spread = dimensionResource(R.dimen.tabs_shadow_spread)
				)
			)
			.clickable(interactionSource = interactionSource, indication = null) { onClick() }
	) {
		Text(
			text = label,
			color = MaterialTheme.colorScheme.onSecondary,
			fontFamily = SourGummy,
			fontSize = 15.sp,
			fontWeight = if (isSelected) FontWeight.W600 else FontWeight.W300,
			textAlign = TextAlign.Center,
			modifier = Modifier.wrapContentHeight(Alignment.CenterVertically)
		)
	}
}