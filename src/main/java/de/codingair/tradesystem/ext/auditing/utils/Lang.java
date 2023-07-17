package de.codingair.tradesystem.ext.auditing.utils;

import de.codingair.tradesystem.ext.auditing.TradeAuditing;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Lang extends de.codingair.tradesystem.spigot.utils.Lang {

    public static @NotNull String get(@NotNull String key, @NotNull P... placeholders) {
        return get(key, null, placeholders);
    }

    public static @NotNull String get(@NotNull String key, @Nullable Player p, @NotNull P... placeholders) {
        try {
            return get(key, null, placeholders);
        } catch (NullPointerException ex) {
            return get(TradeAuditing.getInstance().getFileManager(), key, p, placeholders);
        }
    }

}
