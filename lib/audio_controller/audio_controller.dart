import 'dart:developer';
import 'dart:typed_data';

import 'package:audio_service/audio_service.dart';
import 'package:just_audio/just_audio.dart';
import 'package:musik/misc/circular_doubly_linked_list.dart';

import '../screens/home_screen/home_screen.dart';

class AudioController {
  final _audioPlayer = AudioPlayer();
  late final CircularDoublyLinkedList songList;

  Future<void> init() async {
    _audioPlayer.processingStateStream.listen((state) {
      if (state == ProcessingState.completed) {
        isPlayingSongNotifier.value = false;
      }
    });
  }

  // Method for the song playing logic, and the fetching of song data.
  Future<void> playSong(dynamic songData, Uint8List decodedByte) async {
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
              extras: {
                'filePath': filePath,
                'decodedByte': decodedByte,
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

  // Method to retrieve whatever song data is available based on passed option.
  dynamic getPlayingSongData(String option) {
    final tag = _audioPlayer.sequenceState?.currentSource?.tag;

    switch (option) {
      case 'id':
        return tag == null ? '' : tag.id;
      case 'decodedByte':
        return tag == null ? null : tag.extras?['decodedByte'];
      case 'title':
        return tag == null ? '' : tag.title;
      case 'artist':
        return tag == null ? '' : tag.artist;
    }
  }

  // Method to check whether a song is currently playing or not.
  bool isPlaying() {
    return _audioPlayer.playing;
  }

  // Method to pause a song.
  void pause() {
    if (isPlaying()) {
      _audioPlayer.pause();
    } else {
      play();
    }
  }

  // Method to play a song.
  void play() {
    _audioPlayer.play();
    isPlayingSongNotifier.value = true;
  }

  // Method to skip to next song (and for when a song is deleted).
  void skipToNext() {
    _audioPlayer.stop();
    _audioPlayer.seekToNext();

    if (!_audioPlayer.playing) { // If seeking to next song did nothing, there is no more songs in queue, so hide the floating bar.
      isPlayingSongNotifier.value = false;
    }
  }

  // Method to skip to previous song.
  void skipToPrev() {
    _audioPlayer.seekToPrevious();

    if (!_audioPlayer.playing) { // If seeking to next song did nothing, there is no more songs in queue, so hide the floating bar.
      isPlayingSongNotifier.value = false;
    }
  }

  // Method to seek through a song.
  void seek(double seconds) {
    _audioPlayer.seek(Duration(seconds: seconds.toInt()));
  }
}

final audioController = AudioController();