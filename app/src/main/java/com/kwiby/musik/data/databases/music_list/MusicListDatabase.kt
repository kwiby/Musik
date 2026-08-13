package com.kwiby.musik.data.databases.music_list

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kwiby.musik.data.daos.music_list.MusicListDao
import com.kwiby.musik.data.daos.music_stats.MusicStatsDao
import com.kwiby.musik.data.daos.playlists.PlaylistDao
import com.kwiby.musik.data.data_classes.AudioFile
import com.kwiby.musik.data.data_classes.AudioFileStats
import com.kwiby.musik.data.data_classes.Playlist
import com.kwiby.musik.data.data_classes.PlaylistSong

@Database(
	entities = [
		AudioFile::class,
		AudioFileStats::class,
		Playlist::class,
		PlaylistSong::class
    ],
	version = 1, // EXAMPLE MIGRATION -> Make sure to bump this value up for each db migration
	exportSchema = false
)
abstract class MusicListDatabase: RoomDatabase() {
	abstract fun musicListDao(): MusicListDao
	abstract fun musicStatsDao(): MusicStatsDao
	abstract fun playlistDao(): PlaylistDao

	companion object {
		@Volatile
		private var Instance: MusicListDatabase? = null

		fun getDatabase(context: Context): MusicListDatabase {
			// If the Instance is not null, return it, otherwise create a new database instance.
			return Instance ?: synchronized(this) {
				Room.databaseBuilder(context, MusicListDatabase::class.java, "musik_db")
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