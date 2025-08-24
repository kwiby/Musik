import 'package:flutter/material.dart';
import 'package:musik/audio_controller/audio_controller.dart';
import 'package:musik/misc/custom_scroll_behaviour.dart';
import 'package:musik/misc/shared_prefs.dart';
import 'package:musik/screens/home_screen/home_screen.dart';
import 'package:musik/screens/song_screen/song_screen.dart';
import 'package:musik/themes/theme_manager.dart';
import 'package:provider/provider.dart';

import 'misc/default_icon_loader.dart';
import 'models/add_music_model.dart';

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await loadDefaultIcon();
  await sharedPrefs.init(); // "SharedPrefs().init()" = new instance everytime; "sharedPrefs.init()" ('sharedPrefs' is variable made in model file) = same new instance everytime.
  await audioController.init();

  runApp(
    ChangeNotifierProvider(
      create: (context) => ThemeManager(),
      child: const Musik(),
    ),
  );
}

class Musik extends StatefulWidget {
  const Musik({super.key});

  @override
  State<Musik> createState() => _MusikState();
}

class _MusikState extends State<Musik> {
  @override
  Widget build(BuildContext context) {
    return FutureBuilder(
      future: addMusicModel.init(true),
      builder: (BuildContext context, AsyncSnapshot<void> snapshot) {
        return MaterialApp(
          debugShowCheckedModeBanner: false,
          title: 'Musik',
          theme: Provider.of<ThemeManager>(context).themeData,
          scrollBehavior: CustomScrollBehaviour(),
          home: const HomeScreen(),
          routes: {
            '/HomeScreen': (context) => const HomeScreen(),
            '/SongScreen': (context) => const SongScreen(),
          },
        );
      },
    );
  }
}