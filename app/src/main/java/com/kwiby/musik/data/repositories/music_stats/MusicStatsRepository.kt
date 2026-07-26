package com.kwiby.musik.data.repositories.music_stats

import com.kwiby.musik.data.data_classes.MusicStats
import kotlinx.coroutines.flow.Flow

interface MusicStatsRepository {
	fun getStatsStream(id: Long): Flow<MusicStats>
	suspend fun getStats(id: Long): MusicStats
	suspend fun insertIfAbsent(stats: MusicStats)
	suspend fun incrementPlayCount(id: Long)
	//suspend fun addListenTime(id: Long, listenTimeMs: Long)
	suspend fun logMusicSession(id: Long, listenTimeMs: Long)
}