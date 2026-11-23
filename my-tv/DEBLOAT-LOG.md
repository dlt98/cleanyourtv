# TV debloat log: TCL 65C7L

Started 2026-11-21.

## The TV

- TCL 65C7L, device G08, product `G08_4K_GB`, model "Smart TV Pro", Android 14 (Google TV).
- Firmware `TCL/G08_4K_GB/G08:14/UTT2.250416.001/AU02`, 32-bit userspace (`armeabi-v7a`), 2.4 GB RAM.
- Connected over Wireless debugging: `<TV serial from adb devices>` (TV IP, port shown on its Wireless debugging screen).
  It was already paired and authorised. Nothing is plugged into the USB ports.
- Already disabled before this session: `com.google.android.inputmethod.latin` (Gboard) and
  `com.amazon.amazonvideo.livingroom` (Prime Video). The active keyboard is `com.tcl.inputmethod.international`.

## Method

- Backups taken before any change are in `backup/`: package lists, getprop, the three settings lists, meminfo.
- Only `pm disable-user --user 0` is used. Each package is undone with `adb shell pm enable <package>`.
  `restore.sh` undoes everything.
- The one exception, which the owner asked for: the Projectivy Launcher (a Play Store app, not factory software) is
  uninstalled once FLauncher replaces it.
- Batches hold at most 10 packages. After each batch, screenshots in `shots/` check the home screen, the Inputs
  panel, HDMI, Netflix and YouTube.
- The starting point was the cleanyourtv list for this exact model, `examples/tcl-65c7l-google-tv`, cross-checked
  against `examples/tcl-g08-au-android14` (same board and firmware). The lists disagree on `com.tcl.ttvs` and
  `com.tcl.interactive`: the G08 list says the Source overlay needs both. Both are kept.
- Samba TV viewing tracking (ACR) runs inside `com.tcl.ttvs`, which must stay on because it also handles system
  updates. Turn ACR off in the TV menu instead: Settings > Privacy.

## Kept on purpose

- **The owner uses these:** Netflix, YouTube, TCL Live (`com.tcl.channelplus`), Google Assistant
  (`com.google.android.katniss`), Chromecast (`com.google.android.apps.mediashell`), AirPlay, Miracast,
  MagiConnect, the Game bar, the USB media player and gallery, Google TTS and TalkBack.
- **The TV needs these:** `com.tcl.tv`, `com.tcl.tvinput`, `com.tcl.suspension`, `com.tcl.ttvs`,
  `com.tcl.interactive`, `com.tcl.providers.config`, `com.tcl.initsetup`, `com.tcl.autopair`,
  `com.google.android.tv.remote.service`, `com.tcl.inputmethod.international`, `com.android.vending`,
  `com.google.android.gms`, `com.google.android.gsf`, `com.android.location.fused`, `com.tcl.systemserver`,
  `com.tcl.systemui.plugin`, `com.tcl.factory.view`, Bluetooth, TV provider, TV Settings.
- **Unclear, and not running anyway:** `com.tcl.eva` and `tv.mopa.ginga`.

## Changes

Every row below was written when the package was disabled. Settings changes and other non-package changes are
listed under "Other changes".

