import 'package:shared_preferences/shared_preferences.dart';

class SharedPrefs {
  late final SharedPreferences _sharedPrefs;

  Future<void> init() async {
    _sharedPrefs = await SharedPreferences.getInstance();
  }

  String get theme => _sharedPrefs.getString('theme') ?? 'dark';
  set theme(String value) {
    _sharedPrefs.setString('theme', value);
  }
}

final sharedPrefs = SharedPrefs();