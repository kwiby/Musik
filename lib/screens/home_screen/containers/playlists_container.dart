import 'package:flutter/material.dart';
import 'package:musik/screens/home_screen/home_screen.dart';

class PlaylistsContainer extends Container {
  PlaylistsContainer({super.key});

  @override
  Widget build(BuildContext context) {
    return Stack(
        children: [
          Container(
            margin: const EdgeInsets.only(
              top: 30,
            ),
            decoration: BoxDecoration(
              color: Theme.of(context).colorScheme.primaryContainer,
              borderRadius: const BorderRadius.only(
                topLeft: Radius.circular(50),
                topRight: Radius.circular(50),
              ),
            ),
            child: const Stack(
            ),
          ),

          Positioned.fill(
            bottom: 543,
            child: Row(
              spacing: 30, // To keep the position of other boxes constant
              // while one of them is selected (and therefore bigger), you can
              // add a variable which is a number that subtracts from the
              // spacing to keep it the same all the time
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Container(
                  height: 50,
                  width: 80,
                  margin: const EdgeInsets.only(top: 13, bottom: 3),
                  decoration: BoxDecoration(
                    color: Theme.of(context).colorScheme.primaryContainer,
                    borderRadius: const BorderRadius.all(Radius.circular(10)),
                    boxShadow: [BoxShadow(
                      blurRadius: 1,
                      offset: const Offset(0, -3),
                      color: Theme.of(context).colorScheme.shadow,
                    )],
                  ),
                  child: ElevatedButton(
                    style: const ButtonStyle(
                      padding: WidgetStatePropertyAll(EdgeInsets.zero),
                      backgroundColor: WidgetStateColor.transparent,
                      shadowColor: WidgetStateColor.transparent,
                    ),
                    child: Text(
                      'All Music',
                      style: TextStyle(
                        fontFamily: 'SourGummy',
                        fontVariations: const [FontVariation('wght', 300)],
                        fontSize: 15,
                        color: Theme.of(context).colorScheme.tertiary,
                      ),
                    ),
                    onPressed: () {
                      tabNotifier.value = 'All Music';
                    },
                  ),
                ),

                Container(
                  height: 50,
                  width: 80,
                  decoration: BoxDecoration(
                    color: Theme.of(context).colorScheme.primaryContainer,
                    borderRadius: const BorderRadius.all(Radius.circular(10)),
                    boxShadow: [BoxShadow(
                      blurRadius: 1,
                      offset: const Offset(0, -3),
                      color: Theme.of(context).colorScheme.shadow,
                    )],
                  ),
                  child: ElevatedButton(
                    style: const ButtonStyle(
                      padding: WidgetStatePropertyAll(EdgeInsets.zero),
                      backgroundColor: WidgetStateColor.transparent,
                      shadowColor: WidgetStateColor.transparent,
                    ),
                    child: Text(
                      'Playlists',
                      style: TextStyle(
                        fontFamily: 'SourGummy',
                        fontVariations: const [FontVariation('wght', 600)],
                        fontSize: 15,
                        color: Theme.of(context).colorScheme.tertiary,
                      ),
                    ),
                    onPressed: () {
                      tabNotifier.value = 'Playlists';
                    },
                  ),
                ),
              ],
            ),
          ),
        ],
    );
  }
}