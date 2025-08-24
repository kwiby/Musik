import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:musik/misc/page_navigator.dart';
import 'package:musik/screens/home_screen/containers/floating_bar.dart';
import '../../misc/forced_value_notifier.dart';
import 'containers/watermark.dart';
import 'tabs/all_music_tab/all_music_tab.dart';
import 'tabs/playlists_tab/playlists_tab.dart';

// Value Notifiers (State Updating)
ValueNotifier<String> tabNotifier = ValueNotifier<String>('All Music');
ValueNotifier<bool> isPlayingSongNotifier = ForcedValueNotifier<bool>(false);

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
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

        appBar: const Watermark(),

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
              ValueListenableBuilder<bool>(
                valueListenable: isPlayingSongNotifier,
                builder: (context, isPlayingSong, child) {
                  return Visibility(
                    visible: isPlayingSong,
                    child: Align(
                      alignment: Alignment.bottomCenter,
                      child: Container(
                        height: 30,
                        color: Colors.black
                      ),
                    ),
                  );
                },
              ),

              // Floating bottom bar for currently playing song
              const FloatingBar(),
            ],
          ),
        ),
      ),
    );
  }
}