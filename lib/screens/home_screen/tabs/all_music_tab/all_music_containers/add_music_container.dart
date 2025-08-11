import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:musik/misc/loading_circle.dart';
import 'package:musik/models/add_music_model.dart';
import 'package:musik/screens/home_screen/tabs/all_music_tab/all_music_tab.dart';
import '../../../../../misc/album_art.dart';

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

    WidgetsBinding.instance.addPostFrameCallback((_) => _fetchModelData());
  }

  // -=-  Model Data Fetching Logic  -=-
  Future<void> _fetchModelData() async {
    setState(() => _isLoading = true);

    await addMusicModel.init();

    if (addMusicModel.getIsStoragePermissionGranted()) {
      _audioFiles = List.from(addMusicModel.getOriginalAudioFiles());

      _selectedIndexes.clear();

      _decodedBytes.clear();
      for (dynamic song in addMusicModel.getOriginalAudioFiles()) {
        //final cleanedBase64String = song['albumArtBase64'].replaceAll(RegExp(r'\s'), '');
        _decodedBytes.putIfAbsent(song['title'], () => base64Decode(song['albumArtBase64']));
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

    final searchResults = addMusicModel.getOriginalAudioFiles().where((song) {
      final songTitle = song['title'].toLowerCase();
      final matchesSearch = songTitle.contains(query);

      return matchesSearch;
    }).toList();

    _audioFiles = searchResults;
    _selectedIndexes.clear();
  }

  final Set<int> _selectedIndexes = {}; // A set of all indexes of audio files and their decoded byte data, which were selected through UI.
  @override
  Widget build(BuildContext context) {
    return _isLoading ? Container(padding: const EdgeInsets.only(top: 100), child: const LoadingCircle())
    : addMusicModel.getIsStoragePermissionGranted() == false
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

          // -=-  Not Storage Permission Message  -=-
          Align(
            alignment: Alignment.topCenter,
            child: Text(
              "Storage permission is denied",
              style: TextStyle(
                fontFamily: 'SourGummy',
                fontVariations: const [FontVariation('wght', 400)],
                fontSize: 20,
                color: Theme.of(context).colorScheme.tertiary,
              )
            )
          )
        ]
      )
    )
    : Column(
      children: [
        const Padding(padding: EdgeInsets.only(top: 50)), // Top Padding

        // -=-  Top Bar  -=-
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
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
                  hintText: 'Search Audio Files',
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

            // -=-  Add Selected Button  -=-
            ElevatedButton(
              style: ButtonStyle(
                padding: const WidgetStatePropertyAll(EdgeInsets.zero),
                backgroundColor: WidgetStateColor.transparent,
                shadowColor: WidgetStateColor.transparent,
                shape: WidgetStateProperty.all<CircleBorder>(const CircleBorder()),
              ),
              onPressed: () {
                _selectedIndexes.forEach(print); // TEMPORARY CODE
              },
              child: Icon(
                Icons.add_box_outlined,
                color: Theme.of(context).colorScheme.tertiary,
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
                  fontSize: 20,
                  color: Theme.of(context).colorScheme.tertiary,
                )
              )
            )
            : ListView.separated(
              padding: const EdgeInsets.only(left: 15, right: 15),
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

        const Padding(padding: EdgeInsets.only(bottom: 60)),
      ],
    );
  }
}