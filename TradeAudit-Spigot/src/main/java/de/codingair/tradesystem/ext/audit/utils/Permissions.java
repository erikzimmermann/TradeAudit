package de.codingair.tradesystem.ext.audit.utils;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum Permissions {
    /**
     * Notify admins when a new update is available.
     */
    NOTIFY("tradesystem.audit.updates.notify", true),
    /**
     * Notify admins when players start a trade.
     */
    TRADE_NOTIFY("tradesystem.audit.notify", true),
    /**
     * Enable admins to use the /audit command.
     */
    TRADE_AUDIT("tradesystem.audit.start", true),
    /**
     * Enable admins to use the /tradelog command.
     */
    STATS("tradesystem.audit.stats", true),
    ;

    private final String permission;
    private final boolean admin;

    Permissions(@NotNull String permission, boolean admin) {
        this.permission = permission;
        this.admin = admin;
    }

    /**
     * @return The permission node if permissions are enable in the config.
     */
    public @Nullable String getPermission() {
        if (admin || de.codingair.tradesystem.spigot.utils.Permissions.arePermissionsEnabled()) return permission;
        return null;
    }

    public boolean hasPermission(@NotNull Player player) {
        if (de.codingair.tradesystem.spigot.utils.Permissions.arePermissionsEnabled()) return player.hasPermission(permission);
        else return !admin || player.isOp();
    }
}
