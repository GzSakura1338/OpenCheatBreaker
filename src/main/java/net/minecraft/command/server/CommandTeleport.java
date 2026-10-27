package net.minecraft.command.server;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandBase.CoordinateArg;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class CommandTeleport extends CommandBase {
   @Override
   public String getCommandName() {
      return "tp";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.tp.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.tp.usage");
      }

      byte b = 0;
      Entity entity;
      if (args.length != 2 && args.length != 4 && args.length != 6) {
         entity = getCommandSenderAsPlayer(sender);
      } else {
         entity = getEntity(sender, args[0]);
         b = 1;
      }

      if (args.length != 1 && args.length != 2) {
         if (args.length < b + 3) {
            throw new WrongUsageException("commands.tp.usage");
         }

         if (entity.worldObj != null) {
            int n = b + 1;
            CoordinateArg parseCoordinate2 = parseCoordinate(entity.posX, args[b], true);
            CoordinateArg parseCoordinate3 = parseCoordinate(entity.posY, args[n++], 0, 0, false);
            CoordinateArg parseCoordinate4 = parseCoordinate(entity.posZ, args[n++], true);
            CoordinateArg parseCoordinate5 = parseCoordinate(entity.rotationYaw, args.length > n ? args[n++] : "~", false);
            CoordinateArg parseCoordinate6 = parseCoordinate(entity.rotationPitch, args.length > n ? args[n] : "~", false);
            if (entity instanceof EntityPlayerMP) {
               EnumSet noneOf = EnumSet.noneOf(EnumFlags.class);
               if (parseCoordinate2.isRelative()) {
                  noneOf.add(EnumFlags.X);
               }

               if (parseCoordinate3.isRelative()) {
                  noneOf.add(EnumFlags.Y);
               }

               if (parseCoordinate4.isRelative()) {
                  noneOf.add(EnumFlags.Z);
               }

               if (parseCoordinate6.isRelative()) {
                  noneOf.add(EnumFlags.X_ROT);
               }

               if (parseCoordinate5.isRelative()) {
                  noneOf.add(EnumFlags.Y_ROT);
               }

               float amount = (float)parseCoordinate5.getAmount();
               if (!parseCoordinate5.isRelative()) {
                  amount = MathHelper.wrapAngleTo180_float(amount);
               }

               float amount2 = (float)parseCoordinate6.getAmount();
               if (!parseCoordinate6.isRelative()) {
                  amount2 = MathHelper.wrapAngleTo180_float(amount2);
               }

               if (amount2 > 90.0F || amount2 < -90.0F) {
                  amount2 = MathHelper.wrapAngleTo180_float(180.0F - amount2);
                  amount = MathHelper.wrapAngleTo180_float(amount + 180.0F);
               }

               entity.mountEntity((Entity)null);
               ((EntityPlayerMP)entity).playerNetServerHandler.setPlayerLocation(parseCoordinate2.getAmount(), parseCoordinate3.getAmount(), parseCoordinate4.getAmount(), amount, amount2, noneOf);
               entity.setRotationYawHead(amount);
            } else {
               float f = (float)MathHelper.wrapAngleTo180_double(parseCoordinate5.getResult());
               float f2 = (float)MathHelper.wrapAngleTo180_double(parseCoordinate6.getResult());
               if (f2 > 90.0F || f2 < -90.0F) {
                  f2 = MathHelper.wrapAngleTo180_float(180.0F - f2);
                  f = MathHelper.wrapAngleTo180_float(f + 180.0F);
               }

               entity.setLocationAndAngles(parseCoordinate2.getResult(), parseCoordinate3.getResult(), parseCoordinate4.getResult(), f, f2);
               entity.setRotationYawHead(f);
            }

            notifyOperators(sender, this, "commands.tp.success.coordinates", entity.getName(), parseCoordinate2.getResult(), parseCoordinate3.getResult(), parseCoordinate4.getResult());
         }
      } else {
         Entity entity2 = getEntity(sender, args[args.length - 1]);
         if (entity2.worldObj != entity.worldObj) {
            throw new CommandException("commands.tp.notSameDimension");
         }

         entity.mountEntity((Entity)null);
         if (entity instanceof EntityPlayerMP) {
            ((EntityPlayerMP)entity).playerNetServerHandler.setPlayerLocation(entity2.posX, entity2.posY, entity2.posZ, entity2.rotationYaw, entity2.rotationPitch);
         } else {
            entity.setLocationAndAngles(entity2.posX, entity2.posY, entity2.posZ, entity2.rotationYaw, entity2.rotationPitch);
         }

         notifyOperators(sender, this, "commands.tp.success", entity.getName(), entity2.getName());
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length != 1 && args.length != 2 ? null : getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
