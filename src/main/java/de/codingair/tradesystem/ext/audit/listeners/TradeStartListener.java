package de.codingair.tradesystem.ext.audit.listeners;

import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.tradesystem.ext.audit.TradeAudit;
import de.codingair.tradesystem.ext.audit.utils.Messages;
import de.codingair.tradesystem.ext.audit.utils.Permissions;
import de.codingair.tradesystem.spigot.events.TradeRequestResponseEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TradeStartListener implements Listener {

    @EventHandler
    public void onStart(TradeRequestResponseEvent e) {
        if (e.isAccepted()) {
            SimpleMessage message = Messages.getNotification(e.getSender(), e.getReceiver());

            // send notification
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().equals(e.getSender()) || player.getName().equals(e.getReceiver())) continue;

                if (Permissions.NOTIFY.hasPermission(player)
                        && !TradeAudit.getInstance().getMuted().contains(player.getUniqueId())) {
                    message.send(player);
                }
            }
        }
    }

}
