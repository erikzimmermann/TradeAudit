package de.codingair.tradesystem.ext.audit.listener;

import de.codingair.tradesystem.spigot.events.TradeRequestResponseEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TradeStartListener implements Listener {

    @EventHandler
    public void onStart(TradeRequestResponseEvent e) {
        if (e.isAccepted()) {
            // send notification
        }
    }

}
