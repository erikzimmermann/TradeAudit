package de.codingair.tradesystem.ext.audit;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.codingair.codingapi.API;
import de.codingair.codingapi.files.FileManager;
import de.codingair.codingapi.utils.Value;
import de.codingair.tradesystem.ext.audit.commands.CAudit;
import de.codingair.tradesystem.ext.audit.external.PluginDependencies;
import de.codingair.tradesystem.ext.audit.guis.AuditGUI;
import de.codingair.tradesystem.ext.audit.listeners.TradeCloseListener;
import de.codingair.tradesystem.ext.audit.listeners.TradeItemListener;
import de.codingair.tradesystem.ext.audit.listeners.TradeStartListener;
import de.codingair.tradesystem.ext.audit.listeners.UpdateListener;
import de.codingair.tradesystem.ext.audit.metrics.MetricsListener;
import de.codingair.tradesystem.ext.audit.metrics.MetricsService;
import de.codingair.tradesystem.ext.audit.utils.GrafanaUtils;
import de.codingair.tradesystem.ext.audit.utils.Permissions;
import de.codingair.tradesystem.spigot.TradeSystem;
import de.codingair.tradesystem.spigot.utils.Lang;
import de.codingair.tradesystem.spigot.utils.updates.UpdateNotifier;
import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class TradeAudit extends JavaPlugin {
    private static TradeAudit instance;
    private final FileManager fileManager = new FileManager(this);
    private final Table<UUID, String, AuditGUI> audits = HashBasedTable.create();
    private final Set<UUID> muted = new HashSet<>();

    private MetricsService metricsService;

    private final UpdateNotifier updateNotifier = new UpdateNotifier(getDescription().getVersion(), "TradeAudit", 111665);
    private boolean needsUpdate = false;

    @EventHandler
    public void onEnable() {
        if (checkTradeSystem()) return;

        instance = this;
        API.getInstance().onEnable(this);

        startUpdateNotifier();

        loadFiles();
        loadServices();
        loadCommands();
        loadListeners();

        PluginDependencies.enable();
    }

    @EventHandler
    public void onDisable() {
        PluginDependencies.disable();
        if (instance != null) API.getInstance().onDisable(this);
    }

    private void loadFiles() {
        Lang.init(this, fileManager);
        try {
            GrafanaUtils.createFiles(this);
        } catch (IOException e) {
            getLogger().warning("Could not create grafana files: " + e.getMessage());
        }
    }

    private void loadServices() {
        metricsService = new MetricsService();
    }

    private void loadCommands() {
        new CAudit().register();
    }

    private void loadListeners() {
        Bukkit.getPluginManager().registerEvents(new TradeItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new TradeStartListener(), this);
        Bukkit.getPluginManager().registerEvents(new TradeCloseListener(), this);
        Bukkit.getPluginManager().registerEvents(new UpdateListener(), this);
        Bukkit.getPluginManager().registerEvents(new MetricsListener(), this);
    }

    private void startUpdateNotifier() {
        Value<BukkitTask> task = new Value<>(null);
        Runnable runnable = () -> {
            needsUpdate = updateNotifier.read();

            if (needsUpdate) {
                getLogger().info("-----< TradeAudit >-----");
                getLogger().info("New update available [" + updateNotifier.getUpdateInfo() + "].");
                getLogger().info("You are " + updateNotifier.getReleasesBehind() + " release(s) behind.");
                getLogger().info("Download it on\n\n" + updateNotifier.getDownloadLink() + "\n");
                getLogger().info("------------------------");

                task.getValue().cancel();

                notifyPlayers(null);
            }
        };

        task.setValue(Bukkit.getScheduler().runTaskTimerAsynchronously(getInstance(), runnable, 20L * 60 * 60, 20L * 60 * 60)); //check every hour on GitHub
    }

    public void notifyPlayers(@Nullable Player player) {
        if (player == null) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                notifyPlayers(p);
            }
        } else {
            if (needsUpdate && Permissions.NOTIFY.hasPermission(player)) {
                player.sendMessage("");
                player.sendMessage("");
                player.sendMessage(Lang.getPrefix() + "§7A §anew update §7for §aTradeAudit §7is available §8[§b" + updateNotifier.getUpdateInfo() + "§8]§7. You are §a" + updateNotifier.getReleasesBehind() + "§7 release(s) behind. Download it on §b§n" + this.updateNotifier.getDownloadLink());
                player.sendMessage("");
                player.sendMessage("");
            }
        }
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

    @NotNull
    public static TradeAudit getInstance() {
        return instance;
    }

    @NotNull
    public FileManager getFileManager() {
        return fileManager;
    }

    @NotNull
    public Table<UUID, String, AuditGUI> getAudits() {
        return audits;
    }

    @Nullable
    public AuditGUI getAudit(@NotNull HumanEntity player) {
        return getAudits().column(player.getName()).values().stream().findAny().orElse(null);
    }

    @NotNull
    public Set<UUID> getMuted() {
        return muted;
    }

    @NotNull
    public MetricsService getMetricsService() {
        return metricsService;
    }
}
