import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:musik/misc/default_icon_loader.dart';
import 'package:musik/screens/home_screen/home_screen.dart';

import '../../audio_controller/audio_controller.dart';
import '../../misc/page_navigator.dart';
import 'misc/song_text.dart';

// Value notifiers
ValueNotifier<Duration> positionNotifier = ValueNotifier<Duration>(Duration.zero);
ValueNotifier<Duration> durationNotifier = ValueNotifier<Duration>(Duration.zero);

// Constants
const int fadeInDurationCONSTANT = 150;
const double buttonIconSizeCONSTANT = 30;
const double mainButtonWidthCONSTANT = 50;
const double altButtonWidthCONSTANT = 80;


class SongScreen extends StatefulWidget {
  const SongScreen({super.key});

  @override
  State<SongScreen> createState() => _SongScreenState();
}

class _SongScreenState extends State<SongScreen> {
  bool _isHeroAnimationCompleted = false;

  @override
  void initState() {
    super.initState();

    // Song position update listener.
    audioController.getAudioPlayer().positionStream.listen((position){
      positionNotifier.value = position;
    });

    // Song duration update listener.
    audioController.getAudioPlayer().durationStream.listen((duration) {
      if (duration != null) {
        durationNotifier.value = duration;
      }
    });
  }

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
            AnimatedOpacity(
              opacity: _isHeroAnimationCompleted ? 1 : 0,
              duration: const Duration(milliseconds: fadeInDurationCONSTANT),
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
                      Icons.arrow_back_rounded,
                      color: Theme.of(context).colorScheme.tertiary,
                    ),
                  ),
                ),
              ),
            ),

            // Song info and buttons
            ValueListenableBuilder<bool>(
              valueListenable: isPlayingSongNotifier,
              builder: (context, value, child) {
                return Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      // Song image
                      Hero(
                        tag: 'floating_bar_image',
                        child: SizedBox(
                          height: 250,
                          width: 250,
                          child: ClipRRect(
                            borderRadius: BorderRadius.circular(30),
                            child: audioController.getPlayingSongData('decodedByte') != null
                                ? Image(image: MemoryImage(audioController.getPlayingSongData('decodedByte')), fit: BoxFit.cover)
                                : Image(image: MemoryImage(defaultIcon), fit: BoxFit.cover),
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

                      // Padding between artist and buttons
                      const Padding(padding: EdgeInsets.only(bottom: 100)),

                      // Song timeline
                      AnimatedOpacity(
                        opacity: _isHeroAnimationCompleted ? 1 : 0,
                        duration: const Duration(milliseconds: fadeInDurationCONSTANT),
                        child: Column(
                          children: [
                            // Position and duration texts
                            Row(
                              mainAxisAlignment: MainAxisAlignment.spaceAround,
                              children: [
                                // Current position text
                                ValueListenableBuilder(
                                  valueListenable: positionNotifier,
                                  builder: (context, position, child) {
                                    return Text(
                                      position.toString().split('.').first,
                                      style: TextStyle(
                                        fontFamily: 'SourGummy',
                                        fontVariations: const [FontVariation('wght', 400)],
                                        fontSize: 15,
                                        color: Theme.of(context).colorScheme.tertiary,
                                      ),
                                    );
                                  },
                                ),

                                // Total duration text
                                ValueListenableBuilder(
                                  valueListenable: durationNotifier,
                                  builder: (context, duration, child) {
                                    return Text(
                                      duration.toString().split('.').first,
                                      style: TextStyle(
                                        fontFamily: 'SourGummy',
                                        fontVariations: const [FontVariation('wght', 400)],
                                        fontSize: 15,
                                        color: Theme.of(context).colorScheme.tertiary,
                                      ),
                                    );
                                  },
                                ),
                              ],
                            ),

                            // Timeline
                            ValueListenableBuilder(
                              valueListenable: positionNotifier,
                              builder: (context, position, child) {
                                return SliderTheme(
                                  data: SliderTheme.of(context).copyWith(
                                    trackHeight: 10,
                                    thumbShape: const RoundSliderThumbShape(enabledThumbRadius: 8),
                                    padding: const EdgeInsets.only(top: 5, left: 50, bottom: 10, right: 50)
                                  ),
                                  child: Slider(
                                    min: 0,
                                    max: durationNotifier.value.inMilliseconds.toDouble(),
                                    value: position.inMilliseconds.toDouble().clamp(0, durationNotifier.value.inMilliseconds.toDouble()),
                                    onChanged: audioController.seek,
                                    activeColor: Theme.of(context).colorScheme.secondary,
                                    inactiveColor: Theme.of(context).colorScheme.shadow,
                                  ),
                                );
                              },
                            ),
                          ],
                        ),
                      ),

                      // Buttons
                      Row(
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          // Loop current song button
                          AnimatedOpacity(
                            opacity: _isHeroAnimationCompleted ? 1 : 0,
                            duration: const Duration(milliseconds: fadeInDurationCONSTANT),
                            child: SizedBox(
                              width: altButtonWidthCONSTANT,
                              child: ElevatedButton(
                                style: ButtonStyle(
                                  padding: const WidgetStatePropertyAll(EdgeInsets.zero),
                                  backgroundColor: WidgetStateColor.transparent,
                                  shadowColor: WidgetStateColor.transparent,
                                  shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
                                ),
                                onPressed: () {
                                  setState(() => audioController.toggleLooping());
                                },
                                child: Icon(
                                  audioController.getIsLooping() ? Icons.change_circle_rounded : Icons.loop_rounded,
                                  color: Theme.of(context).colorScheme.tertiary,
                                  size: buttonIconSizeCONSTANT,
                                ),
                              ),
                            ),
                          ),

                          // Skip to previous song button
                          Hero(
                            tag: 'floating_bar_skipToPrev',
                            child: SizedBox(
                              width: mainButtonWidthCONSTANT,
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
                                    log('The state object is not currently in the tree {song_screen.dart -> \'Skip to previous song button\'}!');
                                  }
                                },
                                child: Icon(
                                  Icons.skip_previous_rounded,
                                  color: Theme.of(context).colorScheme.tertiary,
                                  size: buttonIconSizeCONSTANT,
                                ),
                              ),
                            ),
                          ),

                          // Play/pause button
                          Hero(
                            tag: 'floating_bar_pause',
                            child: SizedBox(
                              width: mainButtonWidthCONSTANT,
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
                                    log('The state object is not currently in the tree {song_screen.dart \'Play/pause button\'}!');
                                  }
                                },
                                child: Icon(
                                  audioController.isPlaying() ? Icons.pause_rounded : Icons.play_arrow_rounded,
                                  color: Theme.of(context).colorScheme.tertiary,
                                  size: buttonIconSizeCONSTANT,
                                ),
                              ),
                            ),
                          ),

                          // Skip to next song button
                          Hero(
                            tag: 'floating_bar_skipToNext',
                            child: SizedBox(
                              width: mainButtonWidthCONSTANT,
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
                                    log('The state object is not currently in the tree {song_screen.dart \'Skip to next song button\'}!');
                                  }
                                },
                                child: Icon(
                                  Icons.skip_next_rounded,
                                  color: Theme.of(context).colorScheme.tertiary,
                                  size: buttonIconSizeCONSTANT,
                                ),
                              ),
                            ),
                          ),

                          // Shuffle songs button
                          AnimatedOpacity(
                            opacity: _isHeroAnimationCompleted ? 1 : 0,
                            duration: const Duration(milliseconds: fadeInDurationCONSTANT),
                            child: SizedBox(
                              width: altButtonWidthCONSTANT,
                              child: ElevatedButton(
                                style: ButtonStyle(
                                  padding: const WidgetStatePropertyAll(EdgeInsets.zero),
                                  backgroundColor: WidgetStateColor.transparent,
                                  shadowColor: WidgetStateColor.transparent,
                                  shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
                                ),
                                onPressed: () {
                                  setState(() => audioController.toggleShuffling());
                                },
                                child: Icon(
                                  audioController.getIsShuffling() ? Icons.shuffle_on_rounded : Icons.shuffle_rounded,
                                  color: Theme.of(context).colorScheme.tertiary,
                                  size: audioController.getIsShuffling() ? buttonIconSizeCONSTANT - 6 : buttonIconSizeCONSTANT,
                                ),
                              ),
                            ),
                          ),
                        ],
                      ),
                    ],
                  ),
                );
              },
            ),
          ],
        ),
      ),
    );
  }
}