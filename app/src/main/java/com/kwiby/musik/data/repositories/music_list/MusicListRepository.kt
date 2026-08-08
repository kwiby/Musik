package com.kwiby.musik.data.repositories.music_list

import com.kwiby.musik.data.data_classes.AudioFile
import kotlinx.coroutines.flow.Flow

interface MusicListRepository {
	fun getAllAudioFilesStream(): Flow<List<AudioFile>>
	suspend fun getAudioFileCount(): Int
	suspend fun updateMultipleOrderPos(orderedIds: List<Long>)
	suspend fun deleteMultipleAudioFilesById(ids: Set<Long>)
	suspend fun insertMultipleAudioFiles(audioFiles: List<AudioFile>)
	suspend fun insertAudioFile(audioFile: AudioFile)
	suspend fun editTitle(id: Long, newTitle: String)
	suspend fun editArtist(id: Long, newArtist: String)
}