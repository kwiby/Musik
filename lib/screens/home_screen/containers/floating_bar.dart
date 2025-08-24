import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:musik/audio_controller/audio_controller.dart';
import 'package:musik/misc/page_navigator.dart';
import 'package:musik/screens/song_screen/misc/song_text.dart';
import 'package:musik/screens/song_screen/song_screen.dart';

import '../home_screen.dart';

class FloatingBar extends StatefulWidget {
  const FloatingBar({super.key});

  @override
  State<FloatingBar> createState() => _FloatingBarState();
}

class _FloatingBarState extends State<FloatingBar> {
  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder<bool>(
      valueListenable: isPlayingSongNotifier,
      builder: (context, isPlayingSong, child) {
        return Visibility(
          visible: isPlayingSong,
          child: GestureDetector(
            onTap: () => PageNavigator.navigatePage(context, const SongScreen()),
            child: Stack(
              children: [
                // Background container
                Align(
                  alignment: Alignment.bottomCenter,
                  child: Hero(
                    tag: 'floating_bar_background',
                    child: ClipRRect(
                      borderRadius: BorderRadius.circular(30),
                      child: Container(
                        height: 60,
                        color: Theme.of(context).colorScheme.surface,
                      ),
                    ),
                  ),
                ),

                // Song info and buttons
                Column(
                  mainAxisAlignment: MainAxisAlignment.end,
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      crossAxisAlignment: CrossAxisAlignment.end,
                      children: [
                        // Song info
                        Expanded(
                          child: Row(
                            crossAxisAlignment: CrossAxisAlignment.end,
                            children: [
                              const Padding(padding: EdgeInsets.only(right: 5)), // Start padding

                              // Song album art
                              Hero(
                                tag: 'floating_bar_image',
                                child: SizedBox(
                                  height: 50,
                                  width: 50,
                                  child: ClipRRect(
                                    borderRadius: BorderRadius.circular(30),
                                    child: audioController.getPlayingSongData('decodedByte') != null ? Image.memory(audioController.getPlayingSongData('decodedByte')) : null,
                                  ),
                                ),
                              ),

                              const Padding(padding: EdgeInsets.only(right: 10)), // Padding after album art image

                              // Song title and artist
                              Expanded(
                                child: Column(
                                  mainAxisAlignment: MainAxisAlignment.end,
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: [
                                    // Title
                                    Hero(
                                      tag: 'floating_bar_title',
                                      child: SongText().getTitleText(15, 20)
                                    ),

                                    // Artist
                                    Hero(
                                      tag: 'floating_bar_artist',
                                      child: SongText().getArtistText(13, 20)
                                    ),
                                  ],
                                ),
                              ),
                            ],
                          ),
                        ),

                        // Buttons
                        Row(
                          mainAxisAlignment: MainAxisAlignment.end,
                          children: [
                            // Skip to previous song button
                            Hero(
                              tag: 'floating_bar_skipToPrev',
                              child: SizedBox(
                                width: 30,
                                child: ElevatedButton(
                                  style: ButtonStyle(
                                    padding: const WidgetStatePropertyAll(EdgeInsets.zero),
                                    backgroundColor: WidgetStateColor.transparent,
                                    shadowColor: WidgetStateColor.transparent,
                                    shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
                                  ),
                                  onPressed: () async {
                                    await audioController.skipToPrev();
                                    if (mounted) {
                                      setState(() {});
                                    } else {
                                      log('The state object is not currently in the tree {floating_bar.dart LINE 115}!');
                                    }
                                  },
                                  child: Icon(
                                    Icons.skip_previous,
                                    color: Theme.of(context).colorScheme.tertiary,
                                  ),
                                ),
                              ),
                            ),

                            // Play/pause button
                            Hero(
                              tag: 'floating_bar_pause',
                              child: SizedBox(
                                width: 30,
                                child: ElevatedButton(
                                  style: ButtonStyle(
                                    padding: const WidgetStatePropertyAll(EdgeInsets.zero),
                                    backgroundColor: WidgetStateColor.transparent,
                                    shadowColor: WidgetStateColor.transparent,
                                    shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
                                  ),
                                  onPressed: () async {
                                    await audioController.pause();
                                    if (mounted) {
                                      setState(() {});
                                    } else {
                                      log('The state object is not currently in the tree {floating_bar.dart LINE 145}!');
                                    }
                                  },
                                  child: Icon(
                                    audioController.isPlaying() ? Icons.pause : Icons.play_arrow,
                                    color: Theme.of(context).colorScheme.tertiary,
                                  ),
                                ),
                              ),
                            ),

                            // Skip to next song button
                            Hero(
                              tag: 'floating_bar_skipToNext',
                              child: SizedBox(
                                width: 30,
                                child: ElevatedButton(
                                  style: ButtonStyle(
                                    padding: const WidgetStatePropertyAll(EdgeInsets.zero),
                                    backgroundColor: WidgetStateColor.transparent,
                                    shadowColor: WidgetStateColor.transparent,
                                    shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
                                  ),
                                  onPressed: () async {
                                    await audioController.skipToNext();
                                    if (mounted) {
                                      setState(() {});
                                    } else {
                                      log('The state object is not currently in the tree {floating_bar.dart LINE 173}!');
                                    }
                                  },
                                  child: Icon(
                                    Icons.skip_next,
                                    color: Theme.of(context).colorScheme.tertiary,
                                  ),
                                ),
                              ),
                            ),

                            const Padding(padding: EdgeInsets.only(right: 10)), // Padding after buttons
                          ],
                        ),
                      ],
                    ),

                    const Padding(padding: EdgeInsets.only(bottom: 5)),
                  ],
                ),
              ],
            ),
          ),
        );
      },
    );
  }
}