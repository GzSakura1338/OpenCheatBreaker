package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

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
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.give.usage");
      }

      EntityPlayerMP player = getPlayer(sender, args[0]);
      Item itemByText = getItemByText(sender, args[1]);
      int n = args.length >= 3 ? parseInt(args[2], 1, 64) : 1;
      int n2 = args.length >= 4 ? parseInt(args[3]) : 0;
      ItemStack itemStack = new ItemStack(itemByText, n, n2);
      if (args.length >= 5) {
         String unformattedText = getChatComponentFromNthArg(sender, args, 4).getUnformattedText();

         try {
            itemStack.setTagCompound(JsonToNBT.getTagFromJson(unformattedText));
         } catch (NBTException e) {
            throw new CommandException("commands.give.tagError", e.getMessage());
         }
      }

      boolean addItemStackToInventory = player.inventory.addItemStackToInventory(itemStack);
      if (addItemStackToInventory) {
         player.worldObj.playSoundAtEntity(player, "random.pop", 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
         player.inventoryContainer.detectAndSendChanges();
      }

      if (addItemStackToInventory && itemStack.stackSize <= 0) {
         itemStack.stackSize = 1;
         sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, n);
         EntityItem dropPlayerItemWithRandomChoice = player.dropPlayerItemWithRandomChoice(itemStack, false);
         if (dropPlayerItemWithRandomChoice != null) {
            dropPlayerItemWithRandomChoice.setDespawnImmediately();
         }
      } else {
         sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, n - itemStack.stackSize);
         EntityItem dropPlayerItemWithRandomChoice = player.dropPlayerItemWithRandomChoice(itemStack, false);
         if (dropPlayerItemWithRandomChoice != null) {
            dropPlayerItemWithRandomChoice.setNoPickupDelay();
            dropPlayerItemWithRandomChoice.setOwner(player.getName());
         }
      }

      notifyOperators(sender, this, "commands.give.success", itemStack.getChatComponent(), n, player.getName());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, this.getPlayers())
         : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Item.itemRegistry.getKeys()) : null);
   }

   public String[] getPlayers() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
