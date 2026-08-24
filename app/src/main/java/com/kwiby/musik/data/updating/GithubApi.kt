package com.kwiby.musik.data.updating

import com.kwiby.musik.data.data_classes.updating.GithubRelease
import retrofit2.http.GET
import retrofit2.http.Path

interface GithubApi {
	@GET("repos/{owner}/{repo}/releases")
	suspend fun getReleases(
		@Path("owner") owner: String,
		@Path("repo") repo: String
	): List<GithubRelease>
}