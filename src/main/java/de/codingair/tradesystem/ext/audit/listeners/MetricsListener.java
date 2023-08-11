package de.codingair.tradesystem.ext.audit.listeners;

import de.codingair.tradesystem.ext.audit.TradeAudit;
import de.codingair.tradesystem.ext.audit.metrics.MetricsService;
import de.codingair.tradesystem.spigot.events.TradeFinishEvent;
import de.codingair.tradesystem.spigot.trade.TradeResult;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.impl.economy.EconomyIcon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class MetricsListener implements Listener {

    @EventHandler
    public void onTradeFinish(TradeFinishEvent e) {
        if (!e.getTradeResult()) return;

        log(e.getSenderId(), e.getReceiverId(), e.getSendingPlayerResult());
        log(e.getReceiverId(), e.getSenderId(), e.getReceivingPlayerResult());
    }

    private void log(@NotNull UUID sender, @NotNull UUID receiver, @NotNull TradeResult result) {
        MetricsService service = TradeAudit.getInstance().getMetricsService();

        for (ItemStack item : result.getSendingItems()) {
            service.log(sender, receiver, item);
        }

        for (EconomyIcon<?> icon : result.getEconomyIcons()) {
            service.log(sender, receiver, icon);
        }
    }

}
