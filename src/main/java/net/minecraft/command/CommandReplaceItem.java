package net.minecraft.command;

import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandReplaceItem extends CommandBase {
   public static Map<String, Integer> SHORTCUTS = Maps.newHashMap();

   @Override
   public String getCommandName() {
      return "replaceitem";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.replaceitem.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.replaceitem.usage");
      }

      boolean flag;
      if (args[0].equals("entity")) {
         flag = false;
      } else {
         if (!args[0].equals("block")) {
            throw new WrongUsageException("commands.replaceitem.usage");
         }

         flag = true;
      }

      int n;
      if (flag) {
         if (args.length < 6) {
            throw new WrongUsageException("commands.replaceitem.block.usage");
         }

         n = 4;
      } else {
         if (args.length < 4) {
            throw new WrongUsageException("commands.replaceitem.entity.usage");
         }

         n = 2;
      }

      int slotForShortcut = this.getSlotForShortcut(args[n++]);

      Item item;
      try {
         item = getItemByText(sender, args[n]);
      } catch (NumberInvalidException e) {
         if (Block.getBlockFromName(args[n]) != Blocks.air) {
            throw e;
         }

         item = null;
      }

      n++;
      int n2 = args.length > n ? parseInt(args[n++], 1, 64) : 1;
      int n3 = args.length > n ? parseInt(args[n++]) : 0;
      ItemStack itemStack = new ItemStack(item, n2, n3);
      if (args.length > n) {
         String unformattedText = getChatComponentFromNthArg(sender, args, n).getUnformattedText();

         try {
            itemStack.setTagCompound(JsonToNBT.getTagFromJson(unformattedText));
         } catch (NBTException e) {
            throw new CommandException("commands.replaceitem.tagError", e.getMessage());
         }
      }

      if (itemStack.getItem() == null) {
         itemStack = null;
      }

      if (flag) {
         sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
         BlockPos parseBlockPos2 = parseBlockPos(sender, args, 1, false);
         World entityWorld = sender.getEntityWorld();
         TileEntity tileEntity = entityWorld.getTileEntity(parseBlockPos2);
         if (tileEntity == null || !(tileEntity instanceof IInventory)) {
            throw new CommandException("commands.replaceitem.noContainer", parseBlockPos2.getX(), parseBlockPos2.getY(), parseBlockPos2.getZ());
         }

         IInventory inventory = (IInventory)tileEntity;
         if (slotForShortcut >= 0 && slotForShortcut < inventory.getSizeInventory()) {
            inventory.setInventorySlotContents(slotForShortcut, itemStack);
         }
      } else {
         Entity entity = getEntity(sender, args[1]);
         sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);
         if (entity instanceof EntityPlayer) {
            ((EntityPlayer)entity).inventoryContainer.detectAndSendChanges();
         }

         if (!entity.replaceItemInInventory(slotForShortcut, itemStack)) {
            throw new CommandException("commands.replaceitem.failed", slotForShortcut, n2, itemStack == null ? "Air" : itemStack.getChatComponent());
         }

         if (entity instanceof EntityPlayer) {
            ((EntityPlayer)entity).inventoryContainer.detectAndSendChanges();
         }
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, n2);
      notifyOperators(sender, this, "commands.replaceitem.success", slotForShortcut, n2, itemStack == null ? "Air" : itemStack.getChatComponent());
   }

   public int getSlotForShortcut(String shortcut) throws CommandException {
      if (!SHORTCUTS.containsKey(shortcut)) {
         throw new CommandException("commands.generic.parameter.invalid", shortcut);
      } else {
         return SHORTCUTS.get(shortcut);
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, "entity", "block")
         : (
            args.length == 2 && args[0].equals("entity")
               ? getListOfStringsMatchingLastWord(args, this.getUsernames())
               : (
                  args.length >= 2 && args.length <= 4 && args[0].equals("block")
                     ? findList(args, 1, pos)
                     : (
                        (args.length != 3 || !args[0].equals("entity")) && (args.length != 5 || !args[0].equals("block"))
                           ? (
                              args.length == 4 && args[0].equals("entity") || args.length == 6 && args[0].equals("block")
                                 ? getListOfStringsMatchingLastWord(args, Item.itemRegistry.getKeys())
                                 : null
                           )
                           : getListOfStringsMatchingLastWord(args, SHORTCUTS.keySet())
                     )
               )
         );
   }

   public String[] getUsernames() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return args.length > 0 && args[0].equals("entity") && index == 1;
   }

   static {
      for (int i = 0; i < 54; i++) {
         SHORTCUTS.put("slot.container." + i, i);
      }

      for (int i = 0; i < 9; i++) {
         SHORTCUTS.put("slot.hotbar." + i, i);
      }

      for (int i = 0; i < 27; i++) {
         SHORTCUTS.put("slot.inventory." + i, 9 + i);
      }

      for (int i = 0; i < 27; i++) {
         SHORTCUTS.put("slot.enderchest." + i, 200 + i);
      }

      for (int i = 0; i < 8; i++) {
         SHORTCUTS.put("slot.villager." + i, 300 + i);
      }

      for (int i = 0; i < 15; i++) {
         SHORTCUTS.put("slot.horse." + i, 500 + i);
      }

      SHORTCUTS.put("slot.weapon", 99);
      SHORTCUTS.put("slot.armor.head", 103);
      SHORTCUTS.put("slot.armor.chest", 102);
      SHORTCUTS.put("slot.armor.legs", 101);
      SHORTCUTS.put("slot.armor.feet", 100);
      SHORTCUTS.put("slot.horse.saddle", 400);
      SHORTCUTS.put("slot.horse.armor", 401);
      SHORTCUTS.put("slot.horse.chest", 499);
   }
}
