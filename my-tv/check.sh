#!/usr/bin/env bash
# Checks the TV after a batch: Home, Inputs panel, HDMI 1, Netflix, YouTube.
# Saves one screenshot per step in shots/<tag>-*.png and a 2x3 contact sheet shots/<tag>-sheet.png.
# Usage: ./check.sh <tag>
set -u
cd "$(dirname "$0")"
: "${ANDROID_SERIAL:?export ANDROID_SERIAL first (README.md, Connecting)}"; export ANDROID_SERIAL
tag="$1"; mkdir -p shots

top() { adb shell dumpsys window | grep -m1 mCurrentFocus | sed 's/.*u0 //; s/}.*//'; }
shot() {
  sleep "$2"; adb exec-out screencap -p > "shots/$tag-$1.png"; echo "$1: $(top)"
  # Live TV / HDMI is protected video: screencap returns nothing usable, so put a black frame in the sheet.
  if ! ffmpeg -loglevel error -i "shots/$tag-$1.png" -f null - 2>/dev/null; then
    echo "   (protected video, empty screenshot)"
    ffmpeg -loglevel error -y -f lavfi -i color=black:s=1920x1080 -frames:v 1 "shots/$tag-$1.png"
  fi
}
home() { adb shell input keyevent KEYCODE_HOME; }

home;                                                 shot 1-home 3
adb shell input keyevent KEYCODE_TV_INPUT;            shot 2-inputs 3
adb shell input keyevent KEYCODE_BACK
adb shell am start -a android.intent.action.VIEW \
  -d content://android.media.tv/passthrough/com.tcl.tvinput%2F.TvPassThroughService%2FHW15 >/dev/null
                                                      shot 3-hdmi1 5
home; sleep 2
adb shell monkey -p com.netflix.ninja -c android.intent.category.LEANBACK_LAUNCHER 1 >/dev/null 2>&1
                                                      shot 4-netflix 10
home; sleep 5
adb shell monkey -p com.google.android.youtube.tv -c android.intent.category.LEANBACK_LAUNCHER 1 >/dev/null 2>&1
                                                      shot 5-youtube 8
home;                                                 shot 6-home-again 3

ffmpeg -loglevel error -y -i "shots/$tag-1-home.png" -i "shots/$tag-2-inputs.png" -i "shots/$tag-3-hdmi1.png" \
  -i "shots/$tag-4-netflix.png" -i "shots/$tag-5-youtube.png" -i "shots/$tag-6-home-again.png" \
  -filter_complex "[0][1][2][3][4][5]xstack=inputs=6:layout=0_0|w0_0|w0+w1_0|0_h0|w0_h0|w0+w1_h0,scale=1440:-1" \
  "shots/$tag-sheet.png" && echo "sheet: shots/$tag-sheet.png"
