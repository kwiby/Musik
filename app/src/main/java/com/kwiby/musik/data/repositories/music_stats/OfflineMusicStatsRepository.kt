package com.kwiby.musik.data.repositories.music_stats

import android.database.sqlite.SQLiteConstraintException
import android.util.Log
import com.kwiby.musik.data.daos.music_stats.MusicStatsDao
import com.kwiby.musik.data.data_classes.AudioFileStats
import com.kwiby.musik.data.data_classes.MusicStats
import kotlinx.coroutines.flow.Flow

private const val LOG_TAG = "OfflineMusicStatsRepository"

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
	override suspend fun logPlayCount(id: Long) = musicStatsDao.logPlayCount(id)
	override suspend fun logListenTime(id: Long, listenTimeMs: Long) {
		try {
			musicStatsDao.logListenTime(id, listenTimeMs)
		} catch (_: SQLiteConstraintException) {
			Log.w(LOG_TAG, "Skipped music session log for deleted track id=$id")
		}
	}
	override suspend fun deleteMultipleById(ids: Set<Long>) = musicStatsDao.deleteMultipleById(ids)
}