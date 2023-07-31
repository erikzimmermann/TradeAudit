package de.codingair.tradesystem.ext.audit;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.codingair.codingapi.API;
import de.codingair.codingapi.files.FileManager;
import de.codingair.tradesystem.ext.audit.commands.CAudit;
import de.codingair.tradesystem.ext.audit.guis.AuditGUI;
import de.codingair.tradesystem.ext.audit.listeners.TradeCloseListener;
import de.codingair.tradesystem.ext.audit.listeners.TradeItemListener;
import de.codingair.tradesystem.ext.audit.listeners.TradeStartListener;
import de.codingair.tradesystem.spigot.TradeSystem;
import de.codingair.tradesystem.spigot.utils.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TradeAudit extends JavaPlugin {
    private static TradeAudit instance;
    private final FileManager fileManager = new FileManager(this);
    private final Table<UUID, String, AuditGUI> audits = HashBasedTable.create();
    private final Set<UUID> muted = new HashSet<>();

    @EventHandler
    public void onEnable() {
        if (checkTradeSystem()) return;

        instance = this;
        API.getInstance().onEnable(this);

        loadConfigFiles();
        loadCommands();
        loadListeners();
    }

    @EventHandler
    public void onDisable() {
        if (instance != null) API.getInstance().onDisable(this);
    }

    private boolean checkTradeSystem() {
        TradeSystem tradeSystem = (TradeSystem) Bukkit.getPluginManager().getPlugin("TradeSystem");
        if (tradeSystem == null) return true;

        boolean supported;
        try {
            String name = getDescription().getName().toLowerCase();
            supported = de.codingair.tradesystem.spigot.ext.Extensions.get().containsKey(name);
        } catch (Throwable t) {
            supported = false;
        }

        if (!supported) {
            getLogger().severe("The current version of TradeSystem "
                    + "(v" + tradeSystem.getDescription().getVersion() + ") does not support "
                    + getDescription().getName() + " yet! "
                    + "Please update TradeSystem to the latest version to use this extension.");
            getLogger().severe("At least required: TradeSystem v2.4.0");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        return !supported;
    }

    private void loadConfigFiles() {
        Lang.init(this, fileManager);
    }

    private void loadCommands() {
        new CAudit().register();
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new TradeItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new TradeStartListener(), this);
        Bukkit.getPluginManager().registerEvents(new TradeCloseListener(), this);
    }

    public static TradeAudit getInstance() {
        return instance;
    }

    public FileManager getFileManager() {
        return fileManager;
    }

    public Table<UUID, String, AuditGUI> getAudits() {
        return audits;
    }

    @Nullable
    public AuditGUI getAudit(@NotNull HumanEntity player) {
        return getAudits().column(player.getName()).values().stream().findAny().orElse(null);
    }

    public Set<UUID> getMuted() {
        return muted;
    }
}
