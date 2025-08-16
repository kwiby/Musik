import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:just_audio/just_audio.dart';
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
        resizeToAvoidBottomInset: false,
        backgroundColor: Theme.of(context).colorScheme.surface,

        appBar: AppBar(
          title: const Text(
            'Musik',
            style: TextStyle(
              fontFamily: 'SourGummy',
              fontSize: 30,
              fontVariations: [FontVariation('wght', 900)], // 'wght' = weight, 'wdth' = width
            ),
          ),
          toolbarHeight: 100,
          foregroundColor: Theme.of(context).colorScheme.outline, // 'Musik' title
          centerTitle: false,
          elevation: 0,
        ),

        body: SafeArea(
          child: Stack(
            children: [
              // Containers
              ValueListenableBuilder<String>(
                valueListenable: tabNotifier,
                builder: (context, tab, child) {
                  return tab == 'All Music' ? const AllMusicContainer() : PlaylistsContainer();
                },
              ),

              // Black background for bottom rounded corner cutout areas
              Align(
                alignment: Alignment.bottomCenter,
                child: Container(
                  height: 30,
                  color: Colors.black
                ),
              ),

              // Floating bottom bar for currently playing song
              Align(
                alignment: Alignment.bottomCenter,
                child: ClipRRect(
                  borderRadius: BorderRadius.circular(30),
                  child: Container(
                    height: 60,
                    color: Theme.of(context).colorScheme.surface,
                  ),
                ),
              )
            ],
          ),
        ),
      ),
    );
  }
}