package net.minecraft.command;

import java.util.List;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class CommandParticle extends CommandBase {
   @Override
   public String getCommandName() {
      return "particle";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.particle.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 8) {
         throw new WrongUsageException("commands.particle.usage");
      }

      boolean flag = false;
      EnumParticleTypes enumParticleTypes = null;

      for (EnumParticleTypes enumParticleTypes2 : EnumParticleTypes.values()) {
         if (enumParticleTypes2.hasArguments()) {
            if (args[0].startsWith(enumParticleTypes2.getParticleName())) {
               flag = true;
               enumParticleTypes = enumParticleTypes2;
               break;
            }
         } else if (args[0].equals(enumParticleTypes2.getParticleName())) {
            flag = true;
            enumParticleTypes = enumParticleTypes2;
            break;
         }
      }

      if (!flag) {
         throw new CommandException("commands.particle.notFound", args[0]);
      }

      String str = args[0];
      Vec3 positionVector = sender.getPositionVector();
      double parseDouble2 = (float)parseDouble(positionVector.xCoord, args[1], true);
      double parseDouble3 = (float)parseDouble(positionVector.yCoord, args[2], true);
      double parseDouble4 = (float)parseDouble(positionVector.zCoord, args[3], true);
      double parseDouble5 = (float)parseDouble(args[4]);
      double parseDouble6 = (float)parseDouble(args[5]);
      double parseDouble7 = (float)parseDouble(args[6]);
      double parseDouble8 = (float)parseDouble(args[7]);
      int n = 0;
      if (args.length > 8) {
         n = parseInt(args[8], 0);
      }

      boolean flag2 = false;
      if (args.length > 9 && "force".equals(args[9])) {
         flag2 = true;
      }

      World entityWorld = sender.getEntityWorld();
      if (entityWorld instanceof WorldServer) {
         WorldServer worldServer = (WorldServer)entityWorld;
         int[] ints = new int[enumParticleTypes.getArgumentCount()];
         if (enumParticleTypes.hasArguments()) {
            String[] split = args[0].split("_", 3);

            for (int i = 1; i < split.length; i++) {
               try {
                  ints[i - 1] = Integer.parseInt(split[i]);
               } catch (NumberFormatException e) {
                  throw new CommandException("commands.particle.notFound", args[0]);
               }
            }
         }

         worldServer.spawnParticle(enumParticleTypes, flag2, parseDouble2, parseDouble3, parseDouble4, n, parseDouble5, parseDouble6, parseDouble7, parseDouble8, ints);
         notifyOperators(sender, this, "commands.particle.success", str, Math.max(n, 1));
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, EnumParticleTypes.getParticleNames())
         : (
            args.length > 1 && args.length <= 4
               ? findList(args, 1, pos)
               : (args.length == 10 ? getListOfStringsMatchingLastWord(args, "normal", "force") : null)
         );
   }
}
