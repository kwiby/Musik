import 'dart:convert';

import 'package:flutter/material.dart';

class AlbumArtLoader {
  static Widget loadAlbumArt(String? base64String) {
    if (base64String == null) {
      return const Icon(Icons.music_note);
    } else {
      final bytes = base64Decode(base64String.replaceAll(RegExp(r'\s+'), ''));

      return Image.memory(bytes, fit: BoxFit.cover);
    }
  }
}