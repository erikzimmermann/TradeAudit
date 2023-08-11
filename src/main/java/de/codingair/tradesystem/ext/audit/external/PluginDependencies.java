package de.codingair.tradesystem.ext.audit.external;

import de.codingair.tradesystem.ext.audit.external.mmoitems.MMOItemsDependency;
import de.codingair.tradesystem.spigot.TradeSystem;
import de.codingair.tradesystem.spigot.extras.external.PluginDependency;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public abstract class PluginDependencies {
    private static final PluginDependency[] EXTERNALS = {
            new MMOItemsDependency()
    };

    public static boolean isEnabled(@NotNull Class<? extends PluginDependency> clazz) {
        for (PluginDependency dependency : EXTERNALS) {
            if (dependency.getClass().equals(clazz)) return dependency.isAvailable();
        }

        return false;
    }

    public static void enable() {
        for (PluginDependency external : EXTERNALS) {
            if (external.isAvailable()) {
                if (external instanceof Listener) Bukkit.getPluginManager().registerEvents((Listener) external, TradeSystem.getInstance());
                external.onEnable();
            }
        }
    }

    public static void disable() {
        for (PluginDependency external : EXTERNALS) {
            if (external.isAvailable()) {
                external.onDisable();
                if (external instanceof Listener) HandlerList.unregisterAll((Listener) external);
            }
        }
    }

    @NotNull
    public static <T extends PluginDependency> T get(@NotNull Class<T> c) {
        for (PluginDependency external : EXTERNALS) {
            if (c.isInstance(external)) {
                //noinspection unchecked
                return (T) external;
            }
        }

        throw new IllegalArgumentException("Unknown dependency: " + c.getName());
    }
}
