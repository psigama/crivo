package io.github.rogeriocisi.airlab.platform;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * In production the application connects as {@code airlab_app}, the least-privilege role of V10,
 * while the local and test profiles connect as the database owner. A migration that creates a
 * table or a sequence without granting it to {@code airlab_app} therefore passes every other
 * test and only fails in production. This test closes that gap: it checks the privileges of the
 * role itself, which exists in the test database because V10 creates it.
 *
 * <p>Tables must be readable and insertable; whether a table also accepts UPDATE or DELETE is
 * each migration's decision (the audit trail and the deviations are append-only).
 */
@SpringBootTest
@ActiveProfiles("test")
class AppRoleGrantsTest {

    private static final String SCHEMAS = "('research', 'identity', 'audit')";

    @Autowired
    DataSource dataSource;

    @Test
    @DisplayName("toda tabela dos tres esquemas pode ser lida e receber insercao pelo airlab_app")
    void every_table_is_readable_and_insertable_by_the_app_role() {
        List<String> missing = new JdbcTemplate(dataSource).queryForList("""
                SELECT n.nspname || '.' || c.relname
                FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
                WHERE n.nspname IN %s AND c.relkind IN ('r', 'p')
                  AND c.relname <> 'flyway_schema_history'
                  AND NOT (has_table_privilege('airlab_app', c.oid, 'SELECT')
                           AND has_table_privilege('airlab_app', c.oid, 'INSERT'))
                ORDER BY 1
                """.formatted(SCHEMAS), String.class);

        assertThat(missing)
                .describedAs("tabelas sem SELECT e INSERT para airlab_app; conceda na migracao que as cria")
                .isEmpty();
    }

    @Test
    @DisplayName("toda sequencia dos tres esquemas pode ser usada pelo airlab_app")
    void every_sequence_is_usable_by_the_app_role() {
        // Materialized first: has_sequence_privilege errors on a relation that is not a sequence,
        // and the planner may evaluate it before the relkind filter.
        List<String> missing = new JdbcTemplate(dataSource).queryForList("""
                WITH seq AS MATERIALIZED (
                    SELECT c.oid, n.nspname || '.' || c.relname AS name
                    FROM pg_class c JOIN pg_namespace n ON n.oid = c.relnamespace
                    WHERE n.nspname IN %s AND c.relkind = 'S'
                )
                SELECT name FROM seq
                WHERE NOT has_sequence_privilege('airlab_app', oid, 'USAGE')
                ORDER BY 1
                """.formatted(SCHEMAS), String.class);

        assertThat(missing)
                .describedAs("sequencias sem USAGE para airlab_app: o INSERT com BIGSERIAL falharia em producao")
                .isEmpty();
    }
}
