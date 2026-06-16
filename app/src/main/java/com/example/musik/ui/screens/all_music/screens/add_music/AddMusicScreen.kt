package com.example.musik.ui.screens.all_music.screens.add_music

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.AddBox
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.musik.R
import com.example.musik.ui.components.CustomIconButton

@Composable
fun AddMusicScreen(
	onBackToMusicListButtonClick: () -> Unit
) {
	// ---===---  All Buttons  ---===---
	Row(
		horizontalArrangement = Arrangement.SpaceBetween,
		modifier = Modifier.fillMaxWidth()
	) {
		// ---===---  Back Button  ---===---
		Row {
			Spacer(modifier = Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))
			CustomIconButton(
				{ onBackToMusicListButtonClick() },
				Icons.AutoMirrored.Rounded.ArrowBack,
				stringResource(R.string.back_button)
			)
		}

		// ---===---  Selection Buttons  ---===---
		Row {
			CustomIconButton(
				{  },
				Icons.Rounded.Refresh,
				stringResource(R.string.refresh_button)
			)
			Spacer(modifier = Modifier.width(dimensionResource(R.dimen.zero)))
			CustomIconButton(
				{  },
				Icons.Outlined.AddBox,
				stringResource(R.string.add_selected_music_button)
			)
			Spacer(modifier = Modifier.width(dimensionResource(R.dimen.buttons_horizontal_padding)))
		}
	}

	Spacer(modifier = Modifier.height(dimensionResource(R.dimen.buttons_vertical_padding)))

	// ---===---  Music List  ---===---
	LazyColumn {

	}
}