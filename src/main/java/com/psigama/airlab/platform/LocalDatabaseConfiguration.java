package com.psigama.airlab.platform;

import io.zonky.test.db.postgres.embedded.EmbeddedPostgres;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Brings up PostgreSQL inside the application process, from the real PostgreSQL binaries,
 * with no Docker and no manual installation (Constitution, architectural rule 8).
 *
 * <p>Active only when {@code airlab.local-database.enabled} is true, which the {@code local}
 * profile sets and the {@code prod} profile does not. In production the database is the one
 * installed on the operating system and this class does nothing.
 *
 * <p>The cluster lives under a directory outside the project, so stopping the application does
 * not lose the dry-run data, and deleting that directory is a clean reset.
 *
 * <p><b>One cluster per JVM, not per application context.</b> A PostgreSQL cluster is a process
 * level resource: it holds a port and a lock file in its data directory. The test suite loads
 * several Spring contexts in one JVM, and a second context starting its own instance on the
 * same port fails with {@code could not lock}. So the instance is shared and closed by a JVM
 * shutdown hook rather than by context shutdown, which is also what happens in production,
 * where the context and the JVM end together.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnProperty(prefix = "airlab.local-database", name = "enabled", havingValue = "true")
class LocalDatabaseConfiguration {

    private static final Logger log = LoggerFactory.getLogger(LocalDatabaseConfiguration.class);

    /**
     * Major version of the embedded binaries ({@code embedded-postgres-binaries.version} in
     * {@code pom.xml}), which is also the version in production (docs/DEPLOY.md), so the suite
     * proves what production runs. {@code EmbeddedPostgresVersionTest} checks it against the
     * running server.
     */
    static final int POSTGRES_MAJOR = 18;

    private static final Object MONITOR = new Object();
    private static EmbeddedPostgres shared;
    private static String sharedKey;

    @Bean
    EmbeddedPostgres embeddedPostgres(LocalDatabaseProperties properties) throws IOException {
        Path dataDirectory = Path.of(properties.dataDirectory()).toAbsolutePath().normalize();
        String key = properties.port() + "@" + dataDirectory;

        synchronized (MONITOR) {
            if (shared != null) {
                if (!key.equals(sharedKey)) {
                    throw new IllegalStateException(
                            "este processo ja subiu um PostgreSQL embarcado em " + sharedKey
                                    + " e nao pode subir outro em " + key
                                    + "; use o mesmo porto e diretorio, ou um processo separado");
                }
                log.debug("Reaproveitando o PostgreSQL embarcado de {}", sharedKey);
                return shared;
            }

            Files.createDirectories(dataDirectory.getParent() == null
                    ? dataDirectory
                    : dataDirectory.getParent());

            log.info("Subindo PostgreSQL embarcado na porta {} (dados em {})",
                    properties.port(), dataDirectory);

            // Checked before starting: "could not lock" alone does not tell anyone what to do, and a
            // cluster from another major version only fails deep inside the PostgreSQL log.
            requireSameMajor(dataDirectory);
            requirePortFree(properties.port(), dataDirectory);
            EmbeddedPostgres instance;
            try {
                instance = EmbeddedPostgres.builder()
                        .setPort(properties.port())
                        .setDataDirectory(dataDirectory.toFile())
                        .setCleanDataDirectory(false)
                        .start();
            } catch (IllegalStateException e) {
                // The data directory is locked by another process — and the JDK reports a lock
                // held inside this same JVM as an OverlappingFileLockException, without a message.
                if (e instanceof OverlappingFileLockException
                        || (e.getMessage() != null && e.getMessage().contains("could not lock"))) {
                    throw new IllegalStateException(alreadyInUse(dataDirectory, properties.port()), e);
                }
                throw e;
            }

            shared = instance;
            sharedKey = key;
            Runtime.getRuntime().addShutdownHook(new Thread(() -> close(instance), "airlab-pg-stop"));
            return instance;
        }
    }

