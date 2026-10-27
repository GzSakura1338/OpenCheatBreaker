package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;

public class CommandClearInventory extends CommandBase {
   @Override
   public String getCommandName() {
      return "clear";
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.clear.usage";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      EntityPlayerMP entityPlayerMP = args.length == 0 ? getCommandSenderAsPlayer(sender) : getPlayer(sender, args[0]);
      Item item = args.length >= 2 ? getItemByText(sender, args[1]) : null;
      int n = args.length >= 3 ? parseInt(args[2], -1) : -1;
      int n2 = args.length >= 4 ? parseInt(args[3], -1) : -1;
      NBTTagCompound nbttagCompound = null;
      if (args.length >= 5) {
         try {
            nbttagCompound = JsonToNBT.getTagFromJson(buildString(args, 4));
         } catch (NBTException e) {
            throw new CommandException("commands.clear.tagError", e.getMessage());
         }
      }

      if (args.length >= 2 && item == null) {
         throw new CommandException("commands.clear.failure", entityPlayerMP.getName());
      }

      int clearMatchingItems = entityPlayerMP.inventory.clearMatchingItems(item, n, n2, nbttagCompound);
      entityPlayerMP.inventoryContainer.detectAndSendChanges();
      if (!entityPlayerMP.capabilities.isCreativeMode) {
         entityPlayerMP.updateHeldItem();
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, clearMatchingItems);
      if (clearMatchingItems == 0) {
         throw new CommandException("commands.clear.failure", entityPlayerMP.getName());
      }

      if (n2 == 0) {
         sender.addChatMessage(new ChatComponentTranslation("commands.clear.testing", entityPlayerMP.getName(), clearMatchingItems));
      } else {
         notifyOperators(sender, this, "commands.clear.success", entityPlayerMP.getName(), clearMatchingItems);
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, this.getStringArray())
         : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Item.itemRegistry.getKeys()) : null);
   }

   public String[] getStringArray() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
