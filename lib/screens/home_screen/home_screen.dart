import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:just_audio/just_audio.dart';
import 'package:musik/audio_controller/audio_controller.dart';
import 'package:musik/misc/page_navigator.dart';
import 'tabs/all_music_tab/all_music_tab.dart';
import 'tabs/playlists_tab/playlists_tab.dart';

ValueNotifier<String> tabNotifier = ValueNotifier<String>('All Music');

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  // -=-  Audio Controlling  -=-
  final player = AudioPlayer();

  Duration position = Duration.zero;
  Duration duration = Duration.zero;

  @override
  void initState() {
    super.initState();

    player.positionStream.listen((pos) {
      setState(() => position = pos);
    });

    player.durationStream.listen((dur) {
      setState(() {
        duration = dur ?? Duration.zero;
      });
    });
  }

  // -=-  Main UI  -=-
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
            ),
          ),
          toolbarHeight: 100,
          foregroundColor: Theme.of(context).colorScheme.outline, // 'Musik' title
          centerTitle: false,
          elevation: 0,
        ),

        body: ValueListenableBuilder<String>(
          valueListenable: tabNotifier,
          builder: (context, tab, child) {
            return tab == 'All Music' ? const AllMusicContainer() : PlaylistsContainer();
          },
        ),
      ),
    );
  }
}