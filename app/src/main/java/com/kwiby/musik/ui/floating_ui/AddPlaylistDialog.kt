package com.kwiby.musik.ui.floating_ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.window.DialogWindowProvider
import com.kwiby.musik.R
import com.kwiby.musik.ui.components.customTextSelectionColours
import com.kwiby.musik.ui.view_models.PlaylistsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPlaylistDialog(
	playlistsViewModel: PlaylistsViewModel,
	onDismiss: () -> Unit
) {
	val focusRequester = remember { FocusRequester() }
	val query = remember { mutableStateOf("") }
	val isQueryNotBlank = query.value.isNotBlank()

	LaunchedEffect(Unit) {
		focusRequester.requestFocus()
	}


	BasicAlertDialog(onDismissRequest = onDismiss) {
		val view = LocalView.current
		SideEffect {
			(view.parent as? DialogWindowProvider)?.window?.setDimAmount(0.4f)
		}

		Surface(
			shape = MaterialTheme.shapes.small,
			color = MaterialTheme.colorScheme.secondary,
			shadowElevation = dimensionResource(R.dimen.x_small_padding)
		) {
			Column(
				modifier = Modifier.padding(
					top = dimensionResource(R.dimen.medium_padding),
					start = dimensionResource(R.dimen.medium_padding),
					end = dimensionResource(R.dimen.medium_padding)
				)
			) {
				Text(
					text = stringResource(R.string.add_a_playlist),
					color = MaterialTheme.colorScheme.onSecondary,
					style = MaterialTheme.typography.bodyLarge
				)

				Spacer(Modifier.height(dimensionResource(R.dimen.medium_padding)))

				CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColours()) {
					TextField(
						value = query.value,
						onValueChange = { query.value = it },
						textStyle = MaterialTheme.typography.bodyLarge,
						placeholder = {
							Text(
								text = stringResource(R.string.playlist_query_placeholder),
								style = MaterialTheme.typography.bodyLarge
							)
						},
						singleLine = true,
						shape = MaterialTheme.shapes.small,
						colors = TextFieldDefaults.colors(
							focusedLeadingIconColor = MaterialTheme.colorScheme.onSecondary,
							cursorColor = MaterialTheme.colorScheme.onSecondary,
							focusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
							unfocusedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
							focusedTextColor = MaterialTheme.colorScheme.onSecondary,
							unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
							focusedIndicatorColor = Color.Transparent,
							unfocusedIndicatorColor = Color.Transparent
						),
						keyboardOptions = KeyboardOptions(
							imeAction = ImeAction.Done,
							keyboardType = KeyboardType.Text
						),
						modifier = Modifier
							.fillMaxWidth()
							.focusRequester(focusRequester)
					)
				}

				Spacer(Modifier.height(dimensionResource(R.dimen.xx_small_padding)))

				Row(
					modifier = Modifier.fillMaxWidth(),
					horizontalArrangement = Arrangement.End
				) {
					TextButton(
						onClick = {
							onDismiss()
						},
						shape = MaterialTheme.shapes.large
					) {
						Text(
							text = stringResource(R.string.cancel_playlist_creation),
							color = MaterialTheme.colorScheme.outline,
							style = MaterialTheme.typography.bodyLarge
						)
					}

					TextButton(
						onClick = {
							if (isQueryNotBlank) {
								playlistsViewModel.addPlaylistButton(query.value)
								onDismiss()
							}
						},
						enabled = isQueryNotBlank,
						shape = MaterialTheme.shapes.large
					) {
						Text(
							text = stringResource(R.string.confirm_playlist_creation),
							color = if (isQueryNotBlank) {
								MaterialTheme.colorScheme.outline
							} else {
								MaterialTheme.colorScheme.onSurface
							},
							style = MaterialTheme.typography.bodyLarge
						)
					}
				}
			}
		}
	}
}