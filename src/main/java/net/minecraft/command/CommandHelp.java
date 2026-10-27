package net.minecraft.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;

public class CommandHelp
extends CommandBase {
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
    public List<String> getCommandAliases() {
        return Arrays.asList("?");
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        List<ICommand> list = this.getSortedPossibleCommands(sender);
        int n = 7;
        int n2 = (list.size() - 1) / 7;
        int n3 = 0;
        try {
            n3 = args.length == 0 ? 0 : CommandHelp.parseInt(args[0], 1, n2 + 1) - 1;
        }
        catch (NumberInvalidException numberInvalidException) {
            Map<String, ICommand> map = this.getCommands();
            ICommand iCommand = map.get(args[0]);
            if (iCommand != null) {
                throw new WrongUsageException(iCommand.getCommandUsage(sender), new Object[0]);
            }
            if (MathHelper.parseIntWithDefault(args[0], -1) != -1) {
                throw numberInvalidException;
            }
            throw new CommandNotFoundException();
        }
        int n4 = Math.min((n3 + 1) * 7, list.size());
        ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.help.header", n3 + 1, n2 + 1);
        chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
        sender.addChatMessage(chatComponentTranslation);
        for (int i = n3 * 7; i < n4; ++i) {
            ICommand iCommand = list.get(i);
            ChatComponentTranslation chatComponentTranslation2 = new ChatComponentTranslation(iCommand.getCommandUsage(sender), new Object[0]);
            chatComponentTranslation2.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/" + iCommand.getCommandName() + " "));
            sender.addChatMessage(chatComponentTranslation2);
        }
        if (n3 == 0 && sender instanceof EntityPlayer) {
            ChatComponentTranslation chatComponentTranslation3 = new ChatComponentTranslation("commands.help.footer", new Object[0]);
            chatComponentTranslation3.getChatStyle().setColor(EnumChatFormatting.GREEN);
            sender.addChatMessage(chatComponentTranslation3);
        }
    }

    public List<ICommand> getSortedPossibleCommands(ICommandSender iCommandSender) {
        List<ICommand> list = MinecraftServer.getServer().getCommandManager().getPossibleCommands(iCommandSender);
        Collections.sort(list);
        return list;
    }

    public Map<String, ICommand> getCommands() {
        return MinecraftServer.getServer().getCommandManager().getCommands();
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            Set<String> set = this.getCommands().keySet();
            return CommandHelp.getListOfStringsMatchingLastWord(args, set.toArray(new String[set.size()]));
        }
        return null;
    }
}
