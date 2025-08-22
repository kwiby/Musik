import 'dart:convert';
import 'dart:developer';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:musik/audio_controller/audio_controller.dart';
import 'package:musik/misc/loading_circle.dart';
import 'package:musik/models/add_music_model.dart';
import 'package:musik/screens/home_screen/tabs/all_music_tab/all_music_tab.dart';
import 'package:permission_handler/permission_handler.dart';

import '../../../../../misc/album_art.dart';
import '../../../../../misc/shared_prefs.dart';

class AddMusicContainer extends StatefulWidget {
  const AddMusicContainer({super.key});

  @override
  State<AddMusicContainer> createState() => _AddMusicContainerState();
}

class _AddMusicContainerState extends State<AddMusicContainer> {

  List<Map<String, dynamic>> _audioFiles = [];
  final Map<String, Uint8List> _decodedBytes = {};

  bool _isLoading = true;
  @override
  void initState() {
    super.initState();

    WidgetsBinding.instance.addPostFrameCallback((_) => _fetchModelData(true));
  }

  // -=-  Model Data Fetching Logic  -=-
  Future<void> _fetchModelData(bool useCache) async {
    setState(() => _isLoading = true);

    await addMusicModel.init(useCache);

    if (addMusicModel.getIsStoragePermissionGranted) {
      _audioFiles = List.from(addMusicModel.getOriginalAudioFiles);

      _selectedIndexes.clear();

      _decodedBytes.clear();
      for (dynamic song in addMusicModel.getOriginalAudioFiles) {
        final title = song['title'] as String;
        final albumArtBase64 = song['albumArtBase64'] as String?;

        if (albumArtBase64 != null) {
          _decodedBytes.putIfAbsent(title, () => base64Decode(albumArtBase64));
        } else {
          _decodedBytes.putIfAbsent(title, () => audioController.defaultIcon);
          log("Song album art base64 was not found {add_music_container.dart LINE 55}: $song");
        }
      }
    }

    setState(() {
      _isLoading = false;
    });
  }

  // -=-  Search Box Logic  -=-
  final _searchController = TextEditingController();
  void _searchBook(String query) {
    setState(() {
      _applyFilters();
    });
  }

  void _applyFilters() {
    String query = _searchController.text.toLowerCase();

    final searchResults = (addMusicModel.getOriginalAudioFiles as List<Map<String, dynamic>> ).where((song) {
      final songTitle = song['title'].toLowerCase();

      final matchesSearch = songTitle.contains(query);
      return matchesSearch;
    }).toList();

    _audioFiles = searchResults;
    _selectedIndexes.clear();
  }

  // Logic to add the currently selected songs to the all music list.
  Future<void> _addToAddedSongsList() async {
    setState(() => _isLoading = true);

    if (_selectedIndexes.isNotEmpty) {
      Map<String, dynamic> selectedSongsMap = sharedPrefs.addedSongs.isNotEmpty ? jsonDecode(sharedPrefs.addedSongs) : {};

      Map<String, dynamic>? prevSongData = selectedSongsMap.isEmpty ? null : selectedSongsMap.values.last;
      for (int index in _selectedIndexes) {
        Map<String, dynamic> song = _audioFiles[index];

        if (!selectedSongsMap.containsKey(song['id'].toString())) { // If the song is unique, continue to actually add to the song list.
          selectedSongsMap.putIfAbsent(song['id'].toString(), () => song);

          audioController.addAfter(prevSongData, song, _decodedBytes[song['title']]!);
          prevSongData = song;
        }
      }

      sharedPrefs.addedSongs = jsonEncode(selectedSongsMap);
    }

    isAddingMusicNotifier.value = false;
  }

