package com.example.musik

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.musik.ui.theme.MusikTheme

class MainActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge(navigationBarStyle = SystemBarStyle.dark(0xFF080808.toInt()))
		setContent {
			MusikTheme {
				MusikApp()
			}
		}
	}
}