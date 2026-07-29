package com.kwiby.musik.data.repositories.music_stats

import com.kwiby.musik.data.data_classes.AudioFileStats
import com.kwiby.musik.data.data_classes.MusicStats
import kotlinx.coroutines.flow.Flow

interface MusicStatsRepository {
	fun getStatsByIdStream(id: Long): Flow<AudioFileStats>
	suspend fun getStatsById(id: Long): AudioFileStats
	suspend fun getOverallPlayCount(): Long?
	suspend fun getOverallListenTime(): Long?
	suspend fun getStatsOrderedByPlayCountDESC(): List<MusicStats>
	suspend fun getStatsOrderedByListenTimeDESC(): List<MusicStats>
	suspend fun insertIfAbsent(stats: AudioFileStats)
	suspend fun incrementPlayCount(id: Long)
	suspend fun logMusicSession(id: Long, listenTimeMs: Long)
}