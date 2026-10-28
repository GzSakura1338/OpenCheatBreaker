package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.EnumDifficulty;

public class CommandDifficulty extends CommandBase {
    @Override
    public String getCommandName() {
        return "difficulty";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.difficulty.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length > 0) {
            EnumDifficulty findEnumDifficulty = this.findEnumDifficulty(sender, args[0]);
            MinecraftServer.getServer().setDifficultyForAllWorlds(findEnumDifficulty);
            notifyOperators(sender, this, "commands.difficulty.success", new ChatComponentTranslation(findEnumDifficulty.getDifficultyResourceKey()));
        } else {
            throw new WrongUsageException("commands.difficulty.usage");
        }
    }

    public EnumDifficulty findEnumDifficulty(ICommandSender commandSender, String str) {
        return str.equalsIgnoreCase("peaceful") || str.equalsIgnoreCase("p")
            ? EnumDifficulty.PEACEFUL
            : (
                str.equalsIgnoreCase("easy") || str.equalsIgnoreCase("e")
                    ? EnumDifficulty.EASY
                    : (
                        !str.equalsIgnoreCase("normal") && !str.equalsIgnoreCase("n")
                            ? (
                                !str.equalsIgnoreCase("hard") && !str.equalsIgnoreCase("h")
                                    ? EnumDifficulty.getDifficultyEnum(parseIntBounded(commandSender, str, 0, 3))
                                    : EnumDifficulty.HARD
                            )
                            : EnumDifficulty.NORMAL
                    )
            );
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, "peaceful", "easy", "normal", "hard") : null;
    }
}
