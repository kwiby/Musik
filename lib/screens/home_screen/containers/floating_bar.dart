import 'package:flutter/material.dart';
import 'package:marquee/marquee.dart';
import 'package:musik/audio_controller/audio_controller.dart';

import '../home_screen.dart';

class FloatingBar extends StatefulWidget {
  const FloatingBar({super.key});

  @override
  State<FloatingBar> createState() => _FloatingBarState();
}

class _FloatingBarState extends State<FloatingBar> {

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder<bool>(
      valueListenable: isPlayingSongNotifier,
      builder: (context, isPlayingSong, child) {
        return Visibility(
          visible: isPlayingSong,
          child: Align(
            alignment: Alignment.bottomCenter,
            child: ClipRRect(
              borderRadius: BorderRadius.circular(30),
              child: Container(
                height: 60,
                color: Theme.of(context).colorScheme.surface,
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    // Song info
                    Expanded(
                      child: Row(
                        children: [
                          const Padding(padding: EdgeInsets.only(right: 5)), // Start padding

                          // Song album art
                          CircleAvatar(
                            radius: 25,
                            backgroundImage: audioController.getPlayingSongData('decodedByte') != null
                                ? MemoryImage(audioController.getPlayingSongData('decodedByte')) : null,
                            child: audioController.getPlayingSongData('decodedByte') == null
                                ? const Icon(Icons.question_mark) : null,
                          ),

                          const Padding(padding: EdgeInsets.only(right: 10)), // Padding after album art image

                          // Song title and artist
                          Expanded(
                            child: Column(
                              mainAxisAlignment: MainAxisAlignment.center,
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                // Title
                                SizedBox(
                                  height: 20,
                                  child: LayoutBuilder(
                                    builder: (context, constraints) {
                                      final textStyle = TextStyle(
                                        fontFamily: 'SourGummy',
                                        fontVariations: const [FontVariation('wght', 400)],
                                        fontSize: 15,
                                        color: Theme.of(context).colorScheme.tertiary,
                                      );

                                      // This TextPainter is used as a "ghost text" to first check if the text is long enough and would need scrolling.
                                      final textPainter = TextPainter(
                                        text: TextSpan(
                                          text: audioController.getPlayingSongData('title'),
                                          style: textStyle,
                                        ),
                                        textDirection: TextDirection.ltr,
                                      )..layout();

                                      final textWidth = textPainter.size.width;
                                      final availableWidth = constraints.maxWidth;

                                      // If text fits, use regular Text widget, otherwise use Marquee
                                      if (textWidth <= availableWidth) {
                                        return Text(
                                          audioController.getPlayingSongData('title'),
                                          style: textStyle,
                                          overflow: TextOverflow.ellipsis,
                                          maxLines: 1,
                                        );
                                      } else {
                                        return Marquee(
                                          text: audioController.getPlayingSongData('title'),
                                          style: textStyle,
                                          scrollAxis: Axis.horizontal,
                                          blankSpace: 30,
                                          velocity: 25,
                                          showFadingOnlyWhenScrolling: true,
                                          fadingEdgeStartFraction: 0.1,
                                          fadingEdgeEndFraction: 0.1,
                                          accelerationDuration: Duration.zero,
                                          decelerationDuration: Duration.zero,
                                        );
                                      }
                                    },
                                  ),
                                ),

                                // Artist
                                SizedBox(
                                  height: 20,
                                  child: LayoutBuilder(
                                    builder: (context, constraints) {
                                      const textStyle = TextStyle(
                                        fontFamily: 'SourGummy',
                                        fontVariations: [FontVariation('wght', 300)],
                                        fontSize: 13,
                                        color: Colors.grey,
                                      );

                                      // This TextPainter is used as a "ghost text" to first check if the text is long enough and would need scrolling.
                                      final textPainter = TextPainter(
                                        text: TextSpan(
                                          text: audioController.getPlayingSongData('artist'),
                                          style: textStyle,
                                        ),
                                        textDirection: TextDirection.ltr,
                                      )..layout();

                                      final textWidth = textPainter.size.width;
                                      final availableWidth = constraints.maxWidth;

                                      // If text fits, use regular Text widget, otherwise use Marquee
                                      if (textWidth <= availableWidth) {
                                        return Text(
                                          audioController.getPlayingSongData('artist'),
                                          style: textStyle,
                                          overflow: TextOverflow.ellipsis,
                                          maxLines: 1,
                                        );
                                      } else {
                                        return Marquee(
                                          text: audioController.getPlayingSongData('artist'),
                                          style: textStyle,
                                          scrollAxis: Axis.horizontal,
                                          blankSpace: 30,
                                          velocity: 25,
                                          showFadingOnlyWhenScrolling: true,
                                          fadingEdgeStartFraction: 0.1,
                                          fadingEdgeEndFraction: 0.1,
                                          accelerationDuration: Duration.zero,
                                          decelerationDuration: Duration.zero,
                                        );
                                      }
                                    },
                                  ),
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
                        SizedBox(
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
                              setState(() {});
                            },
                            child: Icon(
                              Icons.skip_previous,
                              color: Theme.of(context).colorScheme.tertiary,
                            ),
                          ),
                        ),

                        // Play/pause button
                        SizedBox(
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
                              setState(() {});
                            },
                            child: Icon(
                              audioController.isPlaying() ? Icons.pause : Icons.play_arrow,
                              color: Theme.of(context).colorScheme.tertiary,
                            ),
                          ),
                        ),

                        // Skip to next song button
                        SizedBox(
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
                              setState(() {});
                            },
                            child: Icon(
                              Icons.skip_next,
                              color: Theme.of(context).colorScheme.tertiary,
                            ),
                          ),
                        ),

                        const Padding(padding: EdgeInsets.only(right: 10)), // Padding after buttons
                      ],
                    ),
                  ],
                ),
              ),
            ),
          ),
        );
      },
    );
  }
}