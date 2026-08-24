package com.kwiby.musik.data.data_classes.updating

import com.google.gson.annotations.SerializedName

data class GithubRelease(
	@SerializedName("tag_name") val tagName: String,
	val name: String?,
	val body: String?,
	val assets: List<GithubAsset>,
	@SerializedName("prerelease") val isPreRelease: Boolean
)