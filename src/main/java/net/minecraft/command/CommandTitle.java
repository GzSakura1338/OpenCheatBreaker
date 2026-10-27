package net.minecraft.command;

import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S45PacketTitle.Type;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent.Serializer;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandTitle extends CommandBase {
   public static Logger LOGGER = LogManager.getLogger();

   @Override
   public String getCommandName() {
      return "title";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.title.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.title.usage");
      }

      if (args.length < 3) {
         if ("title".equals(args[1]) || "subtitle".equals(args[1])) {
            throw new WrongUsageException("commands.title.usage.title");
         }

         if ("times".equals(args[1])) {
            throw new WrongUsageException("commands.title.usage.times");
         }
      }

      EntityPlayerMP player = getPlayer(sender, args[0]);
      Type byName = Type.byName(args[1]);
      if (byName == Type.CLEAR || byName == Type.RESET) {
         if (args.length != 2) {
            throw new WrongUsageException("commands.title.usage");
         }

         S45PacketTitle s45PacketTitle = new S45PacketTitle(byName, (IChatComponent)null);
         player.playerNetServerHandler.sendPacket(s45PacketTitle);
         notifyOperators(sender, this, "commands.title.success");
      } else if (byName == Type.TIMES) {
         if (args.length != 5) {
            throw new WrongUsageException("commands.title.usage");
         }

         int parseInt2 = parseInt(args[2]);
         int parseInt3 = parseInt(args[3]);
         int parseInt4 = parseInt(args[4]);
         S45PacketTitle s45PacketTitle = new S45PacketTitle(parseInt2, parseInt3, parseInt4);
         player.playerNetServerHandler.sendPacket(s45PacketTitle);
         notifyOperators(sender, this, "commands.title.success");
      } else {
         if (args.length < 3) {
            throw new WrongUsageException("commands.title.usage");
         }

         String buildString2 = buildString(args, 2);

         IChatComponent chatComponent;
         try {
            chatComponent = Serializer.jsonToComponent(buildString2);
         } catch (JsonParseException e) {
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            throw new SyntaxErrorException("commands.tellraw.jsonException", rootCause == null ? "" : rootCause.getMessage());
         }

         S45PacketTitle s45PacketTitle = new S45PacketTitle(byName, ChatComponentProcessor.processComponent(sender, chatComponent, player));
         player.playerNetServerHandler.sendPacket(s45PacketTitle);
         notifyOperators(sender, this, "commands.title.success");
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1
         ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames())
         : (args.length == 2 ? getListOfStringsMatchingLastWord(args, Type.getNames()) : null);
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
