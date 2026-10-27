package net.minecraft.command;

import com.google.common.base.Functions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ResourceLocation;

public abstract class CommandBase implements ICommand {
   public static IAdminCommand theAdmin;

   public int getRequiredPermissionLevel() {
      return 4;
   }

   @Override
   public List<String> getCommandAliases() {
      return Collections.emptyList();
   }

   @Override
   public boolean canCommandSenderUseCommand(ICommandSender sender) {
      return sender.canCommandSenderUseCommand(this.getRequiredPermissionLevel(), this.getCommandName());
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return null;
   }

   public static int parseInt(String input) throws NumberInvalidException {
      try {
         return Integer.parseInt(input);
      } catch (NumberFormatException e) {
         throw new NumberInvalidException("commands.generic.num.invalid", input);
      }
   }

   public static int parseInt(String input, int min) throws NumberInvalidException {
      return parseInt(input, min, Integer.MAX_VALUE);
   }

   public static int parseInt(String input, int min, int max) throws NumberInvalidException {
      int parseInt2 = parseInt(input);
      if (parseInt2 < min) {
         throw new NumberInvalidException("commands.generic.num.tooSmall", parseInt2, min);
      } else if (parseInt2 > max) {
         throw new NumberInvalidException("commands.generic.num.tooBig", parseInt2, max);
      } else {
         return parseInt2;
      }
   }

   public static long parseLong(String input) throws NumberInvalidException {
      try {
         return Long.parseLong(input);
      } catch (NumberFormatException e) {
         throw new NumberInvalidException("commands.generic.num.invalid", input);
      }
   }

   public static long parseLong(String input, long min, long max) throws NumberInvalidException {
      long parseLong2 = parseLong(input);
      if (parseLong2 < min) {
         throw new NumberInvalidException("commands.generic.num.tooSmall", parseLong2, min);
      } else if (parseLong2 > max) {
         throw new NumberInvalidException("commands.generic.num.tooBig", parseLong2, max);
      } else {
         return parseLong2;
      }
   }

   public static BlockPos parseBlockPos(ICommandSender sender, String[] args, int startIndex, boolean centerBlock) throws NumberInvalidException {
      BlockPos position = sender.getPosition();
      return new BlockPos(
         parseDouble(position.getX(), args[startIndex], -30000000, 30000000, centerBlock),
         parseDouble(position.getY(), args[startIndex + 1], 0, 256, false),
         parseDouble(position.getZ(), args[startIndex + 2], -30000000, 30000000, centerBlock)
      );
   }

   public static double parseDouble(String input) throws NumberInvalidException {
      try {
         double parseDouble = Double.parseDouble(input);
         if (!Doubles.isFinite(parseDouble)) {
            throw new NumberInvalidException("commands.generic.num.invalid", input);
         } else {
            return parseDouble;
         }
      } catch (NumberFormatException e) {
         throw new NumberInvalidException("commands.generic.num.invalid", input);
      }
   }

   public static double parseDouble(String input, double min) throws NumberInvalidException {
      return parseDouble(input, min, Double.MAX_VALUE);
   }

   public static double parseDouble(String input, double min, double max) throws NumberInvalidException {
      double parseDouble2 = parseDouble(input);
      if (parseDouble2 < min) {
         throw new NumberInvalidException("commands.generic.double.tooSmall", parseDouble2, min);
      } else if (parseDouble2 > max) {
         throw new NumberInvalidException("commands.generic.double.tooBig", parseDouble2, max);
      } else {
         return parseDouble2;
      }
   }

   public static boolean parseBoolean(String input) throws CommandException {
      if (input.equals("true") || input.equals("1")) {
         return true;
      } else if (!input.equals("false") && !input.equals("0")) {
         throw new CommandException("commands.generic.boolean.invalid", input);
      } else {
         return false;
      }
   }

