package io.github.rogeriocisi.airlab.support;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * Dates and times for participants, written out in Brasília time (SPEC 0.23, section 1.4, rule 3):
 * "sexta-feira, 27 de novembro, às 7h". A participant reads a deadline, not a log, so neither UTC
 * nor {@code 2026-11-27 10:00}. The audit trail stays in UTC, and so do the pages that sit beside it.
 *
 * <p>{@code America/Sao_Paulo} is Brasília time in the IANA database, with its history: there has
 * been no daylight saving time since 2019, but an instant from before that still gets its own offset.
 * The caller says "(horário de Brasília)" on the page or in the message.
 */
public final class BrasiliaTime {

    public static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");
    private static final String NONE = "—";

    private BrasiliaTime() {
    }

    /** A deadline, days or weeks ahead, so no year: "sexta-feira, 27 de novembro, às 7h". */
    public static String deadline(Instant instant) {
        if (instant == null) {
            return NONE;
        }
        ZonedDateTime local = instant.atZone(ZONE);
        return local.getDayOfWeek().getDisplayName(TextStyle.FULL, PT_BR) + ", " + dayAndMonth(local)
                + ", " + atHour(local);
    }

    /** Something that already happened: "13 de novembro de 2026, às 7h30". */
    public static String moment(Instant instant) {
        if (instant == null) {
            return NONE;
        }
        ZonedDateTime local = instant.atZone(ZONE);
        return dayAndMonth(local) + " de " + local.getYear() + ", " + atHour(local);
    }

    /** A day: "13 de novembro de 2026". */
    public static String date(Instant instant) {
        if (instant == null) {
            return NONE;
        }
        ZonedDateTime local = instant.atZone(ZONE);
        return dayAndMonth(local) + " de " + local.getYear();
    }

    /** "27 de novembro", and "1º de dezembro" for the first day, as it is written in Brazil. */
    private static String dayAndMonth(ZonedDateTime local) {
        int day = local.getDayOfMonth();
        return (day == 1 ? "1º" : String.valueOf(day)) + " de "
                + local.getMonth().getDisplayName(TextStyle.FULL, PT_BR);
    }

    /** "às 7h", "às 19h05", and "à 1h" or "à 0h30", where the hour is singular. */
    private static String atHour(ZonedDateTime local) {
        int hour = local.getHour();
        int minute = local.getMinute();
        String time = minute == 0 ? hour + "h" : hour + "h" + (minute < 10 ? "0" : "") + minute;
        return (hour <= 1 ? "à " : "às ") + time;
    }
}
