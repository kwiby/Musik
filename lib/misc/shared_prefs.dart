import 'package:shared_preferences/shared_preferences.dart';

class SharedPrefs {
  late final SharedPreferences _sharedPrefs;

  Future<void> init() async {
    _sharedPrefs = await SharedPreferences.getInstance();
  }

  // Currently Active Theme Boolean
  String get theme => _sharedPrefs.getString('theme') ?? 'dark';
  set theme(String value) {
    _sharedPrefs.setString('theme', value);
  }

  // Added Song IDs List/Map
  String get addedSongs => _sharedPrefs.getString('addedSongs') ?? '';
  set addedSongs(String value) {
    _sharedPrefs.setString('addedSongs', value);
  }
}

final sharedPrefs = SharedPrefs();