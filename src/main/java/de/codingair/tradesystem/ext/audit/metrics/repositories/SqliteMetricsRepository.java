package de.codingair.tradesystem.ext.audit.metrics.repositories;

import com.google.gson.JsonObject;
import de.codingair.tradesystem.ext.audit.metrics.MetricsRepository;
import de.codingair.tradesystem.spigot.database.migrations.sqlite.SqlLiteConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class SqliteMetricsRepository implements MetricsRepository {

    @Override
    public void log(@NotNull UUID sender, @Nullable String senderServer, @NotNull String senderWorld, @NotNull UUID receiver, @Nullable String receiverServer, @NotNull String receiverWorld, @NotNull String category, @NotNull String type, @Nullable JsonObject specification, @NotNull BigDecimal quantity) throws SQLException {
        String sql = "INSERT INTO trade_metrics(sender, sender_server, sender_world, receiver, receiver_server, receiver_world, category, type, specification, quantity) VALUES((SELECT id FROM trade_players WHERE uuid=? LIMIT 1),?,?,(SELECT id FROM trade_players WHERE uuid=? LIMIT 1),?,?,?,?,?,?);";

        try (Connection con = SqlLiteConnection.connect(); PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, sender.toString());
            pstmt.setString(2, senderServer);
            pstmt.setString(3, senderWorld);
            pstmt.setString(4, receiver.toString());
            pstmt.setString(5, receiverServer);
            pstmt.setString(6, receiverWorld);
            pstmt.setString(7, category);
            pstmt.setString(8, type);
            pstmt.setString(9, specification == null ? null : specification.toString());
            pstmt.setString(10, quantity.toString());

            pstmt.executeUpdate();
        }
    }
}
