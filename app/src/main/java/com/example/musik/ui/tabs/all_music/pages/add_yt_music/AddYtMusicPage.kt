package com.example.musik.ui.tabs.all_music.pages.add_yt_music

import android.content.Context
import android.net.ConnectivityManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musik.R
import com.example.musik.ui.components.CustomIconButton
import com.example.musik.ui.components.LoadingIndicator
import com.example.musik.ui.misc.folder_manager.LocalFolderManager
import com.example.musik.ui.tabs.all_music.pages.add_yt_music.components.DownloadLocationContainer
import com.example.musik.ui.tabs.all_music.pages.add_yt_music.components.downloader_container.DownloadContainer
import com.example.musik.ui.tabs.all_music.pages.add_yt_music.components.info.NoDownloadLocationMsg
import com.example.musik.ui.tabs.all_music.pages.add_yt_music.components.info.NoInternetMsg
import com.example.musik.ui.view_models.AddYtMusicViewModel
import com.example.musik.ui.view_models.observeConnectivity
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddYtMusicPage(
	addYtMusicViewModel: AddYtMusicViewModel,
	onBackToMusicList: () -> Unit
) {
	val downloadLocation by addYtMusicViewModel.downloadLocation.collectAsStateWithLifecycle()

	val context = LocalContext.current
	val folderManager = LocalFolderManager.current

	val connectivityManager = remember {
		context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
	}
	val isConnected by produceState(
		initialValue = connectivityManager.activeNetwork != null,
		key1 = connectivityManager,
		producer = {
			connectivityManager.observeConnectivity().collectLatest {
				value = it
			}
		}
	)

	val hasValidFolderPerms by addYtMusicViewModel.hasValidFolderPerms.collectAsStateWithLifecycle()
	val lifecycleOwner = LocalLifecycleOwner.current
	DisposableEffect(lifecycleOwner) {
		val observer = LifecycleEventObserver { _, event ->
			if (event == Lifecycle.Event.ON_RESUME) {
				addYtMusicViewModel.checkFolderPerms(folderManager)
			}
		}
		lifecycleOwner.lifecycle.addObserver(observer)

		onDispose {
			lifecycleOwner.lifecycle.removeObserver(observer)
		}
	}
	LaunchedEffect(downloadLocation) {
		if (downloadLocation != null) {
			addYtMusicViewModel.checkFolderPerms(folderManager)
		}
	}

	DisposableEffect(Unit) {
		onDispose {
			addYtMusicViewModel.resetAddYtMusic()
		}
	}
	BackHandler(true) {
		onBackToMusicList()
	}

	Column(
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		Row(
			horizontalArrangement = Arrangement.Start,
			verticalAlignment = Alignment.CenterVertically
		) {
			Spacer(Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))

			// --===--  Back Button  --===--
			CustomIconButton(
				iconImageVector = Icons.AutoMirrored.Rounded.ArrowBack,
				contentDescription = stringResource(R.string.back_button)
			) {
				onBackToMusicList()
			}

			// --===--  Download Location Container  --===--
			DownloadLocationContainer(addYtMusicViewModel)
		}

		Spacer(Modifier.height(dimensionResource(R.dimen.buttons_vertical_padding)))

		if (downloadLocation == null || hasValidFolderPerms == null) {
			// --===--  Loading Circle  --===--
			LoadingIndicator()
		} else if (downloadLocation!!.isEmpty()) {
			// --===--  No Download Location Message  --===--
			NoDownloadLocationMsg()
		} else if (!isConnected) {
			// --===--  No Internet Connection Message  --===--
			NoInternetMsg()
		} else {
			DownloadContainer(addYtMusicViewModel)
		}
	}
}