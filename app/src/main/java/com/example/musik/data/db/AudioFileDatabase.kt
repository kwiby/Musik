package com.example.musik.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.musik.data.data_classes.AudioFile

@Database(entities = [AudioFile::class], version = 1, exportSchema = false)
abstract class AudioFileDatabase: RoomDatabase() {
	abstract fun audioFileDao(): AudioFileDao

	companion object {
		@Volatile
		private var Instance: AudioFileDatabase? = null

		fun getDatabase(context: Context): AudioFileDatabase {
			// If the Instance is not null, return it, otherwise create a new database instance.
			return Instance ?: synchronized(this) {
				Room.databaseBuilder(context, AudioFileDatabase::class.java, "audio_file_database")
					.fallbackToDestructiveMigration(true).build().also {
						Instance = it
					}
			}
		}
	}
}