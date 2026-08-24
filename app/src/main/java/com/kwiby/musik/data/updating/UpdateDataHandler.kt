package com.kwiby.musik.data.updating

import android.os.Build
import com.kwiby.musik.BuildConfig
import com.kwiby.musik.data.data_classes.updating.GithubAsset

fun isNewVersionHigher(newVersion: String): Boolean {
	val newVersionParts = newVersion.split(".").map { it.toIntOrNull() ?: 0 }
	val curVersionParts = BuildConfig.VERSION_NAME.split(".").map { it.toIntOrNull() ?: 0 }

	for (i in 0 until 3) {
		val newVersionVal = newVersionParts.getOrElse(i) { 0 }
		val curVersionVal = curVersionParts.getOrElse(i) { 0 }

		if (newVersionVal != curVersionVal) {
			return newVersionVal > curVersionVal
		}
	}

	return false
}

fun findApkAsset(assets: List<GithubAsset>): GithubAsset? {
	val apkAssets = assets.filter { it.name.endsWith(".apk") }

	for (abi in Build.SUPPORTED_ABIS) {
		apkAssets.find { asset ->
			val nameWithoutExt = asset.name.removeSuffix(".apk")
			nameWithoutExt.endsWith("_$abi")
		}?.let {
			return it
		}
	}

	apkAssets.find {
		it.name.contains("universal", ignoreCase = true)
	}?.let {
		return it
	}

	return apkAssets.firstOrNull()
}