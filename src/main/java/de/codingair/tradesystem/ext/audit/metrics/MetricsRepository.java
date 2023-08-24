package de.codingair.tradesystem.ext.audit.metrics;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.UUID;

public interface MetricsRepository {

    void log(@NotNull UUID sender, @Nullable String senderServer, @NotNull String senderWorld, @NotNull UUID receiver, @Nullable String receiverServer, @NotNull String receiverWorld, @NotNull String category, @NotNull String type,
             @Nullable JsonObject specification, @NotNull BigDecimal quantity) throws SQLException;

}
