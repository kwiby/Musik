import 'dart:developer';
import 'dart:typed_data';

import 'package:audio_service/audio_service.dart';
import 'package:just_audio/just_audio.dart';
import 'package:musik/misc/circular_doubly_linked_list.dart';

import '../screens/home_screen/home_screen.dart';

class AudioController {
  final _audioPlayer = AudioPlayer();
  final CircularDoublyLinkedList _playlist = CircularDoublyLinkedList();
  late Node _currentSong;

  bool _isLooping = false;
  bool _isShuffling = false;

  Future<void> init() async {
    await _audioPlayer.setAudioSource(ConcatenatingAudioSource(children: [])); // Set an empty audio source to load the player for the first time in the app's lifecycle, preventing freezing on first song playing (on its loading).

    // Song completion listener.
    _audioPlayer.processingStateStream.listen((state) async {
      if (state == ProcessingState.completed) {
        if (_isLooping) {
          playNewSongFromPlaylist();
        } else {
          await skipToNext();
        }

        if (!isPlaying()) {
          isPlayingSongNotifier.value = false;
        }
      }
    });
  }

  // Method to setup the circular doubly linked list for the playlist (only used when the app is initialized).
  void setupNewPlaylist(List<dynamic> songs, Map<String, Uint8List> decodedBytes) {
    _playlist.clear();

    for (int index = 0; index < songs.length; index++) {
      dynamic songData = songs[index];
      String songTitle = songData['title'];

      List<dynamic> song = [songData, decodedBytes[songTitle]];

      _playlist.addEnd(song);
    }
  }

  // Method to find and set the current song to the clicked on song.
  Future<void> playCurrentSong(Map<String, dynamic> songData) async {
    Node? songNode = _playlist.getNode(songData);

    if (songNode != null) {
      _currentSong = songNode;
      await playNewSongFromPlaylist();
    } else {
      log('Retrieved null value when getting current song node {audio_controller.dart -> playCurrentSong()}');
    }
  }

  // Method to play the next song in the playlist.
  Future<void> playNewSongFromPlaylist() async {
    List<dynamic> songData = _currentSong.value;
    await playSong(songData[0], songData[1]);
  }

  // Method for the song playing logic, and the fetching of song data.
  Future<void> playSong(Map<String, dynamic> songData, Uint8List decodedByte) async {
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
      log('Error playing song {audio_controller.dart -> playSong()}: $error');
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
      case 'filePath':
        return tag == null ? '' : tag.extras?['filePath'];
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
      play();
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
      if (_isShuffling) {
        _currentSong = _playlist.getRandomNode(_currentSong)!;
      } else {
        _currentSong = _currentSong.next!;
      }

      await playNewSongFromPlaylist();
    }
  }

  // Method to skip to previous song.
  Future<void> skipToPrev() async {
    if (!_playlist.isEmpty) {
      if (_isShuffling) {
        _currentSong = _playlist.getRandomNode(_currentSong)!;
      } else {
        _currentSong = _currentSong.prev!;
      }

      await playNewSongFromPlaylist();
    }
  }

  // Method to seek through a song.
  Future<void> seek(double milliseconds) async {
    await _audioPlayer.seek(Duration(milliseconds: milliseconds.toInt()));
  }

  // Method to toggle looping.
  void toggleLooping() {
    _isLooping = !_isLooping;
  }

  // Method to toggle shuffling.
  void toggleShuffling() {
    _isShuffling = !_isShuffling;
  }

  // Method to get isLooping boolean.
  bool getIsLooping() {
    return _isLooping;
  }

  // Method to get isShuffling boolean.
  bool getIsShuffling() {
    return _isShuffling;
  }

  // Method to get current position of song
  Duration getPosition() {
    return _audioPlayer.position;
  }

  // Method to get duration of song
  Duration getDuration() {
    return _audioPlayer.duration!;
  }

  // Method to get the audio player.
  AudioPlayer getAudioPlayer() {
    return _audioPlayer;
  }
}

final audioController = AudioController();