import 'dart:developer';

import 'package:just_audio/just_audio.dart';
import 'package:permission_handler/permission_handler.dart';

class AudioController {
  late AudioPlayer _player;

  Future<void> init() async {
    _player = AudioPlayer();

    _setupAudioPlayer();
  }

  Future<void> _setupAudioPlayer() async {
    _player.playbackEventStream.listen((event) {},
        onError: (Object error, StackTrace stackTrace) {
          log("A playback event stream error occurred: $error");
        });

    try {
      _player.setAudioSource(AudioSource.file(""));
    } catch (error) {
      log("There was an error loading the audio source: $error");
    }
  }

  void setPlayer(AudioPlayer player) {
    player = _player;
  }

  Future<void> requestStoragePermission() async {
    PermissionStatus status = await Permission.manageExternalStorage.request();

    if (status.isGranted) {
      log("granted");
    } else if (status.isDenied) {
      log("denied");
    }
  }

  void playAndPause() {
    if (_player.playing) {
      _player.pause();
    } else {
      _player.play();
    }
  }

  void seek(double value) {
    _player.seek(Duration(seconds: value.toInt()));
  }

  String formatDuration(Duration dur) {
    final minutes = dur.inMinutes.remainder(60);
    final seconds = dur.inSeconds.remainder(60);

    return "${minutes.toString().padLeft(2, '0')}:${seconds.toString().padRight(2, '0')}";
  }

  /*
  Duration position = Duration.zero;
  Duration duration = Duration.zero;

  @override
  void initState() {
    super.initState();
    player.setAsset('assets/');

    player.positionStream.listen((pos) {
      setState(() => position = pos);
    });

    player.durationStream.listen((dur) {
      setState(() => duration = dur!);
    });
  }
  */
}

final audioController = AudioController();