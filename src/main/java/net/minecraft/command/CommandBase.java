package net.minecraft.command;

import com.google.common.primitives.Doubles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public abstract class CommandBase implements ICommand {
    public static IAdminCommand theAdmin;

    public int getRequiredPermissionLevel() {
        return 4;
    }

    @Override
    public List getCommandAliases() {
        return null;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return sender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return null;
    }

    public static int parseInt(ICommandSender sender, String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            throw new NumberInvalidException("commands.generic.num.invalid", str);
        }
    }

    public static int parseIntWithMin(ICommandSender sender, String str, int min) {
        return parseIntBounded(sender, str, min, Integer.MAX_VALUE);
    }

    public static int parseIntBounded(ICommandSender sender, String str, int min, int max) {
        int parseInt2 = parseInt(sender, str);
        if (parseInt2 < min) {
            throw new NumberInvalidException("commands.generic.num.tooSmall", parseInt2, min);
        } else if (parseInt2 > max) {
            throw new NumberInvalidException("commands.generic.num.tooBig", parseInt2, max);
        } else {
            return parseInt2;
        }
    }

    public static double parseDouble(ICommandSender sender, String str) {
        try {
            double parseDouble = Double.parseDouble(str);
            if (!Doubles.isFinite(parseDouble)) {
                throw new NumberInvalidException("commands.generic.num.invalid", str);
            } else {
                return parseDouble;
            }
        } catch (NumberFormatException e) {
            throw new NumberInvalidException("commands.generic.num.invalid", str);
        }
    }

    public static double parseDouble(ICommandSender sender, String str, double min) {
        return parseDouble(sender, str, min, Double.MAX_VALUE);
    }

    public static double parseDouble(ICommandSender sender, String str, double min, double max) {
        double parseDouble2 = parseDouble(sender, str);
        if (parseDouble2 < min) {
            throw new NumberInvalidException("commands.generic.double.tooSmall", parseDouble2, min);
        } else if (parseDouble2 > max) {
            throw new NumberInvalidException("commands.generic.double.tooBig", parseDouble2, max);
        } else {
            return parseDouble2;
        }
    }

    public static boolean parseBoolean(ICommandSender sender, String str) {
        if (str.equals("true") || str.equals("1")) {
            return true;
        } else if (!str.equals("false") && !str.equals("0")) {
            throw new CommandException("commands.generic.boolean.invalid", str);
        } else {
            return false;
        }
    }

    public static EntityPlayerMP getCommandSenderAsPlayer(ICommandSender sender) {
        if (sender instanceof EntityPlayerMP) {
            return (EntityPlayerMP)sender;
        } else {
            throw new PlayerNotFoundException("You must specify which player you wish to perform this action on.");
        }
    }

    public static EntityPlayerMP getPlayer(ICommandSender sender, String username) {
        EntityPlayerMP matchOnePlayer = PlayerSelector.matchOnePlayer(sender, username);
        if (matchOnePlayer != null) {
            return matchOnePlayer;
        } else {
            matchOnePlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(username);
            if (matchOnePlayer == null) {
                throw new PlayerNotFoundException();
            } else {
                return matchOnePlayer;
            }
        }
    }

    public static String getPlayerName(ICommandSender sender, String query) {
        EntityPlayerMP matchOnePlayer = PlayerSelector.matchOnePlayer(sender, query);
        if (matchOnePlayer != null) {
            return matchOnePlayer.getCommandSenderName();
        } else if (PlayerSelector.hasArguments(query)) {
            throw new PlayerNotFoundException();
        } else {
            return query;
        }
    }

    public static IChatComponent getChatComponentFromNthArg(ICommandSender sender, String[] args, int n) {
        return getChatComponentFromNthArg(sender, args, n, false);
    }

    public static IChatComponent getChatComponentFromNthArg(ICommandSender sender, String[] args, int index, boolean flag) {
        ChatComponentText chatComponentText = new ChatComponentText("");

        for (int index2 = index; index2 < args.length; index2++) {
            if (index2 > index) {
                chatComponentText.appendText(" ");
            }

            IChatComponent chatComponent = new ChatComponentText(args[index2]);
            if (flag) {
                IChatComponent findIChatComponent = PlayerSelector.findIChatComponent(sender, args[index2]);
                if (findIChatComponent != null) {
                    chatComponent = findIChatComponent;
                } else if (PlayerSelector.hasArguments(args[index2])) {
                    throw new PlayerNotFoundException();
                }
            }

            chatComponentText.appendSibling(chatComponent);
        }

        return chatComponentText;
    }

    public static String getStringFromNthArg(ICommandSender sender, String[] args, int index) {
        StringBuilder sb = new StringBuilder();

        for (int index2 = index; index2 < args.length; index2++) {
            if (index2 > index) {
                sb.append(" ");
            }

            String str = args[index2];
            sb.append(str);
        }

        return sb.toString();
    }

    public static double clamp_coord(ICommandSender sender, double d, String str) {
        return clamp_double(sender, d, str, -30000000, 30000000);
    }

    public static double clamp_double(ICommandSender sender, double d, String str, int min, int max) {
        boolean startsWith = str.startsWith("~");
        if (startsWith && Double.isNaN(d)) {
            throw new NumberInvalidException("commands.generic.num.invalid", d);
        }

        double d2 = startsWith ? d : 0.0;
        if (!startsWith || str.length() > 1) {
            boolean flag = str.contains(".");
            if (startsWith) {
                str = str.substring(1);
            }

            d2 += parseDouble(sender, str);
            if (!flag && !startsWith) {
                d2 += 0.5;
            }
        }

        if (min != 0 || max != 0) {
            if (d2 < min) {
                throw new NumberInvalidException("commands.generic.double.tooSmall", d2, min);
            }

            if (d2 > max) {
                throw new NumberInvalidException("commands.generic.double.tooBig", d2, max);
            }
        }

        return d2;
    }

    public static Item getItemByText(ICommandSender sender, String id) {
        Item object = (Item)Item.itemRegistry.getObject(id);
        if (object == null) {
            try {
                Item itemById = Item.getItemById(Integer.parseInt(id));
                if (itemById != null) {
                    ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.generic.deprecatedId", Item.itemRegistry.getNameForObject(itemById));
                    chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.GRAY);
                    sender.addChatMessage(chatComponentTranslation);
                }

                object = itemById;
            } catch (NumberFormatException e) {
            }
        }

        if (object == null) {
            throw new NumberInvalidException("commands.give.notFound", id);
        } else {
            return object;
        }
    }

    public static Block getBlockByText(ICommandSender sender, String id) {
        if (Block.blockRegistry.containsKey(id)) {
            return (Block)Block.blockRegistry.getObject(id);
        }

        try {
            int parseInt = Integer.parseInt(id);
            if (Block.blockRegistry.containsId(parseInt)) {
                Block blockById = Block.getBlockById(parseInt);
                ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.generic.deprecatedId", Block.blockRegistry.getNameForObject(blockById));
                chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.GRAY);
                sender.addChatMessage(chatComponentTranslation);
                return blockById;
            }
        } catch (NumberFormatException e) {
        }

        throw new NumberInvalidException("commands.give.notFound", id);
    }

    public static String joinNiceString(Object[] elements) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < elements.length; i++) {
            String str = elements[i].toString();
            if (i > 0) {
                if (i == elements.length - 1) {
                    sb.append(" and ");
                } else {
                    sb.append(", ");
                }
            }

            sb.append(str);
        }

        return sb.toString();
    }

    public static IChatComponent joinNiceString(IChatComponent[] components) {
        ChatComponentText chatComponentText = new ChatComponentText("");

        for (int i = 0; i < components.length; i++) {
            if (i > 0) {
                if (i == components.length - 1) {
                    chatComponentText.appendText(" and ");
                } else if (i > 0) {
                    chatComponentText.appendText(", ");
                }
            }

            chatComponentText.appendSibling(components[i]);
        }

        return chatComponentText;
    }

    public static String joinNiceStringFromCollection(Collection strings) {
        return joinNiceString(strings.toArray(new String[strings.size()]));
    }

    public static boolean doesStringStartWith(String original, String region) {
        return region.regionMatches(true, 0, original, 0, original.length());
    }

    public static List getListOfStringsMatchingLastWord(String[] args, String... possibilities) {
        String str = args[args.length - 1];
        ArrayList list = new ArrayList();
        String[] possibilities2 = possibilities;
        int length = possibilities.length;

        for (int i = 0; i < length; i++) {
            String str2 = possibilities2[i];
            if (doesStringStartWith(str, str2)) {
                list.add(str2);
            }
        }

        return list;
    }

    public static List getListOfStringsFromIterableMatchingLastWord(String[] args, Iterable possibilities) {
        String str = args[args.length - 1];
        ArrayList list = new ArrayList();

        for (String str2 : (Iterable<String>)possibilities) {
            if (doesStringStartWith(str, str2)) {
                list.add(str2);
            }
        }

        return list;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return false;
    }

    public static void notifyOperators(ICommandSender sender, ICommand command, String msgFormat, Object... msgParams) {
        notifyOperators(sender, command, 0, msgFormat, msgParams);
    }

    public static void notifyOperators(ICommandSender sender, ICommand command, int n, String msgFormat, Object... msgParams) {
        if (theAdmin != null) {
            theAdmin.notifyOperators(sender, command, n, msgFormat, msgParams);
        }
    }

    public static void setAdminCommander(IAdminCommand command) {
        theAdmin = command;
    }

    public int compareTo(ICommand command) {
        return this.getCommandName().compareTo(command.getCommandName());
    }

    @Override
    public int compareTo(Object obj) {
        return this.compareTo((ICommand)obj);
    }
}
