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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.example.audio/files"
    private var audioListCache: List<Map<String, Any?>>? = null
    private var cacheTimestamp: Long = 0

    private val CACHE_VALIDITY_MS_CONSTANT = 60000
    private val CACHE_VALIDITY_MS_MINUTES = 15 // 15 minutes of cache utilization (set to 0 to remove caching)
    private var cache_validity_ms = CACHE_VALIDITY_MS_CONSTANT * CACHE_VALIDITY_MS_MINUTES

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "getAudioFilesWithCache" -> {
                    val page = call.argument<Int>("page") ?: 0
                    val pageSize = call.argument<Int>("pageSize") ?: 1000

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val audioList = getAudioFilesWithCache(page, pageSize)
                            withContext(Dispatchers.Main) {
                                result.success(audioList)
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                result.error("ERROR", e.message, null)
                            }
                        }
                    }
                }
                "getAudioFilesWithoutCache" -> {
                    val page = call.argument<Int>("page") ?: 0
                    val pageSize = call.argument<Int>("pageSize") ?: 1000

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val audioList = getAudioFilesWithoutCache(page, pageSize)
                            withContext(Dispatchers.Main) {
                                result.success(audioList)
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                result.error("ERROR", e.message, null)
                            }
                        }
                    }
                }
                else -> result.notImplemented()
            }
        }
    }

    private fun getAlbumArtBase64(albumId: Long): String? {
        if (albumId == -1L) return null

        val albumArtUri = "content://media/external/audio/albumart/$albumId"

        return try {
            contentResolver.openInputStream(android.net.Uri.parse(albumArtUri))?.use { inputStream ->
                val options = BitmapFactory.Options().apply {
                    inSampleSize = 1 // Higher the number = lower quality
                    inPreferredConfig = Bitmap.Config.ARGB_8888
                }

                val bitmap = BitmapFactory.decodeStream(inputStream, null, options)
                ByteArrayOutputStream().use { outputStream ->
                    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getAudioFilesWithCache(page: Int = 0, pageSize: Int = 1000): List<Map<String, Any?>> {
        val currentTime = System.currentTimeMillis()
        if (page == 0 && audioListCache != null && (currentTime - cacheTimestamp) < cache_validity_ms) {
            return if (pageSize > 0) audioListCache!!.take(pageSize) else audioListCache!!
        }

        val audioList = fetchAudioFilesFromStorage(page, pageSize)

        if (page == 0) {
            audioListCache = audioList
            cacheTimestamp = currentTime
        }

        return audioList
    }

    private fun getAudioFilesWithoutCache(page: Int = 0, pageSize: Int = 1000): List<Map<String, Any?>> {
        val audioList = fetchAudioFilesFromStorage(page, pageSize)

        // Update cache with fresh data and reset timer
        if (page == 0) {
            audioListCache = audioList
            cacheTimestamp = System.currentTimeMillis()
        }

        return audioList
    }

    private fun fetchAudioFilesFromStorage(page: Int, pageSize: Int): List<Map<String, Any?>> {
        val audioList = mutableListOf<Map<String, Any?>>()
        val albumArtCache = mutableMapOf<Long, String?>()

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATA
        )

        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        val uri = if (pageSize > 0) {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.buildUpon()
                .appendQueryParameter("limit", "$pageSize")
                .appendQueryParameter("offset", "${page * pageSize}")
                .build()
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        contentResolver.query(uri, projection, selection, null, sortOrder)?.use { cursor ->
            // First pass: collect unique album IDs
            val albumIds = mutableSetOf<Long>()
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)

            while (cursor.moveToNext()) {
                albumIds.add(cursor.getLong(albumIdColumn))
            }

            // Pre-fetch album arts in batch
            albumIds.forEach { albumId ->
                if (!albumArtCache.containsKey(albumId)) {
                    albumArtCache[albumId] = getAlbumArtBase64(albumId)
                }
            }

            // Second pass: build the audio list
            cursor.moveToPosition(-1) // Reset cursor position
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)

            while (cursor.moveToNext()) {
                val albumId = cursor.getLong(albumIdColumn)
                val audio = mapOf<String, Any?>(
                    "id" to cursor.getLong(idColumn),
                    "title" to cursor.getString(titleColumn),
                    "artist" to cursor.getString(artistColumn),
                    "duration" to cursor.getLong(durationColumn),
                    "filePath" to cursor.getString(dataColumn),
                    "albumArtBase64" to albumArtCache[albumId],
                    "albumArtUri" to "content://media/external/audio/albumart/$albumId"
                )
                audioList.add(audio)
            }
        }

        return audioList
    }
}


/*package com.example.musik

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
}*/