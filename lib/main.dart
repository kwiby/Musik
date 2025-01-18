import 'package:flutter/material.dart';
import 'package:musik/misc/shared_prefs.dart';
import 'package:musik/screens/home_screen/home_screen.dart';
import 'package:musik/themes/theme_manager.dart';
import 'package:provider/provider.dart';

Future main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await sharedPrefs.init();

  runApp(
      ChangeNotifierProvider(
        create: (context) => ThemeManager(),
        child: const MyApp(),
      ),
  );
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Musik',
      theme: Provider.of<ThemeManager>(context).themeData,
      home: const HomeScreen(),
      routes: {
        '/HomeScreen': (context) => const HomeScreen(),
      },
    );
  }
}