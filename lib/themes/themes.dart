import 'package:flutter/material.dart';

class Themes {
  // -=-  Night Theme Colours  -=-
  Color nightPrimaryColour = const Color.fromRGBO(0, 0, 0, 1.0);
  Color nightInversePrimaryColour = const Color.fromRGBO(255, 255, 255, 1.0);
  Color nightBackgroundColour = const Color.fromRGBO(29, 29, 38, 1.0);
  Color nightAccent = const Color.fromRGBO(77, 117, 129, 1.0);
  Color nightTitle = const Color.fromRGBO(198, 198, 231, 1.0);
  Color nightText = const Color.fromRGBO(255, 255, 255, 1.0);
  Color nightContainer = const Color.fromRGBO(40, 40, 52, 1.0);
  Color nightShadow = const Color.fromRGBO(21, 21, 28, 1.0);

  // -=-  Night Theme ThemeData  -=-
  static final ThemeData night = ThemeData(
    useMaterial3: false,
    colorScheme: const ColorScheme.dark().copyWith(
      primary: _themes.nightPrimaryColour,
      inversePrimary: _themes.nightInversePrimaryColour,
      surface: _themes.nightBackgroundColour,
      secondary: _themes.nightAccent,
      outline: _themes.nightTitle,
      tertiary: _themes.nightText,
      primaryContainer: _themes.nightContainer,
      shadow: _themes.nightShadow,
    ),

    // -=-  Night Theme AppBar  -=-
    bottomAppBarTheme: BottomAppBarTheme(
      color: _themes.nightPrimaryColour,
      padding: const EdgeInsets.all(20),
    ),
    progressIndicatorTheme: ProgressIndicatorThemeData(
        color: _themes.nightAccent
    ),
    elevatedButtonTheme: ElevatedButtonThemeData(
        style: ElevatedButton.styleFrom(
          backgroundColor: _themes.nightAccent,
        )
    ),
  );
}

Themes _themes = Themes();
