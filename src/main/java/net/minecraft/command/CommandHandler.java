package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandHandler implements ICommandManager {
   public static Logger logger = LogManager.getLogger();
   public Map<String, ICommand> commandMap = Maps.newHashMap();
   public Set<ICommand> commandSet = Sets.newHashSet();

   @Override
   public int executeCommand(ICommandSender sender, String rawCommand) {
      rawCommand = rawCommand.trim();
      if (rawCommand.startsWith("/")) {
         rawCommand = rawCommand.substring(1);
      }

      String[] split = rawCommand.split(" ");
      String str = split[0];
      split = dropFirstString(split);
      ICommand command = this.commandMap.get(str);
      int usernameIndex = this.getUsernameIndex(command, split);
      int n = 0;
      if (command == null) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.generic.notFound");
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         sender.addChatMessage(chatComponentTranslation);
      } else if (command.canCommandSenderUseCommand(sender)) {
         if (usernameIndex > -1) {
            List matchEntities = PlayerSelector.matchEntities(sender, split[usernameIndex], Entity.class);
            String str2 = split[usernameIndex];
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, matchEntities.size());

            for (Entity entity : (Iterable<Entity>)matchEntities) {
               split[usernameIndex] = entity.getUniqueID().toString();
               if (this.tryExecute(sender, split, command, rawCommand)) {
                  n++;
               }
            }

            split[usernameIndex] = str2;
         } else {
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, 1);
            if (this.tryExecute(sender, split, command, rawCommand)) {
               n++;
            }
         }
      } else {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.generic.permission");
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         sender.addChatMessage(chatComponentTranslation);
      }

      sender.setCommandStat(CommandResultStats.Type.SUCCESS_COUNT, n);
      return n;
   }

   public boolean tryExecute(ICommandSender sender, String[] args, ICommand command, String input) {
      try {
         command.processCommand(sender, args);
         return true;
      } catch (WrongUsageException e) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(
            "commands.generic.usage", new ChatComponentTranslation(e.getMessage(), e.getErrorObjects())
         );
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         sender.addChatMessage(chatComponentTranslation);
      } catch (CommandException e) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(e.getMessage(), e.getErrorObjects());
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         sender.addChatMessage(chatComponentTranslation);
      } catch (Throwable e) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.generic.exception");
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         sender.addChatMessage(chatComponentTranslation);
         logger.warn("Couldn't process command: '" + input + "'");
      }

      return false;
   }

   public ICommand registerCommand(ICommand command) {
      this.commandMap.put(command.getCommandName(), command);
      this.commandSet.add(command);

      for (String str : command.getCommandAliases()) {
         ICommand command2 = this.commandMap.get(str);
         if (command2 == null || !command2.getCommandName().equals(str)) {
            this.commandMap.put(str, command);
         }
      }

      return command;
   }

   public static String[] dropFirstString(String[] input) {
      String[] strs = new String[input.length - 1];
      System.arraycopy(input, 1, strs, 0, input.length - 1);
      return strs;
   }

   @Override
   public List<String> getTabCompletionOptions(ICommandSender sender, String input, BlockPos pos) {
      String[] split = input.split(" ", -1);
      String str = split[0];
      if (split.length == 1) {
         ArrayList newArrayList = Lists.newArrayList();

         for (Entry entry : this.commandMap.entrySet()) {
            if (CommandBase.doesStringStartWith(str, (String)entry.getKey()) && ((ICommand)entry.getValue()).canCommandSenderUseCommand(sender)) {
               newArrayList.add(entry.getKey());
            }
         }

         return newArrayList;
      } else {
         if (split.length > 1) {
            ICommand command = this.commandMap.get(str);
            if (command != null && command.canCommandSenderUseCommand(sender)) {
               return command.addTabCompletionOptions(sender, dropFirstString(split), pos);
            }
         }

         return null;
      }
   }

   @Override
   public List<ICommand> getPossibleCommands(ICommandSender sender) {
      ArrayList newArrayList = Lists.newArrayList();

      for (ICommand command : this.commandSet) {
         if (command.canCommandSenderUseCommand(sender)) {
            newArrayList.add(command);
         }
      }

      return newArrayList;
   }

   @Override
   public Map<String, ICommand> getCommands() {
      return this.commandMap;
   }

   public int getUsernameIndex(ICommand command, String[] args) {
      if (command == null) {
         return -1;
      }

      for (int i = 0; i < args.length; i++) {
         if (command.isUsernameIndex(args, i) && PlayerSelector.matchesMultiplePlayers(args[i])) {
            return i;
         }
      }

      return -1;
   }
}
