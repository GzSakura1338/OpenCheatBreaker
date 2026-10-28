package net.minecraft.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;

public class PlayerSelector {
    public static Pattern tokenPattern = Pattern.compile("^@([parf])(?:\\[([\\w=,!-]*)\\])?$");
    public static Pattern intListPattern = Pattern.compile("\\G([-!]?[\\w-]*)(?:$|,)");
    public static Pattern keyValueListPattern = Pattern.compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");

    public static EntityPlayerMP matchOnePlayer(ICommandSender commandSender, String str) {
        EntityPlayerMP[] matchPlayers2 = matchPlayers(commandSender, str);
        return matchPlayers2 != null && matchPlayers2.length == 1 ? matchPlayers2[0] : null;
    }

    public static IChatComponent findIChatComponent(ICommandSender commandSender, String str) {
        EntityPlayerMP[] matchPlayers2 = matchPlayers(commandSender, str);
        if (matchPlayers2 != null && matchPlayers2.length != 0) {
            IChatComponent[] chatComponents = new IChatComponent[matchPlayers2.length];

            for (int i = 0; i < chatComponents.length; i++) {
                chatComponents[i] = matchPlayers2[i].getFormattedCommandSenderName();
            }

            return CommandBase.joinNiceString(chatComponents);
        } else {
            return null;
        }
    }

