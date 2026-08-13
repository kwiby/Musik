package com.kwiby.musik.ui.view_models

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwiby.musik.data.data_classes.AudioFile
import com.kwiby.musik.data.data_classes.Playlist
import com.kwiby.musik.data.data_classes.PlaylistWithSongCount
import com.kwiby.musik.data.repositories.playlists.OfflinePlaylistsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PlaylistsViewModel(
	private val playlistsRepo: OfflinePlaylistsRepository
) : ViewModel() {
	var isLoading = mutableStateOf(true)
		private set

	val playlists: StateFlow<List<PlaylistWithSongCount>> =
		playlistsRepo.getAllPlaylistsWithSongCounts()
			.onEach { isLoading.value = false }
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = emptyList()
			)
	var songs = mutableStateOf<List<AudioFile>?>(null)
		private set

	var isAddingPlaylist = mutableStateOf(false)
		private set

	var selectedPlaylists = mutableStateOf<List<Playlist>>(emptyList())
		private set
	var selectedSongs = mutableStateOf<List<AudioFile>>(emptyList())
		private set

	val isInPlaylistSelectionMode: Boolean
		get() = selectedPlaylists.value.isNotEmpty()
	var isInMoveMode = mutableStateOf(false)
		private set
	var isInEditPlaylistMode = mutableStateOf(false)
		private set


	private fun disableAllModes() {
		selectedPlaylists.value = emptyList()
		isInMoveMode.value = false
		isInEditPlaylistMode.value = false
	}

	private fun updateSelectedPlaylists(playlist: Playlist) {
		selectedPlaylists.value = if (playlist in selectedPlaylists.value) {
			selectedPlaylists.value - playlist
		} else {
			selectedPlaylists.value + playlist
		}
	}

	fun changeMoveMode(newBool: Boolean) {
		disableAllModes()
		isInMoveMode.value = newBool
	}

	fun toggleEditPlaylistMode() {
		val curModeBool = isInEditPlaylistMode.value
		disableAllModes()
		isInEditPlaylistMode.value = !curModeBool
	}

	fun changeIsAddingPlaylist(newBool: Boolean) {
		isAddingPlaylist.value = newBool
	}

	fun addPlaylistButton(name: String) {
		viewModelScope.launch(Dispatchers.IO) {
			playlistsRepo.createPlaylist(name.trim())
		}
	}

	fun removePlaylistsButton() {
		viewModelScope.launch {
			playlistsRepo.deletePlaylists(selectedPlaylists.value)
		}
		disableAllModes()
	}

	fun handlePlaylistTap(playlist: Playlist) {
		if (isInPlaylistSelectionMode) {
			updateSelectedPlaylists(playlist)
		} else {
			/* onOpenPlaylist() */
		}
	}

	fun handlePlaylistHold(playlist: Playlist) {
		updateSelectedPlaylists(playlist)
	}

	fun reorderPlaylists(newOrder: List<Playlist>) {
		viewModelScope.launch {
			playlistsRepo.reorderPlaylists(newOrder)
		}
	}

	fun reset() {
		songs.value = null
		disableAllModes()
		selectedPlaylists.value = emptyList()
		selectedSongs.value = emptyList()
	}
}