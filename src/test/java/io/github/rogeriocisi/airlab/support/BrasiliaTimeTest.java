package io.github.rogeriocisi.airlab.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Dates and times for participants (SPEC 0.23, section 1.4, rule 3): written out, in Brasília time,
 * never in UTC.
 */
class BrasiliaTimeTest {

    @Test
    @DisplayName("o prazo sai por extenso, com o dia da semana e sem o ano")
    void a_deadline_is_written_out() {
        assertThat(BrasiliaTime.deadline(Instant.parse("2026-11-27T10:00:00Z")))
                .isEqualTo("sexta-feira, 27 de novembro, às 7h");
    }

    @Test
    @DisplayName("o horario e o de Brasilia, inclusive quando muda o dia")
    void the_time_is_brasilia_time_even_across_midnight() {
        assertThat(BrasiliaTime.moment(Instant.parse("2026-11-13T10:30:00Z")))
                .isEqualTo("13 de novembro de 2026, às 7h30");
        assertThat(BrasiliaTime.moment(Instant.parse("2026-11-14T02:05:00Z")))
                .isEqualTo("13 de novembro de 2026, às 23h05");
        assertThat(BrasiliaTime.date(Instant.parse("2026-11-14T02:05:00Z")))
                .isEqualTo("13 de novembro de 2026");
    }

    @Test
    @DisplayName("primeiro dia do mes com ordinal, e a uma hora no singular")
    void first_day_and_one_o_clock() {
        assertThat(BrasiliaTime.deadline(Instant.parse("2026-12-01T04:00:00Z")))
                .isEqualTo("terça-feira, 1º de dezembro, à 1h");
        assertThat(BrasiliaTime.moment(Instant.parse("2026-12-01T03:30:00Z")))
                .isEqualTo("1º de dezembro de 2026, à 0h30");
    }

    @Test
    @DisplayName("um instante antigo leva o fuso da epoca: horario de verao em 2018")
    void an_old_instant_gets_the_offset_of_its_time() {
        assertThat(BrasiliaTime.moment(Instant.parse("2018-11-10T12:00:00Z")))
                .isEqualTo("10 de novembro de 2018, às 10h");
    }

    @Test
    @DisplayName("sem instante, um travessao")
    void no_instant_gives_a_dash() {
        assertThat(BrasiliaTime.deadline(null)).isEqualTo("—");
        assertThat(BrasiliaTime.moment(null)).isEqualTo("—");
        assertThat(BrasiliaTime.date(null)).isEqualTo("—");
    }
}
