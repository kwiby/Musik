import 'dart:developer';

import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

class AddMusicModel {
  static const platform = MethodChannel('com.example.audio/files');

  bool _isStoragePermissionGranted = false;
  List<Map<String, dynamic>> _originalAudioFiles = [];

  Future<void> init(bool useCache) async {
    await requestStoragePermission(useCache);
  }

  // -=-  Getter Methods  -=-
  get getIsStoragePermissionGranted {
    return _isStoragePermissionGranted;
  }

  get getOriginalAudioFiles {
    return _originalAudioFiles;
  }

  // -=-  Processing Methods  -=-
  // Logic to acquire storage access.
  Future<void> requestStoragePermission(bool useCache) async {
    final status = await Permission.audio.request();

    _isStoragePermissionGranted = false;
    if (status.isGranted) {
      _isStoragePermissionGranted = true;
      await fetchAudioFiles(useCache);
    } else {
      log("Storage permission was denied!");
    }
  }

  // Logic to acquire all audio files and their data.
  Future<void> fetchAudioFiles(bool useCache) async {
    try {
      final List<dynamic> result = useCache ? await platform.invokeMethod('getAudioFilesWithCache') : await platform.invokeMethod('getAudioFilesWithoutCache');

      _originalAudioFiles = result.map<Map<String, dynamic>>((item) {
        final map = Map<Object?, Object?>.from(item);
        return map.map<String, dynamic>((key, value) => MapEntry(key.toString(), value));
      }).toList();
    } on PlatformException catch (error) {
      log("Failed to get audio files: $error");
    }
  }
}

final addMusicModel = AddMusicModel();