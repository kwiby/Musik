package com.kwiby.musik.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kwiby.musik.data.data_classes.AudioFile

@Database(
	entities = [AudioFile::class],
	version = 1, // EXAMPLE MIGRATION -> Make sure to bump this value up for each db migration
	exportSchema = false
)
abstract class AudioFileDatabase: RoomDatabase() {
	abstract fun audioFileDao(): AudioFileDao

	companion object {
		@Volatile
		private var Instance: AudioFileDatabase? = null

		fun getDatabase(context: Context): AudioFileDatabase {
			// If the Instance is not null, return it, otherwise create a new database instance.
			return Instance ?: synchronized(this) {
				Room.databaseBuilder(context, AudioFileDatabase::class.java, "music_list_database")
					// EXAMPLE MIGRATION -> .addMigrations(MIGRATION_1_2)
					.fallbackToDestructiveMigration(true)
					.build()
					.also {
						Instance = it
					}
			}
		}
	}
}