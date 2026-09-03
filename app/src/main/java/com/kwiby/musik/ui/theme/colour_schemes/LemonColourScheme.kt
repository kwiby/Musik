package com.kwiby.musik.ui.theme.colour_schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// --===--  Dark  --===--
val LemonDarkColourScheme = darkColorScheme(
	primary = Color(0xFF000000), // Default
	secondary = Color(0xFF343428), // Containers
	background = Color(0xFF26261d), // Background
	onPrimary = Color(0xFFe7e7c6), // Title text
	onSecondary = Color(0xFFFFFFFF), // General text and buttons
	onSurfaceVariant = Color(0xFF999999), // Artist text, drag handler, & app settings button
	surface = Color(0xFF494935), // Version text
	onSurface = Color(0xFF1c1c15), // Shadows
	secondaryContainer = Color(0xFF262621), // Field background
	secondaryFixed = Color(0xFF303029), // Field border
	outline = Color(0xFF81814d), // Main Accent
	outlineVariant = Color(0xFF757547) // Alt Accent
)

// --===--  Light  --===--
val LemonLightColourScheme = lightColorScheme(
	primary = Color(0xFFFFFFFF), // Default
	secondary = Color(0xFFE2E2D9), // Containers
	background = Color(0xFFD7D7CB), // Background
	onPrimary = Color(0xFFb3b399), // Title text
	onSecondary = Color(0xFF000000), // General text and buttons
	onSurfaceVariant = Color(0xFF666666), // Artist text, drag handler, & app settings button
	surface = Color(0xFFCACAB6), // Version text
	onSurface = Color(0xFFEAEAE3), // Shadows
	secondaryContainer = Color(0xFFd0d0cb), // Field background
	secondaryFixed = Color(0xFFc7c7c0), // Field border
	outline = Color(0xFF81814d), // Main Accent
	outlineVariant = Color(0xFF757547) // Alt Accent
)