   public static EntityPlayerMP getCommandSenderAsPlayer(ICommandSender sender) throws PlayerNotFoundException {
      if (sender instanceof EntityPlayerMP) {
         return (EntityPlayerMP)sender;
      } else {
         throw new PlayerNotFoundException("You must specify which player you wish to perform this action on.");
      }
   }

   public static EntityPlayerMP getPlayer(ICommandSender sender, String username) throws PlayerNotFoundException {
      EntityPlayerMP matchOnePlayer = PlayerSelector.matchOnePlayer(sender, username);
      if (matchOnePlayer == null) {
         try {
            matchOnePlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerByUUID(UUID.fromString(username));
         } catch (IllegalArgumentException e) {
         }
      }

      if (matchOnePlayer == null) {
         matchOnePlayer = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(username);
      }

      if (matchOnePlayer == null) {
         throw new PlayerNotFoundException();
      } else {
         return matchOnePlayer;
      }
   }

   public static Entity getEntity(ICommandSender commandSender, String str) throws EntityNotFoundException {
      return getEntity(commandSender, str, Entity.class);
   }

   public static <T extends Entity> T getEntity(ICommandSender commandSender, String str, Class<? extends T> clazz) throws EntityNotFoundException {
      Entity matchOneEntity = PlayerSelector.matchOneEntity(commandSender, str, clazz);
      MinecraftServer server = MinecraftServer.getServer();
      if (matchOneEntity == null) {
         matchOneEntity = server.getConfigurationManager().getPlayerByUsername(str);
      }

      if (matchOneEntity == null) {
         try {
            UUID fromString = UUID.fromString(str);
            matchOneEntity = server.getEntityFromUuid(fromString);
            if (matchOneEntity == null) {
               matchOneEntity = server.getConfigurationManager().getPlayerByUUID(fromString);
            }
         } catch (IllegalArgumentException e) {
            throw new EntityNotFoundException("commands.generic.entity.invalidUuid");
         }
      }

      if (matchOneEntity != null && clazz.isAssignableFrom(matchOneEntity.getClass())) {
         return (T)matchOneEntity;
      } else {
         throw new EntityNotFoundException();
      }
   }

   public static List<Entity> getEntities(ICommandSender commandSender, String str) throws EntityNotFoundException {
      return PlayerSelector.hasArguments(str)
         ? PlayerSelector.matchEntities(commandSender, str, Entity.class)
         : Lists.newArrayList(new Entity[]{getEntity(commandSender, str)});
   }

   public static String getPlayerName(ICommandSender sender, String query) throws PlayerNotFoundException {
      try {
         return getPlayer(sender, query).getName();
      } catch (PlayerNotFoundException e) {
         if (PlayerSelector.hasArguments(query)) {
            throw e;
         } else {
            return query;
         }
      }
   }

   public static String getEntityName(ICommandSender commandSender, String str) throws EntityNotFoundException {
      try {
         return getPlayer(commandSender, str).getName();
      } catch (PlayerNotFoundException e) {
         try {
            return getEntity(commandSender, str).getUniqueID().toString();
         } catch (EntityNotFoundException e2) {
            if (PlayerSelector.hasArguments(str)) {
               throw e2;
            } else {
               return str;
            }
         }
      }
   }

   public static IChatComponent getChatComponentFromNthArg(ICommandSender sender, String[] args, int n) throws CommandException, PlayerNotFoundException {
      return getChatComponentFromNthArg(sender, args, n, false);
   }

   public static IChatComponent getChatComponentFromNthArg(ICommandSender sender, String[] args, int index, boolean flag) throws PlayerNotFoundException {
      ChatComponentText chatComponentText = new ChatComponentText("");

      for (int index2 = index; index2 < args.length; index2++) {
         if (index2 > index) {
            chatComponentText.appendText(" ");
         }

         IChatComponent chatComponent = new ChatComponentText(args[index2]);
         if (flag) {
            IChatComponent matchEntitiesToChatComponent = PlayerSelector.matchEntitiesToChatComponent(sender, args[index2]);
            if (matchEntitiesToChatComponent == null) {
               if (PlayerSelector.hasArguments(args[index2])) {
                  throw new PlayerNotFoundException();
               }
            } else {
               chatComponent = matchEntitiesToChatComponent;
            }
         }

         chatComponentText.appendSibling(chatComponent);
      }

      return chatComponentText;
   }

