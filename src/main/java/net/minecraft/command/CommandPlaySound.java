package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class CommandPlaySound extends CommandBase {
   @Override
   public String getCommandName() {
      return "playsound";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.playsound.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException(this.getCommandUsage(sender));
      }

      int n = 0;
      String str = args[n++];
      EntityPlayerMP player = getPlayer(sender, args[n++]);
      Vec3 positionVector = sender.getPositionVector();
      double xCoord = positionVector.xCoord;
      if (args.length > n) {
         xCoord = parseDouble(xCoord, args[n++], true);
      }

      double yCoord = positionVector.yCoord;
      if (args.length > n) {
         yCoord = parseDouble(yCoord, args[n++], 0, 0, false);
      }

      double zCoord = positionVector.zCoord;
      if (args.length > n) {
         zCoord = parseDouble(zCoord, args[n++], true);
      }

      double d = 1.0;
      if (args.length > n) {
         d = parseDouble(args[n++], 0.0, Float.MAX_VALUE);
      }

      double d2 = 1.0;
      if (args.length > n) {
         d2 = parseDouble(args[n++], 0.0, 2.0);
      }

      double d3 = 0.0;
      if (args.length > n) {
         d3 = parseDouble(args[n], 0.0, 1.0);
      }

      double d4 = d > 1.0 ? d * 16.0 : 16.0;
      double distance = player.getDistance(xCoord, yCoord, zCoord);
      if (distance > d4) {
         if (d3 <= 0.0) {
            throw new CommandException("commands.playsound.playerTooFar", player.getName());
         }

         double d5 = xCoord - player.posX;
         double d6 = yCoord - player.posY;
         double d7 = zCoord - player.posZ;
         double sqrt = Math.sqrt(d5 * d5 + d6 * d6 + d7 * d7);
         if (sqrt > 0.0) {
            xCoord = player.posX + d5 / sqrt * 2.0;
            yCoord = player.posY + d6 / sqrt * 2.0;
            zCoord = player.posZ + d7 / sqrt * 2.0;
         }

         d = d3;
      }

      player.playerNetServerHandler.sendPacket(new S29PacketSoundEffect(str, xCoord, yCoord, zCoord, (float)d, (float)d2));
      notifyOperators(sender, this, "commands.playsound.success", str, player.getName());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 2
         ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames())
         : (args.length > 2 && args.length <= 5 ? findList(args, 2, pos) : null);
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 1;
   }
}
