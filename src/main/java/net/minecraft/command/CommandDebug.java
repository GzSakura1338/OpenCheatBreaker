package net.minecraft.command;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import net.minecraft.profiler.Profiler.Result;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDebug extends CommandBase {
   public static Logger logger = LogManager.getLogger();
   public long profileStartTime;
   public int profileStartTick;

   @Override
   public String getCommandName() {
      return "debug";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.debug.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 1) {
         throw new WrongUsageException("commands.debug.usage");
      }

      if (args[0].equals("start")) {
         if (args.length != 1) {
            throw new WrongUsageException("commands.debug.usage");
         }

         notifyOperators(sender, this, "commands.debug.start");
         MinecraftServer.getServer().enableProfiling();
         this.profileStartTime = MinecraftServer.getCurrentTimeMillis();
         this.profileStartTick = MinecraftServer.getServer().getTickCounter();
      } else {
         if (!args[0].equals("stop")) {
            throw new WrongUsageException("commands.debug.usage");
         }

         if (args.length != 1) {
            throw new WrongUsageException("commands.debug.usage");
         }

         if (!MinecraftServer.getServer().theProfiler.profilingEnabled) {
            throw new CommandException("commands.debug.notStarted");
         }

         long currentTimeMillis = MinecraftServer.getCurrentTimeMillis();
         int tickCounter = MinecraftServer.getServer().getTickCounter();
         long l = currentTimeMillis - this.profileStartTime;
         int n = tickCounter - this.profileStartTick;
         this.saveProfileResults(l, n);
         MinecraftServer.getServer().theProfiler.profilingEnabled = false;
         notifyOperators(sender, this, "commands.debug.stop", (float)l / 1000.0F, n);
      }
   }

   public void saveProfileResults(long timeSpan, int tickSpan) {
      File file = new File(
         MinecraftServer.getServer().getFile("debug"), "profile-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt"
      );
      file.getParentFile().mkdirs();

      try {
         FileWriter fileWriter = new FileWriter(file);
         fileWriter.write(this.getProfileResults(timeSpan, tickSpan));
         fileWriter.close();
      } catch (Throwable e) {
         logger.error("Could not save profiler results to " + file, e);
      }
   }

   public String getProfileResults(long timeSpan, int tickSpan) {
      StringBuilder sb = new StringBuilder();
      sb.append("---- Minecraft Profiler Results ----\n");
      sb.append("// ");
      sb.append(getWittyComment());
      sb.append("\n\n");
      sb.append("Time span: ").append(timeSpan).append(" ms\n");
      sb.append("Tick span: ").append(tickSpan).append(" ticks\n");
      sb.append("// This is approximately ")
         .append(String.format("%.2f", tickSpan / ((float)timeSpan / 1000.0F)))
         .append(" ticks per second. It should be ")
         .append(20)
         .append(" ticks per second\n\n");
      sb.append("--- BEGIN PROFILE DUMP ---\n\n");
      this.process(0, "root", sb);
      sb.append("--- END PROFILE DUMP ---\n\n");
      return sb.toString();
   }

   public void process(int n, String str, StringBuilder stringBuilder) {
      List profilingData = MinecraftServer.getServer().theProfiler.getProfilingData(str);
      if (profilingData != null && profilingData.size() >= 3) {
         for (int i = 1; i < profilingData.size(); i++) {
            Result result = (Result)profilingData.get(i);
            stringBuilder.append(String.format("[%02d] ", n));

            for (int j = 0; j < n; j++) {
               stringBuilder.append(" ");
            }

            stringBuilder.append(result.name)
               .append(" - ")
               .append(String.format("%.2f", result.relativePercentage))
               .append("%/")
               .append(String.format("%.2f", result.absolutePercentage))
               .append("%\n");
            if (!result.name.equals("unspecified")) {
               try {
                  this.process(n + 1, str + "." + result.name, stringBuilder);
               } catch (Exception e) {
                  stringBuilder.append("[[ EXCEPTION ").append(e).append(" ]]");
               }
            }
         }
      }
   }

   public static String getWittyComment() {
      String[] strs = new String[]{
         "Shiny numbers!",
         "Am I not running fast enough? :(",
         "I'm working as hard as I can!",
         "Will I ever be good enough for you? :(",
         "Speedy. Zoooooom!",
         "Hello world",
         "40% better than a crash report.",
         "Now with extra numbers",
         "Now with less numbers",
         "Now with the same numbers",
         "You should add flames to things, it makes them go faster!",
         "Do you feel the need for... optimization?",
         "*cracks redstone whip*",
         "Maybe if you treated it better then it'll have more motivation to work faster! Poor server."
      };

      try {
         return strs[(int)(System.nanoTime() % strs.length)];
      } catch (Throwable e) {
         return "Witty comment unavailable :(";
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, "start", "stop") : null;
   }
}
