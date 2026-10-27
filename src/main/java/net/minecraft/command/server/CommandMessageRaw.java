package net.minecraft.command.server;

import com.google.gson.JsonParseException;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentProcessor;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent.Serializer;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class CommandMessageRaw extends CommandBase {
   @Override
   public String getCommandName() {
      return "tellraw";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.tellraw.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 2) {
         throw new WrongUsageException("commands.tellraw.usage");
      }

      EntityPlayerMP player = getPlayer(sender, args[0]);
      String buildString2 = buildString(args, 1);

      try {
         IChatComponent jsonToComponent = Serializer.jsonToComponent(buildString2);
         player.addChatMessage(ChatComponentProcessor.processComponent(sender, jsonToComponent, player));
      } catch (JsonParseException e) {
         Throwable rootCause = ExceptionUtils.getRootCause(e);
         throw new SyntaxErrorException("commands.tellraw.jsonException", rootCause == null ? "" : rootCause.getMessage());
      }
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return index == 0;
   }
}
