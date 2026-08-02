package com.kwiby.musik.data.daos.music_stats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kwiby.musik.data.data_classes.AudioFileStats
import com.kwiby.musik.data.data_classes.MusicStats
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicStatsDao {
	@Query("SELECT * FROM music_stats WHERE id = :id")
	fun observeStatsById(id: Long): Flow<AudioFileStats>

	@Query("SELECT * FROM music_stats WHERE id = :id")
	suspend fun getStatsById(id: Long): AudioFileStats

	@Query("SELECT SUM(playCount) FROM music_stats")
	suspend fun getOverallPlayCount(): Long?

	@Query("SELECT SUM(totalListenTimeMs) FROM music_stats")
	suspend fun getOverallListenTime(): Long?

	@Query("""
		SELECT 
			music_list.id AS id,
			music_stats.playCount AS playCount,
			music_stats.totalListenTimeMs AS totalListenTimeMs,
			music_list.albumArtUri AS albumArtUri,
			music_list.title AS title,
			music_list.artist AS artist
		FROM music_list
		LEFT JOIN music_stats ON music_list.id = music_stats.id
		ORDER BY
			CASE WHEN music_stats.playCount IS NULL OR music_stats.playCount = 0 
				THEN 1 
				ELSE 0 
			END,
			music_stats.playCount DESC,
			music_list.title ASC
	""")
	suspend fun getStatsOrderedByPlayCountDESC(): List<MusicStats>

	@Query("""
		SELECT 
			music_list.id AS id,
			music_stats.playCount AS playCount,
			music_stats.totalListenTimeMs AS totalListenTimeMs,
			music_list.albumArtUri AS albumArtUri,
			music_list.title AS title,
			music_list.artist AS artist
		FROM music_list
		LEFT JOIN music_stats ON music_list.id = music_stats.id
		ORDER BY
			CASE WHEN music_stats.totalListenTimeMs IS NULL OR music_stats.totalListenTimeMs = 0 
				THEN 1 
				ELSE 0 
			END,
			music_stats.totalListenTimeMs DESC,
			music_list.title ASC
	""")
	suspend fun getStatsOrderedByListenTimeDESC(): List<MusicStats>

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertIfAbsent(stats: AudioFileStats)

	@Query("UPDATE music_stats SET playCount = playCount + 1 WHERE id = :id")
	suspend fun incrementPlayCount(id: Long)

	@Query("""
		UPDATE music_stats
		SET totalListenTimeMs = totalListenTimeMs + :listenTimeMs
		WHERE id = :id
	""")
	suspend fun addListenTime(id: Long, listenTimeMs: Long)

	@Transaction
	suspend fun logPlayCount(id: Long) {
		insertIfAbsent(AudioFileStats(id))
		incrementPlayCount(id)
	}

	@Transaction
	suspend fun logListenTime(id: Long, listenTimeMs: Long) {
		insertIfAbsent(AudioFileStats(id))
		addListenTime(id, listenTimeMs)
	}

	@Query("DELETE FROM music_stats WHERE id IN (:ids)")
	suspend fun deleteMultipleById(ids: Set<Long>)
}