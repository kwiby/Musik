package com.kwiby.musik.data.data_classes.updating

import com.google.gson.annotations.SerializedName

data class GithubAsset(
	val name: String,
	@SerializedName("browser_download_url") val downloadUrl: String,
	val size: Long
)
