import 'package:flutter/material.dart';
import 'package:musik/screens/home_screen/tabs/all_music_tab/all_music_tab.dart';

class AddMusicContainer extends StatelessWidget {
  const AddMusicContainer({super.key});

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Padding(padding: EdgeInsets.only(top: 50)), // Top Padding

        // -=-  Back Button  -=-
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
          ),
        ),

        const Padding(padding: EdgeInsets.symmetric(vertical: 5)),

        // -=-  List of Audio Files  -=-
        Expanded(
          child: StretchingOverscrollIndicator(
            axisDirection: AxisDirection.down,
            child: ListView.separated(
              padding: const EdgeInsets.only(
                left: 30,
                right: 30,
              ),
              scrollDirection: Axis.vertical,
              itemCount: 15,
              itemBuilder: (BuildContext context, int index) {
                return Container(
                  height: 70,
                  color: Colors.red,
                );
              },
              separatorBuilder: (BuildContext context, int index) => const Divider(),
            ),
          ),
        ),
      ],
    );
  }
}