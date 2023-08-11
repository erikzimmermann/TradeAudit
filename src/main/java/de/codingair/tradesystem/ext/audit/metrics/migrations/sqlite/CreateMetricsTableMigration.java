package de.codingair.tradesystem.ext.audit.metrics.migrations.sqlite;

import de.codingair.tradesystem.spigot.database.migrations.Migration;
import org.jetbrains.annotations.NotNull;

public class CreateMetricsTableMigration implements Migration {
    @Override
    public @NotNull String getStatement() {
        return "CREATE TABLE IF NOT EXISTS trade_metrics ("
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "	sender VARCHAR(36) NOT NULL,"
                + "	receiver VARCHAR(36) NOT NULL,"
                + "	category VARCHAR(50) NOT NULL,"
                + "	type VARCHAR(50) NOT NULL,"
                + "	specification TEXT,"
                + "	quantity TEXT NOT NULL,"
                + " date INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP);";
    }
}
