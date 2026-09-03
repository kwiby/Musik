package com.kwiby.musik.ui.theme.colour_schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// --===--  Dark  --===--
val GrapeDarkColourScheme = darkColorScheme(
	primary = Color(0xFF000000), // Default
	secondary = Color(0xFF2e2834), // Containers
	background = Color(0xFF211d26), // Background
	onPrimary = Color(0xFFd6c6e7), // Title text
	onSecondary = Color(0xFFFFFFFF), // General text and buttons
	onSurfaceVariant = Color(0xFF999999), // Artist text, drag handler, & app settings button
	surface = Color(0xFF3f3549), // Version text
	onSurface = Color(0xFF18151c), // Shadows
	secondaryContainer = Color(0xFF232126), // Field background
	secondaryFixed = Color(0xFF2c2930), // Field border
	outline = Color(0xFF664d81), // Main Accent
	outlineVariant = Color(0xFF5d4775) // Alt Accent
)

// --===--  Light  --===--
val GrapeLightColourScheme = lightColorScheme(
	primary = Color(0xFFFFFFFF), // Default
	secondary = Color(0xFFE2E2D9), // Containers
	background = Color(0xFFD7D7CB), // Background
	onPrimary = Color(0xFFa69ab4), // Title text
	onSecondary = Color(0xFF000000), // General text and buttons
	onSurfaceVariant = Color(0xFF666666), // Artist text, drag handler, & app settings button
	surface = Color(0xFFCACAB6), // Version text
	onSurface = Color(0xFFEAEAE3), // Shadows
	secondaryContainer = Color(0xFFd0d0cb), // Field background
	secondaryFixed = Color(0xFFc7c7c0), // Field border
	outline = Color(0xFF664d81), // Main Accent
	outlineVariant = Color(0xFF5d4775) // Alt Accent
)