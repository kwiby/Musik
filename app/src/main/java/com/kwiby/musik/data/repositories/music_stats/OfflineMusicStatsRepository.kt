package com.kwiby.musik.data.repositories.music_stats

import com.kwiby.musik.data.daos.music_stats.MusicStatsDao
import com.kwiby.musik.data.data_classes.AudioFileStats
import com.kwiby.musik.data.data_classes.MusicStats
import kotlinx.coroutines.flow.Flow

class OfflineMusicStatsRepository(
	private val musicStatsDao: MusicStatsDao
) : MusicStatsRepository {
	override fun getStatsByIdStream(id: Long): Flow<AudioFileStats> = musicStatsDao.observeStatsById(id)
	override suspend fun getStatsById(id: Long): AudioFileStats = musicStatsDao.getStatsById(id)
	override suspend fun getOverallPlayCount(): Long? = musicStatsDao.getOverallPlayCount()
	override suspend fun getOverallListenTime(): Long? = musicStatsDao.getOverallListenTime()
	override suspend fun getStatsOrderedByPlayCountDESC(): List<MusicStats> =
		musicStatsDao.getStatsOrderedByPlayCountDESC()
	override suspend fun getStatsOrderedByListenTimeDESC(): List<MusicStats> =
		musicStatsDao.getStatsOrderedByListenTimeDESC()
	override suspend fun insertIfAbsent(stats: AudioFileStats) = musicStatsDao.insertIfAbsent(stats)
	override suspend fun incrementPlayCount(id: Long) = musicStatsDao.incrementPlayCount(id)
	override suspend fun logMusicSession(id: Long, listenTimeMs: Long) =
		musicStatsDao.logMusicSession(id, listenTimeMs)
	override suspend fun deleteMultipleById(ids: Set<Long>) = musicStatsDao.deleteMultipleById(ids)
}