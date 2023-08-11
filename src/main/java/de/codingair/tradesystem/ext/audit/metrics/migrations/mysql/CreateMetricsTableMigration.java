package de.codingair.tradesystem.ext.audit.metrics.migrations.mysql;

import de.codingair.tradesystem.spigot.database.migrations.Migration;
import org.jetbrains.annotations.NotNull;

public class CreateMetricsTableMigration implements Migration {
    @Override
    public @NotNull String getStatement() {
        return "CREATE TABLE IF NOT EXISTS trade_metrics ("
                + "	id INT PRIMARY KEY AUTO_INCREMENT,"
                + "	sender VARCHAR(36) NOT NULL,"
                + "	receiver VARCHAR(36) NOT NULL,"
                + "	category VARCHAR(50) NOT NULL,"
                + "	type VARCHAR(50) NOT NULL,"
                + "	specification TEXT,"
                + "	quantity TEXT NOT NULL,"
                + " date DATETIME NOT NULL DEFAULT NOW());";
    }
}
