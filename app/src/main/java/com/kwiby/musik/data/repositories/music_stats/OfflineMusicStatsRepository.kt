package com.kwiby.musik.data.repositories.music_stats

import com.kwiby.musik.data.daos.music_stats.MusicStatsDao
import com.kwiby.musik.data.data_classes.MusicStats
import kotlinx.coroutines.flow.Flow

class OfflineMusicStatsRepository(
	private val musicStatsDao: MusicStatsDao
) : MusicStatsRepository {
	override fun getStatsStream(id: Long): Flow<MusicStats> = musicStatsDao.observeStats(id)
	override suspend fun getStats(id: Long): MusicStats = musicStatsDao.getStats(id)
	override suspend fun insertIfAbsent(stats: MusicStats) = musicStatsDao.insertIfAbsent(stats)
	override suspend fun incrementPlayCount(id: Long) = musicStatsDao.incrementPlayCount(id)
	//override suspend fun addListenTime(id: Long, listenTimeMs: Long) = musicStatsDao.addListenTime(id, listenTimeMs)
	override suspend fun logMusicSession(id: Long, listenTimeMs: Long) =
		musicStatsDao.logMusicSession(id, listenTimeMs)
}