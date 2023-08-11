package de.codingair.tradesystem.ext.audit.external.mmoitems;

import de.codingair.tradesystem.ext.audit.events.TradeAuditItemLogEvent;
import de.codingair.tradesystem.spigot.extras.external.PluginDependency;
import net.Indyuce.mmoitems.MMOItems;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MMOItemsDependency implements PluginDependency, Listener {

    @EventHandler
    public void onReport(TradeAuditItemLogEvent e) {
        ItemStack item = e.getItemStack();

        String type = getMmoType(item);
        String id = getMmoId(item);

        if (type != null && id != null) {
            e.setPlugin(getPluginName());
            e.setId(id);
            e.setTypeName(type);
        }
    }

    @Nullable
    private String getMmoType(@NotNull ItemStack item) {
        return MMOItems.getTypeName(item);
    }

    @Nullable
    private String getMmoId(@NotNull ItemStack item) {
        return MMOItems.getID(item);
    }

    @Override
    public @NotNull String getPluginName() {
        return "MMOItems";
    }
}
