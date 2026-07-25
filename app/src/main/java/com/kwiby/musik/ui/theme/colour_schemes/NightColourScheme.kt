package com.kwiby.musik.ui.theme.colour_schemes

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// --===--  Dark  --===--
val NightDarkColorScheme = darkColorScheme(
	primary = Color(0xFF000000), // Default
	secondary = Color(0xFF282834), // Containers
	background = Color(0xFF1D1D26), // Background
	onPrimary = Color(0xFFC6C6E7), // Title text
	onSecondary = Color(0xFFFFFFFF), // General text and buttons
	onSurfaceVariant = Color(0xFF999999), // Artist text, drag handler, & app settings button
	surface = Color(0xFF353549), // Version text
	onSurface = Color(0xFF15151C), // Shadows
	secondaryContainer = Color(0xFF212126), // Field background
	secondaryFixed = Color(0xFF292930), // Field border
	outline = Color(0xFF4d7581), // Main Accent
	outlineVariant = Color(0xFF476a75) // Alt Accent
)

// --===--  Light  --===--
val NightLightColorScheme = lightColorScheme(
	primary = Color(0xFF000000), // Default
	secondary = Color(0xFFFFFFFF), // Containers
	background = Color(0xFF1D1D26), // Background
	onPrimary = Color(0xFFC6C6E7), // Title text
	onSecondary = Color(0xFFFFFFFF), // General text and buttons
	onSurfaceVariant = Color(0xFF999999), // Artist text, drag handler, & app settings button
	surface = Color(0xFF353549), // Version text
	onSurface = Color(0xFF15151C), // Shadows
	secondaryContainer = Color(0xFF212126), // Field background
	secondaryFixed = Color(0xFF292930), // Field border
	outline = Color(0xFF4d7581), // Main Accent
	outlineVariant = Color(0xFF476a75) // Alt Accent
)