package de.codingair.tradesystem.ext.audit.metrics.migrations.sqlite;

import de.codingair.tradesystem.spigot.database.migrations.Migration;
import org.jetbrains.annotations.NotNull;

public class CreateMetricsTableMigration implements Migration {
    @Override
    public @NotNull String getStatement() {
        return "CREATE TABLE IF NOT EXISTS trade_metrics ("
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "	sender INTEGER NOT NULL,"
                + "	sender_server VARCHAR(75),"
                + "	sender_world VARCHAR(75) NOT NULL,"
                + "	receiver INTEGER NOT NULL,"
                + "	receiver_server VARCHAR(75),"
                + "	receiver_world VARCHAR(75) NOT NULL,"
                + "	category VARCHAR(50) NOT NULL,"
                + "	type VARCHAR(50) NOT NULL,"
                + "	specification TEXT,"
                + "	quantity TEXT NOT NULL,"
                + " date INTEGER NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + " FOREIGN KEY (sender) REFERENCES trade_players(id) ON DELETE RESTRICT,"
                + " FOREIGN KEY (receiver) REFERENCES trade_players(id) ON DELETE RESTRICT"
                + ");";
    }
}
