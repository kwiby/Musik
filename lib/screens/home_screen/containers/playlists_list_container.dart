import 'package:flutter/material.dart';

class PlaylistsListContainer extends Container {
  PlaylistsListContainer({super.key});

  @override
  Widget build(BuildContext context) {
    return SizedBox(
      height: 600,
      child: Container(
        margin: const EdgeInsets.only(
          top: 30,
          bottom: 10,
          left: 10,
          right: 10,
        ),
        decoration: BoxDecoration(
          color: Theme.of(context).colorScheme.primaryContainer,
          borderRadius: const BorderRadius.only(
            bottomLeft: Radius.circular(15),
            bottomRight: Radius.circular(15),
            topLeft: Radius.circular(50),
            topRight: Radius.circular(50),
          ),
        ),
        child: const Stack(
        ),
      ),
    );
  }
}