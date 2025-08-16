import 'package:flutter/material.dart';
import 'package:musik/audio_controller/audio_controller.dart';
import 'package:musik/misc/custom_scroll_behaviour.dart';
import 'package:musik/misc/shared_prefs.dart';
import 'package:musik/models/add_music_model.dart';
import 'package:musik/screens/home_screen/home_screen.dart';
import 'package:musik/themes/theme_manager.dart';
import 'package:provider/provider.dart';

Future main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await sharedPrefs.init(); // "SharedPrefs().init()" = new instance everytime; "sharedPrefs.init()" ('sharedPrefs' is variable made in model file) = same new instance everytime.
  await audioController.init();
  WidgetsBinding.instance.addPostFrameCallback((_) async {
    await addMusicModel.init(true);
  });
  //await audioController.init();

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
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Musik',
      theme: Provider.of<ThemeManager>(context).themeData,
      scrollBehavior: CustomScrollBehaviour(),
      home: const HomeScreen(),
      routes: {
        '/HomeScreen': (context) => const HomeScreen(),
      },
    );
  }
}