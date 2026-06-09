package com.example.musik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musik.ui.theme.MusikTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			MusikTheme {
				Surface(
					modifier = Modifier.fillMaxSize(),
					color = MaterialTheme.colorScheme.background
				) {
					GreetingText(
						msg = "Hello, World!",
						from = "Musik",
						modifier = Modifier.padding(8.dp)
					)
				}
			}
		}
	}
}

@Composable
fun GreetingText(msg: String, from: String, modifier: Modifier = Modifier) {
	Column(
		verticalArrangement = Arrangement.Center,
		modifier = modifier
	) {
		Text(
			text = msg,
			fontSize = 100.sp,
			lineHeight = 116.sp,
			textAlign = TextAlign.Center
		)
		Text(
			text = from,
			fontSize = 36.sp,
			modifier = Modifier.padding(16.dp).align(alignment = Alignment.CenterHorizontally)
		)
	}
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
	MusikTheme {
		GreetingText(msg = "Hello, World!", from = "Musik")
	}
}