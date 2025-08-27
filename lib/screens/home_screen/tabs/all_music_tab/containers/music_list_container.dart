import 'dart:convert';
import 'dart:developer';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:musik/audio_controller/audio_controller.dart';
import 'package:musik/misc/forced_value_notifier.dart';
import 'package:musik/misc/loading_circle.dart';
import 'package:musik/misc/shared_prefs.dart';
import 'package:musik/models/add_music_model.dart';
import 'package:permission_handler/permission_handler.dart';
import '../../../../../misc/default_icon_loader.dart';
import '../../../../song_screen/misc/song_text.dart';
import '../all_music_tab.dart';

// Value notifiers
ForcedValueNotifier<bool> isLoadingNotifier = ForcedValueNotifier<bool>(true);
//ValueNotifier<Set<int>> _selectedIndexesNotifier = ValueNotifier<Set<int>>({});

// Song data
List<Map<String, dynamic>> _songs = [];
List<Map<String, dynamic>> _beforeMoveSongs = [];
final Map<String, Uint8List> _decodedBytes = {};


// Classes
class AllMusicListContainer extends StatefulWidget {
  const AllMusicListContainer({super.key});

  @override
  State<AllMusicListContainer> createState() => _AllMusicListContainerState();
}

bool _wasPlaylistSetup = false;
Set<int> _selectedIndexes = {};
class _AllMusicListContainerState extends State<AllMusicListContainer> with WidgetsBindingObserver {

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);

    _selectedIndexes.clear();
    _fetchAddedSongs();
  }

  // Code for widget binding observer (for permission status changing)
  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    if (state == AppLifecycleState.resumed) {
      _checkPermissionStatus();
    }
  }

  // Method to fetch added songs from shared preferences.
  void _fetchAddedSongs() {
    //sharedPrefs.addedSongs = jsonEncode({}); // Don't do "= ''", but instead do "= jsonEncode({})"!
    if (sharedPrefs.addedSongs.isNotEmpty) {
      List<Map<String, dynamic>> oldSongs = List.from(_songs);
      //_songs = jsonDecode(sharedPrefs.addedSongs) .values.toList();
      _songs = (jsonDecode(sharedPrefs.addedSongs).values.toList() as List).map((x) => x as Map<String, dynamic>).toList();

      if (!_areSongListsEqual(oldSongs, _songs)) {
        WidgetsBinding.instance.addPostFrameCallback((_) {
          _decodeImageBytes();
        });
      }
    } else {
      _songs.clear();
    }
  }

  // Helper method for _fetchAddedSongs()
  bool _areSongListsEqual(List<dynamic> a, List<dynamic> b) {
    if (a.length != b.length) return false;

    for (int i = 0; i < a.length; i++) {
      if (a[i]['id'] != b[i]['id']) return false;
    }

    return true;
  }

  // Method to decode image bytes.
  void _decodeImageBytes() {
    _decodedBytes.clear();

    for (Map<String, dynamic> song in _songs) {
      //final cleanedBase64String = song['albumArtBase64'].replaceAll(RegExp(r'\s'), '');
      final title = song['title'] as String;
      final albumArtBase64 = song['albumArtBase64'] as String?;

      if (albumArtBase64 != null) {
        _decodedBytes.putIfAbsent(title, () => base64Decode(albumArtBase64));
      } else {
        _decodedBytes.putIfAbsent(title, () => defaultIcon);
        log("Song album art base64 was not found {music_list_container.dart -> _fetchAddedSongs()}: $song");
      }
    }

    if (!_wasPlaylistSetup) {
      audioController.setupNewPlaylist(_songs, _decodedBytes);
      _wasPlaylistSetup = true;
    }

    setState(() {});
  }

  // Method to check storage permission.
  Future<void> _checkPermissionStatus() async {
    if (await Permission.audio.isGranted) {
      addMusicModel.setIsStoragePermissionGranted = true;
    } else {
      log("Storage permission is not granted! {music_list_container.dart -> _checkPermissionStatus()}");
    }

    isLoadingNotifier.value = false;
  }

  // Method to update the songs list in shared preferences confirming the moving of songs.
  Future<void> _updateSharedPrefsAfterMove() async {
    audioController.setupNewPlaylist(_songs, _decodedBytes);

    if (audioController.isPlaying()) {
      audioController.updateAfterMove(_songs);
    }

    await Future.delayed(Duration.zero);

    Map<String, dynamic> newSongsMap = {};
    for (Map<String, dynamic> song in _songs) {
      newSongsMap.putIfAbsent(song['id'].toString(), () => song);
    }

    sharedPrefs.addedSongs = jsonEncode(newSongsMap);
  }

  // Method to remove selected songs.
  void _removeSongs() {
    final sortedSelectedIndexes = _selectedIndexes.toList()..sort((a, b) => b.compareTo(a)); // Sort the selected indexes from highest to lowest (prevents errors as '_songs' shifts while removing other songs)

    String? currentSongId = audioController.getPlayingSongData('id');
    for (int index in sortedSelectedIndexes) {
      if (_songs[index]['id'].toString() == currentSongId) { // If the currently playing song id is equal to the currently deleting index id, stop and play next song in queue if available.
        audioController.stop();
      }

      audioController.remove(_songs[index], _decodedBytes[_songs[index]['title']]!);
      _songs.removeAt(index);
    }

    _selectedIndexes.clear();

    Map<String, dynamic> selectedSongsMap = {};
    for (Map<String, dynamic> song in _songs) {
      selectedSongsMap.putIfAbsent(song['id'].toString(), () => song);
    }

    sharedPrefs.addedSongs = jsonEncode(selectedSongsMap);

    setState(() {});
  }

  // Method to manage song playing and other audio logic.
  Future<void> _playSong(int index) async {
    await audioController.playCurrentSong(_songs[index]);
  }

  // Method to toggle selected tile background colours.
  void toggleSelection(int index) {
    if (_isInMoveMode) {
      setState(() => _isInMoveMode = false);
    }

    final selected = Set<int>.from(_selectedIndexes);

    if (selected.contains(index)) {
      selected.remove(index);
    } else {
      selected.add(index);
    }

    setState(() => _selectedIndexes = selected);
  }

  // Helper method for confirming song moving.
  bool _hasSongsChangedAfterMove() {
    for (int i = 0; i < _songs.length; i++) {
      if (_songs[i]['id'] != _beforeMoveSongs[i]['id']) {
        return true;
      }
    }

    return false;
  }

  // -=-  Main Widget  -=-
  bool _isInMoveMode = false;
  @override
  Widget build(BuildContext context) {
    return ValueListenableBuilder<bool>(
      valueListenable: isLoadingNotifier,
      builder: (BuildContext context, bool value, Widget? child) {
        return isLoadingNotifier.value == true ? const LoadingCircle()
            : addMusicModel.getIsStoragePermissionGranted == false
            ? Container(
            padding: const EdgeInsets.only(top: 100),
            child: Column(
                children: [
                  // -=-  No Audio Access Permission Message  -=-
                  Text(
                    "Allow audio permissions in settings",
                    style: TextStyle(
                      fontFamily: 'SourGummy',
                      fontVariations: const [FontVariation('wght', 400)],
                      fontSize: 17,
                      color: Theme.of(context).colorScheme.tertiary,
                    ),
                  ),

                  Row(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      // -=-  Open Settings Button  -=-
                      SizedBox(
                        width: 40,
                        child: ElevatedButton(
                          style: ButtonStyle(
                            padding: const WidgetStatePropertyAll(EdgeInsets.zero),
                            backgroundColor: WidgetStateColor.transparent,
                            shadowColor: WidgetStateColor.transparent,
                            shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
                          ),
                          onPressed: () {
                            openAppSettings();
                          },
                          child: Icon(
                            Icons.settings_rounded,
                            color: Theme.of(context).colorScheme.tertiary,
                          ),
                        ),
                      ),

                      // -=-  Refresh Button (Not audio access permission)  -=-
                      SizedBox(
                        width: 40,
                        child: ElevatedButton(
                          style: ButtonStyle(
                            padding: const WidgetStatePropertyAll(EdgeInsets.zero),
                            backgroundColor: WidgetStateColor.transparent,
                            shadowColor: WidgetStateColor.transparent,
                            shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
                          ),
                          onPressed: () {
                            _checkPermissionStatus();
                          },
                          child: Icon(
                            Icons.refresh_rounded,
                            color: Theme.of(context).colorScheme.tertiary,
                          ),
                        ),
                      ),
                    ],
                  ),
                ]
            )
        ) : Column(
          children: [
            const Padding(padding: EdgeInsets.only(top: 50)), // Top padding

            // -=-  Buttons  -=-
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                // Editing buttons
                Row(
                  children: [
                    // Move songs button
                    Container(
                      width: 40,
                      margin: const EdgeInsets.only(left: 12),
                      child: ElevatedButton(
                        style: ButtonStyle(
                          padding: const WidgetStatePropertyAll(EdgeInsets.zero),
                          backgroundColor: WidgetStateColor.transparent,
                          shadowColor: WidgetStateColor.transparent,
                          shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
                        ),
                        onPressed: () {
                          if (_songs.isNotEmpty) {
                            _selectedIndexes.clear();

                            if (_isInMoveMode) {
                              if (_hasSongsChangedAfterMove()) {
                                _updateSharedPrefsAfterMove();
                              }

                              _isInMoveMode = false;
                            } else {
                              _beforeMoveSongs = _songs.map((song) => Map<String, dynamic>.from(song)).toList();

                              _isInMoveMode = true;
                            }

                            setState(() {});
                          }
                        },
                        child: Icon(
                          _isInMoveMode ? Icons.check_rounded : Icons.unfold_more_rounded,
                          color: Theme.of(context).colorScheme.tertiary,
                        ),
                      ),
                    ),

                    // -=-  Remove Song Button  -=-
                    Visibility(
                      visible: _selectedIndexes.isNotEmpty,
                      child: SizedBox(
                        width: 40,
                        child: ElevatedButton(
                          style: ButtonStyle(
                            padding: const WidgetStatePropertyAll(EdgeInsets.zero),
                            backgroundColor: WidgetStateColor.transparent,
                            shadowColor: WidgetStateColor.transparent,
                            shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
                          ),
                          onPressed: () { // Logic for when the user removes selected songs.
                            _removeSongs();
                          },
                          child: Icon(
                            Icons.delete_outline_rounded,
                            color: Theme.of(context).colorScheme.tertiary,
                          ),
                        ),
                      ),
                    ),
                  ],
                ),

                // Adding buttons
                Row(
                  children: [
                    // Add to playlist button
                    Visibility(
                      visible: _selectedIndexes.isNotEmpty,
                      child: SizedBox(
                        width: 40,
                        child: ElevatedButton(
                          style: ButtonStyle(
                            padding: const WidgetStatePropertyAll(EdgeInsets.zero),
                            backgroundColor: WidgetStateColor.transparent,
                            shadowColor: WidgetStateColor.transparent,
                            shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
                          ),
                          onPressed: () { // Logic for when the user removes selected songs.
                            _removeSongs();
                          },
                          child: Icon(
                            Icons.playlist_add_rounded,
                            color: Theme.of(context).colorScheme.tertiary,
                          ),
                        ),
                      ),
                    ),

                    // -=-  Add Song Button  -=-
                    Container(
                      width: 40,
                      margin: const EdgeInsets.only(right: 12),
                      child: ElevatedButton(
                        style: ButtonStyle(
                          padding: const WidgetStatePropertyAll(EdgeInsets.zero),
                          backgroundColor: WidgetStateColor.transparent,
                          shadowColor: WidgetStateColor.transparent,
                          shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
                        ),
                        onPressed: () {
                          _selectedIndexes.clear();
                          isAddingMusicNotifier.value = true;
                        },
                        child: Icon(
                          Icons.add_rounded,
                          color: Theme.of(context).colorScheme.tertiary,
                        ),
                      ),
                    ),
                  ],
                ),
              ],
            ),

            const Padding(padding: EdgeInsets.symmetric(vertical: 5)),

            // -=-  Music List  -=-
            Expanded(
              child: StretchingOverscrollIndicator(
                axisDirection: AxisDirection.down,
                child: _songs.isEmpty ? Align(
                    alignment: Alignment.topCenter,
                    child: Text(
                        "No songs added",
                        style: TextStyle(
                          fontFamily: 'SourGummy',
                          fontVariations: const [FontVariation('wght', 400)],
                          fontSize: 17,
                          color: Theme.of(context).colorScheme.tertiary,
                        )
                    )
                ) : ReorderableListView.builder(
                  padding: const EdgeInsets.only(left: 15, right: 15, bottom: 70),
                  scrollDirection: Axis.vertical,
                  itemCount: _songs.length,
                  proxyDecorator: (child, index, animation) => child,
                  itemBuilder: (BuildContext context, int index) {
                    final song = _songs[index];

                    return Column(
                      key: ValueKey(song['id']),
                      children: [
                        ClipRRect(
                            borderRadius: BorderRadius.circular(15),
                            child: Material(
                                color: !_selectedIndexes.contains(index) ? Colors.transparent : Theme.of(context).colorScheme.surface,
                                child: InkWell(
                                    onTap: () async {
                                      if (_selectedIndexes.isNotEmpty) {
                                        toggleSelection(index);
                                      } else {
                                        await _playSong(index);
                                      }
                                    },
                                    onLongPress: () {
                                      toggleSelection(index);
                                    },
                                    child: ListTile(
                                      leading: Row(
                                        mainAxisSize: MainAxisSize.min,
                                        children: [
                                          // Move mode stuff
                                          _isInMoveMode ? ReorderableDragStartListener(
                                            index: index,
                                            child: const Icon(
                                              Icons.drag_indicator_rounded,
                                              color: Colors.grey,
                                              size: 25,
                                            ),
                                          ) : const SizedBox.shrink(),

                                          _isInMoveMode
                                              ? const Padding(padding: EdgeInsets.only(right: 5))
                                              : const Padding(padding: EdgeInsets.zero),

                                          // Song image
                                          ClipRRect(
                                            borderRadius: BorderRadius.circular(10),
                                            child: _decodedBytes.containsKey(song['title'])
                                                ? Image(image: MemoryImage(_decodedBytes[song['title']]!), fit: BoxFit.cover, height: 56, width: 56)
                                                : Image(image: MemoryImage(defaultIcon), fit: BoxFit.cover, height: 56, width: 56),
                                          ),
                                        ],
                                      ),
                                      title: SongText().getTitleText(song['title'], 15, 20),
                                      subtitle: SongText().getArtistText(song['artist'], 13, 20),
                                      trailing: Text(
                                        song['duration'] != null
                                            ? Duration(milliseconds: song['duration']).toString().split('.').first
                                            : 'Unknown Duration',
                                        style: TextStyle(
                                          fontFamily: 'SourGummy',
                                          fontVariations: const [FontVariation('wght', 300)],
                                          fontSize: 13,
                                          color: Theme.of(context).colorScheme.tertiary,
                                        ),
                                      ),
                                    )
                                )
                            )
                        ),

                        // Divider
                        if (index < _songs.length - 1) const Divider(height: 15, thickness: 0.5, indent: 80, endIndent: 5),
                      ],
                    );
                  },
                  onReorder: (oldIndex, newIndex) {
                    if (_isInMoveMode) {
                      bool doAddAfter = newIndex > oldIndex;
                      if (doAddAfter) {
                        newIndex -= 1;
                      }

                      //audioController.swap(_songs[oldIndex], _songs[newIndex], doAddAfter: doAddAfter);

                      final song = _songs.removeAt(oldIndex);
                      _songs.insert(newIndex, song);

                      setState(() {});
                    }
                  },
                  //separatorBuilder: (BuildContext context, int index) => const Divider(height: 15, thickness: 0.5),
                ),
              ),
            ),

            const Padding(padding: EdgeInsets.only(bottom: 00)), // Bottom padding
          ],
        );
      },
    );
  }
}