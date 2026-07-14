import os
import yt_dlp


def get_minimal_opts():
    return {
        "quiet": True,
        "no_warnings": True,
        "extract_flat": True,
        "call_home": False,
        "check_formats": False,
        "external_downloader": None,
        "ffmpeg_location": None, # Do not let it probe ffmpeg during link check
        "hls_prefer_native": True, # Use native Python HLS downloader
        "http_chunk_size": 10485760,
        "js_runtimes": {}
    }

def warm_up():
    """
    Pays yt_dlp's one-time-per-process init costs (extractor registry +
    ffmpeg/tool probing) during app startup instead of on first user action.
    """ 
    import yt_dlp

    try:
        ydl_opts = get_minimal_opts()
        
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            # Do NOT pass a real URL here; pass a dummy string
            # that triggers a quick local failure rather than a network call
            ydl.extract_info("local:dummy", download=False)
    except:
        pass


def download_audio(url, output_dir, ffmpeg_path=None, quickjs_path=None):
    """
    Downloads the best available audio stream for a YouTube URL and
    converts it to mp3 using a bundled ffmpeg binary. Optionally uses
    a bundled QuickJS runtime for yt-dlp's JS challenge solving.

    Args:
        url: YouTube video URL.
        output_dir: Directory (app-sandboxed) to save the output file into.
        ffmpeg_path: Full path to the bundled ffmpeg binary
                     (e.g. applicationInfo.nativeLibraryDir + "/libffmpeg.so").
        quickjs_path: Full path to the bundled QuickJS (qjs) binary.

    Returns:
        dict with success/path/title on success, or success=False/error on failure.
    """
    ydl_opts = get_minimal_opts()
    ydl_opts["format"] = "bestaudio"
    ydl_opts["outtmpl"] = os.path.join(output_dir, "%(title)s.%(ext)s")
    ydl_opts["postprocessors"] = [{
            "key": "FFmpegExtractAudio",
            "preferredcodec": "mp3",
        }]
    if ffmpeg_path:
        ydl_opts["ffmpeg_location"] = ffmpeg_path
    if quickjs_path:
        ydl_opts["js_runtimes"] = {"quickjs": {"path": quickjs_path}}

    try:
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            info = ydl.extract_info(url, download=True)
            filename = ydl.prepare_filename(info)
            base, _ = os.path.splitext(filename)
            final_path = base + ".mp3"
            
            return {
                "isSuccess": True,
                "path": final_path,
                "title": info.get("title"),
            }
    except yt_dlp.utils.DownloadError as e:
        return {
            "isSuccess": False,
            "error": str(e),
        }
    except Exception as e:
        return {
            "isSuccess": False,
            "error": f"Error: {e}",
        }
    

def check_valid_link(url):
    """
    Checks whether a YouTube URL points to a real, accessible video.
    Makes a real network call via yt-dlp (no download).
    """
    if not url or not url.strip():
        return {
            "isValid": False, 
            "error": "Not a valid video URL"
        }
    
    ydl_opts = get_minimal_opts()
    ydl_opts["extract_flat"] = False
    
    try:
        with yt_dlp.YoutubeDL(ydl_opts) as ydl:
            ydl.add_default_info_extractors()
            info = ydl.extract_info(url, download=False)

            if "entries" in info:
                return {
                    "isValid": False, 
                    "error": "URL points to a playlist or collection"
                }
            if info.get("_type") != "video" and info.get("_type") is not None:
                return {
                    "isValid": False, 
                    "error": f"Invalid type: { info.get('_type') }"
                }
            if not info.get("id"):
                return {
                    "isValid": False, 
                    "error": "No video ID found"
                }
                
            return {
                "isValid": True
            }
    except Exception as e:
        return {
            "isValid": False,
            "error": f"Unexpected Error: {e}"
        }