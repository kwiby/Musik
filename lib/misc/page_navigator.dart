import 'dart:collection';

import 'package:flutter/material.dart';
import 'package:musik/screens/home_screen/home_screen.dart';

class PageNavigator {
  static const _pageHistoryLimit = 10;
  static final Queue<Widget> pageHistory = Queue();
  static Widget? nextPage;

  static void changePage(BuildContext context, Widget newPage) {
    Navigator.pushReplacement(context, PageRouteBuilder(
      pageBuilder: (context, animation1, animation2) => newPage,
      transitionDuration: Duration.zero,
      reverseTransitionDuration: Duration.zero,
    ));
  }

  static void navigatePage(BuildContext context, Widget newPage) {
    if (nextPage != null) {
      if ((pageHistory.isEmpty && newPage is! HomeScreen) ||
          (pageHistory.isNotEmpty && pageHistory.first != nextPage)) {
        pageHistory.addFirst(nextPage!);

        if (pageHistory.length > _pageHistoryLimit) {
          pageHistory.removeLast();
        }
      }
    }

    if (!(pageHistory.isEmpty && newPage is HomeScreen)) {
      nextPage = newPage;
    }
    changePage(context, newPage);
  }

  static void backButton(BuildContext context) {
    if (pageHistory.isEmpty && context.widget is! HomeScreen) {
      navigatePage(context, const HomeScreen());
    } else {
      if (pageHistory.first == context.widget) {
        pageHistory.removeFirst();
      }

      if (pageHistory.isNotEmpty) {
        navigatePage(context, pageHistory.first);
        pageHistory.removeFirst();
      } else {
        navigatePage(context, const HomeScreen());
      }
    }
  }
}