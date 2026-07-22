package com.example.musik.crash_handling

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.sp
import com.example.musik.R
import com.example.musik.ui.components.MusikTopAppBar
import com.example.musik.ui.screens.settings.components.options.OptionButton
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun CrashScreen(
	crashLog: String,
	onRestart: () -> Unit
) {
	val context = LocalContext.current

	Scaffold(
		containerColor = MaterialTheme.colorScheme.background,
		topBar = { MusikTopAppBar(null) }
	) { innerPadding ->
		Column(
			modifier = Modifier.padding(innerPadding).fillMaxSize(),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			// --===--  Crash Title  --===--
			Text(
				text = stringResource(R.string.crash_title),
				color = MaterialTheme.colorScheme.onSecondary,
				style = MaterialTheme.typography.titleSmall.copy(
					fontSize = 20.sp
				)
			)

			// --===--  Crash Log --===--
			Box(
				modifier = Modifier
					.weight(1f)
					.fillMaxWidth()
					.padding(
						vertical = dimensionResource(R.dimen.medium_padding),
						horizontal = dimensionResource(R.dimen.medium_padding)
					)
					.background(
						color = MaterialTheme.colorScheme.secondary,
						shape = MaterialTheme.shapes.small
					)
					.verticalScroll(rememberScrollState())
			) {
				Text(
					text = crashLog,
					modifier = Modifier.padding(dimensionResource(R.dimen.small_padding)),
					color = MaterialTheme.colorScheme.onSecondary,
					style = MaterialTheme.typography.bodyMedium
				)
			}

			// --===--  Buttons  --===--
			Column(
				modifier = Modifier.padding(
					start = dimensionResource(R.dimen.medium_padding),
					end = dimensionResource(R.dimen.medium_padding),
					bottom = dimensionResource(R.dimen.medium_padding)
				)
			) {
				Row {
					// --===--  Copy Crash Log Button  --===--
					OptionButton(
						text = stringResource(R.string.copy_crash_log),
						modifier = Modifier.weight(1f),
						enableRippleAnimation = true
					) {
						val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
						clipboard.setPrimaryClip(ClipData.newPlainText("Musik Crash Log", crashLog))
					}

					// --===--  Save Crash Log Button  --===--
					OptionButton(
						text = stringResource(R.string.save_crash_log),
						modifier = Modifier.weight(1f),
						enableRippleAnimation = true
					) {
						saveCrashLog(context, crashLog)
					}
				}

				Spacer(Modifier.height(dimensionResource(R.dimen.medium_padding)))

				// --===--  Restart App Button  --===--
				OptionButton(
					text = stringResource(R.string.restart_app),
					modifier = Modifier.fillMaxWidth(),
					enableRippleAnimation = true
				) {
					onRestart()
				}
			}
		}
	}
}

private fun saveCrashLog(context: Context, crashLog: String) {
	val timestamp = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.US).format(Date())
	val fileName = "musik_crash_log_$timestamp.txt"

	try {
		val resolver = context.contentResolver
		val contentValues = ContentValues().apply {
			put(MediaStore.Downloads.DISPLAY_NAME, fileName)
			put(MediaStore.Downloads.MIME_TYPE, "text/plain")
			put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
		}

		val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

		if (uri != null) {
			resolver.openOutputStream(uri)?.use { stream ->
				stream.write(crashLog.toByteArray())
			}

			Toast.makeText(context, "Saved to ${Environment.DIRECTORY_DOWNLOADS}/$fileName", Toast.LENGTH_LONG).show()
		} else {
			Toast.makeText(context, "Failed to save crash log", Toast.LENGTH_SHORT).show()
		}
	} catch (_: IOException) {
		Toast.makeText(context, "Failed to save crash log", Toast.LENGTH_SHORT).show()
	}
}