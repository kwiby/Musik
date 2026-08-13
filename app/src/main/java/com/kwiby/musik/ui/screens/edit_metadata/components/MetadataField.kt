package com.kwiby.musik.ui.screens.edit_metadata.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import com.kwiby.musik.ui.components.customTextSelectionColours

@Composable
fun MetadataField(
	query: String,
	onValueChange: (String) -> Unit,
	placeholderText: String,
	keyboardType: KeyboardType = KeyboardType.Text
) {
	CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColours()) {
		TextField(
			value = query,
			onValueChange = { onValueChange(it) },
			textStyle = MaterialTheme.typography.bodyLarge,
			placeholder = {
				Text(
					text = placeholderText,
					style = MaterialTheme.typography.bodyLarge
				)
			},
			singleLine = true,
			shape = MaterialTheme.shapes.small,
			colors = TextFieldDefaults.colors(
				focusedLeadingIconColor = MaterialTheme.colorScheme.onSecondary,
				cursorColor = MaterialTheme.colorScheme.onSecondary,
				focusedContainerColor = MaterialTheme.colorScheme.secondaryFixed,
				unfocusedContainerColor = MaterialTheme.colorScheme.secondaryFixed,
				focusedTextColor = MaterialTheme.colorScheme.onSecondary,
				unfocusedTextColor = MaterialTheme.colorScheme.onSecondary,
				focusedIndicatorColor = Color.Transparent,
				unfocusedIndicatorColor = Color.Transparent
			),
			keyboardOptions = KeyboardOptions(
				imeAction = ImeAction.Done,
				keyboardType = keyboardType
			),
			modifier = Modifier.fillMaxWidth()
		)
	}
}