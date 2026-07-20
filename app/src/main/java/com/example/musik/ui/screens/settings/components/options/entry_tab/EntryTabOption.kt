package com.example.musik.ui.screens.settings.components.options.entry_tab

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.musik.R
import com.example.musik.ui.screens.settings.components.options.OptionButton
import com.example.musik.ui.screens.settings.components.options.OptionHeader
import com.example.musik.ui.view_models.NavViewModel
import com.example.musik.ui.view_models.Tab

@Composable
fun EntryTabOption(
	navViewModel: NavViewModel
) {
	val entryPointTab by navViewModel.entryTab.collectAsStateWithLifecycle()

	OptionHeader(stringResource(R.string.settings_header_entry_tab))
	Spacer(Modifier.height(dimensionResource(R.dimen.option_header_bottom_padding)))

	Row {
		// --===--  All Music  --===--
		OptionButton(
			text = stringResource(R.string.all_music),
			modifier = Modifier.weight(1f),
			isSelected = entryPointTab == Tab.ALL_MUSIC
		) {
			navViewModel.setEntryTab(Tab.ALL_MUSIC)
		}

		// --===--  Playlists  --===--
		OptionButton(
			text = stringResource(R.string.playlists),
			modifier = Modifier.weight(1f),
			isSelected = entryPointTab == Tab.PLAYLISTS
		) {
			navViewModel.setEntryTab(Tab.PLAYLISTS)
		}

		// --===--  Statistics  --===--
		OptionButton(
			text = stringResource(R.string.stats),
			modifier = Modifier.weight(1f),
			isSelected = entryPointTab == Tab.STATS
		) {
			navViewModel.setEntryTab(Tab.STATS)
		}
	}
}