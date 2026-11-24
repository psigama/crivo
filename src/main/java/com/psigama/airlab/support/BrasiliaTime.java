package com.psigama.airlab.support;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Dates and times for participants, written out in Brasília time (SPEC 0.23, section 1.4, rule 3):
 * "sexta-feira, 27 de novembro, às 7h", "Friday, November 27, at 7 a.m.". A participant reads a
 * deadline, not a log, so neither UTC nor {@code 2026-11-27 10:00}. The audit trail stays in UTC,
 * and so do the pages that sit beside it.
 *
 * <p>Since SPEC 0.28 (section 1.5, rule 7) it is written out in either language of the interface,
 * each in its own convention — the month before the day in English, the weekday spelled out in
 * both — and in the same time zone: the study runs in Brazil, and the deadline is the same instant
 * whoever reads it. The caller says "(horário de Brasília)" or "(Brasília time)" on the page or in
 * the message.
 *
 * <p>{@code America/Sao_Paulo} is Brasília time in the IANA database, with its history: there has
 * been no daylight saving time since 2019, but an instant from before that still gets its own offset.
 */
public final class BrasiliaTime {

    public static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final Locale EN = Locale.ENGLISH;
    private static final String NONE = "—";

    private BrasiliaTime() {
    }

    /**
     * A deadline, days or weeks ahead, so no year: "sexta-feira, 27 de novembro, às 7h",
     * "Friday, November 27, at 7 a.m.".
     */
    public static String deadline(Instant instant, Language language) {
        if (instant == null) {
            return NONE;
        }
        ZonedDateTime local = instant.atZone(ZONE);
        if (language == Language.EN) {
            return local.getDayOfWeek().getDisplayName(TextStyle.FULL, EN) + ", " + monthAndDay(local)
                    + ", " + atHourEn(local);
        }
        return local.getDayOfWeek().getDisplayName(TextStyle.FULL, PT_BR) + ", " + dayAndMonth(local)
                + ", " + atHour(local);
    }

    /** Something that already happened: "13 de novembro de 2026, às 7h30", "November 13, 2026, at 7:30 a.m.". */
    public static String moment(Instant instant, Language language) {
        if (instant == null) {
            return NONE;
        }
        ZonedDateTime local = instant.atZone(ZONE);
        if (language == Language.EN) {
            return monthAndDay(local) + ", " + local.getYear() + ", " + atHourEn(local);
        }
        return dayAndMonth(local) + " de " + local.getYear() + ", " + atHour(local);
    }

    /** A day: "13 de novembro de 2026", "November 13, 2026". */
    public static String date(Instant instant, Language language) {
        if (instant == null) {
            return NONE;
        }
        ZonedDateTime local = instant.atZone(ZONE);
        if (language == Language.EN) {
            return monthAndDay(local) + ", " + local.getYear();
        }
        return dayAndMonth(local) + " de " + local.getYear();
    }

    /** The same deadline in the language of origin, for a caller that has no language at hand. */
    public static String deadline(Instant instant) {
        return deadline(instant, Language.DEFAULT);
    }

    public static String moment(Instant instant) {
        return moment(instant, Language.DEFAULT);
    }

    public static String date(Instant instant) {
        return date(instant, Language.DEFAULT);
    }

    /** "27 de novembro", and "1º de dezembro" for the first day, as it is written in Brazil. */
    private static String dayAndMonth(ZonedDateTime local) {
        int day = local.getDayOfMonth();
        return (day == 1 ? "1º" : String.valueOf(day)) + " de "
                + local.getMonth().getDisplayName(TextStyle.FULL, PT_BR);
    }

    /** "November 27", month first, as it is written in the United States. */
    private static String monthAndDay(ZonedDateTime local) {
        return local.getMonth().getDisplayName(TextStyle.FULL, EN) + " " + local.getDayOfMonth();
    }

    /** "às 7h", "às 19h05", and "à 1h" or "à 0h30", where the hour is singular. */
    private static String atHour(ZonedDateTime local) {
        int hour = local.getHour();
        int minute = local.getMinute();
        String time = minute == 0 ? hour + "h" : hour + "h" + (minute < 10 ? "0" : "") + minute;
        return (hour <= 1 ? "à " : "às ") + time;
    }

    /** "at 7 a.m.", "at 7:05 p.m.", "at noon", "at midnight": the twelve-hour clock, spelled out. */
    private static String atHourEn(ZonedDateTime local) {
        int hour = local.getHour();
        int minute = local.getMinute();
        if (minute == 0 && hour == 12) {
            return "at noon";
        }
        if (minute == 0 && hour == 0) {
            return "at midnight";
        }
        int twelve = hour % 12 == 0 ? 12 : hour % 12;
        String time = minute == 0
                ? String.valueOf(twelve)
                : twelve + ":" + (minute < 10 ? "0" : "") + minute;
        return "at " + time + " " + (hour < 12 ? "a.m." : "p.m.");
    }
}
