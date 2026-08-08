package com.kwiby.musik.data.daos.music_list

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.kwiby.musik.data.data_classes.AudioFile
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicListDao {
	@Query("SELECT * from music_list WHERE title LIKE '%' || :search || '%'")
	fun searchByTitle(search: String): Flow<List<AudioFile>>

	@Query("SELECT * from music_list WHERE id = :id")
	fun getById(id: Int): Flow<AudioFile>

	@Query("SELECT * FROM music_list ORDER BY orderPos ASC")
	fun getAll(): Flow<List<AudioFile>>

	@Query("SELECT COUNT(*) FROM music_list")
	suspend fun getCount(): Int

	@Query("UPDATE music_list SET orderPos = :orderPos WHERE id = :id")
	suspend fun updateOrderPos(id: Long, orderPos: Int)

	@Transaction
	suspend fun updateMultipleOrderPos(orderedIds: List<Long>) {
		orderedIds.forEachIndexed { index, id ->
			updateOrderPos(id, index)
		}
	}

	@Query("DELETE FROM music_list WHERE id IN (:ids)")
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

	@Query("UPDATE music_list SET title = :newTitle WHERE id = :id")
	suspend fun editTitle(id: Long, newTitle: String)

	@Query("UPDATE music_list SET artist = :newArtist WHERE id = :id")
	suspend fun editArtist(id: Long, newArtist: String)
}