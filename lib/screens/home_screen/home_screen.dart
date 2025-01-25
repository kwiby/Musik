import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:musik/misc/page_navigator.dart';
import 'containers/all_music_container.dart';
import 'containers/playlists_container.dart';

ValueNotifier<String> tabNotifier = ValueNotifier<String>('All Music');

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      onPopInvokedWithResult: (didPop, dynamic) {
        if (PageNavigator.pageHistory.isNotEmpty) {
          PageNavigator.backButton(context);
        } else {
          SystemNavigator.pop();
        }
      },
      child: Scaffold(
        backgroundColor: Theme.of(context).colorScheme.surface,

        appBar: AppBar(
          title: const Text(
              'Musik',
              style: TextStyle(
                  fontFamily: 'SourGummy',
                  fontSize: 30,
                  fontVariations: [FontVariation('wght', 900)], // 'wght' = width, 'wdth' = width
              )
          ),
          toolbarHeight: 100,
          foregroundColor: Theme.of(context).colorScheme.tertiary, // 'Musik' title
          centerTitle: false,
          elevation: 0,
        ),

        body: ValueListenableBuilder<String>(
            valueListenable: tabNotifier,
            builder: (context, tab, child) {
              return tab == 'All Music' ? AllMusicContainer() : PlaylistsContainer();
            },
        ),
      ),
    );
  }
}