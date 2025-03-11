import 'package:flutter/material.dart';
import 'package:musik/screens/home_screen/tabs/all_music_tab/all_music_tab.dart';

class AddMusicContainer extends StatelessWidget {
  const AddMusicContainer({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(
        top: 50,
        bottom: 30,
      ),
      decoration: const BoxDecoration(
        //color: Colors.blueAccent, // DEBUG BACKGROUND COLOUR
        borderRadius: BorderRadius.all(Radius.circular(5)),
      ),
      child: Stack(
          children: [
            // -=-  Add Music Button  -=-
            Align(
                alignment: Alignment.topLeft,
                child: ElevatedButton(
                  style: ButtonStyle(
                    padding: const WidgetStatePropertyAll(EdgeInsets.zero),
                    backgroundColor: WidgetStateColor.transparent,
                    shadowColor: WidgetStateColor.transparent,
                    shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
                  ),
                  onPressed: () {
                    isAddingMusicNotifier.value = false;
                  },
                  child: Icon(
                    Icons.arrow_back_outlined,
                    color: Theme.of(context).colorScheme.tertiary,
                  ),
                )
            ),
          ]
      ),
    );
  }
}