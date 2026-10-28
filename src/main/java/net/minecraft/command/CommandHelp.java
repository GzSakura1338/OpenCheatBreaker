package net.minecraft.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class CommandHelp extends CommandBase {
    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.help.usage";
    }

    @Override
    public List getCommandAliases() {
        return Arrays.asList("?");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        List sortedPossibleCommands = this.getSortedPossibleCommands(sender);
        byte b = 7;
        int n = (sortedPossibleCommands.size() - 1) / b;
        boolean flag = false;

        int n2;
        try {
            n2 = args.length == 0 ? 0 : parseIntBounded(sender, args[0], 1, n + 1) - 1;
        } catch (NumberInvalidException e) {
            Map commands = this.getCommands();
            ICommand command = (ICommand)commands.get(args[0]);
            if (command != null) {
                throw new WrongUsageException(command.getCommandUsage(sender));
            }

            if (MathHelper.parseIntWithDefault(args[0], -1) != -1) {
                throw e;
            }

            throw new CommandNotFoundException();
        }

        int min = Math.min((n2 + 1) * b, sortedPossibleCommands.size());
        ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.help.header", n2 + 1, n + 1);
        chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
        sender.addChatMessage(chatComponentTranslation);

        for (int i = n2 * b; i < min; i++) {
            ICommand command = (ICommand)sortedPossibleCommands.get(i);
            ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation(command.getCommandUsage(sender));
            chatComponentTranslation2.getChatStyle().setChatClickEvent(new ClickEvent(Action.SUGGEST_COMMAND, "/" + command.getCommandName() + " "));
            sender.addChatMessage(chatComponentTranslation2);
        }

        if (n2 == 0 && sender instanceof EntityPlayer) {
            ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation("commands.help.footer");
            chatComponentTranslation2.getChatStyle().setColor(EnumChatFormatting.GREEN);
            sender.addChatMessage(chatComponentTranslation2);
        }
    }

    public List getSortedPossibleCommands(ICommandSender commandSender) {
        List possibleCommands = MinecraftServer.getServer().getCommandManager().getPossibleCommands(commandSender);
        Collections.sort(possibleCommands);
        return possibleCommands;
    }

    public Map getCommands() {
        return MinecraftServer.getServer().getCommandManager().getCommands();
    }
}
