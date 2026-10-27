package net.minecraft.command;

import java.util.List;
import java.util.Random;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;

public class CommandWeather extends CommandBase {
   @Override
   public String getCommandName() {
      return "weather";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.weather.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length >= 1 && args.length <= 2) {
         int n = (300 + new Random().nextInt(600)) * 20;
         if (args.length >= 2) {
            n = parseInt(args[1], 1, 1000000) * 20;
         }

         WorldServer worldServer = MinecraftServer.getServer().worldServers[0];
         WorldInfo worldInfo = worldServer.getWorldInfo();
         if ("clear".equalsIgnoreCase(args[0])) {
            worldInfo.setCleanWeatherTime(n);
            worldInfo.setRainTime(0);
            worldInfo.setThunderTime(0);
            worldInfo.setRaining(false);
            worldInfo.setThundering(false);
            notifyOperators(sender, this, "commands.weather.clear");
         } else if ("rain".equalsIgnoreCase(args[0])) {
            worldInfo.setCleanWeatherTime(0);
            worldInfo.setRainTime(n);
            worldInfo.setThunderTime(n);
            worldInfo.setRaining(true);
            worldInfo.setThundering(false);
            notifyOperators(sender, this, "commands.weather.rain");
         } else {
            if (!"thunder".equalsIgnoreCase(args[0])) {
               throw new WrongUsageException("commands.weather.usage");
            }

            worldInfo.setCleanWeatherTime(0);
            worldInfo.setRainTime(n);
            worldInfo.setThunderTime(n);
            worldInfo.setRaining(true);
            worldInfo.setThundering(true);
            notifyOperators(sender, this, "commands.weather.thunder");
         }
      } else {
         throw new WrongUsageException("commands.weather.usage");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, "clear", "rain", "thunder") : null;
   }
}
