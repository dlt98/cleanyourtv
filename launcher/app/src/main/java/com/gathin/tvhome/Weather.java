package com.gathin.tvhome;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The weather pill (config weather_lat / weather_lon): the current temperature and sky from Open-Meteo, which needs
 * no API key, and the hourly forecast for the rest of the day for its popup. Parsing only; MainActivity fetches.
 */
final class Weather {
    private Weather() {}

    /** At least this many hours in the popup, running into tomorrow late in the evening. */
    static final int MIN_HOURS = 6;
    /** At most this many, so the popup fits under the pill. */
    static final int MAX_HOURS = 12;

    static final class Hour {
        final String time;   // "15:00"
        final int temp, code, rainChance;
        final boolean day;

        Hour(String time, int temp, int code, int rainChance, boolean day) {
            this.time = time;
            this.temp = temp;
            this.code = code;
            this.rainChance = rainChance;
            this.day = day;
        }
    }

    static final class Report {
        final int temp, code;
        final boolean day;
        final List<Hour> hours;

        Report(int temp, int code, boolean day, List<Hour> hours) {
            this.temp = temp;
            this.code = code;
            this.day = day;
            this.hours = hours;
        }
    }

    static String url(double lat, double lon) {
        return String.format(Locale.ROOT, "https://api.open-meteo.com/v1/forecast?latitude=%.4f&longitude=%.4f"
                + "&current=temperature_2m,weather_code,is_day"
                + "&hourly=temperature_2m,weather_code,precipitation_probability,is_day"
                + "&timezone=auto&forecast_days=2", lat, lon);
    }

    /**
     * The report in an Open-Meteo answer: current conditions, then the hours from the current one to the end of
     * today (at least MIN_HOURS, at most MAX_HOURS). Times are the location's own (timezone=auto).
     */
    static Report parse(String json) throws JSONException {
        JSONObject root = new JSONObject(json);
        JSONObject current = root.getJSONObject("current");
        String now = current.getString("time");                 // "2026-11-21T18:45"
        String today = now.substring(0, 10), nowHour = now.substring(0, 13);
        JSONObject hourly = root.getJSONObject("hourly");
        JSONArray times = hourly.getJSONArray("time");
        JSONArray temps = hourly.getJSONArray("temperature_2m");
        JSONArray codes = hourly.getJSONArray("weather_code");
        JSONArray rain = hourly.optJSONArray("precipitation_probability");
        JSONArray days = hourly.optJSONArray("is_day");
        List<Hour> hours = new ArrayList<>();
        for (int i = 0; i < times.length() && hours.size() < MAX_HOURS; i++) {
            String t = times.getString(i);
            if (t.substring(0, 13).compareTo(nowHour) < 0) continue;
            if (!t.startsWith(today) && hours.size() >= MIN_HOURS) break;
            hours.add(new Hour(t.substring(11, 16), (int) Math.round(temps.getDouble(i)), codes.getInt(i),
                    rain == null ? -1 : rain.optInt(i, -1), days == null || days.optInt(i, 1) == 1));
        }
        return new Report((int) Math.round(current.getDouble("temperature_2m")), current.getInt("weather_code"),
                current.optInt("is_day", 1) == 1, hours);
    }

    /** An emoji for a WMO weather code (what Open-Meteo returns). */
    static String emoji(int code, boolean day) {
        if (code == 0) return day ? "☀️" : "🌙";                 // ☀️ 🌙
        if (code == 1) return day ? "🌤️" : "🌙";           // 🌤️ 🌙
        if (code == 2) return day ? "⛅" : "☁️";                       // ⛅ ☁️
        if (code == 3) return "☁️";                                        // ☁️
        if (code == 45 || code == 48) return "🌫️";                   // 🌫️
        if (code >= 51 && code <= 57) return "🌦️";                   // 🌦️
        if (code >= 61 && code <= 67) return "🌧️";                   // 🌧️
        if (code >= 71 && code <= 77 || code == 85 || code == 86) return "🌨️";   // 🌨️
        if (code >= 80 && code <= 82) return "🌦️";                   // 🌦️
        if (code >= 95) return "⛈️";                                       // ⛈️
        return "🌡️";                                                 // 🌡️
    }

    /** A short English name for a WMO weather code. */
    static String label(int code) {
        if (code == 0) return "Clear";
        if (code == 1) return "Mostly clear";
        if (code == 2) return "Partly cloudy";
        if (code == 3) return "Overcast";
        if (code == 45 || code == 48) return "Fog";
        if (code >= 51 && code <= 57) return "Drizzle";
        if (code >= 61 && code <= 67) return "Rain";
        if (code >= 71 && code <= 77 || code == 85 || code == 86) return "Snow";
        if (code >= 80 && code <= 82) return "Showers";
        if (code >= 95) return "Thunderstorm";
        return "Weather";
    }
}