    /**
     * Refuses to start when something already listens on the database port, with a message that
     * says what is most likely going on and what to do. Almost always it is another instance of
     * the Crivo, or the PostgreSQL left behind by one that was killed instead of stopped.
     */
    static void requirePortFree(int port, Path dataDirectory) {
        // The JDK's default address reuse is the right one on each system: on Linux it lets a port
        // in TIME_WAIT after a restart be bound, and on Windows it never binds over a live listener.
        try (ServerSocket probe = new ServerSocket()) {
            probe.bind(new InetSocketAddress(InetAddress.getLoopbackAddress(), port));
        } catch (IOException e) {
            throw new IllegalStateException(alreadyInUse(dataDirectory, port), e);
        }
    }

    /**
     * Refuses a data directory created by another major version of PostgreSQL, which the embedded
     * binaries cannot open. It happens once on each machine after the binaries move to a new major
     * version. A directory with no cluster yet passes: the start creates one.
     */
    static void requireSameMajor(Path dataDirectory) throws IOException {
        Path versionFile = dataDirectory.resolve("PG_VERSION");
        if (!Files.isRegularFile(versionFile)) {
            return;
        }
        String found = Files.readString(versionFile).strip();
        if (!found.equals(String.valueOf(POSTGRES_MAJOR))) {
            throw new IllegalStateException(otherMajor(dataDirectory, found));
        }
    }

    /** What to tell the person starting the application. ASCII only, like the rest of the log. */
    static String otherMajor(Path dataDirectory, String found) {
        return "Os dados em " + dataDirectory + " foram criados pelo PostgreSQL " + found
                + ", e o Crivo agora embarca o PostgreSQL " + POSTGRES_MAJOR + ", que nao abre esses arquivos. "
                + "Fora de producao os dados sao ficticios: apague esse diretorio para comecar do zero, "
                + "ou de a AIRLAB_LOCAL_DB_DIR um diretorio novo (nos testes, AIRLAB_TEST_DB_DIR).";
    }

    /** What to tell the person starting the application. ASCII only, like the rest of the log. */
    static String alreadyInUse(Path dataDirectory, int port) {
        return "O banco local ja esta em uso (porta " + port + ", dados em " + dataDirectory + "). "
                + "Quase sempre e outra instancia do Crivo rodando: veja se http://localhost:8080 responde, "
                + "ou encerre a outra janela com Ctrl+C. Se nenhuma estiver aberta, sobrou um processo "
                + "'postgres' de uma execucao interrompida: encerre-o (Gerenciador de Tarefas no Windows, "
                + "pkill -f pgdata no Linux) e tente de novo. Para rodar duas instancias de proposito, de a "
                + "cada uma AIRLAB_LOCAL_DB_DIR e AIRLAB_LOCAL_DB_PORT proprios.";
    }

    @Bean
    DataSource dataSource(EmbeddedPostgres embeddedPostgres, LocalDatabaseProperties properties) throws SQLException {
        String database = properties.databaseName();
        if (database == null || database.isBlank() || "postgres".equals(database)) {
            return embeddedPostgres.getPostgresDatabase();
        }
        // A name, not an expression: it goes into DDL, which takes no bind parameters.
        if (!database.matches("[a-z][a-z0-9_]{0,62}")) {
            throw new IllegalArgumentException("nome de banco invalido: " + database);
        }
        try (Connection connection = embeddedPostgres.getPostgresDatabase().getConnection();
             Statement statement = connection.createStatement()) {
            if (properties.recreateDatabase()) {
                statement.execute("DROP DATABASE IF EXISTS " + database);
            }
            boolean exists;
            try (ResultSet rows = statement.executeQuery(
                    "SELECT 1 FROM pg_database WHERE datname = '" + database + "'")) {
                exists = rows.next();
            }
            if (!exists) {
                statement.execute("CREATE DATABASE " + database);
            }
        }
        log.info("Banco {} no PostgreSQL embarcado{}", database,
                properties.recreateDatabase() ? " (recriado)" : "");
        return embeddedPostgres.getDatabase("postgres", database);
    }

    private static void close(EmbeddedPostgres instance) {
        try {
            instance.close();
        } catch (IOException e) {
            // Nothing useful is left to do while the JVM is going down.
            log.warn("Falha ao encerrar o PostgreSQL embarcado: {}", e.getMessage());
        }
    }
}
