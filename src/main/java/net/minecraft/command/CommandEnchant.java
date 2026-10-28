package net.minecraft.command;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;

public class CommandEnchant extends CommandBase {
    @Override
    public String getCommandName() {
        return "enchant";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.enchant.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException("commands.enchant.usage");
        }

        EntityPlayerMP player = getPlayer(sender, args[0]);
        int parseIntBounded2 = parseIntBounded(sender, args[1], 0, Enchantment.enchantmentsList.length - 1);
        int n = 1;
        ItemStack currentEquippedItem = player.getCurrentEquippedItem();
        if (currentEquippedItem == null) {
            throw new CommandException("commands.enchant.noItem");
        }

        Enchantment enchantment = Enchantment.enchantmentsList[parseIntBounded2];
        if (enchantment == null) {
            throw new NumberInvalidException("commands.enchant.notFound", parseIntBounded2);
        }

        if (!enchantment.canApply(currentEquippedItem)) {
            throw new CommandException("commands.enchant.cantEnchant");
        }

        if (args.length >= 3) {
            n = parseIntBounded(sender, args[2], enchantment.getMinLevel(), enchantment.getMaxLevel());
        }

        if (currentEquippedItem.hasTagCompound()) {
            NBTTagList enchantmentTagList = currentEquippedItem.getEnchantmentTagList();
            if (enchantmentTagList != null) {
                for (int i = 0; i < enchantmentTagList.tagCount(); i++) {
                    short short2 = enchantmentTagList.getCompoundTagAt(i).getShort("id");
                    if (Enchantment.enchantmentsList[short2] != null) {
                        Enchantment enchantment2 = Enchantment.enchantmentsList[short2];
                        if (!enchantment2.canApplyTogether(enchantment)) {
                            throw new CommandException(
                                "commands.enchant.cantCombine", enchantment.getTranslatedName(n), enchantment2.getTranslatedName(enchantmentTagList.getCompoundTagAt(i).getShort("lvl"))
                            );
                        }
                    }
                }
            }
        }

        currentEquippedItem.addEnchantment(enchantment, n);
        notifyOperators(sender, this, "commands.enchant.success");
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.getListOfPlayers()) : null;
    }

    public String[] getListOfPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}
