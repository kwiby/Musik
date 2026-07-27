package com.kwiby.musik.data.repositories.music_stats

import com.kwiby.musik.data.daos.music_stats.MusicStatsDao
import com.kwiby.musik.data.data_classes.AudioFile
import com.kwiby.musik.data.data_classes.MusicStats
import kotlinx.coroutines.flow.Flow

class OfflineMusicStatsRepository(
	private val musicStatsDao: MusicStatsDao
) : MusicStatsRepository {
	override fun getStatsByIdStream(id: Long): Flow<MusicStats> = musicStatsDao.observeStatsById(id)
	override suspend fun getStatsById(id: Long): MusicStats = musicStatsDao.getStatsById(id)
	override suspend fun getStatsOrderedByPlayCountDESC(): List<AudioFile> =
		musicStatsDao.getStatsOrderedByPlayCountDESC()
	override suspend fun getStatsOrderedByListenTimeDESC(): List<AudioFile> =
		musicStatsDao.getStatsOrderedByListenTimeDESC()
	override suspend fun insertIfAbsent(stats: MusicStats) = musicStatsDao.insertIfAbsent(stats)
	override suspend fun incrementPlayCount(id: Long) = musicStatsDao.incrementPlayCount(id)
	override suspend fun logMusicSession(id: Long, listenTimeMs: Long) =
		musicStatsDao.logMusicSession(id, listenTimeMs)
}