import 'package:flutter/material.dart';
import 'package:musik/misc/shared_prefs.dart';
import 'package:musik/themes/themes.dart';

class ThemeManager with ChangeNotifier {
  ThemeData? _themeData;

  ThemeManager() {
    loadThemePref();
  }

  ThemeData get themeData => _themeData ?? Themes.night;

  set themeData(ThemeData themeData) {
    _themeData = themeData;
    notifyListeners();
  }

  void setTheme(String value) {
    switch (value) {
      case 'night':
        themeData = Themes.night;
        break;
      default:
        themeData = Themes.night;
        break;
    }

    sharedPrefs.theme = value;
  }

  Future<void> loadThemePref() async {
    switch (sharedPrefs.theme) {
      case 'night':
        _themeData = Themes.night;
        break;
      default:
        _themeData = Themes.night;
        break;
    }

    notifyListeners();
  }
}