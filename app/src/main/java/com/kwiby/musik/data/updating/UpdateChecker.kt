package com.kwiby.musik.data.updating

import android.util.Log
import com.kwiby.musik.data.data_classes.updating.UpdateInfo
import com.kwiby.musik.data.datastore.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val LOG_TAG = "UpdateChecker"

class UpdateChecker(
	private val dataStoreManager: DataStoreManager
) {
	private val minCheckTime = TimeUnit.HOURS.toMillis(6)

	private val owner = "kwiby"
	private val repo = "Musik"

	private val api: GithubApi by lazy {
		Retrofit.Builder()
			.baseUrl("https://api.github.com/")
			.addConverterFactory(GsonConverterFactory.create())
			.build()
			.create(GithubApi::class.java)
	}

	suspend fun checkForUpdates(
		doForce: Boolean = false
	): UpdateInfo? = withContext(Dispatchers.IO) {
		val lastCheckTime = dataStoreManager.updateLastCheckTime.first()
		val curTime = System.currentTimeMillis()
		if (!doForce && curTime - lastCheckTime < minCheckTime) return@withContext null

		try {
			val releases = api.getReleases(owner, repo)
			val release = releases.firstOrNull() ?: return@withContext null
			//if (release.isPreRelease) { return@withContext null }

			dataStoreManager.setUpdateLastCheckTime(curTime)

			val latestVersion = release.tagName.removePrefix("v").substringBefore("-")
			if (!isNewVersionHigher(latestVersion)) { return@withContext null }

			val apkAsset = findApkAsset(release.assets) ?: return@withContext null

			return@withContext UpdateInfo(
				versionName = latestVersion,
				apkUrl = apkAsset.downloadUrl,
				changelog = release.body ?: "No changelog provided"
			)
		} catch (e: Exception) {
			Log.e(LOG_TAG, "Update check failed", e)
			return@withContext null
		}
	}
}