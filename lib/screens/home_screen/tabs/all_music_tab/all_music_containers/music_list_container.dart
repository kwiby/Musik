import 'dart:convert';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:musik/audio_controller/audio_controller.dart';
import 'package:musik/misc/shared_prefs.dart';

import '../../../../../misc/album_art.dart';
import '../all_music_tab.dart';

class AllMusicListContainer extends StatefulWidget {
  const AllMusicListContainer({super.key});

  @override
  State<AllMusicListContainer> createState() => _AllMusicListContainerState();
}

class _AllMusicListContainerState extends State<AllMusicListContainer>{
  List<dynamic> _songs = [];
  final Map<String, Uint8List> _decodedBytes = {};

  @override
  void initState() {
    super.initState();

    _fetchAddedSongs();
  }

  // Method to fetch added songs from shared preferences.
  void _fetchAddedSongs() {
    //sharedPrefs.addedSongs = jsonEncode({}); // Don't do "= ''", but instead do "= jsonEncode({})"!
    _songs.clear();

    if (sharedPrefs.addedSongs.isNotEmpty) {
      _songs = jsonDecode(sharedPrefs.addedSongs).values.toList();

      _decodedBytes.clear();
      for (dynamic song in _songs) {
        //final cleanedBase64String = song['albumArtBase64'].replaceAll(RegExp(r'\s'), '');
        _decodedBytes.putIfAbsent(song['title'], () => base64Decode(song['albumArtBase64']));
      }
    }
  }

  // Method to remove selected songs.
  void _removeSongs() {
    final sortedSelectedIndexes = _selectedIndexes.toList()..sort((a, b) => b.compareTo(a)); // Sort the selected indexes from highest to lowest (prevents errors as '_songs' shifts while removing other songs)

    String? currentSongId = audioController.currentlyPlayingSongId();
    for (int index in sortedSelectedIndexes) {
      if (_songs[index]['id'].toString() == currentSongId) { // If the currently playing song id is equal to the currently deleting index id, stop and play next song in queue if available.
        audioController.stop();
      }

      _songs.removeAt(index);
    }
    _selectedIndexes.clear();

    Map<String, dynamic> selectedSongsMap = {};
    for (dynamic song in _songs) {
      selectedSongsMap.putIfAbsent(song['id'].toString(), () => song);
    }

    sharedPrefs.addedSongs = jsonEncode(selectedSongsMap);

    setState(() => _isInSelectionMode = false);
  }

  // Method to manage song playing and other audio logic.
  void _playSong(int index) {
    dynamic songData = _songs[index];

    audioController.playSong(songData);
  }

  bool _isInSelectionMode = false;
  final Set<int> _selectedIndexes = {};
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Padding(padding: EdgeInsets.only(top: 50)), // Top padding

        // -=-  Remove Song Button  -=-
        Row(
          mainAxisAlignment: MainAxisAlignment.end,
          children: [
            // -=-  Remove Song Button  -=-
            Visibility(
              visible: _isInSelectionMode,
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
                    Icons.delete_forever,
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
                  isAddingMusicNotifier.value = true;
                },
                child: Icon(
                  Icons.add,
                  color: Theme.of(context).colorScheme.tertiary,
                ),
              ),
            ),
          ],
        ),

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
            ) : ListView.separated(
              padding: const EdgeInsets.only(left: 15, right: 15, bottom: 70),
              scrollDirection: Axis.vertical,
              itemCount: _songs.length,
              itemBuilder: (BuildContext context, int index) {
                final song = _songs[index];

                return ClipRRect(
                  borderRadius: BorderRadius.circular(15),
                  child: Material(
                    color: !_selectedIndexes.contains(index) ? Colors.transparent : Theme.of(context).colorScheme.surface,
                    child: InkWell(
                      child: ListTile(
                        leading: ClipRRect(
                          borderRadius: BorderRadius.circular(10),
                          child: AlbumArtLoader.loadAlbumArt(song['albumArtBase64'], _decodedBytes[song['title']]!)
                        ),
                        title: Text(
                          song['title'] ?? 'Unknown Title',
                          style: TextStyle(
                            fontFamily: 'SourGummy',
                            fontVariations: const [FontVariation('wght', 500)],
                            fontSize: 15,
                            color: Theme.of(context).colorScheme.tertiary,
                          )
                        ),
                        subtitle: Text(
                          song['artist'] ?? "Unknown Artist",
                          style: const TextStyle(
                            fontFamily: 'SourGummy',
                            fontVariations: [FontVariation('wght', 300)],
                            fontSize: 13,
                          )
                        ),
                        trailing: Text(
                          song['duration'] != null
                              ? Duration(milliseconds: song['duration']).toString().split('.').first : 'Unknown Duration',
                          style: TextStyle(
                            fontFamily: 'SourGummy',
                            fontVariations: const [FontVariation('wght', 300)],
                            fontSize: 13,
                            color: Theme.of(context).colorScheme.tertiary,
                          )
                        ),
                        onTap: () {
                          if (_isInSelectionMode) {
                            setState(() {
                              if (_selectedIndexes.contains(index)) {
                                _selectedIndexes.remove(index);
                              } else {
                                _selectedIndexes.add(index);
                              }
                            });

                            if (_selectedIndexes.isEmpty) {
                              _isInSelectionMode = false;
                            }
                          } else {
                            _playSong(index);
                          }
                        },
                        onLongPress: () { // Users must long press to enter selection mode (instead of long pressing songs, users can just tap to add to selection)
                          _isInSelectionMode = true;

                          setState(() {
                            if (_selectedIndexes.contains(index)) {
                              _selectedIndexes.remove(index);
                            } else {
                              _selectedIndexes.add(index);
                            }
                          });

                          if (_selectedIndexes.isEmpty) {
                            _isInSelectionMode = false;
                          }
                        },
                      )
                    )
                  )
                );
              },
              separatorBuilder: (BuildContext context, int index) => const Divider(height: 15, thickness: 0.5),
            ),
          ),
        ),

        const Padding(padding: EdgeInsets.only(bottom: 00)), // Bottom padding
      ],
    );
  }
}