package de.codingair.tradesystem.ext.audit.guis.utils;

import de.codingair.tradesystem.spigot.trade.Trade;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.MultiTradeIcon;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.TradeIcon;
import de.codingair.tradesystem.spigot.trade.gui.layout.types.impl.basic.ShowStatusIcon;
import de.codingair.tradesystem.spigot.trade.gui.layout.utils.Perspective;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ShowPrimaryStatusIcon extends MultiTradeIcon {
    private final ShowStatusIcon icon;

    public ShowPrimaryStatusIcon(@NotNull ShowStatusIcon icon) {
        super(icon.getIcons());
        this.icon = icon;
    }

    @Override
    public @NotNull TradeIcon currentTradeIcon(@NotNull Trade trade, @NotNull Perspective perspective, @NotNull Player viewer) {
        return icon.currentTradeIcon(trade, perspective.flip(), viewer);
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public void serialize(@NotNull DataOutputStream dataOutputStream) throws IOException {
    }

    @Override
    public void deserialize(@NotNull DataInputStream dataInputStream) throws IOException {
    }
}
