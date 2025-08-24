import 'package:flutter/services.dart';

late final Uint8List defaultIcon;

Future<void> loadDefaultIcon() async {
  final byteData = await rootBundle.load('assets/icons/Musik_Icon.png');
  final uInt8List = byteData.buffer.asUint8List();

  defaultIcon = uInt8List;
}