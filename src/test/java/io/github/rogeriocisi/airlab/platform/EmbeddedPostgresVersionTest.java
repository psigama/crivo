package io.github.rogeriocisi.airlab.platform;

import static org.assertj.core.api.Assertions.assertThat;

import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

/**
 * The suite runs on the same major version of PostgreSQL as production (Constitution P9 and
 * deployment rule 4; docs/DEPLOY.md records the production version). If the binaries in
 * {@code pom.xml} move, this fails until {@link LocalDatabaseConfiguration#POSTGRES_MAJOR} and
 * the production server move with them.
 */
@SpringBootTest
@ActiveProfiles("test")
class EmbeddedPostgresVersionTest {

    @Autowired
    DataSource dataSource;

    @Test
    @DisplayName("o PostgreSQL embarcado dos testes e da mesma versao principal que o de producao")
    void the_embedded_server_is_the_production_major_version() {
        Integer versionNum = new JdbcTemplate(dataSource)
                .queryForObject("SELECT current_setting('server_version_num')::int", Integer.class);

        assertThat(versionNum / 10000).isEqualTo(LocalDatabaseConfiguration.POSTGRES_MAJOR);
    }
}