| Package | What it was | Undo |
|---|---|---|
| `com.tcl.logkit` | TCL log capture and upload | `adb shell pm enable com.tcl.logkit` |
| `com.tcl.usercenter` | TCL account sign-in and nags | `adb shell pm enable com.tcl.usercenter` |
| `com.google.android.feedback` | Google feedback reporter | `adb shell pm enable com.google.android.feedback` |
| `com.android.tv.feedbackconsent` | Feedback/diagnostics consent screen | `adb shell pm enable com.android.tv.feedbackconsent` |
| `com.google.android.partnersetup` | Google partner attribution | `adb shell pm enable com.google.android.partnersetup` |
| `com.android.adservices.api` | Android ad services (Privacy Sandbox ads, topics) | `adb shell pm enable com.android.adservices.api` |
| `com.android.ondevicepersonalization.services` | On-device ad personalisation | `adb shell pm enable com.android.ondevicepersonalization.services` |
| `com.android.federatedcompute.services` | Federated learning / usage-data compute | `adb shell pm enable com.android.federatedcompute.services` |
| `com.tcl.dashboard` | TCL promo dashboard (MessageBox content panel) | `adb shell pm enable com.tcl.dashboard` |
| `com.tcl.messagebox` | TCL push messages and promos | `adb shell pm enable com.tcl.messagebox` |
| `com.tcl.tv.tclhome_passive` | Pushes TCL content to the home screen at boot | `adb shell pm enable com.tcl.tv.tclhome_passive` |
| `com.tcl.partnercustomizer` | TCL rows/tiles on Google TV home (TCL Channel tile) | `adb shell pm enable com.tcl.partnercustomizer` |
| `com.tcl.waterfall.overseas` | TCL Channel movie/show recommendation browser | `adb shell pm enable com.tcl.waterfall.overseas` |
| `com.tcl.exhibit` | Store demo mode | `adb shell pm enable com.tcl.exhibit` |
| `com.tcl.esticker` | Retail on-screen sticker | `adb shell pm enable com.tcl.esticker` |
| `android.autoinstalls.config.google.gtvpai` | Silently installs partner apps (Play Auto Installs) | `adb shell pm enable android.autoinstalls.config.google.gtvpai` |
| `com.google.android.apps.tv.dreamx` | Google TV ambient screensaver (shows promos), 46 MB | `adb shell pm enable com.google.android.apps.tv.dreamx` |
| `com.tcl.guard` | TCL Guard 'cleaner' / app manager | `adb shell pm enable com.tcl.guard` |
| `com.mediatek.backgrounddetection` | MediaTek background-app detector (boot receiver only) | `adb shell pm enable com.mediatek.backgrounddetection` |
| `com.android.printspooler` | Printing service | `adb shell pm enable com.android.printspooler` |
| `com.tcl.android.webview` | Spare TCL WebView (Google WebView stays) | `adb shell pm enable com.tcl.android.webview` |
| `com.mediatek.support.webview` | Spare MediaTek WebView (Google WebView stays) | `adb shell pm enable com.mediatek.support.webview` |
| `com.google.android.syncadapters.calendar` | Google Calendar sync on the TV, 17 MB | `adb shell pm enable com.google.android.syncadapters.calendar` |
| `com.tcl.ocean.instructions` | TCL e-manual (User Guide) | `adb shell pm enable com.tcl.ocean.instructions` |
| `com.tcl.repairguide` | TCL repair guide | `adb shell pm enable com.tcl.repairguide` |
| `com.tcl.hearaid` | TCL hearing test, volume adaptation, TCL wireless-speaker pairing | `adb shell pm enable com.tcl.hearaid` |
| `com.tcl.t_solo` | TCL 'listen alone' Bluetooth headphones mode | `adb shell pm enable com.tcl.t_solo` |
| `com.google.android.play.games` | Google Play Games | `adb shell pm enable com.google.android.play.games` |
| `com.google.android.youtube.tvmusic` | YouTube Music (owner does not use it) | `adb shell pm enable com.google.android.youtube.tvmusic` |
| `com.tcl.smartalexa` | Amazon Alexa client (owner does not use it) | `adb shell pm enable com.tcl.smartalexa` |
| `com.tcl.UpdatePeripheral` | TCL soundbar/speaker firmware updater (no TCL audio gear) | `adb shell pm enable com.tcl.UpdatePeripheral` |
| `com.google.android.apps.tv.launcherx` | Google TV home screen (ads + recommendation rows), replaced by FLauncher | `adb shell pm enable com.google.android.apps.tv.launcherx` |
| `com.google.android.tungsten.setupwraith` | Google TV setup wizard; its RecoveryActivity grabs Home once Google TV home is off | `adb shell pm enable com.google.android.tungsten.setupwraith` |

## Other changes

