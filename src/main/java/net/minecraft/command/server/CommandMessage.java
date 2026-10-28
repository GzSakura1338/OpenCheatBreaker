package net.minecraft.command.server;

import java.util.Arrays;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class CommandMessage extends CommandBase {
    @Override
    public List getCommandAliases() {
        return Arrays.asList("w", "msg");
    }

    @Override
    public String getCommandName() {
        return "tell";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.message.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException("commands.message.usage");
        }

        EntityPlayerMP player = getPlayer(sender, args[0]);
        if (player == null) {
            throw new PlayerNotFoundException();
        }

        if (player == sender) {
            throw new PlayerNotFoundException("commands.message.sameTarget");
        }

        IChatComponent chatComponentFromNthArg = getChatComponentFromNthArg(sender, args, 1, !(sender instanceof EntityPlayer));
        ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(
            "commands.message.display.incoming", sender.getFormattedCommandSenderName(), chatComponentFromNthArg.createCopy()
        );
        ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation("commands.message.display.outgoing", player.getFormattedCommandSenderName(), chatComponentFromNthArg.createCopy());
        chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);
        chatComponentTranslation2.getChatStyle().setColor(EnumChatFormatting.GRAY).setItalic(true);
        player.addChatMessage(chatComponentTranslation);
        sender.addChatMessage(chatComponentTranslation2);
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}
