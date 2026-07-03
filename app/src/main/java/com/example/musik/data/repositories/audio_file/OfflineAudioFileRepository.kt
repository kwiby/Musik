package com.example.musik.data.repositories.audio_file

import com.example.musik.data.db.AudioFileDao
import com.example.musik.data.data_classes.AudioFile
import kotlinx.coroutines.flow.Flow

class OfflineAudioFileRepository(private val audioFileDao: AudioFileDao): AudioFileRepository {
	override fun getAllAudioFilesStream(): Flow<List<AudioFile>> = audioFileDao.getAll()

	override fun getAudioFileByIdStream(id: Int): Flow<AudioFile?> = audioFileDao.getById(id)

	override suspend fun getAudioFileCount(): Int = audioFileDao.getCount()

	override suspend fun updateMultipleOrderPos(orderedIds: List<Long>) {
		orderedIds.forEachIndexed { index, id ->
			audioFileDao.updateOrderPos(id, index)
		}
	}

	override suspend fun deleteMultipleAudioFilesById(ids: Set<Long>) = audioFileDao.deleteMultipleById(ids)

	override suspend fun insertMultipleAudioFiles(audioFiles: List<AudioFile>) = audioFileDao.insertMultiple(audioFiles)

	override suspend fun deleteMultipleAudioFiles(audioFiles: List<AudioFile>) = audioFileDao.deleteMultiple(audioFiles)

	override suspend fun updateMultipleAudioFiles(audioFiles: List<AudioFile>) = audioFileDao.updateMultiple(audioFiles)

	override suspend fun insertAudioFile(audioFile: AudioFile) = audioFileDao.insert(audioFile)

	override suspend fun deleteAudioFile(audioFile: AudioFile) = audioFileDao.delete(audioFile)

	override suspend fun updateAudioFile(audioFile: AudioFile) = audioFileDao.update(audioFile)
}