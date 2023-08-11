package de.codingair.tradesystem.ext.audit.metrics;

import de.codingair.tradesystem.ext.audit.TradeAudit;
import de.codingair.tradesystem.ext.audit.metrics.repositories.MysqlMetricsRepository;
import de.codingair.tradesystem.ext.audit.metrics.repositories.SqliteMetricsRepository;
import de.codingair.tradesystem.ext.audit.utils.SpecificationUtils;
import de.codingair.tradesystem.spigot.TradeSystem;
import de.codingair.tradesystem.spigot.database.migrations.mysql.MysqlMigrations;
import de.codingair.tradesystem.spigot.database.migrations.sqlite.SqLiteMigrations;
import de.codingair.tradesystem.spigot.trade.gui.layout.registration.EditorInfo;
import de.codingair.tradesystem.spigot.trade.gui.layout.registration.IconHandler;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.impl.economy.EconomyIcon;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public class MetricsService {
    private final MetricsRepository repository;

    public MetricsService() {
        registerMigrations();

        switch (TradeSystem.database().getType()) {
            case MYSQL:
                repository = new MysqlMetricsRepository();
                break;

            case SQLITE:
                repository = new SqliteMetricsRepository();
                break;

            default:
                repository = null;
                TradeAudit.getInstance().getLogger().severe("Cannot find database of type '" + TradeSystem.database().getType() + "'!");
        }
    }

    public void log(@NotNull UUID sender, @NotNull UUID receiver, @NotNull ItemStack item) {
        if (repository == null) return;

        try {
            repository.log(sender, receiver, "item", item.getType().name(), SpecificationUtils.getItemSpecification(sender, receiver, item), BigDecimal.valueOf(item.getAmount()));
        } catch (Exception e) {
            TradeAudit.getInstance().getLogger().severe("Failed to log metrics: " + e.getMessage());
        }
    }

    public void log(@NotNull UUID sender, @NotNull UUID receiver, @NotNull EconomyIcon<?> icon) {
        if (repository == null) return;
        if (icon.getValue().equals(icon.getDefault())) return;

        try {
            EditorInfo info = IconHandler.getInfo(icon.getClass());

            repository.log(sender, receiver, "economy", info.getName(), null, icon.getValue());
        } catch (Exception e) {
            TradeAudit.getInstance().getLogger().severe("Failed to log metrics: " + e.getMessage());
        }
    }

    private void registerMigrations() {
        MysqlMigrations.getInstance().register(TradeAudit.getInstance(),
                new de.codingair.tradesystem.ext.audit.metrics.migrations.mysql.CreateMetricsTableMigration()
        );
        SqLiteMigrations.getInstance().register(TradeAudit.getInstance(),
                new de.codingair.tradesystem.ext.audit.metrics.migrations.sqlite.CreateMetricsTableMigration()
        );
    }

}
