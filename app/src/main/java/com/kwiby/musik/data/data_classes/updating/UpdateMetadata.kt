package com.kwiby.musik.data.data_classes.updating

data class UpdateMetadata(
	val versionCode: Long,
	val versionName: String,
	val apkUrl: String,
	val changelog: String
)
