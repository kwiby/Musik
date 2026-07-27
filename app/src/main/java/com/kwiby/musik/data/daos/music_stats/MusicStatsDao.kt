package com.kwiby.musik.data.daos.music_stats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kwiby.musik.data.data_classes.AudioFile
import com.kwiby.musik.data.data_classes.MusicStats
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicStatsDao {
	@Query("SELECT * FROM music_stats WHERE musicId = :id")
	fun observeStatsById(id: Long): Flow<MusicStats>

	@Query("SELECT * FROM music_stats WHERE musicId = :id")
	suspend fun getStatsById(id: Long): MusicStats

	@Query("""
		SELECT music_list.* FROM music_list
		LEFT JOIN music_stats ON music_list.id = music_stats.musicId
		ORDER BY
			CASE WHEN music_stats.playCount IS NULL OR music_stats.playCount = 0 
				THEN 1 
				ELSE 0 
			END,
			music_stats.playCount DESC,
			music_list.title ASC;
	""")
	suspend fun getStatsOrderedByPlayCountDESC(): List<AudioFile>

	@Query("""
		SELECT music_list.* FROM music_list
		LEFT JOIN music_stats ON music_list.id = music_stats.musicId
		ORDER BY
			CASE WHEN music_stats.totalListenTimeMs IS NULL OR music_stats.totalListenTimeMs = 0 
				THEN 1 
				ELSE 0 
			END,
			music_stats.totalListenTimeMs DESC,
			music_list.title ASC;
	""")
	suspend fun getStatsOrderedByListenTimeDESC(): List<AudioFile>

	@Insert(onConflict = OnConflictStrategy.IGNORE)
	suspend fun insertIfAbsent(stats: MusicStats)

	@Query("UPDATE music_stats SET playCount = playCount + 1 WHERE musicId = :id")
	suspend fun incrementPlayCount(id: Long)

	@Query("""
		UPDATE music_stats
		SET totalListenTimeMs = totalListenTimeMs + :listenTimeMs
		WHERE musicId = :id
	""")
	suspend fun addListenTime(id: Long, listenTimeMs: Long)

	@Transaction
	suspend fun logMusicSession(id: Long, listenTimeMs: Long) {
		insertIfAbsent(MusicStats(id))
		addListenTime(id, listenTimeMs)
	}
}