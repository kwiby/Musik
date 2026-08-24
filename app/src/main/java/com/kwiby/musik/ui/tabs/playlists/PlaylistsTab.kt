package com.kwiby.musik.ui.tabs.playlists

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kwiby.musik.data.data_classes.playlist.Playlist
import com.kwiby.musik.ui.floating_ui.dialogs.AddPlaylistDialog
import com.kwiby.musik.ui.floating_ui.dialogs.RenamePlaylistDialog
import com.kwiby.musik.ui.floating_ui.dialogs.add_songs_dialog.AddSongsDialog
import com.kwiby.musik.ui.tabs.playlists.pages.playlists.PlaylistsPage
import com.kwiby.musik.ui.tabs.playlists.pages.songs.SongsPage
import com.kwiby.musik.ui.view_models.NavViewModel
import com.kwiby.musik.ui.view_models.PlaybackViewModel
import com.kwiby.musik.ui.view_models.PlaylistsViewModel

@Composable
fun PlaylistsTab(
	playlistsViewModel: PlaylistsViewModel,
	playbackViewModel: PlaybackViewModel,
	navViewModel: NavViewModel
) {
	val isPlaylistOpen = playlistsViewModel.openedPlaylistId.value != null
	val isAddingPlaylist by playlistsViewModel.isAddingPlaylist
	val isAddingSongs by playlistsViewModel.isAddingSongs

	var selectedPlaylistToRename by remember { mutableStateOf<Playlist?>(null) }

	// --===-- Dialogs --===--
	when {
		isAddingPlaylist -> AddPlaylistDialog(
			playlistsViewModel = playlistsViewModel,
			onDismiss = { playlistsViewModel.setIsAddingPlaylist(false) }
		)
		isAddingSongs -> AddSongsDialog(
			playlistsViewModel = playlistsViewModel,
			playbackViewModel = playbackViewModel,
			onDismiss = { playlistsViewModel.setIsAddingSongs(false) }
		)
		selectedPlaylistToRename != null -> RenamePlaylistDialog(
			playlistsViewModel = playlistsViewModel,
			playlist = selectedPlaylistToRename!!,
			onDismiss = { selectedPlaylistToRename = null }
		)
	}

	if (!isPlaylistOpen) {
		PlaylistsPage(
			playlistsViewModel = playlistsViewModel,
			changeSelectedPlaylistToRename = { selectedPlaylistToRename = it }
		)
	} else {
		SongsPage(
			playlistsViewModel = playlistsViewModel,
			playbackViewModel = playbackViewModel,
			navViewModel = navViewModel,
			changeSelectedPlaylistToRename = { selectedPlaylistToRename = it }
		)
	}
}