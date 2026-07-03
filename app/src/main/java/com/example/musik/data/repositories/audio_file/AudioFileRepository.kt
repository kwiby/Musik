package com.example.musik.data.repositories.audio_file

import com.example.musik.data.data_classes.AudioFile
import kotlinx.coroutines.flow.Flow

interface AudioFileRepository {
	fun getAllAudioFilesStream(): Flow<List<AudioFile>>

	fun getAudioFileByIdStream(id: Int): Flow<AudioFile?>

	suspend fun getAudioFileCount(): Int

	suspend fun updateMultipleOrderPos(orderedIds: List<Long>)

	suspend fun deleteMultipleAudioFilesById(ids: Set<Long>)

	suspend fun insertMultipleAudioFiles(audioFiles: List<AudioFile>)

	suspend fun deleteMultipleAudioFiles(audioFiles: List<AudioFile>)

	suspend fun updateMultipleAudioFiles(audioFiles: List<AudioFile>)

	suspend fun insertAudioFile(audioFile: AudioFile)

	suspend fun deleteAudioFile(audioFile: AudioFile)

	suspend fun updateAudioFile(audioFile: AudioFile)
}