package de.codingair.tradesystem.ext.auditing;

import de.codingair.codingapi.API;
import de.codingair.codingapi.files.FileManager;
import de.codingair.tradesystem.ext.auditing.utils.Lang;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class TradeAuditing extends JavaPlugin {
    private static TradeAuditing instance;
    private final FileManager fileManager = new FileManager(this);

    @EventHandler
    public void onEnable() {
        instance = this;
        API.getInstance().onEnable(this);

        loadConfigFiles();
    }

    @EventHandler
    public void onDisable() {
        API.getInstance().onDisable(this);
    }

    private void loadConfigFiles() {
        this.fileManager.loadFile("Config", "/");

        Lang.initPreDefinedLanguages(this);
        Lang.checkLanguageKeys(this, fileManager);
    }

    public static TradeAuditing getInstance() {
        return instance;
    }

    public FileManager getFileManager() {
        return fileManager;
    }
}
