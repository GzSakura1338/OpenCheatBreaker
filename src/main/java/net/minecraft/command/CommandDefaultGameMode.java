package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings.GameType;

public class CommandDefaultGameMode extends CommandGameMode {
   @Override
   public String getCommandName() {
      return "defaultgamemode";
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.defaultgamemode.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length <= 0) {
         throw new WrongUsageException("commands.defaultgamemode.usage");
      }

      GameType gameModeFromCommand = this.getGameModeFromCommand(sender, args[0]);
      this.setGameType(gameModeFromCommand);
      notifyOperators(sender, this, "commands.defaultgamemode.success", new ChatComponentTranslation("gameMode." + gameModeFromCommand.getName()));
   }

   public void setGameType(GameType gameMode) {
      MinecraftServer server = MinecraftServer.getServer();
      server.setGameType(gameMode);
      if (server.getForceGamemode()) {
         for (EntityPlayerMP entityPlayerMP : MinecraftServer.getServer().getConfigurationManager().getPlayerList()) {
            entityPlayerMP.setGameType(gameMode);
            entityPlayerMP.fallDistance = 0.0F;
         }
      }
   }
}
