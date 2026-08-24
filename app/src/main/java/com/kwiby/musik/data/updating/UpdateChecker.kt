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
		Log.d("debug", "1: start")
		val lastCheckTime = dataStoreManager.updateLastCheckTime.first()
		Log.d("debug", "2: lastCheckTime=$lastCheckTime")
		val curTime = System.currentTimeMillis()
		if (!doForce && curTime - lastCheckTime < minCheckTime) return@withContext null
		Log.d("debug", "3: passed throttle check")

		try {
			val releases = api.getReleases(owner, repo)
			Log.d("debug", "4: got ${releases.size} releases")
			val release = releases.firstOrNull() ?: return@withContext null
			Log.d("debug", "5: release=$release")
			//if (release.isPreRelease) { return@withContext null }

			dataStoreManager.setUpdateLastCheckTime(curTime)
			Log.d("debug", "6: saved check time")

			val latestVersion = release.tagName.removePrefix("v").substringBefore("-")
			Log.d("debug", "7: latestVersion=$latestVersion")
			if (!isNewVersionHigher(latestVersion)) { return@withContext null }
			Log.d("debug", "8: version is higher")

			val apkAsset = findApkAsset(release.assets) ?: return@withContext null
			Log.d("debug", "9: apkAsset=$apkAsset")

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