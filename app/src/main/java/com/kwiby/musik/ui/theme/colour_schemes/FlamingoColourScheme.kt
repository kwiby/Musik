package com.kwiby.musik.ui.theme.colour_schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// --===--  Dark  --===--
val FlamingoDarkColourScheme = darkColorScheme(
	primary = Color(0xFF000000), // Default
	secondary = Color(0xFF342834), // Containers
	background = Color(0xFF261d26), // Background
	onPrimary = Color(0xFFe7c6e7), // Title text
	onSecondary = Color(0xFFFFFFFF), // General text and buttons
	onSurfaceVariant = Color(0xFF999999), // Artist text, drag handler, & app settings button
	surface = Color(0xFF493549), // Version text
	onSurface = Color(0xFF1c151c), // Shadows
	secondaryContainer = Color(0xFF262126), // Field background
	secondaryFixed = Color(0xFF302930), // Field border
	outline = Color(0xFF814d7f), // Main Accent
	outlineVariant = Color(0xFF754770) // Alt Accent
)

// --===--  Light  --===--
val FlamingoLightColourScheme = lightColorScheme(
	primary = Color(0xFFFFFFFF), // Default
	secondary = Color(0xFFE2E2D9), // Containers
	background = Color(0xFFD7D7CB), // Background
	onPrimary = Color(0xFFb399b3), // Title text
	onSecondary = Color(0xFF000000), // General text and buttons
	onSurfaceVariant = Color(0xFF666666), // Artist text, drag handler, & app settings button
	surface = Color(0xFFCACAB6), // Version text
	onSurface = Color(0xFFEAEAE3), // Shadows
	secondaryContainer = Color(0xFFd0d0cb), // Field background
	secondaryFixed = Color(0xFFc7c7c0), // Field border
	outline = Color(0xFF814d7f), // Main Accent
	outlineVariant = Color(0xFF754770) // Alt Accent
)