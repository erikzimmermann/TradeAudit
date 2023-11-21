package de.codingair.tradesystem.ext.audit.utils;

import de.codingair.tradesystem.spigot.utils.FileUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class GrafanaUtils {
    private static final String INTERNAL_PATH = "grafana/";
    private static final String EXTERNAL_PATH = "Grafana/";
    private static final String VERSION = "TradeSystem-1693561823349.json";

    public static void createFiles(@NotNull JavaPlugin plugin) throws IOException {
        FileUtils.createFile(plugin, INTERNAL_PATH, EXTERNAL_PATH, "README.md", true);
        FileUtils.createFile(plugin, INTERNAL_PATH, EXTERNAL_PATH, VERSION, false);
    }

}
