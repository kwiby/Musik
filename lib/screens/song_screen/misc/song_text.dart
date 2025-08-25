import 'package:flutter/material.dart';
import 'package:marquee/marquee.dart';

class SongText {
  Widget getTitleText(String text, double fontSize, double height) {
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
                text: text,
                style: textStyle,
              ),
              textDirection: TextDirection.ltr,
            )..layout();

            final textWidth = textPainter.size.width;
            final availableWidth = constraints.maxWidth;

            // If text fits, use regular Text widget, otherwise use Marquee
            if (textWidth <= availableWidth) {
              return Text(
                text,
                style: textStyle,
                overflow: TextOverflow.ellipsis,
                maxLines: 1,
              );
            } else {
              return Marquee(
                text: text,
                style: textStyle,
                scrollAxis: Axis.horizontal,
                blankSpace: 30,
                velocity: 25,
                showFadingOnlyWhenScrolling: true,
                fadingEdgeStartFraction: 0.1,
                fadingEdgeEndFraction: 0.1,
                accelerationDuration: Duration.zero,
                decelerationDuration: Duration.zero,
                pauseAfterRound: const Duration(seconds: 1),
              );
            }
          },
        ),
      ),
    );
  }

  Widget getArtistText(String text, double fontSize, double height) {
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
                text: text,
                style: textStyle,
              ),
              textDirection: TextDirection.ltr,
            )..layout();

            final textWidth = textPainter.size.width;
            final availableWidth = constraints.maxWidth;

            // If text fits, use regular Text widget, otherwise use Marquee
            if (textWidth <= availableWidth) {
              return Text(
                text,
                style: textStyle,
                overflow: TextOverflow.ellipsis,
                maxLines: 1,
              );
            } else {
              return Marquee(
                text: text,
                style: textStyle,
                scrollAxis: Axis.horizontal,
                blankSpace: 30,
                velocity: 25,
                showFadingOnlyWhenScrolling: true,
                fadingEdgeStartFraction: 0.1,
                fadingEdgeEndFraction: 0.1,
                accelerationDuration: Duration.zero,
                decelerationDuration: Duration.zero,
                pauseAfterRound: const Duration(seconds: 1),
              );
            }
          },
        ),
      ),
    );
  }
}