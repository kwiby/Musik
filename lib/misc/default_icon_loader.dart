import 'package:flutter/services.dart';

class DefaultIconLoader {
  static Future<Uint8List> loadDefaultIcon() async {
    final byteData = await rootBundle.load('assets/icons/Musik_Icon.png');
    final uInt8List = byteData.buffer.asUint8List();

    return uInt8List;
  }
}