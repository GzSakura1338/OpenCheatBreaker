package net.minecraft.command.server;

import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.IPBanEntry;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;

public class CommandBanIp extends CommandBase {
   public static Pattern pattern = Pattern.compile(
      "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$"
   );

   @Override
   public String getCommandName() {
      return "ban-ip";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public boolean canCommandSenderUseCommand(ICommandSender sender) {
      return MinecraftServer.getServer().getConfigurationManager().getBannedIPs().isLanServer() && super.canCommandSenderUseCommand(sender);
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.banip.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length >= 1 && args[0].length() > 1) {
         IChatComponent chatComponent = args.length >= 2 ? getChatComponentFromNthArg(sender, args, 1) : null;
         Matcher matcher = pattern.matcher(args[0]);
         if (matcher.matches()) {
            this.handleICommandSender(sender, args[0], chatComponent == null ? null : chatComponent.getUnformattedText());
         } else {
            EntityPlayerMP playerByUsername = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(args[0]);
            if (playerByUsername == null) {
               throw new PlayerNotFoundException("commands.banip.invalid");
            }

            this.handleICommandSender(sender, playerByUsername.getPlayerIP(), chatComponent == null ? null : chatComponent.getUnformattedText());
         }
      } else {
         throw new WrongUsageException("commands.banip.usage");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
   }

   public void handleICommandSender(ICommandSender sender, String address, String reason) {
      IPBanEntry ipbanEntry = new IPBanEntry(address, (Date)null, sender.getName(), (Date)null, reason);
      MinecraftServer.getServer().getConfigurationManager().getBannedIPs().addEntry(ipbanEntry);
      List playersMatchingAddress = MinecraftServer.getServer().getConfigurationManager().getPlayersMatchingAddress(address);
      String[] strs = new String[playersMatchingAddress.size()];
      int n = 0;

      for (EntityPlayerMP entityPlayerMP : (Iterable<EntityPlayerMP>)playersMatchingAddress) {
         entityPlayerMP.playerNetServerHandler.kickPlayerFromServer("You have been IP banned.");
         strs[n++] = entityPlayerMP.getName();
      }

      if (playersMatchingAddress.isEmpty()) {
         notifyOperators(sender, this, "commands.banip.success", address);
      } else {
         notifyOperators(sender, this, "commands.banip.success.players", address, joinNiceString(strs));
      }
   }
}
