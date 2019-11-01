/*
 * Copyright © Litmusblox 2019. All rights reserved.
 */

package io.litmusblox.server;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.qatools.embed.postgresql.EmbeddedPostgres;
import ru.yandex.qatools.embed.postgresql.PostgresExecutable;
import ru.yandex.qatools.embed.postgresql.PostgresProcess;
import ru.yandex.qatools.embed.postgresql.PostgresStarter;
import ru.yandex.qatools.embed.postgresql.config.AbstractPostgresConfig;
import ru.yandex.qatools.embed.postgresql.config.PostgresConfig;
import ru.yandex.qatools.embed.postgresql.distribution.Version;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author : Shital Raval
 * Date : 1/11/19
 * Time : 4:50 PM
 * Class Name : EmbeddedPostgresConfiguration
 * Project Name : server
 */
@Configuration
@Log4j2
@Sql({"classpath:createDb.sql", "classpath:insertData.sql"})
public class EmbeddedPostgresConfiguration {

    @Bean(destroyMethod = "stop")
    public PostgresProcess postgresProcess() throws IOException {
        log.info("Starting embedded Postgres");

        String tempDir = System.getProperty("java.io.tmpdir");
        String dataDir = tempDir + "/database_for_tests";
        String binariesDir = System.getProperty("java.io.tmpdir") + "/postgres_binaries";

        PostgresConfig postgresConfig = new PostgresConfig(
                Version.V10_3,
                new AbstractPostgresConfig.Net("localhost", 5430),
                new AbstractPostgresConfig.Storage("integration-tests-db", dataDir),
                new AbstractPostgresConfig.Timeout(60_000),
                new AbstractPostgresConfig.Credentials("test", "password")
        );

        PostgresStarter<PostgresExecutable, PostgresProcess> runtime =
                PostgresStarter.getInstance(EmbeddedPostgres.cachedRuntimeConfig(Paths.get(binariesDir)));
        PostgresExecutable exec = runtime.prepare(postgresConfig);
        PostgresProcess process = exec.start();

        return process;
    }

    @Bean(destroyMethod = "close")
    @DependsOn("postgresProcess")
    DataSource dataSource(PostgresProcess postgresProcess) {
        PostgresConfig postgresConfig = postgresProcess.getConfig();

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:postgresql://localhost:5430/" + postgresConfig.storage().dbName());
        config.setUsername(postgresConfig.credentials().username());
        config.setPassword(postgresConfig.credentials().password());

        return new HikariDataSource(config);
    }
}
