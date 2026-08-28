<div align="center">

<!-- Banner -->
![Banner](https://raw.githubusercontent.com/kwiby/Musik/refs/heads/main/images/banner.png)

<!-- Title/Description-->
# Musik
### An Android music player featuring YouTube audio downloading, metadata editing, and more!

<!-- Shields -->
[![GitHub Release](https://img.shields.io/github/v/release/kwiby/Musik?style=for-the-badge&label=latest%20release&labelColor=%233a3a3a&color=%236e76f0)](https://github.com/kwiby/Musik/releases/latest)
&nbsp;
[![GitHub Downloads (all assets, all releases)](https://img.shields.io/github/downloads/kwiby/Musik/total?style=for-the-badge&label=Downloads&labelColor=%233a3a3a&color=%2358b074)](https://github.com/kwiby/Musik/releases)
&nbsp;
[![GitHub Issues](https://img.shields.io/github/issues-raw/kwiby/Musik?style=for-the-badge&label=Open%20Issues&labelColor=%233a3a3a&color=%23b05858)](https://github.com/kwiby/Musik/issues)
&nbsp;
[![GitHub License](https://img.shields.io/github/license/kwiby/Musik?style=for-the-badge&labelColor=%233a3a3a&color=%23b68f4d)](https://github.com/kwiby/Musik/blob/main/LICENSE)

</div>

<!-- Features -->
<h2 align="center">Features</h2>

<img align="right" width="290" src="https://raw.githubusercontent.com/kwiby/Musik/refs/heads/main/images/ui_screenshots.png" alt="UI Screenshots">

- Direct audio downloading from YouTube.
- Built-in metadata editing.
  - Editable fields (future updates may include more):
    - Artwork
    - Title
    - Artist
    - Album
    - Album artist
    - Track number
    - Disc number
    - Genre
    - Year
- Music statistics.
  - Available stats (future updates may include more):
    - Play count
    - Listen time
- Music playlists.
- Sleep timer.
- Audio file format conversion to mp3.
- Very cool themes/styles!

<!-- Installation -->
<h2 align="center">Installation</h2>
  
### Device Requirements:
- Android 10 or above.
- 200 MB - 300 MB of free storage space.
### Installing
1. Go to the [latest release page](https://github.com/kwiby/Musik/releases/latest).
2. Select one of the `.apk` files to download from based on your device architecture (`arm64-v8a`, `armeabi-v7a`, `x86_64`, or `x86`).

> [!TIP]
> **If you don't know what your device architecture is, download the `universal` one.**

4. Open the `.apk` file after downloading and follow the steps shown to fully install.
6. Once installed, open the app and accept the permission when prompted.
7. Finally, go to settings at the top right of the app, scroll down to `Update YtDlp`, and select `Nightly`.
   - This is a highly recommended step to ensure that downloading functions properly.
   - Afterwards, you will only need to update YtDlp every ~90 days. Read the explanation given in the app for more details.

<!-- Acknowledgements -->
<h2 align="center">Acknowledgements</h2>

This app was made possible with the following libraries:
- [youtubedl-android](https://github.com/yausername/youtubedl-android)
- [TagLib](https://github.com/Kyant0/taglib)
- [yt-dlp](https://github.com/yt-dlp/yt-dlp)
