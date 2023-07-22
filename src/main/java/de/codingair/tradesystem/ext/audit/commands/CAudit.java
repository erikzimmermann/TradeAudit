package de.codingair.tradesystem.ext.audit.commands;

import de.codingair.codingapi.player.chat.SimpleMessage;
import de.codingair.codingapi.player.gui.inventory.v2.exceptions.AlreadyOpenedException;
import de.codingair.codingapi.player.gui.inventory.v2.exceptions.IsWaitingException;
import de.codingair.codingapi.player.gui.inventory.v2.exceptions.NoPageException;
import de.codingair.codingapi.server.commands.builder.BaseComponent;
import de.codingair.codingapi.server.commands.builder.CommandBuilder;
import de.codingair.codingapi.server.commands.builder.CommandComponent;
import de.codingair.codingapi.server.commands.builder.special.MultiCommandComponent;
import de.codingair.tradesystem.ext.audit.TradeAudit;
import de.codingair.tradesystem.ext.audit.guis.AuditGUI;
import de.codingair.tradesystem.ext.audit.utils.Messages;
import de.codingair.tradesystem.spigot.TradeSystem;
import de.codingair.tradesystem.spigot.trade.Trade;
import de.codingair.tradesystem.spigot.trade.gui.layout.utils.Perspective;
import de.codingair.tradesystem.spigot.utils.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class CAudit extends CommandBuilder {
    public CAudit() {
        super(TradeAudit.getInstance(), "audit", new BaseComponent() {
            @Override
            public void noPermission(CommandSender sender, String s, CommandComponent commandComponent) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("No_Permissions"));
            }

            @Override
            public void onlyFor(boolean players, CommandSender sender, String label, CommandComponent commandComponent) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Only_for_Player"));
            }

            @Override
            public void unknownSubCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Command_Audit"));
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String[] args) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Command_Audit"));
                return true;
            }
        }.setOnlyPlayers(true), true);

        getBaseComponent().addChild(new CommandComponent("start") {
            @Override
            public boolean runCommand(CommandSender sender, String s, String[] strings) {
                sender.sendMessage(Lang.getPrefix() + Lang.get("Command_Audit_Start"));
                return true;
            }
        });

        getComponent("start").addChild(new MultiCommandComponent() {
            @Override
            public void addArguments(CommandSender commandSender, String[] strings, List<String> list) {
                for (String name : TradeSystem.handler().getTrades().keySet()) {
                    Player player = Bukkit.getPlayerExact(name);

                    if (player != null) list.add(player.getName());
                    else list.add(TradeSystem.proxy().getCaseSensitive(name));
                }
            }

            @Override
            public boolean runCommand(CommandSender sender, String label, String arg, String[] args) {
                Trade trade = TradeSystem.handler().getTrades().get(arg.toLowerCase());

                if (trade == null) {
                    sender.sendMessage(Lang.getPrefix() + Lang.get("Not_Trading"));
                    return true;
                }

                try {
                    AuditGUI gui = new AuditGUI(trade, (Player) sender);
                    gui.open();
                    TradeSystem.handler().playStartSound(gui.getPlayer());

                    TradeAudit.getInstance().getAudits().put(trade.getUniqueId(Perspective.PRIMARY), sender.getName(), gui);
                } catch (AlreadyOpenedException | NoPageException | IsWaitingException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
        });

        getBaseComponent().addChild(new CommandComponent("mute") {
            @Override
            public boolean runCommand(CommandSender sender, String s, String[] strings) {
                if (TradeAudit.getInstance().getMuted().add(((Player) sender).getUniqueId())) {
                    Messages.build(Lang.getPrefix() + Lang.get("Command_Audit_Mute")).send((Player) sender);
                } else {
                    Messages.build(Lang.getPrefix() + Lang.get("Command_Audit_Mute_Already")).send((Player) sender);
                }
                return true;
            }
        });

        getBaseComponent().addChild(new CommandComponent("unmute") {
            @Override
            public boolean runCommand(CommandSender sender, String s, String[] strings) {
                if (TradeAudit.getInstance().getMuted().remove(((Player) sender).getUniqueId())) {
                    Messages.build(Lang.getPrefix() + Lang.get("Command_Audit_Unmute")).send((Player) sender);
                } else {
                    Messages.build(Lang.getPrefix() + Lang.get("Command_Audit_Unmute_Already")).send((Player) sender);
                }
                return true;
            }
        });
    }
}
