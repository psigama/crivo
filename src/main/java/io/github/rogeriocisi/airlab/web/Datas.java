package io.github.rogeriocisi.airlab.web;

import io.github.rogeriocisi.airlab.support.BrasiliaTime;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

/**
 * Date formatting for the templates, in two registers (SPEC 0.23, section 1.4, rule 3).
 *
 * <p>{@code ${@datas.utc(instant)}} is for the pages that sit beside the audit trail: the
 * administrator's, the studies and the stamps. Always UTC, and said so on the page: the trail is in
 * UTC, and a panel in another zone would not match it.
 *
 * <p>{@code prazo}, {@code momento} and {@code data} are for the participants' screens: Brasília time,
 * written out, as {@link BrasiliaTime} does it. The page says "(horário de Brasília)" or
 * "(Brasília time)", and the {@code <time datetime>} around the text keeps the UTC instant.
 *
 * <p>Since SPEC 0.28 (section 1.5, rule 7) those three come out in the language of the page, which
 * is read here rather than passed by every template: the same expression in a Portuguese and in an
 * English template gives the date each reader expects, and no template had to change.
 */
@Component("datas")
class Datas {

    private static final DateTimeFormatter MINUTE =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneOffset.UTC);

    public String utc(Instant instant) {
        return instant == null ? "—" : MINUTE.format(instant);
    }

    /** A deadline: "sexta-feira, 27 de novembro, às 7h", "Friday, November 27, at 7 a.m.". */
    public String prazo(Instant instant) {
        return BrasiliaTime.deadline(instant, LanguagePages.current());
    }

    /** Something that happened: "13 de novembro de 2026, às 7h30", "November 13, 2026, at 7:30 a.m.". */
    public String momento(Instant instant) {
        return BrasiliaTime.moment(instant, LanguagePages.current());
    }

    /** A day: "13 de novembro de 2026", "November 13, 2026". */
    public String data(Instant instant) {
        return BrasiliaTime.date(instant, LanguagePages.current());
    }
}
