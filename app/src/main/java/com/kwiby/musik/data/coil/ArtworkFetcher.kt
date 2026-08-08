package com.kwiby.musik.data.coil

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.util.Log
import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.decode.DataSource
import coil3.decode.ImageSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.SourceFetchResult
import coil3.request.Options
import coil3.size.pxOrElse
import okio.Buffer
import okio.FileSystem
import android.net.Uri as netUri
import coil3.Uri as coilUri

private const val LOG_TAG = "ArtworkFetcher"

class ArtworkFetcher(
	private val context: Context,
	private val uri: netUri,
	private val sizePx: Int
) : Fetcher {
	override suspend fun fetch(): FetchResult? {
		Log.d(LOG_TAG, "fetch() called for uri=$uri")

		val bytes = try {
			val retriever = MediaMetadataRetriever()
			try {
				retriever.setDataSource(context, uri)
				retriever.embeddedPicture
			} finally {
				retriever.release()
			}
		} catch (e: Exception) {
			Log.e(LOG_TAG, "MediaMetadataRetriever failed for uri=$uri", e)
			return null
		}

		if (bytes == null) {
			Log.w(LOG_TAG, "No embedded picture for $uri")
			return null
		}
		Log.d("debug", "Extracted embedded picture, size=${bytes.size}")

		val bitmap = if (sizePx > 0) {
			val options = BitmapFactory.Options().apply {
				inJustDecodeBounds = true
				BitmapFactory.decodeByteArray(bytes, 0, bytes.size, this)
				inSampleSize = calculateInSampleSize(this, sizePx, sizePx)
				inJustDecodeBounds = false
			}
			BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
		} else {
			BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
		} ?: return null

		val buffer = Buffer()
		val outputStream = buffer.outputStream()
		bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
		bitmap.recycle()

		return SourceFetchResult(
			source = ImageSource(
				source = buffer,
				fileSystem = FileSystem.SYSTEM
			),
			mimeType = "image/jpeg",
			dataSource = DataSource.DISK
		)
	}

	private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
		val height = options.outHeight
		val width = options.outWidth
		var inSampleSize = 1

		if (height > reqHeight || width > reqWidth) {
			val halfHeight = height / 2
			val halfWidth = width / 2

			while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
				inSampleSize *= 2
			}
		}
		return inSampleSize
	}

	class Factory(private val context: Context) : Fetcher.Factory<coilUri> {
		override fun create(data: coilUri, options: Options, imageLoader: ImageLoader): Fetcher? {
			Log.d(LOG_TAG, "Factory.create called with data=$data, authority=${data.authority}")

			if (data.authority != "media" || !data.toString().contains("/audio/media/")) {
				return null
			}

			val netUri = data.toString().toUri()
			val sizePx = options.size.width.pxOrElse { 512 }
			Log.d(LOG_TAG, "URI matched, creating fetcher with sizePx=$sizePx")

			return ArtworkFetcher(context, netUri, sizePx)
		}
	}
}