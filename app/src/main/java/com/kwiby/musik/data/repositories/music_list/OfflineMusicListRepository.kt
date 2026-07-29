package com.kwiby.musik.data.repositories.music_list

import com.kwiby.musik.data.daos.music_list.MusicListDao
import com.kwiby.musik.data.data_classes.AudioFile
import kotlinx.coroutines.flow.Flow

class OfflineMusicListRepository(
	private val musicListDao: MusicListDao
) : MusicListRepository {
	override fun getAllAudioFilesStream(): Flow<List<AudioFile>> = musicListDao.getAll()
	override suspend fun getAudioFileCount(): Int = musicListDao.getCount()
	override suspend fun updateMultipleOrderPos(orderedIds: List<Long>) =
		musicListDao.updateMultipleOrderPos(orderedIds)
	override suspend fun deleteMultipleAudioFilesById(ids: Set<Long>) =
		musicListDao.deleteMultipleById(ids)
	override suspend fun insertMultipleAudioFiles(audioFiles: List<AudioFile>) =
		musicListDao.insertMultiple(audioFiles)
	override suspend fun insertAudioFile(audioFile: AudioFile) = musicListDao.insert(audioFile)
}