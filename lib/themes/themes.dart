import 'package:flutter/material.dart';

class Themes {
  // -=-  Dark Theme Colours  -=-
  Color darkPrimaryColour = const Color.fromRGBO(0, 0, 0, 1.0);
  Color darkInversePrimaryColour = const Color.fromRGBO(255, 255, 255, 1.0);
  Color darkBackgroundColour = const Color.fromRGBO(29, 29, 38, 1.0);
  Color darkAccent = const Color.fromRGBO(77, 117, 129, 1.0);
  Color darkTitle = const Color.fromRGBO(198, 198, 231, 1.0);
  Color darkContainer = const Color.fromRGBO(40, 40, 52, 1.0);

  // -=-  Light Theme Colours  -=-
  Color lightPrimaryColour = const Color.fromRGBO(255, 255, 255, 1.0);
  Color lightInversePrimaryColour = const Color.fromRGBO(0, 0, 0, 1.0);
  Color lightBackgroundColour = const Color.fromRGBO(232, 232, 232, 1.0);
  Color lightAccent = const Color.fromRGBO(77, 117, 129, 1.0);
  Color lightAppBar = const Color.fromRGBO(238, 238, 238, 1.0);

  // -=-  Dark Theme ThemeData  -=-
  static final ThemeData dark = ThemeData(
    useMaterial3: false,
    colorScheme: const ColorScheme.dark().copyWith(
      primary: _themes.darkPrimaryColour,
      inversePrimary: _themes.darkInversePrimaryColour,
      surface: _themes.darkBackgroundColour,
      secondary: _themes.darkAccent,
      tertiary: _themes.darkTitle,
      primaryContainer: _themes.darkContainer,
    ),

    // -=-  Dark Theme AppBar  -=-
    bottomAppBarTheme: BottomAppBarTheme(
      color: _themes.darkPrimaryColour,
      padding: const EdgeInsets.all(20),
    ),
    progressIndicatorTheme: ProgressIndicatorThemeData(
        color: _themes.darkAccent
    ),
    elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: _themes.darkAccent,
        )
    ),
  );

  // -=-  Light Theme ThemeData  -=-
  static final ThemeData light = ThemeData(
    useMaterial3: false,
    colorScheme: const ColorScheme.light().copyWith(
      primary: _themes.lightPrimaryColour,
      inversePrimary: _themes.lightInversePrimaryColour,
      surface: _themes.lightBackgroundColour,
      secondary: _themes.lightAccent,
    ),

    // -=-  Dark Theme AppBar  -=-
    appBarTheme: const AppBarTheme().copyWith(
        backgroundColor: _themes.lightAppBar,
        toolbarHeight: 70,
        shape: const BorderDirectional(bottom: BorderSide(color: Colors.white))),
    bottomAppBarTheme: BottomAppBarTheme(
      color: _themes.lightAppBar,
      padding: const EdgeInsets.all(20),
    ),
    progressIndicatorTheme: ProgressIndicatorThemeData(
        color: _themes.lightAccent
    ),
    elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: _themes.lightAccent,
        )
    ),
  );
}

Themes _themes = Themes();
