package com.gathin.tvhome;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The memory pill's popup: which apps have a process right now, most recent first, from
 * {@code dumpsys activity lru}. Android 14 shows other apps' processes only to a caller holding DUMP and
 * PACKAGE_USAGE_STATS, both granted with pm grant over ADB; without them the list is empty.
 */
final class RunningApps {
    private RunningApps() {}

    // "  #42: cch    CAC  ------ 2761:com.google.android.youtube.tv/u0a64 act:activities|recents"
    // "  #39: cch+ 5 CEM  ------ 3507:com.android.vending:background/u0a53"
    private static final Pattern PROCESS = Pattern.compile("^\\s*#\\d+:.*?\\s\\d+:([A-Za-z0-9_.]+)(?::[^/\\s]*)?/\\S+");

    /** Package names in the order the dump lists them, each once. */
    static List<String> parse(Iterable<String> lines) {
        Set<String> packages = new LinkedHashSet<>();
        for (String line : lines) {
            Matcher m = PROCESS.matcher(line);
            if (m.find()) packages.add(m.group(1));
        }
        return new ArrayList<>(packages);
    }

    /** Runs the dump. Off the main thread: it can take a few hundred milliseconds. */
    static List<String> read() {
        List<String> lines = new ArrayList<>();
        try {
            Process p = new ProcessBuilder("dumpsys", "activity", "lru").redirectErrorStream(true).start();
            try (BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                for (String line; (line = r.readLine()) != null;) lines.add(line);
            }
            p.waitFor();
        } catch (IOException e) {
            return new ArrayList<>();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new ArrayList<>();
        }
        return parse(lines);
    }
}
