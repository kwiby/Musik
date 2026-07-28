package com.kwiby.musik.data.repositories.music_stats

import com.kwiby.musik.data.data_classes.AudioFile
import com.kwiby.musik.data.data_classes.MusicStats
import kotlinx.coroutines.flow.Flow

interface MusicStatsRepository {
	fun getStatsByIdStream(id: Long): Flow<MusicStats>
	suspend fun getStatsById(id: Long): MusicStats
	suspend fun getOverallPlayCount(): Long?
	suspend fun getOverallListenTime(): Long?
	suspend fun getStatsOrderedByPlayCountDESC(): List<AudioFile>
	suspend fun getStatsOrderedByListenTimeDESC(): List<AudioFile>
	suspend fun insertIfAbsent(stats: MusicStats)
	suspend fun incrementPlayCount(id: Long)
	suspend fun logMusicSession(id: Long, listenTimeMs: Long)
}