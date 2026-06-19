package com.example.musik.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.musik.data.models.AudioFile
import kotlinx.coroutines.flow.Flow

@Dao
interface AudioFileDao {
	@Query("SELECT * from audio_files WHERE title LIKE '%' || :search || '%'")
	fun searchAudioFilesByTitle(search: String): Flow<List<AudioFile>>

	@Query("SELECT COUNT(*) FROM audio_files")
	fun getAudioFileCount(): Flow<Int>

	@Query("SELECT * FROM audio_files ORDER BY title ASC")
	fun getAllAudioFiles(): Flow<List<AudioFile>>

	@Query("SELECT * from audio_files WHERE id = :id")
	fun getAudioFileById(id: Int): Flow<AudioFile>

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertMultiple(audioFiles: List<AudioFile>)

	@Delete
	suspend fun deleteMultiple(audioFiles: List<AudioFile>)

	@Update
	suspend fun updateMultiple(audioFiles: List<AudioFile>)

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insert(audioFile: AudioFile)

	@Delete
	suspend fun delete(audioFile: AudioFile)

	@Update
	suspend fun update(audioFile: AudioFile)
}