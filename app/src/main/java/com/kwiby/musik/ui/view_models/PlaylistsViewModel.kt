package com.kwiby.musik.ui.view_models

import androidx.lifecycle.ViewModel
import com.kwiby.musik.data.repositories.playlists.OfflinePlaylistsRepository

class PlaylistsViewModel(
	private val playlistsRepo: OfflinePlaylistsRepository
) : ViewModel() {

}