  final Set<int> _selectedIndexes = {}; // A set of all indexes of audio files and their decoded byte data, which were selected through UI.
  @override
  Widget build(BuildContext context) {
    return _isLoading ? Container(padding: const EdgeInsets.only(top: 100), child: const LoadingCircle())
    : addMusicModel.getIsStoragePermissionGranted == false
    ? Container(
      padding: const EdgeInsets.only(top: 50),
      child: Column(
        children: [
          // -=-  Back Button (Storage Not Granted)  -=-
          Align(
            alignment: Alignment.topLeft,
            child: ElevatedButton(
              style: ButtonStyle(
                padding: const WidgetStatePropertyAll(EdgeInsets.zero),
                backgroundColor: WidgetStateColor.transparent,
                shadowColor: WidgetStateColor.transparent,
                shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
              ),
              onPressed: () {
                isAddingMusicNotifier.value = false;
              },
              child: Icon(
                Icons.arrow_back_outlined,
                color: Theme.of(context).colorScheme.tertiary,
              ),
            ),
          ),

          const Padding(padding: EdgeInsets.only(top: 10)),

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
                    Icons.settings,
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
                    _fetchModelData(true);
                  },
                  child: Icon(
                    Icons.refresh,
                    color: Theme.of(context).colorScheme.tertiary,
                  ),
                ),
              ),
            ],
          ),
        ]
      )
    )
    : Column(
      children: [
        const Padding(padding: EdgeInsets.only(top: 50)), // Top Padding

        // -=-  Top Bar  -=-
        Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            // -=-  Back Button (Storage Granted)  -=-
            ElevatedButton(
              style: ButtonStyle(
                padding: const WidgetStatePropertyAll(EdgeInsets.zero),
                backgroundColor: WidgetStateColor.transparent,
                shadowColor: WidgetStateColor.transparent,
                shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
              ),
              onPressed: () {
                isAddingMusicNotifier.value = false;
              },
              child: Icon(
                Icons.arrow_back_outlined,
                color: Theme.of(context).colorScheme.tertiary,
              ),
            ),

            // -=-  Search Box  -=-
            Expanded(
              child: TextField(
                style: const TextStyle(
                  fontFamily: 'SourGummy',
                  fontVariations: [FontVariation('wght', 300)],
                  fontSize: 15,
                ),
                cursorColor: Theme.of(context).colorScheme.inversePrimary,
                controller: _searchController,
                decoration: InputDecoration(
                  prefixIcon: const Icon(Icons.search),
                  prefixIconColor: WidgetStateColor.resolveWith((states) {
                    if (states.contains(WidgetState.focused)) {
                      return Theme.of(context).colorScheme.inversePrimary;
                    } else {
                      return Colors.grey;
                    }
                  }),
                  hintText: 'Search audio files',
                  hintStyle: const TextStyle(
                    fontFamily: 'SourGummy',
                    fontVariations: [FontVariation('wght', 300)],
                    fontSize: 15,
                  ),
                  focusedBorder: UnderlineInputBorder(
                    borderSide: BorderSide(color: Theme.of(context).colorScheme.inversePrimary),
                  ),
                ),

                onChanged: _searchBook,
              ),
            ),

            // -=-  Refresh Button (Audio access permission granted)  -=-
            Container(
              width: 40,
              margin: const EdgeInsets.only(left: 10),
              child: ElevatedButton(
                style: ButtonStyle(
                  padding: const WidgetStatePropertyAll(EdgeInsets.zero),
                  backgroundColor: WidgetStateColor.transparent,
                  shadowColor: WidgetStateColor.transparent,
                  shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
                ),
                onPressed: () {
                  _searchController.text = '';
                  _fetchModelData(false);
                },
                child: Icon(
                  Icons.refresh,
                  color: Theme.of(context).colorScheme.tertiary,
                ),
              ),
            ),

            // -=-  Add Selected Button  -=-
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
                  _addToAddedSongsList();
                },
                child: Icon(
                  Icons.add_box_outlined,
                  color: Theme.of(context).colorScheme.tertiary,
                ),
              ),
            ),
          ],
        ),

        const Padding(padding: EdgeInsets.symmetric(vertical: 5)),

        // -=-  List of Audio Files  -=-
        Expanded(
          child: StretchingOverscrollIndicator(
            axisDirection: AxisDirection.down,
            child: _audioFiles.isEmpty ? Align(
              alignment: Alignment.topCenter,
              child: Text(
                "No audio files found",
                style: TextStyle(
                  fontFamily: 'SourGummy',
                  fontVariations: const [FontVariation('wght', 400)],
                  fontSize: 17,
                  color: Theme.of(context).colorScheme.tertiary,
                )
              )
            )
            : ListView.separated(
              padding: const EdgeInsets.only(left: 15, right: 15, bottom: 70),
              scrollDirection: Axis.vertical,
              itemCount: _audioFiles.length,
              itemBuilder: (BuildContext context, int index) {
                final song = _audioFiles[index];

                return ClipRRect(
                  borderRadius: BorderRadius.circular(15),
                  child: Material(
                    color: !_selectedIndexes.contains(index) ? Colors.transparent : Theme.of(context).colorScheme.surface,
                    child: InkWell(
                      child: ListTile(
                        leading: ClipRRect(
                          borderRadius: BorderRadius.circular(10),
                          child: AlbumArtLoader.loadAlbumArt(song['albumArtBase64'], _decodedBytes[song['title']]!),
                        ),
                        title: Text(
                          song['title'] ?? 'Unknown Title',
                          style: TextStyle(
                            fontFamily: 'SourGummy',
                            fontVariations: const [FontVariation('wght', 400)],
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
                        onTap: () { // What happens when the user selects/deselects a song.
                          setState(() {
                            if (_selectedIndexes.contains(index)) {
                              _selectedIndexes.remove(index);
                            } else {
                              _selectedIndexes.add(index);
                            }
                          });
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

        const Padding(padding: EdgeInsets.only(bottom: 0)), // Bottom padding
      ],
    );
  }
}