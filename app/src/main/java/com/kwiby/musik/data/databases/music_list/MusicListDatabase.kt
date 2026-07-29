package com.kwiby.musik.data.databases.music_list

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kwiby.musik.data.daos.music_list.MusicListDao
import com.kwiby.musik.data.daos.music_stats.MusicStatsDao
import com.kwiby.musik.data.data_classes.AudioFile
import com.kwiby.musik.data.data_classes.AudioFileStats

@Database(
	entities = [AudioFile::class, AudioFileStats::class],
	version = 1, // EXAMPLE MIGRATION -> Make sure to bump this value up for each db migration
	exportSchema = false
)
abstract class MusicListDatabase: RoomDatabase() {
	abstract fun musicListDao(): MusicListDao
	abstract fun musicStatsDao(): MusicStatsDao

	companion object {
		@Volatile
		private var Instance: MusicListDatabase? = null

		fun getDatabase(context: Context): MusicListDatabase {
			// If the Instance is not null, return it, otherwise create a new database instance.
			return Instance ?: synchronized(this) {
				Room.databaseBuilder(context, MusicListDatabase::class.java, "music_list")
					.addMigrations(/* MIGRATION_1_2 */)
					.fallbackToDestructiveMigration(true)
					.build()
					.also {
						Instance = it
					}
			}
		}
	}
}