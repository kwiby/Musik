package com.example.musik.data.misc

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.example.musik.data.models.AudioFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun fetchAudioFiles(context: Context): List<AudioFile> = withContext(Dispatchers.IO) {
	val audioFileList = mutableListOf<AudioFile>()

	val contentResolver = context.contentResolver
	val projection = arrayOf(
		MediaStore.Audio.Media._ID,
		MediaStore.Audio.Media.ALBUM_ID,
		MediaStore.Audio.Media.TITLE,
		MediaStore.Audio.Media.ARTIST,
		MediaStore.Audio.Media.DURATION
	)

	val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
	val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"
	val mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

	val cursor = contentResolver.query(mediaUri, projection, selection, null, sortOrder)
	cursor?.use {
		val idColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
		val albumIdColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
		val titleColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
		val artistColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
		val durationColumn = it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

		while (it.moveToNext()) {
			val id = it.getLong(idColumn)
			val title = it.getString(titleColumn) ?: "Unknown Title"
			val artist = it.getString(artistColumn) ?: "Unknown Artist"
			val duration = it.getLong(durationColumn)

			val albumArtUri = ContentUris.withAppendedId(
				MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI, it.getLong(albumIdColumn)
			)
			val contentUri = ContentUris.withAppendedId(mediaUri, id)

			audioFileList.add(AudioFile(id, contentUri.toString(), albumArtUri.toString(), title, artist, duration))
		}
	}

	audioFileList
}