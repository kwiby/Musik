package com.kwiby.musik.data.databases

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// EXAMPLE MIGRATION -> Check AudioFileDatabase for example code
val MIGRATION_1_2 = object : Migration(1, 2) {
	override fun migrate(db: SupportSQLiteDatabase) {
		db.execSQL(
			"ALTER TABLE music_list ADD COLUMN playCount INTEGER NOT NULL DEFAULT 0"
		)
	}
}