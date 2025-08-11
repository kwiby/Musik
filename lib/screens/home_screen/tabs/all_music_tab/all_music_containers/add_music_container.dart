import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:musik/misc/loading_circle.dart';
import 'package:musik/screens/home_screen/tabs/all_music_tab/all_music_tab.dart';
import 'package:permission_handler/permission_handler.dart';
import '../../../../../misc/album_art.dart';

bool _isStoragePermissionGranted = false;

class AddMusicContainer extends StatefulWidget {
  const AddMusicContainer({super.key});

  @override
  State<AddMusicContainer> createState() => _AddMusicContainerState();
}

class _AddMusicContainerState extends State<AddMusicContainer> {
  static const platform = MethodChannel('com.example.audio/files');

  List<Map<String, dynamic>> _audioFiles = [];
  final List<Uint8List> _decodedBytes = [];

  bool _isLoading = true;
  @override
  void initState() {
    super.initState();

    if (_isStoragePermissionGranted) {
      fetchAudioFiles();
    } else {
      requestStoragePermission();
    }
  }

  Future<void> requestStoragePermission() async {
    final status = await Permission.audio.request();

    _isStoragePermissionGranted = false;
    if (status.isGranted) {
      setState(() {
        _isStoragePermissionGranted = true;
      });
      fetchAudioFiles();
    } else {
      print("Storage permission was denied!");
    }
  }

  Future<void> fetchAudioFiles() async {
    try {
      final List<dynamic> result = await platform.invokeMethod('getAudioFiles');

      _audioFiles = result.map<Map<String, dynamic>>((item) {
        final map = Map<Object?, Object?>.from(item);
        return map.map<String, dynamic>((key, value) => MapEntry(key.toString(), value));
      }).toList();

      for (dynamic song in _audioFiles) {
        _decodedBytes.add(base64Decode(song['albumArtBase64'].replaceAll(RegExp(r'\s+'), '')));
      }

      setState(() {
        _isLoading = false;
      });
    } on PlatformException catch (error) {
      print("Failed to get audio files: $error");
    }
  }

  final Set<int> _selectedIndexes = {};
  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        const Padding(padding: EdgeInsets.only(top: 50)), // Top Padding

        // -=-  Back Button  -=-
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

        const Padding(padding: EdgeInsets.symmetric(vertical: 5)),

        // -=-  List of Audio Files  -=-
        Expanded(
          child: StretchingOverscrollIndicator(
            axisDirection: AxisDirection.down,
            child: _isLoading ? const LoadingCircle()
              : _isStoragePermissionGranted == false
                ? Align(
                  alignment: Alignment.topCenter,
                  child: Text(
                    "Storage permission is denied",
                    style: TextStyle(
                      fontFamily: 'SourGummy',
                      fontVariations: const [FontVariation('wght', 400)],
                      fontSize: 20,
                      color: Theme.of(context).colorScheme.tertiary,
                    ),
                  ))
                : _audioFiles.isEmpty
                  ? Align(
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
                                child: AlbumArtLoader.loadAlbumArt(song['albumArtBase64'], _decodedBytes[index])
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
                                    ? Duration(milliseconds: song['duration']).toString().split('.').first : '',
                                style: TextStyle(
                                  fontFamily: 'SourGummy',
                                  fontVariations: const [FontVariation('wght', 300)],
                                  fontSize: 13,
                                  color: Theme.of(context).colorScheme.tertiary,
                                )
                              ),
                              onTap: () {
                                print("'${song['title']}' tapped!");

                                setState(() {
                                  if (_selectedIndexes.contains(index)) {
                                    _selectedIndexes.remove(index);
                                  } else {
                                    _selectedIndexes.add(index);
                                  }
                                });
                              },
                              onLongPress: () {
                                print("'${song['title']}' long pressed!");
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
      ],
    );
  }
}