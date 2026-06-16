package com.example.musik.data.repository

import com.example.musik.data.models.AudioFile
import kotlinx.coroutines.flow.Flow

interface AudioFileRepository {
	fun getAllAudioFilesStream(): Flow<List<AudioFile>>

	fun getAudioFileByIdStream(id: Int): Flow<AudioFile?>

	fun getAudioFileCountStream(): Flow<Int>

	suspend fun insertAudioFile(audioFile: AudioFile)

	suspend fun deleteAudioFile(audioFile: AudioFile)

	suspend fun updateAudioFile(audioFile: AudioFile)
}