import 'dart:developer';

import 'package:flutter/material.dart';

import '../../audio_controller/audio_controller.dart';
import '../../misc/page_navigator.dart';
import 'misc/song_text.dart';

class SongScreen extends StatefulWidget {
  const SongScreen({super.key});

  @override
  State<SongScreen> createState() => _SongScreenState();
}

class _SongScreenState extends State<SongScreen> {
  bool _isHeroAnimationCompleted = false;

  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      onPopInvokedWithResult: (didPop, dynamic) => PageNavigator.backButton(context),
      child: Scaffold(
        resizeToAvoidBottomInset: false,
        backgroundColor: Colors.transparent,

        body: Stack(
          children: [
            // Background
            Hero(
              tag: 'floating_bar_background',
              child: Container(
                color: Theme.of(context).colorScheme.surface,
                height: double.infinity,
                width: double.infinity,
              ),
              flightShuttleBuilder: (flightContext, animation, flightDirection, fromHeroContext, toHeroContext) {
                if (flightDirection == HeroFlightDirection.push) {
                  animation.addStatusListener((status) {
                    if (status == AnimationStatus.completed && mounted) {
                      setState(() => _isHeroAnimationCompleted = true);
                    }
                  });
                }


                return flightDirection == HeroFlightDirection.push ? toHeroContext.widget : fromHeroContext.widget;
              },
            ),

            // Back button
            Visibility(
              visible: _isHeroAnimationCompleted,
              child: SafeArea(
                child: Align(
                  alignment: Alignment.topLeft,
                  child: ElevatedButton(
                    style: ButtonStyle(
                      padding: const WidgetStatePropertyAll(EdgeInsets.only(top: 30)),
                      backgroundColor: WidgetStateColor.transparent,
                      shadowColor: WidgetStateColor.transparent,
                      shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
                    ),
                    onPressed: () {
                      _isHeroAnimationCompleted = false;
                      PageNavigator.backButton(context);
                    },
                    child: Icon(
                      Icons.arrow_back_outlined,
                      color: Theme.of(context).colorScheme.tertiary,
                    ),
                  ),
                ),
              ),
            ),

            // Song info and buttons
            Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  // Song image
                  Hero(
                    tag: 'floating_bar_image',
                    child: SizedBox(
                      height: 200,
                      width: 200,
                      child: ClipRRect(
                        borderRadius: BorderRadius.circular(30),
                        child: audioController.getPlayingSongData('decodedByte') != null ? Image.memory(audioController.getPlayingSongData('decodedByte')) : null,
                      ),
                    ),
                  ),

                  // Padding between image and title
                  const Padding(padding: EdgeInsets.only(bottom: 15)),

                  // Title
                  Hero(
                    tag: 'floating_bar_title',
                    child: SongText().getTitleText(20, 25)
                  ),

                  // Artist
                  Hero(
                    tag: 'floating_bar_artist',
                    child: SongText().getArtistText(18, 25)
                  ),

                  // Buttons
                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      // Skip to previous song button
                      Hero(
                        tag: 'floating_bar_skipToPrev',
                        child: SizedBox(
                          width: 60,
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
                                log('The state object is not currently in the tree {song_screen.dart LINE 132}!');
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
                          width: 60,
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
                                log('The state object is not currently in the tree {song_screen.dart LINE 157}!');
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
                          width: 60,
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
                                log('The state object is not currently in the tree {song_screen.dart LINE 188}!');
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
            ),
          ],
        ),
      ),
    );
  }
}