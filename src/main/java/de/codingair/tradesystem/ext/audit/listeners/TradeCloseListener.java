package de.codingair.tradesystem.ext.audit.listeners;

import de.codingair.codingapi.player.gui.inventory.v2.exceptions.AlreadyClosedException;
import de.codingair.tradesystem.ext.audit.TradeAudit;
import de.codingair.tradesystem.ext.audit.guis.AuditGUI;
import de.codingair.tradesystem.spigot.events.TradeFinishEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class TradeCloseListener implements Listener {

    @EventHandler
    public void onFinish(TradeFinishEvent e) {
        for (AuditGUI auditGUI : TradeAudit.getInstance().getAudits().row(e.getSenderId()).values()) {
            try {
                auditGUI.close();
            } catch (AlreadyClosedException ignored) {
            }
        }
    }

}
