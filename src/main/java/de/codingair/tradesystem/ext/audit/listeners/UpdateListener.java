package de.codingair.tradesystem.ext.audit.listeners;

import de.codingair.tradesystem.ext.audit.TradeAudit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdateListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        TradeAudit.getInstance().notifyPlayers(p);
    }

}