    public static EntityPlayerMP[] matchPlayers(ICommandSender commandSender, String str) {
        Matcher matcher = tokenPattern.matcher(str);
        if (matcher.matches()) {
            Map argumentMap = getArgumentMap(matcher.group(2));
            String group = matcher.group(1);
            int defaultMinimumRange = getDefaultMinimumRange(group);
            int defaultMaximumRange = getDefaultMaximumRange(group);
            int defaultMinimumLevel = getDefaultMinimumLevel(group);
            int defaultMaximumLevel = getDefaultMaximumLevel(group);
            int defaultCount = getDefaultCount(group);
            int iD = GameType.NOT_SET.getID();
            ChunkCoordinates commandSenderPosition = commandSender.getCommandSenderPosition();
            Map createHashMap2 = createHashMap(argumentMap);
            String str2 = null;
            String str3 = null;
            boolean flag = false;
            if (argumentMap.containsKey("rm")) {
                defaultMinimumRange = MathHelper.parseIntWithDefault((String)argumentMap.get("rm"), defaultMinimumRange);
                flag = true;
            }

            if (argumentMap.containsKey("r")) {
                defaultMaximumRange = MathHelper.parseIntWithDefault((String)argumentMap.get("r"), defaultMaximumRange);
                flag = true;
            }

            if (argumentMap.containsKey("lm")) {
                defaultMinimumLevel = MathHelper.parseIntWithDefault((String)argumentMap.get("lm"), defaultMinimumLevel);
            }

            if (argumentMap.containsKey("l")) {
                defaultMaximumLevel = MathHelper.parseIntWithDefault((String)argumentMap.get("l"), defaultMaximumLevel);
            }

            if (argumentMap.containsKey("x")) {
                commandSenderPosition.posX = MathHelper.parseIntWithDefault((String)argumentMap.get("x"), commandSenderPosition.posX);
                flag = true;
            }

            if (argumentMap.containsKey("y")) {
                commandSenderPosition.posY = MathHelper.parseIntWithDefault((String)argumentMap.get("y"), commandSenderPosition.posY);
                flag = true;
            }

            if (argumentMap.containsKey("z")) {
                commandSenderPosition.posZ = MathHelper.parseIntWithDefault((String)argumentMap.get("z"), commandSenderPosition.posZ);
                flag = true;
            }

            if (argumentMap.containsKey("m")) {
                iD = MathHelper.parseIntWithDefault((String)argumentMap.get("m"), iD);
            }

            if (argumentMap.containsKey("c")) {
                defaultCount = MathHelper.parseIntWithDefault((String)argumentMap.get("c"), defaultCount);
            }

            if (argumentMap.containsKey("team")) {
                str3 = (String)argumentMap.get("team");
            }

            if (argumentMap.containsKey("name")) {
                str2 = (String)argumentMap.get("name");
            }

            World world = flag ? commandSender.getEntityWorld() : null;
            if (group.equals("p") || group.equals("a")) {
                List findPlayers = MinecraftServer.getServer()
                    .getConfigurationManager()
                    .findPlayers(commandSenderPosition, defaultMinimumRange, defaultMaximumRange, defaultCount, iD, defaultMinimumLevel, defaultMaximumLevel, createHashMap2, str2, str3, world);
                return findPlayers.isEmpty() ? new EntityPlayerMP[0] : (EntityPlayerMP[])findPlayers.toArray(new EntityPlayerMP[findPlayers.size()]);
            } else if (group.equals("r")) {
                List findPlayers = MinecraftServer.getServer().getConfigurationManager().findPlayers(commandSenderPosition, defaultMinimumRange, defaultMaximumRange, 0, iD, defaultMinimumLevel, defaultMaximumLevel, createHashMap2, str2, str3, world);
                Collections.shuffle(findPlayers);
                findPlayers = findPlayers.subList(0, Math.min(defaultCount, findPlayers.size()));
                return findPlayers.isEmpty() ? new EntityPlayerMP[0] : (EntityPlayerMP[])findPlayers.toArray(new EntityPlayerMP[findPlayers.size()]);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static Map createHashMap(Map map) {
        HashMap map2 = new HashMap();

        for (String str : (Iterable<String>)map.keySet()) {
            if (str.startsWith("score_") && str.length() > "score_".length()) {
                String substring = str.substring("score_".length());
                map2.put(substring, MathHelper.parseIntWithDefault((String)map.get(str), 1));
            }
        }

        return map2;
    }

    public static boolean matchesMultiplePlayers(String str) {
        Matcher matcher = tokenPattern.matcher(str);
        if (matcher.matches()) {
            Map argumentMap = getArgumentMap(matcher.group(2));
            String group = matcher.group(1);
            int defaultCount = getDefaultCount(group);
            if (argumentMap.containsKey("c")) {
                defaultCount = MathHelper.parseIntWithDefault((String)argumentMap.get("c"), defaultCount);
            }

            return defaultCount != 1;
        } else {
            return false;
        }
    }

    public static boolean hasTheseArguments(String str, String str2) {
        Matcher matcher = tokenPattern.matcher(str);
        if (!matcher.matches()) {
            return false;
        }

        String group = matcher.group(1);
        return str2 == null || str2.equals(group);
    }

    public static boolean hasArguments(String str) {
        return hasTheseArguments(str, null);
    }

    public static int getDefaultMinimumRange(String str) {
        return 0;
    }

    public static int getDefaultMaximumRange(String str) {
        return 0;
    }

    public static int getDefaultMaximumLevel(String str) {
        return Integer.MAX_VALUE;
    }

    public static int getDefaultMinimumLevel(String str) {
        return 0;
    }

    public static int getDefaultCount(String str) {
        return str.equals("a") ? 0 : 1;
    }

    public static Map getArgumentMap(String str) {
        HashMap map = new HashMap();
        if (str == null) {
            return map;
        }

        Matcher matcher = intListPattern.matcher(str);
        int n = 0;

        int n2;
        for (n2 = -1; matcher.find(); n2 = matcher.end()) {
            String str2 = null;
            switch (n++) {
                case 0:
                    str2 = "x";
                    break;
                case 1:
                    str2 = "y";
                    break;
                case 2:
                    str2 = "z";
                    break;
                case 3:
                    str2 = "r";
            }

            if (str2 != null && matcher.group(1).length() > 0) {
                map.put(str2, matcher.group(1));
            }
        }

        if (n2 < str.length()) {
            matcher = keyValueListPattern.matcher(n2 == -1 ? str : str.substring(n2));

            while (matcher.find()) {
                map.put(matcher.group(1), matcher.group(2));
            }
        }

        return map;
    }
}
