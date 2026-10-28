package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;

public class CommandEffect extends CommandBase {
    @Override
    public String getCommandName() {
        return "effect";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.effect.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException("commands.effect.usage");
        }

        EntityPlayerMP player = getPlayer(sender, args[0]);
        if (args[1].equals("clear")) {
            if (player.getActivePotionEffects().isEmpty()) {
                throw new CommandException("commands.effect.failure.notActive.all", player.getCommandSenderName());
            }

            player.clearActivePotions();
            notifyOperators(sender, this, "commands.effect.success.removed.all", player.getCommandSenderName());
        } else {
            int parseIntWithMin2 = parseIntWithMin(sender, args[1], 1);
            int n = 600;
            int n2 = 30;
            int n3 = 0;
            if (parseIntWithMin2 < 0 || parseIntWithMin2 >= Potion.potionTypes.length || Potion.potionTypes[parseIntWithMin2] == null) {
                throw new NumberInvalidException("commands.effect.notFound", parseIntWithMin2);
            }

            if (args.length >= 3) {
                n2 = parseIntBounded(sender, args[2], 0, 1000000);
                if (Potion.potionTypes[parseIntWithMin2].isInstant()) {
                    n = n2;
                } else {
                    n = n2 * 20;
                }
            } else if (Potion.potionTypes[parseIntWithMin2].isInstant()) {
                n = 1;
            }

            if (args.length >= 4) {
                n3 = parseIntBounded(sender, args[3], 0, 255);
            }

            if (n2 == 0) {
                if (!player.isPotionActive(parseIntWithMin2)) {
                    throw new CommandException(
                        "commands.effect.failure.notActive", new ChatComponentTranslation(Potion.potionTypes[parseIntWithMin2].getName()), player.getCommandSenderName()
                    );
                }

                player.removePotionEffect(parseIntWithMin2);
                notifyOperators(
                    sender, this, "commands.effect.success.removed", new ChatComponentTranslation(Potion.potionTypes[parseIntWithMin2].getName()), player.getCommandSenderName()
                );
            } else {
                PotionEffect potionEffect = new PotionEffect(parseIntWithMin2, n, n3);
                player.addPotionEffect(potionEffect);
                notifyOperators(
                    sender, this, "commands.effect.success", new ChatComponentTranslation(potionEffect.getEffectName()), parseIntWithMin2, n3, player.getCommandSenderName(), n2
                );
            }
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.getAllUsernames()) : null;
    }

    public String[] getAllUsernames() {
        return MinecraftServer.getServer().getAllUsernames();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}
