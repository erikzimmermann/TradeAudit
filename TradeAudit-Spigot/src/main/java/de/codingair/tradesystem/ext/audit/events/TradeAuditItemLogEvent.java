package de.codingair.tradesystem.ext.audit.events;

import com.google.gson.JsonObject;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TradeAuditItemLogEvent extends TradeAuditEvent {
    private static final HandlerList handlerList = new HandlerList();
    private final UUID sender;
    private final UUID receiver;
    private final ItemStack item;
    private final JsonObject specification = new JsonObject();

    public TradeAuditItemLogEvent(@NotNull UUID sender, @NotNull UUID receiver, @NotNull ItemStack item) {
        this.sender = sender;
        this.receiver = receiver;
        this.item = item;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return getHandlerList();
    }

    /**
     * @return The {@link UUID} of the player who trades the item.
     */
    @NotNull
    public UUID getSender() {
        return sender;
    }

    /**
     * @return The {@link UUID} of the player who receives the item.
     */
    @NotNull
    public UUID getReceiver() {
        return receiver;
    }

    /**
     * @return The {@link ItemStack} being transferred.
     */
    @NotNull
    public ItemStack getItemStack() {
        return item;
    }

    /**
     * Gets access to the specification that should be saved to the database. Please use any pre-existing methods for modifying this before adding proprietary data.
     *
     * @return The item specification that will be saved into the database.
     */
    @NotNull
    public JsonObject getSpecification() {
        return specification;
    }

    /**
     * @param plugin The name of the plugin that is responsible for this item.
     */
    public void setPlugin(@NotNull String plugin) {
        specification.addProperty("plugin", plugin);
    }

    /**
     * @param typeName The type name of the item that is being traded. Initially used for MMOItems.
     */
    public void setTypeName(@NotNull String typeName) {
        specification.addProperty("type-name", typeName);
    }

    /**
     * @param id The id of the item that is being traded. Initially used for MMOItems.
     */
    public void setId(@NotNull String id) {
        specification.addProperty("id", id);
    }
}
