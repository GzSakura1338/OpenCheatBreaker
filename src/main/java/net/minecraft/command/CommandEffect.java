package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
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
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.effect.usage");
      }

      EntityLivingBase entity = getEntity(sender, args[0], EntityLivingBase.class);
      if (args[1].equals("clear")) {
         if (entity.getActivePotionEffects().isEmpty()) {
            throw new CommandException("commands.effect.failure.notActive.all", entity.getName());
         }

         entity.clearActivePotions();
         notifyOperators(sender, this, "commands.effect.success.removed.all", entity.getName());
      } else {
         int n;
         try {
            n = parseInt(args[1], 1);
         } catch (NumberInvalidException e) {
            Potion potionFromResourceLocation = Potion.getPotionFromResourceLocation(args[1]);
            if (potionFromResourceLocation == null) {
               throw e;
            }

            n = potionFromResourceLocation.id;
         }

         int n2 = 600;
         int n3 = 30;
         int n4 = 0;
         if (n < 0 || n >= Potion.potionTypes.length || Potion.potionTypes[n] == null) {
            throw new NumberInvalidException("commands.effect.notFound", n);
         }

         Potion potion = Potion.potionTypes[n];
         if (args.length >= 3) {
            n3 = parseInt(args[2], 0, 1000000);
            if (potion.isInstant()) {
               n2 = n3;
            } else {
               n2 = n3 * 20;
            }
         } else if (potion.isInstant()) {
            n2 = 1;
         }

         if (args.length >= 4) {
            n4 = parseInt(args[3], 0, 255);
         }

         boolean flag = true;
         if (args.length >= 5 && "true".equalsIgnoreCase(args[4])) {
            flag = false;
         }

         if (n3 > 0) {
            PotionEffect potionEffect = new PotionEffect(n, n2, n4, false, flag);
            entity.addPotionEffect(potionEffect);
            notifyOperators(sender, this, "commands.effect.success", new ChatComponentTranslation(potionEffect.getEffectName()), n, n4, entity.getName(), n3);
         } else {
            if (!entity.isPotionActive(n)) {
               throw new CommandException("commands.effect.failure.notActive", new ChatComponentTranslation(potion.getName()), entity.getName());
            }

            entity.removePotionEffect(n);
            notifyOperators(sender, this, "commands.effect.success.removed", new ChatComponentTranslation(potion.getName()), entity.getName());
         }
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, this.getAllUsernames())
         : (
            args.length == 2
               ? getListOfStringsMatchingLastWord(args, Potion.getPotionLocations())
               : (args.length == 5 ? getListOfStringsMatchingLastWord(args, "true", "false") : null)
         );
   }

   public String[] getAllUsernames() {
      return MinecraftServer.getServer().getAllUsernames();
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
