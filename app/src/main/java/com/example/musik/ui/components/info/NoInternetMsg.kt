package com.example.musik.ui.components.info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.musik.R

@Composable
fun NoInternetMsg() {
	Column(
		verticalArrangement = Arrangement.Top,
		horizontalAlignment = Alignment.CenterHorizontally,
		modifier = Modifier.offset(y = dimensionResource(R.dimen.no_internet_offset))
	) {
		Text(
			text = stringResource(R.string.no_internet_msg),
			style = MaterialTheme.typography.titleSmall,
			color = MaterialTheme.colorScheme.onSecondary
		)
	}
}