import 'dart:developer';

import 'package:audio_service/audio_service.dart';
import 'package:just_audio/just_audio.dart';

import '../screens/home_screen/home_screen.dart';

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
            artUri: Uri.parse(songData['albumArtUri']),
            extras: {
              'filePath': filePath,
            }
          ),
        ),
        initialPosition: Duration.zero,
        preload: true,
      );

      play();
    } catch (error) {
      log('Error playing song {audio_controller.dart LINE 42}: $error');
    }
  }

  bool isPlaying() {
    return _audioPlayer.playing;
  }

  String? currentlyPlayingSongId() {
    return (_audioPlayer.sequenceState?.currentSource?.tag as MediaItem).id;
  }

  void stop() {
    _audioPlayer.stop();
    _audioPlayer.seekToNext();

    if (!_audioPlayer.playing) { // If seeking to next song did nothing, there is no more songs in queue, so hide the floating bar.
      isPlayingSongNotifier.value = false;
    }
  }

  void pause() {
    if (isPlaying()) {
      _audioPlayer.pause();
    } else {
      play();
    }
  }

  void play() {
    _audioPlayer.play();
    isPlayingSongNotifier.value = true;
  }

  void seek(double seconds) {
    _audioPlayer.seek(Duration(seconds: seconds.toInt()));
  }
}

final audioController = AudioController();