import 'package:flutter/material.dart';
import 'package:musik/misc/shared_prefs.dart';
import 'package:musik/themes/themes.dart';

class ThemeManager with ChangeNotifier {
  ThemeData? _themeData;

  ThemeManager() {
    loadThemePref();
  }

  ThemeData get themeData => _themeData ?? Themes.dark;

  set themeData(ThemeData themeData) {
    _themeData = themeData;
    notifyListeners();
  }

  void setTheme(String value) {
    switch (value) {
      case 'light':
        themeData = Themes.light;
        break;
      case 'dark':
        themeData = Themes.dark;
        break;
      default:
        themeData = Themes.dark;
        break;
    }

    sharedPrefs.theme = value;
  }

  Future<void> loadThemePref() async {
    switch (sharedPrefs.theme) {
      case 'light':
        _themeData = Themes.light;
        break;
      case 'dark':
        _themeData = Themes.dark;
        break;
      default:
        _themeData = Themes.dark;
        break;
    }

    notifyListeners();
  }
}