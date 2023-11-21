package de.codingair.tradesystem.ext.audit.metrics;

import de.codingair.tradesystem.ext.audit.TradeAudit;
import de.codingair.tradesystem.spigot.events.TradeFinishEvent;
import de.codingair.tradesystem.spigot.trade.TradeResult;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.impl.economy.EconomyIcon;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MetricsListener implements Listener {

    @EventHandler
    public void onTradeFinish(TradeFinishEvent e) {
        if (!e.getTradeResult()) return;

        // avoid duplicated logging by checking if the player is null
        // if the player is null, the player is on another server
        if (e.getSendingPlayer() != null) log(e.getSendingPlayerResult(), e.getReceivingPlayerResult());
        if (e.getReceivingPlayer() != null) log(e.getReceivingPlayerResult(), e.getSendingPlayerResult());
    }

    private void log(@NotNull TradeResult senderResult, @NotNull TradeResult receiverResult) {
        MetricsService service = TradeAudit.getInstance().getMetricsService();

        for (ItemStack item : senderResult.getSendingItems()) {
            service.log(
                    senderResult.getPlayerId(), senderResult.getPlayerServer(), senderResult.getPlayerWorld(),
                    receiverResult.getPlayerId(), receiverResult.getPlayerServer(), receiverResult.getPlayerWorld(),
                    item
            );
        }

        for (EconomyIcon<?> icon : senderResult.getEconomyIcons()) {
            service.log(
                    senderResult.getPlayerId(), senderResult.getPlayerServer(), senderResult.getPlayerWorld(),
                    receiverResult.getPlayerId(), receiverResult.getPlayerServer(), receiverResult.getPlayerWorld(),
                    icon
            );
        }
    }

}
