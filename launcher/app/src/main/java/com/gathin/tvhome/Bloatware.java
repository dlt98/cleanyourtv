package com.gathin.tvhome;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 * Apps the owner tagged as bloatware from the tile menu. They leave the grid and are listed in the panel behind
 * the "Bloatware" button at the bottom of it, where they can be opened or untagged.
 */
final class Bloatware {
    private Bloatware() {}

    private static SharedPreferences prefs(Context context) {
        return context.getSharedPreferences("bloatware", Context.MODE_PRIVATE);
    }

    /** A copy: the set SharedPreferences returns must not be changed. */
    static Set<String> load(Context context) {
        return new HashSet<>(prefs(context).getStringSet("packages", new HashSet<>()));
    }

    static void set(Context context, String pkg, boolean bloat) {
        Set<String> packages = load(context);
        if (bloat) packages.add(pkg);
        else packages.remove(pkg);
        prefs(context).edit().putStringSet("packages", packages).apply();
    }
}
