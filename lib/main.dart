import 'package:flutter/material.dart';
import 'package:musik/misc/shared_prefs.dart';
import 'package:musik/screens/home_screen/home_screen.dart';

Future main() async {
  WidgetsFlutterBinding.ensureInitialized();

  await sharedPrefs.init();

  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Musik',
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
        useMaterial3: true,
      ),
      home: const HomeScreen(),
      routes: {
        '/HomeScreen': (context) => const HomeScreen(),
      },
    );
  }
}