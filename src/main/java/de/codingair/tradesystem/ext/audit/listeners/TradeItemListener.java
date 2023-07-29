package de.codingair.tradesystem.ext.audit.listeners;

import de.codingair.codingapi.player.gui.inventory.v2.exceptions.AlreadyOpenedException;
import de.codingair.codingapi.player.gui.inventory.v2.exceptions.IsWaitingException;
import de.codingair.codingapi.player.gui.inventory.v2.exceptions.NoPageException;
import de.codingair.codingapi.server.specification.Version;
import de.codingair.tradesystem.ext.audit.TradeAudit;
import de.codingair.tradesystem.ext.audit.guis.AuditGUI;
import de.codingair.tradesystem.spigot.TradeSystem;
import de.codingair.tradesystem.spigot.trade.Trade;
import de.codingair.tradesystem.spigot.trade.gui.Actions;
import de.codingair.tradesystem.spigot.trade.gui.InventoryMask;
import de.codingair.tradesystem.spigot.trade.gui.layout.shulker.ShulkerPeekGUI;
import de.codingair.tradesystem.spigot.trade.gui.layout.utils.Perspective;
import de.codingair.tradesystem.spigot.utils.Lang;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class TradeItemListener implements Listener {

    @NotNull
    private Actions.Configuration getConfiguration(@NotNull AuditGUI gui) {
        Player player = gui.getPlayer();
        Actions.Configuration configuration = Actions.Configuration.DEFAULT();
        Trade trade = gui.getTrade();

        // performance improvement with HashSet
        Set<Integer> slots = new HashSet<>(trade.getSlots());
        Set<Integer> otherSlots = new HashSet<>(trade.getOtherSlots());

        configuration.inventoryMapper = (event, slot) -> {
            if (otherSlots.contains(slot)) {
                if (trade.getGUIs()[1] != null) {
                    // bukkit trade
                    return InventoryMask.of(trade.getGUIs()[1].getInventory());
                }

                return new InventoryMask() {
                    @Override
                    public void setItem(int slot, @Nullable ItemStack item) {
                        // slot was already mapped to primary perspective
                        int slotId = trade.getSlots().indexOf(slot);
                        if (slotId == -1)
                            throw new IllegalStateException("Slot " + slot + " is not part of the trade.");
                        trade.updateDisplayItem(Perspective.PRIMARY, slotId, item);
                    }

                    @Override
                    public @Nullable ItemStack getItem(int slot) {
                        // slot was already mapped to primary perspective
                        int slotId = trade.getSlots().indexOf(slot);
                        if (slotId == -1)
                            throw new IllegalStateException("Slot " + slot + " is not part of the trade.");
                        return trade.getCurrentOfferedItem(Perspective.SECONDARY, slotId);
                    }

                    @Override
                    public @NotNull Object getHolder() {
                        return trade;
                    }
                };
            } else return InventoryMask.of(trade.getGUIs()[0].getInventory());
        };

        configuration.targetSlots = e -> {
            if (e instanceof InventoryClickEvent) {
                InventoryClickEvent event = (InventoryClickEvent) e;

                if (slots.contains(event.getRawSlot())) return trade.getSlots();
                if (otherSlots.contains(event.getRawSlot())) return trade.getOtherSlots();

                // player should only interact with bottom inventory (MOVE_TO_OTHER_INVENTORY should be cancelled)
                return Collections.emptyList();
            } else if (e instanceof InventoryDragEvent) {
                InventoryDragEvent event = (InventoryDragEvent) e;

                if (slots.containsAll(event.getNewItems().keySet())) return trade.getSlots();
                if (otherSlots.containsAll(event.getNewItems().keySet())) return trade.getOtherSlots();

                // player should only interact with bottom inventory
                return Collections.emptyList();
            } else
                throw new IllegalArgumentException("Unsupported event type: " + e.getClass().getName());
        };

        configuration.isItemAllowedInInventory = (items, targetSlots) -> {
            for (ItemStack item : items) {
                if (TradeSystem.getInstance().getTradeManager().isBlocked(trade, item)) {
                    player.sendMessage(Lang.getPrefix() + Lang.get("Trade_Placed_Blocked_Item", player));
                    TradeSystem.getInstance().getTradeManager().playBlockSound(player);
                    return false;
                }
            }

            Perspective perspective;
            // performance improvement with HashSet
            if (slots.containsAll(targetSlots)) perspective = Perspective.PRIMARY;
            else if (otherSlots.containsAll(targetSlots)) perspective = Perspective.SECONDARY;
            else perspective = Perspective.TERTIARY;

            if (perspective.isMain()) {
                if (!TradeSystem.getInstance().getTradeManager().isDropItems() && !trade.fitsTrade(perspective, items)) {
                    player.sendMessage(Lang.getPrefix() + Lang.get("Trade_Partner_No_Space", player));
                    TradeSystem.getInstance().getTradeManager().playBlockSound(player);
                    return false;
                }
            }

            return true;
        };
        configuration.collectFromBothInventories = false;
        configuration.slotMapper = slot -> {
            if (otherSlots.contains(slot)) {
                int idx = trade.getOtherSlots().indexOf(slot);
                return trade.getSlots().get(idx);
            }

            return slot;
        };

        return configuration;
    }

    //use higher priority than the GUI listener of TradeSystem
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onModify(InventoryClickEvent e) {
        AuditGUI gui = TradeAudit.getInstance().getAudit(e.getWhoClicked());
        if (gui == null) return;

        // allow blocking by other plugins
        if (e.isCancelled()) return;

        // cancel everything and project changes later
        e.setCancelled(true);

        if (checkForShulkerBoxes(e, gui, gui.getPlayer())) return;

        // project click event on trading GUI directly to allow modifications in another listener
        boolean offerChange = Actions.projectResult(e, getConfiguration(gui));
        handleResult(offerChange, gui.getTrade());
    }

    //use higher priority than the GUI listener of TradeSystem
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onModify(InventoryDragEvent e) {
        AuditGUI gui = TradeAudit.getInstance().getAudit(e.getWhoClicked());
        if (gui == null) return;

        // Cancelling the drag event resets the cursor in a later tick.
        // Therefore, simply remove all new items added during this event.
        e.setCancelled(false);

        // project click event on trading GUI directly to allow modifications in another listener
        boolean offerChange = Actions.projectResult(e, getConfiguration(gui));
        handleResult(offerChange, gui.getTrade());
    }

    private void handleResult(boolean offerChange, @NotNull Trade trade) {
        // balance items from other player before updating
        boolean cannotDropItems = !TradeSystem.getInstance().getTradeManager().isDropItems();
        if (cannotDropItems) {
            for (Perspective perspective : Perspective.main()) {
                trade.cancelItemOverflow(perspective);
            }
        }

        // update trade
        if (offerChange) {
            trade.onTradeOfferChange(false);
            trade.updateLater();
        }
    }

    private boolean checkForShulkerBoxes(@NotNull InventoryClickEvent event, @NotNull AuditGUI gui, @NotNull Player player) {
        boolean topInventory = event.getView().getTopInventory().equals(event.getClickedInventory());
        if (topInventory) {

            Perspective owner;
            if (gui.getTrade().getSlots().contains(event.getSlot())) owner = Perspective.PRIMARY;
            else if (gui.getTrade().getOtherSlots().contains(event.getSlot())) owner = Perspective.SECONDARY;
            else return false;

            boolean tradeSlots = gui.getTrade().getSlots().contains(event.getSlot()) || gui.getTrade().getOtherSlots().contains(event.getSlot());
            if (tradeSlots) {
                // shulker peeking
                if (event.getClick() == ClickType.RIGHT && Version.atLeast(11) && ShulkerPeekGUI.isShulkerBox(event.getCurrentItem())) {
                    openShulkerPeekingGUI(gui, event.getCurrentItem(), player, owner);
                    return true;
                }
            }
        }

        return false;
    }

    private void openShulkerPeekingGUI(@NotNull AuditGUI gui, @NotNull ItemStack item, @NotNull Player player, @NotNull Perspective owner) {
        try {
            gui.openNestedGUI(new ShulkerPeekGUI(player, item, owner), true, true);
        } catch (AlreadyOpenedException | NoPageException | IsWaitingException ex) {
            throw new RuntimeException(ex);
        }
    }
}
