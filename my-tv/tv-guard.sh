#!/usr/bin/env bash
# Exits 1 (and says why) when someone is using the TV: a video or music session is playing, or an app other than
# a home screen, Settings or the quick panel is in front. Run it before sending any key presses.
: "${ANDROID_SERIAL:?export ANDROID_SERIAL first (README.md, Connecting)}"; export ANDROID_SERIAL
adb get-state >/dev/null 2>&1 || { echo "UNREACHABLE: no TV on ADB"; exit 1; }
focus=$(adb shell dumpsys window | grep -m1 mCurrentFocus | sed 's/.*u0 //; s/}.*//')
if adb shell dumpsys media_session | grep -q "state=PlaybackState {state=3"; then
  echo "BUSY: something is playing ($focus)"; exit 1
fi
case "$focus" in
  com.gathin.tvhome*|com.android.tv.settings*|null) echo "idle: $focus" ;;
  *) echo "BUSY: $focus is in front"; exit 1 ;;
esac
