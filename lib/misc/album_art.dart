import 'dart:typed_data';
import 'package:flutter/material.dart';

class AlbumArtLoader {
  static Widget loadAlbumArt(String? base64String, Uint8List decodeByte) {
    if (base64String == null) {
      return const Icon(Icons.question_mark);
    } else {
      return Image.memory(decodeByte, fit: BoxFit.cover, width: 56, height: 56); // 56 is max height for ListTile for some reason.
    }
  }
}