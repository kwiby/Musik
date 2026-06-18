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
			text = stringResource(R.string.add_music_search_bar)
		) },
		leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
		singleLine = true,
		colors = TextFieldDefaults.colors(
			focusedContainerColor = MaterialTheme.colorScheme.secondary,
			unfocusedContainerColor = MaterialTheme.colorScheme.secondary,
			focusedLeadingIconColor = MaterialTheme.colorScheme.onSecondary,
			focusedIndicatorColor = MaterialTheme.colorScheme.onSecondary,
			cursorColor = MaterialTheme.colorScheme.onSecondary,
			focusedTextColor = MaterialTheme.colorScheme.onSecondary,
			unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
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