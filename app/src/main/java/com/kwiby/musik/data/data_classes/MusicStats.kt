package com.kwiby.musik.data.data_classes

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_stats")
data class MusicStats(
	@PrimaryKey val musicId: Long,
	val playCount: Int = 0,
	val totalListenTimeMs: Long = 0L,
)
