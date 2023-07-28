package de.codingair.tradesystem.ext.audit.guis;

import de.codingair.codingapi.player.gui.inventory.v2.GUI;
import de.codingair.codingapi.player.gui.inventory.v2.Page;
import de.codingair.codingapi.player.gui.inventory.v2.exceptions.AlreadyOpenedException;
import de.codingair.codingapi.player.gui.inventory.v2.exceptions.IsNotWaitingException;
import de.codingair.codingapi.player.gui.inventory.v2.exceptions.IsWaitingException;
import de.codingair.codingapi.player.gui.inventory.v2.exceptions.NoPageException;
import de.codingair.tradesystem.ext.audit.TradeAudit;
import de.codingair.tradesystem.spigot.TradeSystem;
import de.codingair.tradesystem.spigot.trade.Trade;
import de.codingair.tradesystem.spigot.trade.gui.layout.TradeLayout;
import de.codingair.tradesystem.spigot.trade.gui.layout.registration.IconHandler;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.TradeIcon;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.Transition;
import de.codingair.tradesystem.spigot.trade.gui.layout.utils.Perspective;
import de.codingair.tradesystem.spigot.trade.subscribe.PlayerSubscriber;
import de.codingair.tradesystem.spigot.utils.Lang;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class AuditGUI extends GUI implements PlayerSubscriber {
    private final Trade trade;

    public AuditGUI(@NotNull Trade trade, @NotNull Player player) {
        super(player, TradeAudit.getInstance(), false);
        this.trade = trade;

        buildInventory(this.trade.getGUIs()[0].getSize(), buildTitle());

        Page bg = new AuditPage(this, trade);
        registerPage(bg, true);

        this.trade.subscribe(this);
    }

    @NotNull
    private String buildTitle() {
        int remaining = (int) Math.ceil((TradeSystem.handler().getCountdownInterval() * (TradeSystem.handler().getCountdownRepetitions() - trade.getCountdownTicks())) / 20F);

        return Lang.get("Audit_GUI_Title", player,
                new Lang.P("player1", trade.getNames()[0]),
                new Lang.P("player2", trade.getNames()[1]),
                new Lang.P("countdown", trade.getCountdown() != null ? Lang.get("Fancy_Countdown").replace("%seconds%", String.valueOf(remaining)) : "")
        );
    }

    @Override
    public void run() {
        getActive().setTitle(buildTitle()); // update title for deployment
        updateItems();                      // update items
        applyTradeIconChanges();            // update icon data
        updateTradeIcons();                 // deploy everything
    }

    private void updateTradeIcons() {
        getActive().rebuild();
    }

    private void updateItems() {
        for (int i = 0; i < trade.getSlots().size(); i++) {
            int slot = trade.getSlots().get(i);
            int otherSlot = trade.getOtherSlots().get(i);

            setItem(slot, trade.getCurrentOfferedItem(Perspective.PRIMARY, i));
            setItem(otherSlot, trade.getCurrentOfferedItem(Perspective.SECONDARY, i));
        }
    }

    private void applyTradeIconChanges() {
        for (Perspective perspective : Perspective.main()) {
            TradeLayout layout = trade.getLayout()[perspective.id()];
            TradeIcon[] icons = layout.getIcons();
            for (int slot = 0; slot < icons.length; slot++) {
                TradeIcon icon = icons[slot];
                AuditPage page = (AuditPage) getActive();

                if (icon instanceof Transition) {
                    int viewerSlot;
                    if (perspective.isPrimary()) {
                        viewerSlot = slot;
                    } else {
                        // find slot for secondary perspective
                        Transition<?, ?> t = (Transition<?, ?>) icon;
                        TradeIcon c = layout.getIcon(t.getTargetClass());
                        viewerSlot = layout.getSlotOf(c);
                    }

                    TradeIcon preview = page.getLayout().getIcons()[viewerSlot];

                    if (preview instanceof Transition.Consumer) {
                        informTransition(icon, preview);
                    }
                }
            }
        }
    }

    private void informTransition(@NotNull TradeIcon icon, @NotNull TradeIcon consumer) {
        try {
            Method method = IconHandler.findInform(icon.getClass(), icon.getClass());

            method.invoke(icon, consumer);
        } catch (ClassCastException | InvocationTargetException | IllegalAccessException | NoSuchMethodException ex) {
            throw new IllegalStateException("Cannot execute method inform(TradeIcon) of " + icon.getClass().getName(), ex);
        }
    }

    @Override
    public void open() throws AlreadyOpenedException, NoPageException, IsWaitingException {
        // invoke update method before opening
        run();
        super.open();
    }

    @Override
    protected void continueGUI() throws IsNotWaitingException {
        if (trade.isCancelling()) return;
        super.continueGUI();
    }

    @Override
    public void destroy() {
        super.destroy();
        TradeAudit.getInstance().getAudits().remove(trade.getUniqueId(Perspective.PRIMARY), player.getName());

        // unsubscribe later to allow closing the inventory from TradeSystem's side
        Bukkit.getScheduler().runTask(TradeAudit.getInstance(), () -> this.trade.unsubscribe(this));
    }

    @NotNull
    public Trade getTrade() {
        return trade;
    }

    @Override
    public @NotNull Player getPlayer() {
        return super.getPlayer();
    }
}
