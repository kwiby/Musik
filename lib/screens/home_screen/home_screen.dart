import 'package:flutter/material.dart';
import 'package:musik/misc/page_navigator.dart';
import 'package:google_fonts/google_fonts.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      onPopInvokedWithResult: (didPop, dynamic) {
        if (PageNavigator.pageHistory.isNotEmpty) {
          PageNavigator.backButton(context);
        }
      },
      child: Scaffold(
        backgroundColor: Theme.of(context).colorScheme.surface,

        appBar: AppBar(
          title: Text('Home', style: GoogleFonts.roboto(fontSize: 20)),
          foregroundColor: Theme.of(context).colorScheme.onSurface,
          centerTitle: true,
          shape: const Border(
            bottom: BorderSide(color: Colors.transparent)
          )
        ),
      ),
    );
  }
}