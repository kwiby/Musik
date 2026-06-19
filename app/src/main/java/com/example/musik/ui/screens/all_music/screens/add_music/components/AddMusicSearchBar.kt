package com.example.musik.ui.screens.all_music.screens.add_music.components

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.example.musik.R
import com.example.musik.ui.view_models.AddMusicViewModel

@Composable
fun AddMusicSearchbar(
	viewModel: AddMusicViewModel,
	modifier: Modifier = Modifier
) {
	val searchQuery by viewModel.searchQuery.collectAsState()
	val focusManager = LocalFocusManager.current

	TextField(
		value = searchQuery,
		onValueChange = { viewModel.onSearchQueryChange(it) },
		placeholder = { Text(
			text = stringResource(R.string.add_music_search_bar),
			style = MaterialTheme.typography.bodyLarge
		) },
		leadingIcon =
			{
				Icon(Icons.Default.Search,
				contentDescription = stringResource(R.string.add_music_search_bar))
			},
		singleLine = true,
		shape = MaterialTheme.shapes.extraLarge,
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
		keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
		keyboardActions = KeyboardActions(
			onSearch = {
				focusManager.clearFocus()
			}
		),
		modifier = modifier
	)
}