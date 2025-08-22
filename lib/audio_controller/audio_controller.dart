import 'dart:developer';
import 'dart:typed_data';

import 'package:audio_service/audio_service.dart';
import 'package:just_audio/just_audio.dart';
import 'package:musik/misc/circular_doubly_linked_list.dart';

import '../misc/default_icon_loader.dart';
import '../screens/home_screen/home_screen.dart';

class AudioController {
  final _audioPlayer = AudioPlayer();
  final CircularDoublyLinkedList _playlist = CircularDoublyLinkedList();
  late Node _currentSong;
  late Uint8List defaultIcon;

  Future<void> init() async {
    _audioPlayer.processingStateStream.listen((state) async {
      if (state == ProcessingState.completed) {
        await skipToNext();

        if (!isPlaying()) {
          isPlayingSongNotifier.value = false;
        }
      }
    });

    defaultIcon = await DefaultIconLoader.loadDefaultIcon();
  }

  // Method to setup the circular doubly linked list for the playlist.
  Future<void> setupPlaylist(List<dynamic> songs, Map<String, Uint8List> decodedBytes, int startingIndex) async {
    _playlist.clear();

    final int numOfSongs = songs.length;

    int index = startingIndex;
    while (_playlist.size != numOfSongs) {
      dynamic songData = songs[index];
      String songTitle = songData['title'];

      List<dynamic> song = [songData, decodedBytes[songTitle]];

      _playlist.addEnd(song);

      index = (index + 1) % numOfSongs;
    }

    _currentSong = _playlist.getStart()!;
    await playNewSongFromPlaylist();
  }

  // Method to play the next song in the playlist.
  Future<void> playNewSongFromPlaylist() async {
    List<dynamic> songData = _currentSong.value;
    await playSong(songData[0], songData[1]);
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

      await play();
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
  Future<void> pause() async {
    if (isPlaying()) {
      await _audioPlayer.pause();
    } else {
      await play();
    }
  }

  // Method to add a song.
  Future<void> addAfter(Map<String, dynamic>? previousSong, Map<String, dynamic> newSongData, Uint8List decodedByte) async {
    List<dynamic> song = [newSongData, decodedByte];

    _playlist.addAfter(previousSong, song);
  }

  // Method to remove a song.
  Future<void> remove(Map<String, dynamic> songData, Uint8List decodedByte) async {
    List<dynamic> song = [songData, decodedByte];

    _playlist.remove(song);
  }

  // Method to play a song.
  Future<void> play() async {
    isPlayingSongNotifier.value = true;
    await _audioPlayer.play();
  }

  // Method stop a song.
  Future<void> stop() async {
    await _audioPlayer.stop();
    isPlayingSongNotifier.value = false;
  }

  // Method to skip to next song.
  Future<void> skipToNext() async {
    if (!_playlist.isEmpty) {
      _currentSong = _currentSong.next!;
      await playNewSongFromPlaylist();
    }
  }

  // Method to skip to previous song.
  Future<void> skipToPrev() async {
    if (!_playlist.isEmpty) {
      _currentSong = _currentSong.prev!;
      await playNewSongFromPlaylist();
    }
  }

  // Method to seek through a song.
  Future<void> seek(double seconds) async {
    await _audioPlayer.seek(Duration(seconds: seconds.toInt()));
  }
}

final audioController = AudioController();