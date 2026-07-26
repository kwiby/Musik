package com.kwiby.musik.data.daos.music_stats

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.kwiby.musik.data.data_classes.MusicStats
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicStatsDao {
	@Query("SELECT * FROM music_stats WHERE musicId = :id")
	fun observeStats(id: Long): Flow<MusicStats>

	@Query("SELECT * FROM music_stats WHERE musicId = :id")
	suspend fun getStats(id: Long): MusicStats

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