package com.example.musik

import android.provider.MediaStore
import android.database.Cursor
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.util.Base64
import android.os.Bundle
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.embedding.android.FlutterActivity
import java.io.ByteArrayOutputStream
import java.io.InputStream

class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.example.audio/files"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "getAudioFiles") {
                val audioList = getAudioFiles()
                result.success(audioList)
            } else {
                result.notImplemented()
            }
        }
    }

    private fun getAlbumArtBase64(albumId: Long) : String? {
        val albumArtUri = "content://media/external/audio/albumart/$albumId";

        try {
            val inputStream : InputStream? = contentResolver.openInputStream(android.net.Uri.parse(albumArtUri))

            inputStream?.use {
                val bitmap = BitmapFactory.decodeStream(it);
                val outputStream = ByteArrayOutputStream()

                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

                val byteArray = outputStream.toByteArray()

                return Base64.encodeToString(byteArray, Base64.DEFAULT)
            }
        } catch (error : Exception) {
            error.printStackTrace()
        }

        return null
    }

    private fun getAudioFiles(): List<Map<String, Any?>> {
        val audioList = mutableListOf<Map<String, Any?>>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATA // file path
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        val cursor: Cursor? = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )

        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (cursor.moveToNext()) {
                val albumId = cursor.getLong(albumIdColumn)
                val albumArtBase64 = getAlbumArtBase64(albumId)

                val audio = mapOf<String, Any?>(
                    "id" to cursor.getLong(idColumn),
                    "title" to cursor.getString(titleColumn),
                    "artist" to cursor.getString(artistColumn),
                    "duration" to cursor.getLong(durationColumn),
                    "filePath" to cursor.getString(dataColumn),
                    "albumArtBase64" to albumArtBase64
                )

                audioList.add(audio)
            }
        }

        return audioList
    }
}
