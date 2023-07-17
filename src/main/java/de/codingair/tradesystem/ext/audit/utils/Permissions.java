package de.codingair.tradesystem.ext.audit.utils;

import de.codingair.tradesystem.spigot.TradeSystem;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public enum Permissions {
    NOTIFY("TradeSystem.Auditing.Notify", true),
    ;

    private final String permission;
    private final boolean admin;

    Permissions(@NotNull String permission, boolean admin) {
        this.permission = permission;
        this.admin = admin;
    }

    public @NotNull String getPermission() {
        return permission;
    }

    public boolean hasPermission(@NotNull Player player) {
        if (TradeSystem.getInstance().arePermissionsEnabled()) return player.hasPermission(permission);
        else return !admin;
    }
}