   public static String buildString(String[] args, int startPos) {
      StringBuilder sb = new StringBuilder();

      for (int startPos2 = startPos; startPos2 < args.length; startPos2++) {
         if (startPos2 > startPos) {
            sb.append(" ");
         }

         String str = args[startPos2];
         sb.append(str);
      }

      return sb.toString();
   }

   public static CommandBase.CoordinateArg parseCoordinate(double base, String str, boolean centerBlock) throws NumberInvalidException {
      return parseCoordinate(base, str, -30000000, 30000000, centerBlock);
   }

   public static CommandBase.CoordinateArg parseCoordinate(double d, String str, int min, int max, boolean centerBlock) throws NumberInvalidException {
      boolean startsWith = str.startsWith("~");
      if (startsWith && Double.isNaN(d)) {
         throw new NumberInvalidException("commands.generic.num.invalid", d);
      }

      double d2 = 0.0;
      if (!startsWith || str.length() > 1) {
         boolean flag = str.contains(".");
         if (startsWith) {
            str = str.substring(1);
         }

         d2 += parseDouble(str);
         if (!flag && !startsWith && centerBlock) {
            d2 += 0.5;
         }
      }

      if (min != 0 || max != 0) {
         if (d2 < min) {
            throw new NumberInvalidException("commands.generic.double.tooSmall", d2, min);
         }

         if (d2 > max) {
            throw new NumberInvalidException("commands.generic.double.tooBig", d2, max);
         }
      }

      return new CommandBase.CoordinateArg(d2 + (startsWith ? d : 0.0), d2, startsWith);
   }

   public static double parseDouble(double base, String input, boolean centerBlock) throws NumberInvalidException {
      return parseDouble(base, input, -30000000, 30000000, centerBlock);
   }

   public static double parseDouble(double base, String input, int min, int max, boolean centerBlock) throws NumberInvalidException {
      boolean startsWith = input.startsWith("~");
      if (startsWith && Double.isNaN(base)) {
         throw new NumberInvalidException("commands.generic.num.invalid", base);
      }

      double d = startsWith ? base : 0.0;
      if (!startsWith || input.length() > 1) {
         boolean flag = input.contains(".");
         if (startsWith) {
            input = input.substring(1);
         }

         d += parseDouble(input);
         if (!flag && !startsWith && centerBlock) {
            d += 0.5;
         }
      }

      if (min != 0 || max != 0) {
         if (d < min) {
            throw new NumberInvalidException("commands.generic.double.tooSmall", d, min);
         }

         if (d > max) {
            throw new NumberInvalidException("commands.generic.double.tooBig", d, max);
         }
      }

      return d;
   }

   public static Item getItemByText(ICommandSender sender, String id) throws NumberInvalidException {
      ResourceLocation resourceLocation = new ResourceLocation(id);
      Item object = Item.itemRegistry.getObject(resourceLocation);
      if (object == null) {
         throw new NumberInvalidException("commands.give.item.notFound", resourceLocation);
      } else {
         return object;
      }
   }

   public static Block getBlockByText(ICommandSender sender, String id) throws NumberInvalidException {
      ResourceLocation resourceLocation = new ResourceLocation(id);
      if (!Block.blockRegistry.containsKey(resourceLocation)) {
         throw new NumberInvalidException("commands.give.block.notFound", resourceLocation);
      } else {
         Block object = Block.blockRegistry.getObject(resourceLocation);
         if (object == null) {
            throw new NumberInvalidException("commands.give.block.notFound", resourceLocation);
         } else {
            return object;
         }
      }
   }

