import 'package:flutter/material.dart';
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
                  children: [
                    const Padding(padding: EdgeInsets.only(right: 5)),

                    CircleAvatar(
                      radius: 25,
                      backgroundImage: audioController.getPlayingSongData('decodedByte') != null
                        ? MemoryImage(audioController.getPlayingSongData('decodedByte'))
                        : null,
                      child: audioController.getPlayingSongData('decodedByte') == null
                        ? const Icon(Icons.question_mark)
                        : null,
                    ),

                    const Padding(padding: EdgeInsets.only(right: 10)),

                    Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          audioController.getPlayingSongData('title'),
                          style: TextStyle(
                            fontFamily: 'SourGummy',
                            fontVariations: const [FontVariation('wght', 400)],
                            fontSize: 15,
                            color: Theme.of(context).colorScheme.tertiary,
                          )
                        ),

                        Text(
                            audioController.getPlayingSongData('artist'),
                            style: const TextStyle(
                              fontFamily: 'SourGummy',
                              fontVariations: [FontVariation('wght', 300)],
                              fontSize: 13,
                              color: Colors.grey,
                            )
                        ),
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