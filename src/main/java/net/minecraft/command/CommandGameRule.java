package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.GameRules;
import net.minecraft.world.GameRules.ValueType;

public class CommandGameRule extends CommandBase {
   @Override
   public String getCommandName() {
      return "gamerule";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.gamerule.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      GameRules gameRules = this.getGameRules();
      String str = args.length > 0 ? args[0] : "";
      String str2 = args.length > 1 ? buildString(args, 1) : "";
      switch (args.length) {
         case 0:
            sender.addChatMessage(new ChatComponentText(joinNiceString(gameRules.getRules())));
            break;
         case 1:
            if (!gameRules.hasRule(str)) {
               throw new CommandException("commands.gamerule.norule", str);
            }

            String string = gameRules.getString(str);
            sender.addChatMessage(new ChatComponentText(str).appendText(" = ").appendText(string));
            sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, gameRules.getInt(str));
            break;
         default:
            if (gameRules.areSameType(str, ValueType.BOOLEAN_VALUE) && !"true".equals(str2) && !"false".equals(str2)) {
               throw new CommandException("commands.generic.boolean.invalid", str2);
            }

            gameRules.setOrCreateGameRule(str, str2);
            handleGameRules(gameRules, str);
            notifyOperators(sender, this, "commands.gamerule.success");
      }
   }

   public static void handleGameRules(GameRules rules, String str) {
      if ("reducedDebugInfo".equals(str)) {
         byte b = (byte)(rules.getBoolean(str) ? 22 : 23);

         for (EntityPlayerMP entityPlayerMP : MinecraftServer.getServer().getConfigurationManager().getPlayerList()) {
            entityPlayerMP.playerNetServerHandler.sendPacket(new S19PacketEntityStatus(entityPlayerMP, b));
         }
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length == 1) {
         return getListOfStringsMatchingLastWord(args, this.getGameRules().getRules());
      }

      if (args.length == 2) {
         GameRules gameRules = this.getGameRules();
         if (gameRules.areSameType(args[0], ValueType.BOOLEAN_VALUE)) {
            return getListOfStringsMatchingLastWord(args, "true", "false");
         }
      }

      return null;
   }

   public GameRules getGameRules() {
      return MinecraftServer.getServer().worldServerForDimension(0).getGameRules();
   }
}
