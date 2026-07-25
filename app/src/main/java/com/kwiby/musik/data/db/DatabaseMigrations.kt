package com.kwiby.musik.data.db

/* EXAMPLE MIGRATION -> Check AudioFileDatabase for example code
val MIGRATION_1_2 = object : Migration(1, 2) {
	override fun migrate(db: SupportSQLiteDatabase) {
		db.execSQL("""
            CREATE TABLE audio_file_new (
                id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                artist TEXT NOT NULL,
                filePath TEXT NOT NULL
            )
        """)
		db.execSQL("""
            INSERT INTO audio_file_new (id, title, artist, filePath)
            SELECT id, title, artist, filePath FROM AudioFile
        """)
		db.execSQL("DROP TABLE AudioFile")
		db.execSQL("ALTER TABLE audio_file_new RENAME TO AudioFile")
	}
}
 */