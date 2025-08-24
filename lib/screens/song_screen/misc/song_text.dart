import 'package:flutter/material.dart';
import 'package:marquee/marquee.dart';

import '../../../audio_controller/audio_controller.dart';

class SongText {
  Widget getTitleText(double fontSize, double height) {
    return Material(
      color: Colors.transparent,
      child: SizedBox(
        height: height,
        child: LayoutBuilder(
          builder: (context, constraints) {
            final textStyle = TextStyle(
              fontFamily: 'SourGummy',
              fontVariations: const [FontVariation('wght', 400)],
              fontSize: fontSize,
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
    );
  }

  Widget getArtistText(double fontSize, double height) {
    return Material(
      color: Colors.transparent,
      child: SizedBox(
        height: height,
        child: LayoutBuilder(
          builder: (context, constraints) {
            final textStyle = TextStyle(
              fontFamily: 'SourGummy',
              fontVariations: const [FontVariation('wght', 300)],
              fontSize: fontSize,
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
    );
  }
}