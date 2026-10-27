package net.minecraft.command;

import java.util.List;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

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
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.enchant.usage");
      }

      EntityPlayerMP player = getPlayer(sender, args[0]);
      sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 0);

      int n;
      try {
         n = parseInt(args[1], 0);
      } catch (NumberInvalidException e) {
         Enchantment enchantmentByLocation = Enchantment.getEnchantmentByLocation(args[1]);
         if (enchantmentByLocation == null) {
            throw e;
         }

         n = enchantmentByLocation.effectId;
      }

      int n2 = 1;
      ItemStack currentEquippedItem = player.getCurrentEquippedItem();
      if (currentEquippedItem == null) {
         throw new CommandException("commands.enchant.noItem");
      }

      Enchantment enchantmentById = Enchantment.getEnchantmentById(n);
      if (enchantmentById == null) {
         throw new NumberInvalidException("commands.enchant.notFound", n);
      }

      if (!enchantmentById.canApply(currentEquippedItem)) {
         throw new CommandException("commands.enchant.cantEnchant");
      }

      if (args.length >= 3) {
         n2 = parseInt(args[2], enchantmentById.getMinLevel(), enchantmentById.getMaxLevel());
      }

      if (currentEquippedItem.hasTagCompound()) {
         NBTTagList enchantmentTagList = currentEquippedItem.getEnchantmentTagList();
         if (enchantmentTagList != null) {
            for (int i = 0; i < enchantmentTagList.tagCount(); i++) {
               short short2 = enchantmentTagList.getCompoundTagAt(i).getShort("id");
               if (Enchantment.getEnchantmentById(short2) != null) {
                  Enchantment enchantmentById2 = Enchantment.getEnchantmentById(short2);
                  if (!enchantmentById2.canApplyTogether(enchantmentById)) {
                     throw new CommandException(
                        "commands.enchant.cantCombine", enchantmentById.getTranslatedName(n2), enchantmentById2.getTranslatedName(enchantmentTagList.getCompoundTagAt(i).getShort("lvl"))
                     );
                  }
               }
            }
         }
      }

      currentEquippedItem.addEnchantment(enchantmentById, n2);
      notifyOperators(sender, this, "commands.enchant.success");
      sender.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, 1);
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, this.getListOfPlayers())
         : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Enchantment.getSet()) : null);
   }

   public String[] getListOfPlayers() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
