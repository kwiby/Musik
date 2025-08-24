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
                      Visibility(
                        visible: _isHeroAnimationCompleted,
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
                              builder: (context, value, child) {
                                return SliderTheme(
                                  data: SliderTheme.of(context).copyWith(
                                    trackHeight: 15,
                                    thumbShape: const RoundSliderThumbShape(enabledThumbRadius: 11),
                                  ),
                                  child: Slider(
                                    activeColor: Theme.of(context).colorScheme.secondary,
                                    inactiveColor: Theme.of(context).colorScheme.shadow,
                                    min: 0,
                                    max: durationNotifier.value.inMilliseconds.toDouble(),
                                    value: positionNotifier.value.inMilliseconds.toDouble().clamp(0, durationNotifier.value.inMilliseconds.toDouble()),
                                    onChanged: audioController.seek,
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
                                    log('The state object is not currently in the tree {song_screen.dart -> \'Skip to previous song button\'}!');
                                  }
                                },
                                child: Icon(
                                  Icons.skip_previous,
                                  color: Theme.of(context).colorScheme.tertiary,
                                  size: 40,
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
                                    log('The state object is not currently in the tree {song_screen.dart \'Play/pause button\'}!');
                                  }
                                },
                                child: Icon(
                                  audioController.isPlaying() ? Icons.pause : Icons.play_arrow,
                                  color: Theme.of(context).colorScheme.tertiary,
                                  size: 40,
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
                                    log('The state object is not currently in the tree {song_screen.dart \'Skip to next song button\'}!');
                                  }
                                },
                                child: Icon(
                                  Icons.skip_next,
                                  color: Theme.of(context).colorScheme.tertiary,
                                  size: 40,
                                ),
                              ),
                            ),
                          ),

                          const Padding(padding: EdgeInsets.only(right: 10)), // Padding after buttons
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