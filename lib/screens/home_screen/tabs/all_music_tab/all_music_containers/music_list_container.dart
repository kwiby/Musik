import 'package:flutter/material.dart';
import '../all_music_tab.dart';

class AllMusicListContainer extends StatelessWidget {
  const AllMusicListContainer({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Padding(padding: EdgeInsets.only(top: 50)), // Top Padding
        // -=-  Add Music Button  -=-
        Align(
          alignment: Alignment.topRight,
          child: ElevatedButton(
            style: ButtonStyle(
              padding: const WidgetStatePropertyAll(EdgeInsets.zero),
              backgroundColor: WidgetStateColor.transparent,
              shadowColor: WidgetStateColor.transparent,
              shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
            ),
            onPressed: () {
              isAddingMusicNotifier.value = true;
            },
            child: Icon(
              Icons.add,
              color: Theme.of(context).colorScheme.tertiary,
            ),
          ),
        ),
      ],
    );
  }
}