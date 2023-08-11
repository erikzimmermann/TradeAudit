package de.codingair.tradesystem.ext.audit.metrics.repositories;

import com.google.gson.JsonObject;
import de.codingair.tradesystem.ext.audit.metrics.MetricsRepository;
import de.codingair.tradesystem.spigot.database.migrations.mysql.MySQLConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class MysqlMetricsRepository implements MetricsRepository {

    @Override
    public void log(@NotNull UUID sender, @NotNull UUID receiver, @NotNull String category, @NotNull String type, @Nullable JsonObject specification, @NotNull BigDecimal quantity) throws SQLException {
        String sql = "INSERT INTO trade_metrics(sender, receiver, category, type, specification, quantity) VALUES(?,?,?,?,?,?);";

        try (Connection con = MySQLConnection.getConnection().get(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, sender.toString());
            pstmt.setString(2, receiver.toString());
            pstmt.setString(3, category);
            pstmt.setString(4, type);
            pstmt.setString(5, specification == null ? null : specification.toString());
            pstmt.setString(6, quantity.toString());

            pstmt.executeUpdate();
        }
    }
}
