package de.codingair.tradesystem.ext.audit.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.codingair.codingapi.server.specification.Version;
import de.codingair.tradesystem.ext.audit.events.TradeAuditItemLogEvent;
import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class SpecificationUtils {

    @Nullable
    public static JsonObject getItemSpecification(@NotNull UUID sender, @NotNull UUID receiver, @NotNull ItemStack item) {
        TradeAuditItemLogEvent event = new TradeAuditItemLogEvent(sender, receiver, item);
        Bukkit.getPluginManager().callEvent(event);

        JsonObject specification = event.getSpecification();

        if (item.hasItemMeta() && item.getItemMeta() != null) {
            ItemMeta meta = item.getItemMeta();

            addCustomModelData(specification, meta);
            addDisplayName(specification, meta);
            addLore(specification, meta);
            addEnchantments(specification, meta);
        }

        if (specification.isEmpty()) return null;
        return specification;
    }

    private static void addCustomModelData(@NotNull JsonObject specification, @NotNull ItemMeta meta) {
        if (Version.atLeast(14)) {
            if (meta.hasCustomModelData()) {
                specification.addProperty("model", meta.getCustomModelData());
            }
        }
    }

    private static void addDisplayName(@NotNull JsonObject specification, @NotNull ItemMeta meta) {
        if (meta.hasDisplayName()) {
            specification.addProperty("name", meta.getDisplayName());
        }
    }

    private static void addLore(@NotNull JsonObject specification, @NotNull ItemMeta meta) {
        if (meta.hasLore() && meta.getLore() != null) {
            JsonArray array = new JsonArray();
            meta.getLore().forEach(array::add);

            specification.add("lore", array);
        }
    }

    private static void addEnchantments(@NotNull JsonObject specification, @NotNull ItemMeta meta) {
        if (meta.hasEnchants()) {
            JsonObject enchants = new JsonObject();
            meta.getEnchants().forEach((enchantment, level) -> enchants.addProperty(getEnchantmentName(enchantment), level));

            specification.add("enchantments", enchants);
        }
    }

    @NotNull
    private static String getEnchantmentName(@NotNull Enchantment enchantment) {
        if (Version.atLeast(13)) {
            return enchantment.getKey().getKey();
        } else {
            //noinspection deprecation
            return enchantment.getName();
        }
    }

}
