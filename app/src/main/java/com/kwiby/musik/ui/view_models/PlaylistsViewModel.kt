package com.kwiby.musik.ui.view_models

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kwiby.musik.data.data_classes.MusicDetails
import com.kwiby.musik.data.data_classes.Playlist
import com.kwiby.musik.data.data_classes.PlaylistWithSongCount
import com.kwiby.musik.data.repositories.music_list.OfflineMusicListRepository
import com.kwiby.musik.data.repositories.playlists.OfflinePlaylistsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private const val LOG_TAG = "PlaylistsViewModel"

class PlaylistsViewModel(
	private val playlistsRepo: OfflinePlaylistsRepository,
	musicListRepo: OfflineMusicListRepository
) : ViewModel() {
	var isLoadingPlaylists = mutableStateOf(true)
		private set
	var isLoadingSongs = mutableStateOf(true)
		private set
	var isLoadingAllMusic = mutableStateOf(true)
		private set

	val playlists: StateFlow<List<PlaylistWithSongCount>> =
		playlistsRepo.getAllPlaylistsWithSongCounts()
			.onEach { isLoadingPlaylists.value = false }
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = emptyList()
			)

	var openedPlaylistId = mutableStateOf<Long?>(null)
		private set
	val openedPlaylist: StateFlow<PlaylistWithSongCount?> =
		combine(playlists, snapshotFlow { openedPlaylistId.value }) { list, id ->
			id?.let { playlistId -> list.find { it.playlist.id == playlistId } }
		}.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5_000),
			initialValue = null
		)
	var songs: StateFlow<List<MusicDetails>>? = null
		private set
	val allMusic: StateFlow<List<MusicDetails>> =
		musicListRepo.getAllAudioFilesStream()
			.map { list -> list.map { it.toMusicDetails() } }
			.onEach { isLoadingAllMusic.value = false }
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = emptyList()
			)

	var isAddingPlaylist = mutableStateOf(false)
		private set
	var isAddingSongs = mutableStateOf(false)
		private set

	var selectedPlaylists = mutableStateOf<List<Playlist>>(emptyList())
		private set
	var selectedSongIds = mutableStateOf<List<Long>>(emptyList())
		private set

	val isInPlaylistSelectionMode: Boolean
		get() = selectedPlaylists.value.isNotEmpty()
	val isInSongSelectionMode: Boolean
		get() = selectedSongIds.value.isNotEmpty()
	var isInPlaylistMoveMode = mutableStateOf(false)
		private set
	var isInSongMoveMode = mutableStateOf(false)
		private set
	var isInEditPlaylistMode = mutableStateOf(false)
		private set
	var isInEditMetadataMode = mutableStateOf(false)
		private set


	private fun updateSelectedPlaylists(playlist: Playlist) {
		selectedPlaylists.value = if (playlist in selectedPlaylists.value) {
			selectedPlaylists.value - playlist
		} else {
			selectedPlaylists.value + playlist
		}
	}

	private fun updateSelectedSongIds(songId: Long) {
		selectedSongIds.value = if (songId in selectedSongIds.value) {
			selectedSongIds.value - songId
		} else {
			selectedSongIds.value + songId
		}
	}

	private fun setEditMetadataMode(bool: Boolean) {
		isInEditMetadataMode.value = bool
	}

	private fun openPlaylist(playlistId: Long) {
		isLoadingSongs.value = true

		openedPlaylistId.value = playlistId
		songs = playlistsRepo.getAllSongsInPlaylist(playlistId)
			.map { list -> list.map { it.toMusicDetails() } }
			.onEach { isLoadingSongs.value = false }
			.stateIn(
				scope = viewModelScope,
				started = SharingStarted.WhileSubscribed(5_000),
				initialValue = emptyList()
			)
	}

	fun disableAllModes() {
		selectedPlaylists.value = emptyList()
		selectedSongIds.value = emptyList()
		isInPlaylistMoveMode.value = false
		isInSongMoveMode.value = false
		isInEditPlaylistMode.value = false
		isInEditMetadataMode.value = false
	}

	fun closePlaylist() {
		openedPlaylistId.value = null
		songs = null
	}

	fun setPlaylistMoveMode(newBool: Boolean) {
		disableAllModes()
		isInPlaylistMoveMode.value = newBool
	}

	fun setSongMoveMode(newBool: Boolean) {
		disableAllModes()
		isInSongMoveMode.value = newBool
	}

	fun toggleEditPlaylistMode() {
		val curModeBool = isInEditPlaylistMode.value
		disableAllModes()
		isInEditPlaylistMode.value = !curModeBool
	}

	fun setIsAddingPlaylist(newBool: Boolean) {
		isAddingPlaylist.value = newBool
	}

	fun setIsAddingSongs(newBool: Boolean) {
		isAddingSongs.value = newBool
	}

	fun addPlaylistButton(name: String) {
		viewModelScope.launch {
			playlistsRepo.createPlaylist(name.trim())
		}
	}

	fun renamePlaylistButton(playlistId: Long, newName: String) {
		viewModelScope.launch {
			playlistsRepo.renamePlaylist(playlistId, newName.trim())
		}
	}

	fun removePlaylistsButton() {
		viewModelScope.launch {
			playlistsRepo.deletePlaylists(selectedPlaylists.value)
		}
		disableAllModes()
	}

	fun handleBack() {
		disableAllModes()
	}

	fun handlePlaylistTap(playlist: Playlist) {
		if (isInPlaylistSelectionMode) {
			updateSelectedPlaylists(playlist)
		} else {
			openPlaylist(playlist.id)
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

	fun reorderSongsInPlaylist(newIdOrder: List<Long>) {
		if (openedPlaylist.value == null) {
			Log.e(LOG_TAG, "The opened playlist is null")
			return
		}

		viewModelScope.launch {
			playlistsRepo.reorderSongsInPlaylist(openedPlaylist.value!!.playlist.id, newIdOrder)
		}
	}

	fun handleSongTap(id: Long, onPlayMusic: () -> Unit) {
		if (isInSongSelectionMode) {
			updateSelectedSongIds(id)
		} else {
			onPlayMusic()
		}
	}

	fun handleSongHold(id: Long) {
		updateSelectedSongIds(id)
	}

	fun enterEditMetadataModeButton() {
		disableAllModes()
		setEditMetadataMode(true)
	}

	fun exitEditMetadataModeButton() {
		setEditMetadataMode(false)
	}

	fun editMetadataButton(navViewModel: NavViewModel, contentUri: Uri, id: Long) {
		navViewModel.navToScreen(Screen.EditMetadata(contentUri, id))
	}

	fun confirmSongMoveButton(
		playbackViewModel: PlaybackViewModel,
		localOrder: List<MusicDetails>
	) {
		if (openedPlaylist.value == null || songs == null) {
			Log.e(LOG_TAG, "Opened playlist and/or songs is null")
			return
		}

		playbackViewModel.setQueue(
			items = localOrder.map { it.toMediaItem() },
			queueSource = PlaybackViewModel.QueueSource(
				playbackSource = PlaybackViewModel.PlaybackSource.PLAYLIST,
				sourceId = openedPlaylistId.value!!
			),
			isStarting = false,
			isReordering = true
		)
	}

	fun addSongsToPlaylist(
		setQueueFunc: (List<MusicDetails>) -> Unit,
		playlistId: Long,
		songIds: List<Long>
	) {
		val currentSongs = songs?.value ?: emptyList()
		val currentIds = currentSongs.map { it.id }.toSet()
		val allMusicById = allMusic.value.associateBy { it.id }
		val addedSongs = songIds
			.filter { it !in currentIds }
			.mapNotNull { allMusicById[it] }
		val newQueueItems = currentSongs + addedSongs
		setQueueFunc(newQueueItems)

		viewModelScope.launch {
			playlistsRepo.addSongsToPlaylist(playlistId, songIds)
		}
	}

	fun addSongsToPlaylists(playlistIds: List<Long>, songIds: List<Long>) {
		viewModelScope.launch {
			playlistsRepo.addSongsToPlaylists(playlistIds, songIds)
		}
	}

	fun removeSongsFromPlaylist(
		removeFromQueueFunc: (List<Long>) -> Unit,
		playlistId: Long,
		songs: List<Long>
	) {
		removeFromQueueFunc(songs)
		viewModelScope.launch {
			playlistsRepo.removeSongsFromPlaylist(playlistId, songs)
		}
	}

	fun resetPlaylists() {
		disableAllModes()
		selectedPlaylists.value = emptyList()
	}

	fun resetSongs() {
		openedPlaylistId.value = null
		songs = null
		selectedSongIds.value = emptyList()
	}
}