package com.psigama.airlab.platform;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * The embedded database refuses to start next to another instance, or on a cluster from another
 * major version, with a message that says what to do, instead of the bare "could not lock" or the
 * line deep in the PostgreSQL log that the library gives.
 */
class LocalDatabaseConfigurationTest {

    private static final Path DATA = Path.of("dados-de-teste").toAbsolutePath();

    @Test
    @DisplayName("cluster de outra versao do PostgreSQL: recusa com a mensagem que diz o que fazer")
    void a_cluster_from_another_major_version_is_refused_with_guidance(@TempDir Path data) throws Exception {
        Files.writeString(data.resolve("PG_VERSION"), "16\n");

        assertThatThrownBy(() -> LocalDatabaseConfiguration.requireSameMajor(data))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining(data.toString())
                .hasMessageContaining("PostgreSQL 16")
                .hasMessageContaining("PostgreSQL " + LocalDatabaseConfiguration.POSTGRES_MAJOR)
                .hasMessageContaining("AIRLAB_LOCAL_DB_DIR")
                .hasMessageContaining("AIRLAB_TEST_DB_DIR");
        assertThat(LocalDatabaseConfiguration.otherMajor(data, "16").chars().allMatch(c -> c < 128)).isTrue();
    }

    @Test
    @DisplayName("cluster da mesma versao, ou diretorio ainda sem cluster: nada a dizer")
    void the_same_major_version_or_no_cluster_passes(@TempDir Path data) throws Exception {
        assertThatCode(() -> LocalDatabaseConfiguration.requireSameMajor(data)).doesNotThrowAnyException();
        assertThatCode(() -> LocalDatabaseConfiguration.requireSameMajor(data.resolve("ainda-nao-existe")))
                .doesNotThrowAnyException();

        Files.writeString(data.resolve("PG_VERSION"), LocalDatabaseConfiguration.POSTGRES_MAJOR + "\n");
        assertThatCode(() -> LocalDatabaseConfiguration.requireSameMajor(data)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("porta do banco ocupada: recusa com a mensagem que diz o que fazer")
    void an_occupied_port_is_refused_with_guidance() throws Exception {
        try (ServerSocket other = new ServerSocket()) {
            other.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
            int port = other.getLocalPort();

            assertThatThrownBy(() -> LocalDatabaseConfiguration.requirePortFree(port, DATA))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("ja esta em uso")
                    .hasMessageContaining("porta " + port)
                    .hasMessageContaining("AIRLAB_LOCAL_DB_PORT");
        }
    }

    @Test
    @DisplayName("porta livre: nada a dizer")
    void a_free_port_passes() throws Exception {
        int port;
        try (ServerSocket probe = new ServerSocket()) {
            probe.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0));
            port = probe.getLocalPort();
        }
        int free = port;
        assertThatCode(() -> LocalDatabaseConfiguration.requirePortFree(free, DATA)).doesNotThrowAnyException();
    }

    @Test
    @DisplayName("a mensagem cita o diretorio, a outra instancia e o processo esquecido, sem acento no log")
    void the_message_names_the_likely_causes() {
        String message = LocalDatabaseConfiguration.alreadyInUse(DATA, 5433);

        assertThat(message).contains(DATA.toString()).contains("http://localhost:8080")
                .contains("Ctrl+C").contains("postgres").contains("AIRLAB_LOCAL_DB_DIR");
        assertThat(message.chars().allMatch(c -> c < 128)).isTrue();
    }
}
