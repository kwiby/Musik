package com.example.musik.ui.components

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

private data class ThumbInfo(
	val offsetRatio: Float,
	val heightRatio: Float
)

fun Modifier.verticalScrollbar(
	state: LazyListState
): Modifier = composed {
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

			// Total items in the data set
			val totalItems = layoutInfo.totalItemsCount
			// The total height of all items (or the maximum possible scroll offset)
			// We use viewportEndOffset to correctly represent the extent of the list
			val viewportHeight = layoutInfo.viewportSize.height.toFloat()
			val totalContentHeight = layoutInfo.viewportStartOffset + layoutInfo.totalItemsCount * (visibleItems.firstOrNull()?.size?.toFloat() ?: 0f)

			if (totalItems == 0 || visibleItems.isEmpty() || totalContentHeight <= viewportHeight) {
				null // Don't draw the scrollbar if the content fits on one screen
			} else {
				val firstItem = visibleItems.first()
				val itemHeight = firstItem.size.toFloat()

				// Current scroll offset in pixels
				val scrolledPx = (firstItem.index * itemHeight) - firstItem.offset

				// The maximum distance we can actually scroll
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


/*
@Composable
fun Modifier.verticalScrollbar(
	state: LazyListState
): Modifier {
	val colour = MaterialTheme.colorScheme.outlineVariant
	val width = 2.dp

	val targetAlpha = if (state.isScrollInProgress) 1f else 0f
	val duration = if (state.isScrollInProgress) 150 else 500

	val alpha by animateFloatAsState(
		targetValue = targetAlpha,
		animationSpec = tween(durationMillis = duration)
	)

	return drawWithContent {
		drawContent()

		val firstVisibleElementIndex = state.layoutInfo.visibleItemsInfo.firstOrNull()?.index
		val needDrawScrollbar = state.isScrollInProgress || alpha > 0.0f

		// Draw scrollbar if scrolling or if the animation is still running and lazy column has content
		if (needDrawScrollbar && firstVisibleElementIndex != null) {
			val elementHeight = this.size.height / state.layoutInfo.totalItemsCount
			val scrollbarOffsetY = firstVisibleElementIndex * elementHeight
			val scrollbarHeight = state.layoutInfo.visibleItemsInfo.size * elementHeight
			val widthPx = width.toPx()

			drawRoundRect(
				color = colour,
				topLeft = Offset(this.size.width - widthPx, scrollbarOffsetY),
				size = Size(widthPx, scrollbarHeight),
				alpha = alpha,
				cornerRadius = CornerRadius(widthPx, widthPx)
			)
		}
	}
}
*/