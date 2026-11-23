# My TV: TCL 65C7L — what was changed and how to undo it

Written 2026-11-21. This folder is the record of everything done to this TV over ADB, so the TV can be put back
even if nothing else survives. The launcher code itself lives in [`../launcher`](../launcher) on this branch.

## The TV

- TCL 65C7L, device G08, product `G08_4K_GB`, model "Smart TV Pro".
- Android 14 (Google TV), firmware `UTT2.250416.001 AU02`, 32-bit userspace (`armeabi-v7a`), 2.4 GB RAM.
- Nothing is plugged into the USB ports.
- Not rooted, bootloader locked, Widevine L1. Never root or unlock it: that factory-resets the TV and can drop
  Widevine to L3 (Netflix falls back to SD).

## Current state

| What | State | Undo |
|---|---|---|
| 33 factory packages | Disabled with `pm disable-user --user 0`, not uninstalled. List: [`disabled.txt`](disabled.txt); one line per package with the reason in [`DEBLOAT-LOG.md`](DEBLOAT-LOG.md) | `restore.sh` |
| Gboard, Prime Video | Were already disabled before any of this; left as they were | `adb shell pm enable <package>` |
| Google TV home (`com.google.android.apps.tv.launcherx`) and setup wizard (`com.google.android.tungsten.setupwraith`) | Disabled, so the custom launcher keeps Home | `restore.sh`, or level 1 below |
| Home screen | Custom launcher `com.gathin.tvhome`, built from this repo | level 1 below |
| Launcher permissions | `DUMP` and `PACKAGE_USAGE_STATS` granted with `pm grant` (running-apps list) | `adb shell pm revoke com.gathin.tvhome <permission>` |
| Screensaver | `com.gathin.tvhome/.ClockDream` (was unset: Google's ambient screensaver) | `adb shell settings delete secure screensaver_components` |
| Animation scales | 0.5 (were 1.0) | `restore.sh` sets 1.0 |
| Uninstalled | Projectivy Launcher and FLauncher, both Play Store apps, at the owner's request | Install again from the Play Store |
| Wi-Fi | The saved network was forgotten and joined again in Settings, so it no longer belongs to the setup wizard | Nothing to undo |

The packages that must **never** be disabled on this TV (Wi-Fi, HDMI, remote, keyboard, updates, boot loop) are
listed in `DEBLOAT-LOG.md` under "Kept on purpose". The TCL traps in short: `com.tcl.suspension` (Input key menu),
`com.tcl.ttvs` (system updates), `com.tcl.tv` and `com.tcl.tvinput` (HDMI / antenna), `com.tcl.providers.config` and
`com.tcl.initsetup` (Wi-Fi), `com.tcl.autopair`, `com.tcl.tcl_bt_rcu_service` and
`com.google.android.tv.remote.service` (remote), `com.android.location.fused` (boot loop).

## Connecting to the TV

1. On the TV: Settings > System > About > tap "Android TV OS build" 7 times to unlock Developer options, then
   Settings > System > Developer options > **Wireless debugging** on.
2. On the computer (macOS: `brew install android-platform-tools`):

   ```sh
   adb mdns services          # lists the TV's _adb-tls-connect address
   adb devices                # the TV shows up here once paired
   ```

   If it is not listed as a device, pair it: on the TV choose "Pair device with pairing code", then
   `adb pair <ip>:<pairing port> <code>`. If the TV asks "Allow debugging?", tick "Always allow" and accept.
3. Point every command at it:

   ```sh
   export ANDROID_SERIAL=$(adb devices | awk 'NR==2 {print $1}')
   ```

## Revert plan: use the smallest step that fixes the problem

### Level 1: the launcher is broken (crash, blank Home, Home key does nothing)

Bring Google TV's home back; the custom launcher stays installed and can be made Home again later.

```sh
adb shell pm enable com.google.android.apps.tv.launcherx
adb shell pm enable com.google.android.tungsten.setupwraith
adb shell cmd package set-home-activity com.google.android.apps.tv.launcherx/.home.HomeActivity
adb reboot
```

Without a computer: the remote's gear button opens the TV's settings even when Home is broken. Go to Settings >
Apps > See all apps > Show system apps, open "Google TV Home", choose Enable, do the same for "Setup wraith" /
"Android Setup", then restart the TV.

To go back to the custom launcher afterwards: disable those two again with `pm disable-user --user 0` and run
`adb shell cmd package set-home-activity com.gathin.tvhome/.MainActivity`.

### Level 2: one thing stopped working

Turn back on the package that matches the symptom, test, and keep the rest disabled:

| Symptom | Enable |
|---|---|
| A web page or login screen inside an app is blank | `com.tcl.android.webview`, `com.mediatek.support.webview` |
| Casting to the TV stopped (Chromecast) | `com.google.android.tungsten.setupwraith` |
| TCL wireless speakers or hearing settings missing | `com.tcl.hearaid` |
| TCL soundbar firmware update needed | `com.tcl.UpdatePeripheral` |
| Calendar on the TV | `com.google.android.syncadapters.calendar` |
| Printing | `com.android.printspooler` |
| Any other feature | Enable the most likely package from `DEBLOAT-LOG.md`, one at a time, testing after each |

```sh
adb shell pm enable <package>
```

Then delete its line from `disabled.txt` and its `restore_pkg` line from `restore.sh`, so the records stay true.

### Level 3: undo everything

```sh
./restore.sh && adb reboot
```

This re-enables all 33 packages, resets the screensaver and the animation scales, and gives Home back to Google TV.
After the reboot, the custom launcher can be removed for good with `adb shell pm uninstall com.gathin.tvhome`
(only once Google TV home is back on screen).

### Level 4: no computer available

Everything can be undone from the TV itself:

1. Settings > Apps > See all apps > Show system apps: open each app named in `disabled.txt` and choose Enable.
   Start with "Google TV Home" and "Android Setup", so Home works again.
2. Settings > System > Developer options: set Window, Transition and Animator scale back to 1x.
3. Settings > System > Ambient mode / Screensaver: pick Google's ambient mode again.
4. Restart the TV.

### Level 5: last resort

Settings > System > About > Factory reset. This turns every disabled package back on and removes the custom
launcher and all sideloaded apps. It does not touch Widevine. Afterwards the TV is exactly as it came from the box.

## After a TCL firmware update

Updates can turn packages back on or give Home back to Google TV. Check:

```sh
adb shell pm list packages -d | sort          # compare with disabled.txt
adb shell cmd package resolve-activity --brief -a android.intent.action.MAIN -c android.intent.category.HOME
adb shell dumpsys package com.gathin.tvhome | grep -E "DUMP|PACKAGE_USAGE_STATS"
```

Re-disable anything that came back with `./disable-one.sh <package> "<why>"` (one package per command), set Home again
with `set-home-activity`, and repeat the two `pm grant` lines below if the permissions are gone.

## Rebuild and reinstall the launcher

Needs JDK 17 and Android SDK platform 34 / build-tools 34. For the weather pill, add the TV's location to
`launcher/local.properties` (git-ignored; without it the pill stays hidden):

```properties
weather.latitude=<decimal degrees>
weather.longitude=<decimal degrees>
```

```sh
cd launcher
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
./gradlew testReleaseUnitTest assembleRelease
adb push app/build/outputs/apk/release/app-release.apk /data/local/tmp/tvhome.apk
adb shell pm install -r /data/local/tmp/tvhome.apk && adb shell rm /data/local/tmp/tvhome.apk
adb shell pm grant com.gathin.tvhome android.permission.DUMP
adb shell pm grant com.gathin.tvhome android.permission.PACKAGE_USAGE_STATS
adb shell cmd package set-home-activity com.gathin.tvhome/.MainActivity
```

`pm install` from the TV's shell is used because `adb install` can hang on Android 14 waiting for a confirmation on
the TV. Installing a second Home app resets the Home choice, which is why `set-home-activity` comes last.

The release APK is signed with the build computer's Android debug key (`~/.android/debug.keystore`). An update only
installs over the existing app when it is signed with the same key. With a new key, run
`adb shell pm uninstall com.gathin.tvhome` first; that loses the favourites order and the bloatware tags.

## What this branch changes in the launcher

All under `launcher/app/src/main`:

- `res/values/config.xml`: five favourites (Netflix, YouTube, Live TV, VLC, Media Player), Live TV app `com.tcl.tv`,
  Inputs and Wi-Fi pills off, the Settings pill and key open the quick settings panel, `weather_pill` and
  `memory_pill` on.
- `app/build.gradle`: the weather location is read from `launcher/local.properties` (`weather.latitude`,
  `weather.longitude`), which git ignores, so no home location is ever committed.
- `Weather.java`: Open-Meteo forecast (no API key) for the weather pill and its popup.
- `RunningApps.java`: the running-apps list behind the memory pill, read from `dumpsys activity lru`.
- `Bloatware.java`, `AppAdapter.java`: apps tagged as bloatware leave the grid and are listed behind the
  "Bloatware · N" button at the bottom.
- `MainActivity.java`: the two pills, their popups, the tile menu's "Mark as bloatware" and the bloatware panel.
- `AndroidManifest.xml`: INTERNET (weather), KILL_BACKGROUND_PROCESSES, DUMP and PACKAGE_USAGE_STATS.
- Tests: `WeatherTest`, `RunningAppsTest`.

Android 14 does not let a normal app read CPU load or close other apps, so the memory pill shows RAM only and closes
apps through their App info page.

## Files in this folder

| File | What it is |
|---|---|
| `restore.sh` | Undoes every change (level 3) |
| `disabled.txt` | The 33 packages disabled by this work, one per line |
| `DEBLOAT-LOG.md` | Every change in order, with the reason and the exact undo command |
| `disable-one.sh` | Disables one package and records it in `restore.sh`, `disabled.txt` and the log |
| `check.sh` | Screenshots Home, the Input menu, HDMI 1, Netflix and YouTube after a change |
| `tv-guard.sh` | Refuses to continue while something is playing, so key presses never interrupt viewing |
| `lists/packages-before.txt` | Every installed package before any change (`pm list packages -f`) |
| `lists/disabled-before.txt`, `lists/disabled-after.txt` | Disabled packages before and after |

Kept off GitHub on purpose, because this repository is public: the raw `getprop`, `settings list` and
`dumpsys meminfo` dumps, the screenshots, the TV's serial and network details, and the home location. The owner keeps
them in a private copy outside this repository.
