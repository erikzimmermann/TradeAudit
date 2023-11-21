package de.codingair.tradesystem.ext.audit.metrics.migrations.mysql;

import de.codingair.tradesystem.spigot.database.migrations.Migration;
import org.jetbrains.annotations.NotNull;

public class CreateMetricsTableMigration implements Migration {
    @Override
    public @NotNull String getStatement() {
        return "CREATE TABLE IF NOT EXISTS trade_metrics ("
                + "	id INT PRIMARY KEY AUTO_INCREMENT,"
                + "	sender INT NOT NULL,"
                + "	sender_server VARCHAR(75),"
                + "	sender_world VARCHAR(75) NOT NULL,"
                + "	receiver INT NOT NULL,"
                + "	receiver_server VARCHAR(75),"
                + "	receiver_world VARCHAR(75) NOT NULL,"
                + "	category VARCHAR(50) NOT NULL,"
                + "	type VARCHAR(50) NOT NULL,"
                + "	specification TEXT,"
                + "	quantity TEXT NOT NULL,"
                + " date DATETIME NOT NULL DEFAULT NOW(),"
                + " FOREIGN KEY (sender) REFERENCES trade_players(id) ON DELETE RESTRICT,"
                + " FOREIGN KEY (receiver) REFERENCES trade_players(id) ON DELETE RESTRICT"
                + ");";
    }
}
