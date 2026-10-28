package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkStatistics.PacketStat;
import net.minecraft.util.ChatComponentText;

public class CommandNetstat extends CommandBase {
    @Override
    public String getCommandName() {
        return "netstat";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.players.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (sender instanceof EntityPlayer) {
            sender.addChatMessage(new ChatComponentText("Command is not available for players"));
        } else if (args.length <= 0 || args[0].length() <= 1) {
            String str = "reads: " + NetworkManager.STATISTICS.getLongValue();
            str = str + ", writes: " + NetworkManager.STATISTICS.getLongValue2();
            sender.addChatMessage(new ChatComponentText(str));
        } else if ("hottest-read".equals(args[0])) {
            sender.addChatMessage(new ChatComponentText(NetworkManager.STATISTICS.getPacketStat().toString()));
        } else if ("hottest-write".equals(args[0])) {
            sender.addChatMessage(new ChatComponentText(NetworkManager.STATISTICS.getPacketStat3().toString()));
        } else if ("most-read".equals(args[0])) {
            sender.addChatMessage(new ChatComponentText(NetworkManager.STATISTICS.getPacketStat2().toString()));
        } else if ("most-write".equals(args[0])) {
            sender.addChatMessage(new ChatComponentText(NetworkManager.STATISTICS.getPacketStat4().toString()));
        } else if ("packet-read".equals(args[0])) {
            if (args.length > 1 && args[1].length() > 0) {
                try {
                    int parseInt = Integer.parseInt(args[1].trim());
                    PacketStat findPacketStat = NetworkManager.STATISTICS.findPacketStat(parseInt);
                    this.handleICommandSender(sender, parseInt, findPacketStat);
                } catch (Exception e) {
                    sender.addChatMessage(new ChatComponentText("Packet " + args[1] + " not found!"));
                }
            } else {
                sender.addChatMessage(new ChatComponentText("Packet id is missing"));
            }
        } else if ("packet-write".equals(args[0])) {
            if (args.length > 1 && args[1].length() > 0) {
                try {
                    int parseInt = Integer.parseInt(args[1].trim());
                    PacketStat findPacketStat2 = NetworkManager.STATISTICS.findPacketStat2(parseInt);
                    this.handleICommandSender(sender, parseInt, findPacketStat2);
                } catch (Exception e) {
                    sender.addChatMessage(new ChatComponentText("Packet " + args[1] + " not found!"));
                }
            } else {
                sender.addChatMessage(new ChatComponentText("Packet id is missing"));
            }
        } else if ("read-count".equals(args[0])) {
            sender.addChatMessage(new ChatComponentText("total-read-count" + NetworkManager.STATISTICS.getLongValue3()));
        } else if ("write-count".equals(args[0])) {
            sender.addChatMessage(new ChatComponentText("total-write-count" + NetworkManager.STATISTICS.getLongValue4()));
        } else {
            sender.addChatMessage(new ChatComponentText("Unrecognized: " + args[0]));
        }
    }

    public void handleICommandSender(ICommandSender commandSender, int n, PacketStat packetStat) {
        if (packetStat != null) {
            commandSender.addChatMessage(new ChatComponentText(packetStat.toString()));
        } else {
            commandSender.addChatMessage(new ChatComponentText("Packet " + n + " not found!"));
        }
    }
}
