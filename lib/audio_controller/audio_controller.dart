import 'dart:developer';

import 'package:audio_service/audio_service.dart';
import 'package:just_audio/just_audio.dart';

class AudioController {
  final _audioPlayer = AudioPlayer();

  Future<void> init() async {

  }

  Future<void> playSong(dynamic songData) async {
    _audioPlayer.playbackEventStream.listen((event) {},
        onError: (Object error, StackTrace stackTrace) {
          log('A stream error occurred {audio_controller.dart LINE 16}: $error');
        }
    );

    try {
      String filePath = songData['filePath'];

      await _audioPlayer.setAudioSource(
        AudioSource.uri(Uri.file(filePath),
          tag: MediaItem(
            id: songData['id'].toString(),
            title: songData['title'],
            artist: songData['artist'],
            duration: Duration(milliseconds: songData['duration']),
            artUri: songData['albumArtUri'],
            extras: {
              'filePath': filePath,
            }
          ),
        ),
        initialPosition: Duration.zero,
        preload: true,
      );

      _audioPlayer.play();
    } catch (error) {
      log('Error playing song {audio_controller.dart LINE 42}: $error');
    }
  }

  void pause() {
    if (_audioPlayer.playing) {
      _audioPlayer.pause();
    } else {
      _audioPlayer.play();
    }
  }

  void seek(double seconds) {
    _audioPlayer.seek(Duration(seconds: seconds.toInt()));
  }
}

final audioController = AudioController();