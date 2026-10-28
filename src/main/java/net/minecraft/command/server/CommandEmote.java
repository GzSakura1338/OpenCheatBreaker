package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IChatComponent;

public class CommandEmote extends CommandBase {
    @Override
    public String getCommandName() {
        return "me";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.me.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0) {
            IChatComponent chatComponentFromNthArg = getChatComponentFromNthArg(sender, args, 0, sender.canCommandSenderUseCommand(1, "me"));
            MinecraftServer.getServer()
                .getConfigurationManager()
                .sendChatMsg(new ChatComponentTranslation("chat.type.emote", sender.getFormattedCommandSenderName(), chatComponentFromNthArg));
        } else {
            throw new WrongUsageException("commands.me.usage");
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
    }
}
