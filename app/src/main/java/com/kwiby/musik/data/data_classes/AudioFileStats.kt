package com.kwiby.musik.data.data_classes

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
	tableName = "music_stats",
	foreignKeys = [
		ForeignKey(
			entity = AudioFile::class,
			parentColumns = ["id"],
			childColumns = ["id"],
			onDelete = ForeignKey.CASCADE
		)
	]
)
data class AudioFileStats(
	@PrimaryKey val id: Long,
	val playCount: Int = 0,
	val totalListenTimeMs: Long = 0L,
)