   public static String joinNiceString(Object[] elements) {
      StringBuilder sb = new StringBuilder();

      for (int i = 0; i < elements.length; i++) {
         String str = elements[i].toString();
         if (i > 0) {
            if (i == elements.length - 1) {
               sb.append(" and ");
            } else {
               sb.append(", ");
            }
         }

         sb.append(str);
      }

      return sb.toString();
   }

   public static IChatComponent join(List<IChatComponent> components) {
      ChatComponentText chatComponentText = new ChatComponentText("");

      for (int i = 0; i < components.size(); i++) {
         if (i > 0) {
            if (i == components.size() - 1) {
               chatComponentText.appendText(" and ");
            } else if (i > 0) {
               chatComponentText.appendText(", ");
            }
         }

         chatComponentText.appendSibling((IChatComponent)components.get(i));
      }

      return chatComponentText;
   }

   public static String joinNiceStringFromCollection(Collection<String> strings) {
      return joinNiceString(strings.toArray(new String[strings.size()]));
   }

   public static List<String> findList(String[] strs, int n, BlockPos blockPos) {
      if (blockPos == null) {
         return null;
      }

      int n2 = strs.length - 1;
      String str;
      if (n2 == n) {
         str = Integer.toString(blockPos.getX());
      } else if (n2 == n + 1) {
         str = Integer.toString(blockPos.getY());
      } else {
         if (n2 != n + 2) {
            return null;
         }

         str = Integer.toString(blockPos.getZ());
      }

      return Lists.newArrayList(new String[]{str});
   }

   public static List<String> findList2(String[] strs, int n, BlockPos blockPos) {
      if (blockPos == null) {
         return null;
      }

      int n2 = strs.length - 1;
      String str;
      if (n2 == n) {
         str = Integer.toString(blockPos.getX());
      } else {
         if (n2 != n + 1) {
            return null;
         }

         str = Integer.toString(blockPos.getZ());
      }

      return Lists.newArrayList(new String[]{str});
   }

   public static boolean doesStringStartWith(String original, String region) {
      return region.regionMatches(true, 0, original, 0, original.length());
   }

   public static List<String> getListOfStringsMatchingLastWord(String[] args, String... possibilities) {
      return getListOfStringsMatchingLastWord(args, Arrays.asList(possibilities));
   }

   public static List<String> getListOfStringsMatchingLastWord(String[] strs, Collection<?> collection) {
      String str = strs[strs.length - 1];
      ArrayList newArrayList = Lists.newArrayList();
      if (!collection.isEmpty()) {
         for (String str2 : Iterables.transform(collection, Functions.toStringFunction())) {
            if (doesStringStartWith(str, str2)) {
               newArrayList.add(str2);
            }
         }

         if (newArrayList.isEmpty()) {
            for (Object obj : collection) {
               if (obj instanceof ResourceLocation && doesStringStartWith(str, ((ResourceLocation)obj).getResourcePath())) {
                  newArrayList.add(String.valueOf(obj));
               }
            }
         }
      }

      return newArrayList;
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return false;
   }

   public static void notifyOperators(ICommandSender sender, ICommand command, String msgFormat, Object... msgParams) {
      notifyOperators(sender, command, 0, msgFormat, msgParams);
   }

   public static void notifyOperators(ICommandSender sender, ICommand command, int n, String msgFormat, Object... msgParams) {
      if (theAdmin != null) {
         theAdmin.notifyOperators(sender, command, n, msgFormat, msgParams);
      }
   }

   public static void setAdminCommander(IAdminCommand command) {
      theAdmin = command;
   }

   public int compareTo(ICommand command) {
      return this.getCommandName().compareTo(command.getCommandName());
   }

   public static class CoordinateArg {
      public double result;
      public double amount;
      public boolean relative;

      public CoordinateArg(double d, double d2, boolean flag) {
         this.result = d;
         this.amount = d2;
         this.relative = flag;
      }

      public double getResult() {
         return this.result;
      }

      public double getAmount() {
         return this.amount;
      }

      public boolean isRelative() {
         return this.relative;
      }
   }
}
