package com.gathin.tvhome;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

public class RunningAppsTest {
    @Test public void readsPackagesInDumpOrderOnce() {
        // Lines from a TCL 65C7L (Android 14), dumpsys activity lru.
        assertEquals(Arrays.asList("me.efesser.flauncher", "com.google.android.katniss", "com.google.android.youtube.tv",
                        "com.netflix.ninja", "com.android.vending", "com.tcl.ttvs"),
                RunningApps.parse(Arrays.asList(
                        "ACTIVITY MANAGER LRU PROCESSES (dumpsys activity lru)",
                        "  Activities:",
                        "  #48: fg     TOP  LCMNFU 1808:me.efesser.flauncher/u0a122 act:activities|recents",
                        "  #47: vis    BFGS LCMNFU 3674:com.google.android.katniss:interactor/u0a54 act:client|treated",
                        "  #42: cch    CAC  ------ 2761:com.google.android.youtube.tv/u0a64 act:activities|recents",
                        "  #41: cch+ 5 SVC  ------ 2684:com.netflix.ninja/u0a71 act:activities|recents",
                        "  Other:",
                        "  #39: cch+ 5 CEM  ------ 3507:com.android.vending:background/u0a53",
                        "  #38: cch+ 5 CEM  ------ 3360:com.android.vending/u0a53",
                        "  #44: prcp   FGS  ---NFU 1617:com.tcl.ttvs/1000 act:client")));
    }

    @Test public void aPermissionDenialListsNothing() {
        assertEquals(Collections.emptyList(), RunningApps.parse(Collections.singletonList(
                "Permission Denial: can't dump ActivityManager from from pid=5782, uid=10123 due to missing "
                        + "android.permission.PACKAGE_USAGE_STATS permission")));
    }
}
