import 'package:flutter/material.dart';

class Watermark extends StatefulWidget implements PreferredSizeWidget {
  const Watermark({super.key});

  @override
  State<Watermark> createState() => _WatermarkState();

  @override
  Size get preferredSize => const Size.fromHeight(100);
}

class _WatermarkState extends State<Watermark> {
  @override
  Widget build(BuildContext context) {
    return AppBar(
      title: const Text(
        'Musik',
        style: TextStyle(
          fontFamily: 'SourGummy',
          fontSize: 30,
          fontVariations: [FontVariation('wght', 900)], // 'wght' = weight, 'wdth' = width
        ),
      ),
      toolbarHeight: 100,
      foregroundColor: Theme.of(context).colorScheme.outline, // 'Musik' title
      centerTitle: false,
      elevation: 0,
    );
  }
}