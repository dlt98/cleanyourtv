package com.gathin.tvhome;

import org.junit.Test;

import static org.junit.Assert.*;

public class WeatherTest {
    /** Hours 00..47 of 2026-11-21 and 22; temperature = hour index, code 61 from 20:00, 3 otherwise. */
    private static String answer(String now) {
        StringBuilder times = new StringBuilder(), temps = new StringBuilder(), codes = new StringBuilder(),
                rain = new StringBuilder(), day = new StringBuilder();
        for (int i = 0; i < 48; i++) {
            String sep = i == 0 ? "" : ",";
            times.append(sep).append(String.format("\"2026-11-%02dT%02d:00\"", 21 + i / 24, i % 24));
            temps.append(sep).append(i + 0.4);
            codes.append(sep).append(i % 24 >= 20 ? 61 : 3);
            rain.append(sep).append(i % 24 >= 20 ? 80 : 0);
            day.append(sep).append(i % 24 >= 7 && i % 24 < 17 ? 1 : 0);
        }
        return "{\"current\":{\"time\":\"" + now + "\",\"temperature_2m\":13.6,\"weather_code\":2,\"is_day\":0},"
                + "\"hourly\":{\"time\":[" + times + "],\"temperature_2m\":[" + temps + "],\"weather_code\":[" + codes
                + "],\"precipitation_probability\":[" + rain + "],\"is_day\":[" + day + "]}}";
    }

    @Test public void currentConditionsAreRounded() throws Exception {
        Weather.Report r = Weather.parse(answer("2026-11-21T09:45"));
        assertEquals(14, r.temp);
        assertEquals(2, r.code);
        assertFalse(r.day);
    }

    @Test public void listsTheHoursLeftTodayFromTheCurrentHour() throws Exception {
        Weather.Report r = Weather.parse(answer("2026-11-21T15:20"));
        assertEquals(9, r.hours.size());                  // 15:00 .. 23:00
        assertEquals("15:00", r.hours.get(0).time);
        assertEquals("23:00", r.hours.get(8).time);
        assertEquals(80, r.hours.get(5).rainChance);      // 20:00
        assertEquals(61, r.hours.get(5).code);
    }

    @Test public void lateInTheEveningRunsIntoTomorrow() throws Exception {
        Weather.Report r = Weather.parse(answer("2026-11-21T22:05"));
        assertEquals(Weather.MIN_HOURS, r.hours.size());   // 22, 23, then 00..03 tomorrow
        assertEquals("03:00", r.hours.get(5).time);
    }

    @Test public void morningIsCappedSoThePopupFits() throws Exception {
        assertEquals(Weather.MAX_HOURS, Weather.parse(answer("2026-11-21T06:00")).hours.size());
    }

    @Test public void urlAsksForTheLocationInDotDecimals() {
        String url = Weather.url(51.5072, -0.1276);
        assertTrue(url, url.contains("latitude=51.5072&longitude=-0.1276"));
    }

    @Test public void codesMapToSkies() {
        assertEquals("Clear", Weather.label(0));
        assertEquals("Rain", Weather.label(63));
        assertEquals("Thunderstorm", Weather.label(95));
        assertNotEquals(Weather.emoji(0, true), Weather.emoji(0, false));
    }
}