| Change | Why | Undo |
|---|---|---|
| Screensaver set to `com.android.dreams.basic/.Colors` (was unset, so Google's ambient screensaver `dreamx` ran) | `dreamx` shows promos and was disabled; the TV needs a screensaver that still exists | `adb shell settings delete secure screensaver_components` |
| `window_animation_scale`, `transition_animation_scale`, `animator_duration_scale` set to 0.5 (were 1.0) | Menus feel faster | `adb shell settings put global <name> 1.0` for each |
| `pm trim-caches 999G` | Cleared app caches; `/data` went from 9.2 GB to 8.8 GB used | Nothing to undo; apps rebuild caches |
| Projectivy Launcher (`com.spocky.projengmenu`) **uninstalled** | Owner asked for it; its accessibility service grabbed the Home key over any other launcher | Install "Projectivy Launcher" from the Play Store |
| FLauncher (`me.efesser.flauncher`) installed and set as Home | Ad-free grid launcher, built from the owner's fork `dlt98/flauncher` (`~/Documents/development/flauncher`) | `adb shell cmd package set-home-activity com.google.android.apps.tv.launcherx/.home.HomeActivity` after re-enabling launcherx |
| Saved Wi-Fi (home network) forgotten and re-joined by the owner in Settings | It belonged to `setupwraith`; disabling the wizard would have dropped Wi-Fi | Nothing to undo |

## Home screen takeover

1. FLauncher installed via `adb push` + `pm install -r`, then `cmd package set-home-activity me.efesser.flauncher/.MainActivity`.
2. Google TV home still won while enabled, so `launcherx` was disabled.
3. `setupwraith/.RecoveryActivity` then took Home (blank, Home key did nothing), so `setupwraith` was disabled and the
   Home activity set again. Chromecast was advertised on the network before and after, and after the reboot.
4. After a reboot FLauncher is still Home and all 35 disabled packages stayed disabled (33 from this session plus
   Gboard and Prime Video, which were off before).

## Verification

`check.sh <tag>` presses Home, opens the Inputs panel, switches to HDMI 1, launches Netflix and YouTube, and saves a
contact sheet in `shots/`. HDMI and Netflix come back as black frames because the video is protected; the window
focus line proves they opened. All four batches and the post-reboot run passed (`shots/b1-sheet.png` to
`shots/b4-sheet.png`, `shots/final-sheet.png`).

## Memory (dumpsys meminfo)

| | Before | After (reboot + 2 min) | Change |
|---|---|---|---|
| Used RAM | 1,779,805K | 1,563,433K | -216 MB |
| Used PSS (apps) | 1,182,581K | 1,075,141K | -107 MB |
| Kernel | 597,224K | 488,292K | -109 MB |
| Truly free | 189,472K | 247,252K | +58 MB |
| Processes | 180 | 167 | -13 |

"Free RAM" as Android reports it (cached + free) went from 1,061 MB to 957 MB, because a freshly booted TV has less
kernel file cache. Used RAM is the comparable figure.

## Not changed on purpose

- Samba TV / ACR lives in `com.tcl.ttvs`, which also runs system updates. Turn ACR off in Settings > Privacy.
- The Inputs panel still shows fixed "Google TV Home", "TCL Channel" and "User Guide" tiles; those targets are
  disabled, so the tiles do nothing.
- YouTube's own ads are served inside the YouTube app; disabling packages cannot remove them.

## Custom launcher (2026-11-21, evening)

Replaced FLauncher as Home with a fork of the CleanYourTV launcher (MIT): `dlt98/cleanyourtv`, cloned to
`~/Documents/development/cleanyourtv`, package `com.gathin.tvhome`. FLauncher was later uninstalled (see below).

What the fork adds or sets for this TV (`launcher/app/src/main`):

- Favourites row of five: Netflix, YouTube, Live TV, VLC, Media Player (`res/values/config.xml` `dock_apps`, `columns`
  5). Change them on the TV: hold OK on a tile > Add to dock / Replace in dock.
- Top right: a memory pill (RAM %; OK lists running apps, a row opens that app's App info to force-stop it), a weather
  pill from Open-Meteo for the location in `launcher/local.properties` (OK shows the rest of the day), and Settings, which opens the quick settings panel.
  Inputs and Wi-Fi pills are off.
- Bloatware: hold OK on a tile > Mark as bloatware. The app leaves the grid and is listed behind the "Bloatware · N"
  button at the bottom of the grid; hold OK on it there to restore it.
- Clock screensaver: `screensaver_components` set to `com.gathin.tvhome/.ClockDream`.

Not possible on Android 14 without root, so left out: CPU % (apps cannot read CPU load) and a "Close all" button
(apps can no longer close other apps; Force stop goes through App info).

Build and install (from `~/Documents/development/cleanyourtv/launcher`):

```sh
export JAVA_HOME=$(/usr/libexec/java_home -v 17) ANDROID_SERIAL=<TV serial from adb devices>
./gradlew testReleaseUnitTest assembleRelease
adb push app/build/outputs/apk/release/app-release.apk /data/local/tmp/tvhome.apk
adb shell pm install -r /data/local/tmp/tvhome.apk && adb shell rm /data/local/tmp/tvhome.apk
# once, for the running-apps list (survives updates and reboots):
adb shell pm grant com.gathin.tvhome android.permission.DUMP
adb shell pm grant com.gathin.tvhome android.permission.PACKAGE_USAGE_STATS
adb shell cmd package set-home-activity com.gathin.tvhome/.MainActivity
```

The release build is signed with this Mac's Android debug key (`~/.android/debug.keystore`); back it up, or updates
will need an uninstall first.

Checked on the TV with screenshots (`shots/h*.png`): favourites row and app grid, both pills and their popups, the
Settings quick panel, the tile menu, tagging and restoring an app as bloatware, Google Assistant from the mic key, and
that the launcher is still Home after a reboot. `tv-guard.sh` checked the screen was idle before every key press.

Undo: `adb shell cmd package set-home-activity me.efesser.flauncher/.MainActivity` (back to FLauncher), or
`restore.sh` (back to Google TV home).

## FLauncher removed (2026-11-21)

The owner kept the custom launcher. FLauncher was uninstalled from the TV (`pm uninstall me.efesser.flauncher`,
a Play Store app) and its fork's local clone was deleted. Fallback Home is now Google TV's own launcher (see the
revert plan in `README.md` of `dlt98/cleanyourtv`, folder `my-tv/`).
