package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;

public class CommandGive extends CommandBase {
    @Override
    public String getCommandName() {
        return "give";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.give.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new WrongUsageException("commands.give.usage");
        }

        EntityPlayerMP player = getPlayer(sender, args[0]);
        Item itemByText = getItemByText(sender, args[1]);
        int n = 1;
        int n2 = 0;
        if (args.length >= 3) {
            n = parseIntBounded(sender, args[2], 1, 64);
        }

        if (args.length >= 4) {
            n2 = parseInt(sender, args[3]);
        }

        ItemStack itemStack = new ItemStack(itemByText, n, n2);
        if (args.length >= 5) {
            String unformattedText = getChatComponentFromNthArg(sender, args, 4).getUnformattedText();

            try {
                NBTBase createNBTException = JsonToNBT.createNBTException(unformattedText);
                if (!(createNBTException instanceof NBTTagCompound)) {
                    notifyOperators(sender, this, "commands.give.tagError", "Not a valid tag");
                    return;
                }

                itemStack.setTagCompound((NBTTagCompound)createNBTException);
            } catch (NBTException e) {
                notifyOperators(sender, this, "commands.give.tagError", e.getMessage());
                return;
            }
        }

        EntityItem dropPlayerItemWithRandomChoice = player.dropPlayerItemWithRandomChoice(itemStack, false);
        dropPlayerItemWithRandomChoice.delayBeforeCanPickup = 0;
        dropPlayerItemWithRandomChoice.setOwner(player.getCommandSenderName());
        notifyOperators(sender, this, "commands.give.success", itemStack.toHoverableText(), n, player.getCommandSenderName());
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1
            ? getListOfStringsMatchingLastWord(args, this.getPlayers())
            : (args.length == 2 ? getListOfStringsFromIterableMatchingLastWord(args, Item.itemRegistry.getKeys()) : null);
    }

    public String[] getPlayers() {
        return MinecraftServer.getServer().getAllUsernames();
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}
