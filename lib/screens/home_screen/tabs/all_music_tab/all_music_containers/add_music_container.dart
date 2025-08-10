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

      setState(() {
        _audioFiles = result.map<Map<String, dynamic>>((item) {
          final map = Map<Object?, Object?>.from(item);
          return map.map<String, dynamic>((key, value) => MapEntry(key.toString(), value));
        }).toList();

        _isLoading = false;
      });
    } on PlatformException catch (error) {
      print("Failed to get audio files: $error");
    }
  }

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
                    ))
                  : ListView.separated(
                    padding: const EdgeInsets.only(
                      left: 15,
                      right: 15,
                    ),
                    scrollDirection: Axis.vertical,
                    itemCount: _audioFiles.length,
                    itemBuilder: (BuildContext context, int index) {
                      final file = _audioFiles[index];

                      return ListTile(
                        leading: ClipRRect(
                          borderRadius: BorderRadius.circular(10),
                          child: SizedBox(
                            width: 50,
                            height: 50,
                            child: FittedBox(
                              fit: BoxFit.cover,
                              alignment: Alignment.center,
                              child: AlbumArtLoader.loadAlbumArt(file['albumArtBase64']),
                            ),
                          )
                        ),
                        title: Text(
                            file['title'] ?? 'Unknown Title',
                            style: TextStyle(
                              fontFamily: 'SourGummy',
                              fontVariations: const [FontVariation('wght', 500)],
                              fontSize: 15,
                              color: Theme.of(context).colorScheme.tertiary,
                            )
                        ),
                        subtitle: Text(
                            file['artist'] ?? "Unknown Artist",
                            style: const TextStyle(
                              fontFamily: 'SourGummy',
                              fontVariations: [FontVariation('wght', 300)],
                              fontSize: 13,
                            )
                        ),
                        trailing: Text(
                            file['duration'] != null
                                ? Duration(milliseconds: file['duration']).toString().split('.').first : '',
                            style: TextStyle(
                              fontFamily: 'SourGummy',
                              fontVariations: const [FontVariation('wght', 300)],
                              fontSize: 13,
                              color: Theme.of(context).colorScheme.tertiary,
                            )
                        ),
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