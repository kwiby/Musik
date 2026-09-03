package com.kwiby.musik.ui.theme.colour_schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// --===--  Dark  --===--
val LimeDarkColourScheme = darkColorScheme(
	primary = Color(0xFF000000), // Default
	secondary = Color(0xFF283429), // Containers
	background = Color(0xFF1d261f), // Background
	onPrimary = Color(0xFFc6e7c9), // Title text
	onSecondary = Color(0xFFFFFFFF), // General text and buttons
	onSurfaceVariant = Color(0xFF999999), // Artist text, drag handler, & app settings button
	surface = Color(0xFF354939), // Version text
	onSurface = Color(0xFF151c17), // Shadows
	secondaryContainer = Color(0xFF212622), // Field background
	secondaryFixed = Color(0xFF29302b), // Field border
	outline = Color(0xFF4d8159), // Main Accent
	outlineVariant = Color(0xFF477551) // Alt Accent
)

// --===--  Light  --===--
val LimeLightColourScheme = lightColorScheme(
	primary = Color(0xFFFFFFFF), // Default
	secondary = Color(0xFFE2E2D9), // Containers
	background = Color(0xFFD7D7CB), // Background
	onPrimary = Color(0xFFa0baa3), // Title text
	onSecondary = Color(0xFF000000), // General text and buttons
	onSurfaceVariant = Color(0xFF666666), // Artist text, drag handler, & app settings button
	surface = Color(0xFFCACAB6), // Version text
	onSurface = Color(0xFFEAEAE3), // Shadows
	secondaryContainer = Color(0xFFd0d0cb), // Field background
	secondaryFixed = Color(0xFFc7c7c0), // Field border
	outline = Color(0xFF4d8159), // Main Accent
	outlineVariant = Color(0xFF477551) // Alt Accent
)