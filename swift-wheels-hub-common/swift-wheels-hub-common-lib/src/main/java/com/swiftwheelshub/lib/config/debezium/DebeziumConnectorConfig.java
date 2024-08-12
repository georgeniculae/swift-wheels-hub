package com.swiftwheelshub.lib.config.debezium;

import com.swiftwheelshub.exception.SwiftWheelsHubException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
@ConditionalOnProperty(prefix = "debezium", name = "enabled")
public class DebeziumConnectorConfig {

    @Value("${debezium.connector-name}")
    private String connectorName;

    @Value("${debezium.database-host}")
    private String databaseHost;

    @Value("${debezium.database-name}")
    private String databaseName;

    @Value("${debezium.database-port}")
    private String databasePort;

    @Value("${spring.datasource.username}")
    private String databaseUsername;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    @Value("${debezium.topic-name}")
    private String topicName;

    @Value("${debezium.server-name}")
    private String serverName;

    @Value("${debezium.server-id}")
    private int serverId;

    @Value("${debezium.table-name}")
    private String tableName;

    @Value("${debezium.schema-name}")
    private String schemaName;

    @Value("${debezium.slot-name}")
    private String slotName;

    @Bean
    public io.debezium.config.Configuration debeziumConnector() {
        try {
            File offsetStorageTempFile = File.createTempFile("offsets_", ".dat");
            File dbHistoryTempFile = File.createTempFile("dbhistory_", ".dat");

            return io.debezium.config.Configuration.create()
                    .with("name", connectorName)
                    .with("connector.class", "io.debezium.connector.postgresql.PostgresConnector")
                    .with("plugin.name", "pgoutput")
                    .with("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore")
                    .with("offset.storage.file.filename", offsetStorageTempFile.getAbsolutePath())
                    .with("offset.flush.interval.ms", "60000")
                    .with("decimal.handling.mode", "string")
                    .with("database.hostname", databaseHost)
                    .with("database.port", databasePort)
                    .with("database.user", databaseUsername)
                    .with("database.password", databasePassword)
                    .with("database.dbname", databaseName)
                    .with("table.include.list", schemaName + "." + tableName)
                    .with("include.schema.changes", "false")
                    .with("database.allowPublicKeyRetrieval", "true")
                    .with("database.server.id", "10181")
                    .with("database.server.name", serverName)
                    .with("database.history", "io.debezium.relational.history.FileDatabaseHistory")
                    .with("database.history.file.filename", dbHistoryTempFile.getAbsolutePath())
                    .with("topic.prefix", topicName)
                    .with("publication.name", slotName)
                    .with("slot.name", slotName)
                    .build();
        } catch (Exception e) {
            throw new SwiftWheelsHubException(e.getMessage());
        }
    }

}
