package com.example.musik.ui.screens.all_music

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun AllMusicScreen(
	onNavToAddSongsScreen: () -> Unit
) {
	Column(
		horizontalAlignment = Alignment.CenterHorizontally
	) {
		// ---===---  All Buttons  ---===---
		Row(
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			// ---===---  Editing Buttons  ---===---
			Row {

			}

			// ---===---  Adding Buttons  ---===---
			Row{
				Button(
					onClick = { onNavToAddSongsScreen }
				) {

				}
			}
		}

		// ---===---  Music List  ---===---
		LazyColumn() {

		}
	}
}