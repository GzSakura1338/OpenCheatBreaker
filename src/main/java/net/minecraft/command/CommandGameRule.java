package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.GameRules;

public class CommandGameRule extends CommandBase {
    @Override
    public String getCommandName() {
        return "gamerule";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.gamerule.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 2) {
            String str = args[0];
            String str2 = args[1];
            GameRules gameRules = this.getGameRules();
            if (gameRules.hasRule(str)) {
                gameRules.setOrCreateGameRule(str, str2);
                notifyOperators(sender, this, "commands.gamerule.success");
            } else {
                notifyOperators(sender, this, "commands.gamerule.norule", str);
            }
        } else if (args.length == 1) {
            String str = args[0];
            GameRules gameRules = this.getGameRules();
            if (gameRules.hasRule(str)) {
                String gameRuleStringValue = gameRules.getGameRuleStringValue(str);
                sender.addChatMessage(new ChatComponentText(str).appendText(" = ").appendText(gameRuleStringValue));
            } else {
                notifyOperators(sender, this, "commands.gamerule.norule", str);
            }
        } else {
            if (args.length != 0) {
                throw new WrongUsageException("commands.gamerule.usage");
            }

            GameRules gameRules = this.getGameRules();
            sender.addChatMessage(new ChatComponentText(joinNiceString(gameRules.getRules())));
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1
            ? getListOfStringsMatchingLastWord(args, this.getGameRules().getRules())
            : (args.length == 2 ? getListOfStringsMatchingLastWord(args, "true", "false") : null);
    }

    public GameRules getGameRules() {
        return MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
    }
}
