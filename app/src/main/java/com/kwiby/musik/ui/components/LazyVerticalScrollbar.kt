package com.kwiby.musik.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp

fun Modifier.lazyVerticalScrollbar(
	state: LazyListState
): Modifier = composed {
	data class ThumbInfo(
		val offsetRatio: Float,
		val heightRatio: Float
	)

	val colour = MaterialTheme.colorScheme.outlineVariant
	val width = 2.5.dp

	val targetAlpha = if (state.isScrollInProgress) 1f else 0f
	val duration = if (state.isScrollInProgress) 150 else 500

	val alpha by animateFloatAsState(
		targetValue = targetAlpha,
		animationSpec = tween(durationMillis = duration),
		label = "scrollbarAlpha"
	)

	val thumbInfo by remember {
		derivedStateOf {
			val layoutInfo = state.layoutInfo
			val visibleItems = layoutInfo.visibleItemsInfo
			val totalItems = layoutInfo.totalItemsCount
			val viewportHeight = layoutInfo.viewportSize.height.toFloat()
			val totalContentHeight = layoutInfo.viewportStartOffset + layoutInfo.totalItemsCount * (visibleItems.firstOrNull()?.size?.toFloat() ?: 0f)

			if (totalItems == 0 || visibleItems.isEmpty() || totalContentHeight <= viewportHeight) {
				null
			} else {
				val firstItem = visibleItems.first()
				val itemHeight = firstItem.size.toFloat()
				val scrolledPx = (firstItem.index * itemHeight) - firstItem.offset
				val maxScrollableDistance = totalContentHeight - viewportHeight

				ThumbInfo(
					offsetRatio = (scrolledPx / maxScrollableDistance).coerceIn(0f, 1f),
					heightRatio = (viewportHeight / totalContentHeight).coerceIn(0.1f, 1f)
				)
			}
		}
	}

	drawWithContent {
		drawContent()

		val info = thumbInfo
		if (info != null && (state.isScrollInProgress || alpha > 0f)) {
			val widthPx = width.toPx()
			val trackHeight = this.size.height
			val thumbHeight = (trackHeight * info.heightRatio).coerceAtLeast(widthPx * 2)
			val maxOffset = trackHeight - thumbHeight
			val offsetY = (info.offsetRatio * maxOffset).coerceIn(0f, maxOffset)

			drawRoundRect(
				color = colour,
				topLeft = Offset(this.size.width - widthPx, offsetY),
				size = Size(widthPx, thumbHeight),
				alpha = alpha,
				cornerRadius = CornerRadius(widthPx, widthPx)
			)
		}
	}
}