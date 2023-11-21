package de.codingair.tradesystem.ext.audit.utils;

import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.tradesystem.ext.audit.TradeAudit;
import de.codingair.tradesystem.spigot.utils.Lang;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Messages {

    @NotNull
    public static SimpleMessage getNotification(@NotNull String sender, @NotNull String receiver) {
        SimpleMessage message = new SimpleMessage(Lang.getPrefix() + Lang.get("Audit_Notification",
                new Lang.P("player1", sender),
                new Lang.P("player2", receiver)
        ), TradeAudit.getInstance());

        Player online = Bukkit.getPlayerExact(sender);
        if (online == null) online = Bukkit.getPlayerExact(receiver);
        if (online == null) throw new NullPointerException("Cannot create notification message for offline player!");

        TextComponent tc = new TextComponent(Lang.get("Click_Audit"));
        //noinspection deprecation
        tc.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(Lang.get("Want_To_Trade_Hover"))}));
        tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/audit start " + online.getName()));
        message.replace("%audit%", tc);

        return prepare(message);
    }

    @NotNull
    public static SimpleMessage build(@NotNull String text) {
        return prepare(new SimpleMessage(text, TradeAudit.getInstance()));
    }

    @NotNull
    public static SimpleMessage prepare(@NotNull SimpleMessage message) {
        TextComponent mute = new TextComponent(Lang.get("Click_Mute"));
        //noinspection deprecation
        mute.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(Lang.get("Want_To_Trade_Hover"))}));
        mute.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/audit mute"));

        TextComponent unmute = new TextComponent(Lang.get("Click_Unmute"));
        //noinspection deprecation
        unmute.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent(Lang.get("Want_To_Trade_Hover"))}));
        unmute.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/audit unmute"));

        message.replace("%mute%", mute);
        message.replace("%unmute%", unmute);

        return message;
    }

}
