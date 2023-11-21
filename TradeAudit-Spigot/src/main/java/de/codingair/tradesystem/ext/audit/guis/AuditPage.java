package de.codingair.tradesystem.ext.audit.guis;

import de.codingair.codingapi.player.gui.inventory.v2.GUI;
import de.codingair.codingapi.player.gui.inventory.v2.Page;
import de.codingair.tradesystem.ext.audit.guis.utils.ShowPrimaryStatusIcon;
import de.codingair.tradesystem.spigot.TradeSystem;
import de.codingair.tradesystem.spigot.trade.Trade;
import de.codingair.tradesystem.spigot.trade.gui.layout.Pattern;
import de.codingair.tradesystem.spigot.trade.gui.layout.TradeLayout;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.TradeIcon;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.Transition;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.impl.basic.ShowStatusIcon;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.impl.basic.StatusIcon;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.impl.basic.TradeSlot;
import de.codingair.tradesystem.spigot.trade.gui.layout.utils.Perspective;
import org.jetbrains.annotations.NotNull;

public class AuditPage extends Page {
    private final Trade trade;
    private final TradeLayout layout;

    public AuditPage(GUI gui, Trade trade) {
        super(gui);
        this.trade = trade;

        Pattern pattern = TradeSystem.getInstance().getLayoutManager().getActive();
        layout = pattern.build();
        TradeIcon[] icons = layout.getIcons();

        TradeLayout previewLayout = pattern.build();

        // prepare icons
        for (int slot = 0; slot < icons.length; slot++) {
            TradeIcon icon = icons[slot];
            if (icon == null) continue;
            if (icon instanceof TradeSlot) icons[slot] = null;
            else if (icon instanceof Transition) {
                icons[slot] = previewLayout.getIcon(((Transition<?, ?>) icon).getTargetClass());
            } else if (icon instanceof StatusIcon) {
                icons[slot] = new ShowPrimaryStatusIcon(previewLayout.getIcon(ShowStatusIcon.class));
            }
        }
    }

    @Override
    public void buildItems() {
        // apply icons
        for (int slot = 0; slot < layout.getIcons().length; slot++) {
            TradeIcon icon = layout.getIcons()[slot];
            if (icon == null) continue;

            addButton(slot, icon.getButton(trade, Perspective.PRIMARY, gui.getPlayer()));
        }
    }

    @NotNull
    public TradeLayout getLayout() {
        return layout;
    }
}
