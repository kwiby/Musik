import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:musik/misc/page_navigator.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      onPopInvokedWithResult: (didPop, dynamic) {
        if (PageNavigator.pageHistory.isNotEmpty) {
          PageNavigator.backButton(context);
        } else {
          SystemNavigator.pop();
        }
      },
      child: Scaffold(
        backgroundColor: Theme.of(context).colorScheme.surface,

        appBar: AppBar(
          title: const Text('Musik', style: TextStyle(fontFamily: 'SourGummy', fontSize: 30, fontWeight: FontWeight.bold)),
          foregroundColor: Theme.of(context).colorScheme.onSurface,
          centerTitle: false,
          shape: const Border(
            bottom: BorderSide(color: Colors.transparent)
          )
        ),
      ),
    );
  }
}