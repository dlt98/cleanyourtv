#!/usr/bin/env bash
# Re-enables every package disabled during the TV debloat, resets the screensaver and animation scales,
# and gives Home back to Google TV.
# Usage: export ANDROID_SERIAL=<serial from adb devices>; ./restore.sh
set -u
: "${ANDROID_SERIAL:?export ANDROID_SERIAL first (README.md, Connecting)}"; export ANDROID_SERIAL

restore_pkg() { echo "enable $1"; adb shell pm enable "$1"; }

# --- packages (appended as they are disabled) ---
restore_pkg com.tcl.logkit
restore_pkg com.tcl.usercenter
restore_pkg com.google.android.feedback
restore_pkg com.android.tv.feedbackconsent
restore_pkg com.google.android.partnersetup
restore_pkg com.android.adservices.api
restore_pkg com.android.ondevicepersonalization.services
restore_pkg com.android.federatedcompute.services
restore_pkg com.tcl.dashboard
restore_pkg com.tcl.messagebox
restore_pkg com.tcl.tv.tclhome_passive
restore_pkg com.tcl.partnercustomizer
restore_pkg com.tcl.waterfall.overseas
restore_pkg com.tcl.exhibit
restore_pkg com.tcl.esticker
restore_pkg android.autoinstalls.config.google.gtvpai
restore_pkg com.google.android.apps.tv.dreamx
# screensaver (was unset = Google ambient screensaver)
echo "reset screensaver"; adb shell settings delete secure screensaver_components
restore_pkg com.tcl.guard
restore_pkg com.mediatek.backgrounddetection
restore_pkg com.android.printspooler
restore_pkg com.tcl.android.webview
restore_pkg com.mediatek.support.webview
restore_pkg com.google.android.syncadapters.calendar
restore_pkg com.tcl.ocean.instructions
restore_pkg com.tcl.repairguide
restore_pkg com.tcl.hearaid
restore_pkg com.tcl.t_solo
restore_pkg com.google.android.play.games
restore_pkg com.google.android.youtube.tvmusic
restore_pkg com.tcl.smartalexa
restore_pkg com.tcl.UpdatePeripheral
# animation speeds (were 1.0)
echo "reset animations"
adb shell settings put global window_animation_scale 1.0
adb shell settings put global transition_animation_scale 1.0
adb shell settings put global animator_duration_scale 1.0
# Projectivy Launcher was UNINSTALLED at the owner's request (Play Store app, not factory).
# To get it back: install "Projectivy Launcher" from the Play Store on the TV.
# FLauncher was tried and later uninstalled. The custom launcher (com.gathin.tvhome, dlt98/cleanyourtv) stays
# installed; to make it Home again: adb shell cmd package set-home-activity com.gathin.tvhome/.MainActivity
restore_pkg com.google.android.apps.tv.launcherx
restore_pkg com.google.android.tungsten.setupwraith
# Home screen back to Google TV
echo "home -> Google TV"; adb shell cmd package set-home-activity com.google.android.apps.tv.launcherx/.home.HomeActivity
echo "done - reboot the TV: adb reboot"
