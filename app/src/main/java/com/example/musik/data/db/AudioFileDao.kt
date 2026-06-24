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
	fun searchByTitle(search: String): Flow<List<AudioFile>>

	@Query("SELECT * from audio_files WHERE id = :id")
	fun getById(id: Int): Flow<AudioFile>

	@Query("SELECT * FROM audio_files ORDER BY orderPos ASC")
	fun getAll(): Flow<List<AudioFile>>

	@Query("SELECT COUNT(*) FROM audio_files")
	suspend fun getCount(): Int

	@Query("UPDATE audio_files SET orderPos = :orderPos WHERE id = :id")
	suspend fun updateOrderPos(id: Long, orderPos: Int)

	@Query("DELETE FROM audio_files WHERE id IN (:ids)")
	suspend fun deleteMultipleById(ids: Set<Long>)

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