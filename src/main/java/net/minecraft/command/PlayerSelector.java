package net.minecraft.command;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings.GameType;

public class PlayerSelector {
   public static Pattern tokenPattern = Pattern.compile("^@([pare])(?:\\[([\\w=,!-]*)\\])?$");
   public static Pattern intListPattern = Pattern.compile("\\G([-!]?[\\w-]*)(?:$|,)");
   public static Pattern keyValueListPattern = Pattern.compile("\\G(\\w+)=([-!]?[\\w-]*)(?:$|,)");
   public static Set<String> WORLD_BINDING_ARGS = Sets.newHashSet(new String[]{"x", "y", "z", "dx", "dy", "dz", "rm", "r"});

   public static EntityPlayerMP matchOnePlayer(ICommandSender sender, String token) {
      return matchOneEntity(sender, token, EntityPlayerMP.class);
   }

   public static <T extends Entity> T matchOneEntity(ICommandSender sender, String token, Class<? extends T> targetClass) {
      List matchEntities2 = matchEntities(sender, token, targetClass);
      return (T)(matchEntities2.size() == 1 ? matchEntities2.get(0) : null);
   }

   public static IChatComponent matchEntitiesToChatComponent(ICommandSender sender, String token) {
      List matchEntities2 = matchEntities(sender, token, Entity.class);
      if (matchEntities2.isEmpty()) {
         return null;
      }

      ArrayList newArrayList = Lists.newArrayList();

      for (Entity entity : (Iterable<Entity>)matchEntities2) {
         newArrayList.add(entity.getDisplayName());
      }

      return CommandBase.join(newArrayList);
   }

   public static <T extends Entity> List<T> matchEntities(ICommandSender sender, String token, Class<? extends T> targetClass) {
      Matcher matcher = tokenPattern.matcher(token);
      if (matcher.matches() && sender.canCommandSenderUseCommand(1, "@")) {
         Map argumentMap = getArgumentMap(matcher.group(2));
         if (!isEntityTypeValid(sender, argumentMap)) {
            return Collections.emptyList();
         }

         String group = matcher.group(1);
         BlockPos createBlockPos2 = createBlockPos(argumentMap, sender.getPosition());
         List worlds = getWorlds(sender, argumentMap);
         ArrayList newArrayList = Lists.newArrayList();

         for (World world : (Iterable<World>)worlds) {
            if (world != null) {
               ArrayList newArrayList2 = Lists.newArrayList();
               newArrayList2.addAll(findList(argumentMap, group));
               newArrayList2.addAll(getXpLevelPredicates(argumentMap));
               newArrayList2.addAll(getGamemodePredicates(argumentMap));
               newArrayList2.addAll(getTeamPredicates(argumentMap));
               newArrayList2.addAll(getScorePredicates(argumentMap));
               newArrayList2.addAll(getNamePredicates(argumentMap));
               newArrayList2.addAll(findList(argumentMap, createBlockPos2));
               newArrayList2.addAll(getRotationsPredicates(argumentMap));
               newArrayList.addAll(filterResults(argumentMap, targetClass, newArrayList2, group, world, createBlockPos2));
            }
         }

         return findList(newArrayList, argumentMap, sender, targetClass, group, createBlockPos2);
      } else {
         return Collections.emptyList();
      }
   }

   public static List<World> getWorlds(ICommandSender sender, Map<String, String> argumentMap) {
      ArrayList newArrayList = Lists.newArrayList();
      if (checkMap(argumentMap)) {
         newArrayList.add(sender.getEntityWorld());
      } else {
         Collections.addAll(newArrayList, MinecraftServer.getServer().worldServers);
      }

      return newArrayList;
   }

   public static <T extends Entity> boolean isEntityTypeValid(ICommandSender commandSender, Map<String, String> params) {
      String str = get(params, "type");
      str = str != null && str.startsWith("!") ? str.substring(1) : str;
      if (str != null && !EntityList.isStringValidEntityName(str)) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.generic.entity.invalidType", str);
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         commandSender.addChatMessage(chatComponentTranslation);
         return false;
      } else {
         return true;
      }
   }

   public static List<Predicate<Entity>> findList(Map<String, String> map, String str) {
      ArrayList newArrayList = Lists.newArrayList();
      String str2 = get(map, "type");
      final boolean flag = str2 != null && str2.startsWith("!");
      if (flag) {
         str2 = str2.substring(1);
      }

      boolean flag2 = !str.equals("e");
      boolean flag3 = str.equals("r") && str2 != null;
      if ((str2 == null || !str.equals("e")) && !flag3) {
         if (flag2) {
            newArrayList.add(new Predicate<Entity>() {
               public boolean apply(Entity entity) {
                  return entity instanceof EntityPlayer;
               }
            });
         }
      } else {
         final String str3 = str2;
         newArrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               return EntityList.isStringEntityName(entity, str3) != flag;
            }
         });
      }

      return newArrayList;
   }

   public static List<Predicate<Entity>> getXpLevelPredicates(Map<String, String> map) {
      ArrayList newArrayList = Lists.newArrayList();
      final int parseIntWithDefault2 = parseIntWithDefault(map, "lm", -1);
      final int parseIntWithDefault3 = parseIntWithDefault(map, "l", -1);
      if (parseIntWithDefault2 > -1 || parseIntWithDefault3 > -1) {
         newArrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               if (!(entity instanceof EntityPlayerMP)) {
                  return false;
               }

               EntityPlayerMP entityPlayerMP = (EntityPlayerMP)entity;
               return (parseIntWithDefault2 <= -1 || entityPlayerMP.experienceLevel >= parseIntWithDefault2) && (parseIntWithDefault3 <= -1 || entityPlayerMP.experienceLevel <= parseIntWithDefault3);
            }
         });
      }

      return newArrayList;
   }

   public static List<Predicate<Entity>> getGamemodePredicates(Map<String, String> map) {
      ArrayList newArrayList = Lists.newArrayList();
      final int parseIntWithDefault2 = parseIntWithDefault(map, "m", GameType.NOT_SET.getID());
      if (parseIntWithDefault2 != GameType.NOT_SET.getID()) {
         newArrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               if (!(entity instanceof EntityPlayerMP)) {
                  return false;
               }

               EntityPlayerMP entityPlayerMP = (EntityPlayerMP)entity;
               return entityPlayerMP.theItemInWorldManager.getGameType().getID() == parseIntWithDefault2;
            }
         });
      }

      return newArrayList;
   }

   public static List<Predicate<Entity>> getTeamPredicates(Map<String, String> map) {
      ArrayList newArrayList = Lists.newArrayList();
      String str = get(map, "team");
      final boolean flag = str != null && str.startsWith("!");
      if (flag) {
         str = str.substring(1);
      }

      if (str != null) {
         final String str2 = str;
         newArrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               if (!(entity instanceof EntityLivingBase)) {
                  return false;
               }

               EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
               Team team = entityLivingBase.getTeam();
               String str3 = team == null ? "" : team.getRegisteredName();
               return str3.equals(str2) != flag;
            }
         });
      }

      return newArrayList;
   }

   public static List<Predicate<Entity>> getScorePredicates(Map<String, String> map) {
      ArrayList newArrayList = Lists.newArrayList();
      final Map findMap2 = findMap(map);
      if (findMap2 != null && findMap2.size() > 0) {
         newArrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               Scoreboard scoreboard = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();

               for (Entry entry : (Iterable<Entry>)findMap2.entrySet()) {
                  String key = (String)entry.getKey();
                  boolean flag = false;
                  if (key.endsWith("_min") && key.length() > 4) {
                     flag = true;
                     key = key.substring(0, key.length() - 4);
                  }

                  ScoreObjective objective = scoreboard.getObjective(key);
                  if (objective == null) {
                     return false;
                  }

                  String str = entity instanceof EntityPlayerMP ? entity.getName() : entity.getUniqueID().toString();
                  if (!scoreboard.entityHasObjective(str, objective)) {
                     return false;
                  }

                  Score valueFromObjective = scoreboard.getValueFromObjective(str, objective);
                  int scorePoints = valueFromObjective.getScorePoints();
                  if (scorePoints < (Integer)entry.getValue() && flag) {
                     return false;
                  }

                  if (scorePoints > (Integer)entry.getValue() && !flag) {
                     return false;
                  }
               }

               return true;
            }
         });
      }

      return newArrayList;
   }

   public static List<Predicate<Entity>> getNamePredicates(Map<String, String> map) {
      ArrayList newArrayList = Lists.newArrayList();
      String str = get(map, "name");
      final boolean flag = str != null && str.startsWith("!");
      if (flag) {
         str = str.substring(1);
      }

      if (str != null) {
         final String str2 = str;
         newArrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               return entity.getName().equals(str2) != flag;
            }
         });
      }

      return newArrayList;
   }

   public static List<Predicate<Entity>> findList(Map<String, String> map, final BlockPos blockPos) {
      ArrayList newArrayList = Lists.newArrayList();
      final int parseIntWithDefault2 = parseIntWithDefault(map, "rm", -1);
      final int parseIntWithDefault3 = parseIntWithDefault(map, "r", -1);
      if (blockPos != null && (parseIntWithDefault2 >= 0 || parseIntWithDefault3 >= 0)) {
         final int n = parseIntWithDefault2 * parseIntWithDefault2;
         final int n2 = parseIntWithDefault3 * parseIntWithDefault3;
         newArrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               int distanceSqToCenter = (int)entity.getDistanceSqToCenter(blockPos);
               return (parseIntWithDefault2 < 0 || distanceSqToCenter >= n) && (parseIntWithDefault3 < 0 || distanceSqToCenter <= n2);
            }
         });
      }

      return newArrayList;
   }

   public static List<Predicate<Entity>> getRotationsPredicates(Map<String, String> map) {
      ArrayList newArrayList = Lists.newArrayList();
      if (map.containsKey("rym") || map.containsKey("ry")) {
         final int computeInt2 = computeInt(parseIntWithDefault(map, "rym", 0));
         final int computeInt3 = computeInt(parseIntWithDefault(map, "ry", 359));
         newArrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               int computeInt = PlayerSelector.computeInt((int)Math.floor(entity.rotationYaw));
               return computeInt2 > computeInt3 ? computeInt >= computeInt2 || computeInt <= computeInt3 : computeInt >= computeInt2 && computeInt <= computeInt3;
            }
         });
      }

      if (map.containsKey("rxm") || map.containsKey("rx")) {
         final int computeInt2 = computeInt(parseIntWithDefault(map, "rxm", 0));
         final int computeInt3 = computeInt(parseIntWithDefault(map, "rx", 359));
         newArrayList.add(new Predicate<Entity>() {
            public boolean apply(Entity entity) {
               int computeInt = PlayerSelector.computeInt((int)Math.floor(entity.rotationPitch));
               return computeInt2 > computeInt3 ? computeInt >= computeInt2 || computeInt <= computeInt3 : computeInt >= computeInt2 && computeInt <= computeInt3;
            }
         });
      }

      return newArrayList;
   }

   public static <T extends Entity> List<T> filterResults(
      Map<String, String> params, Class<? extends T> entityClass, List<Predicate<Entity>> inputList, String type, World worldIn, BlockPos position
   ) {
      ArrayList newArrayList = Lists.newArrayList();
      String str = get(params, "type");
      str = str != null && str.startsWith("!") ? str.substring(1) : str;
      boolean flag = !type.equals("e");
      boolean flag2 = type.equals("r") && str != null;
      int parseIntWithDefault2 = parseIntWithDefault(params, "dx", 0);
      int parseIntWithDefault3 = parseIntWithDefault(params, "dy", 0);
      int parseIntWithDefault4 = parseIntWithDefault(params, "dz", 0);
      int parseIntWithDefault5 = parseIntWithDefault(params, "r", -1);
      Predicate and = Predicates.and(inputList);
      Predicate and2 = Predicates.and(EntitySelectors.selectAnything, and);
      if (position != null) {
         int size = worldIn.playerEntities.size();
         int size2 = worldIn.loadedEntityList.size();
         boolean flag3 = size < size2 / 16;
         if (params.containsKey("dx") || params.containsKey("dy") || params.containsKey("dz")) {
            final AxisAlignedBB createAxisAlignedBB2 = createAxisAlignedBB(position, parseIntWithDefault2, parseIntWithDefault3, parseIntWithDefault4);
            if (flag && flag3 && !flag2) {
               Predicate predicate = new Predicate<Entity>() {
                  public boolean apply(Entity entity) {
                     return entity.posX >= createAxisAlignedBB2.minX && entity.posY >= createAxisAlignedBB2.minY && entity.posZ >= createAxisAlignedBB2.minZ
                        ? entity.posX < createAxisAlignedBB2.maxX && entity.posY < createAxisAlignedBB2.maxY && entity.posZ < createAxisAlignedBB2.maxZ
                        : false;
                  }
               };
               newArrayList.addAll(worldIn.getPlayers(entityClass, Predicates.and(and2, predicate)));
            } else {
               newArrayList.addAll(worldIn.getEntitiesWithinAABB(entityClass, createAxisAlignedBB2, and2));
            }
         } else if (parseIntWithDefault5 >= 0) {
            AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
               position.getX() - parseIntWithDefault5,
               position.getY() - parseIntWithDefault5,
               position.getZ() - parseIntWithDefault5,
               position.getX() + parseIntWithDefault5 + 1,
               position.getY() + parseIntWithDefault5 + 1,
               position.getZ() + parseIntWithDefault5 + 1
            );
            if (flag && flag3 && !flag2) {
               newArrayList.addAll(worldIn.getPlayers(entityClass, and2));
            } else {
               newArrayList.addAll(worldIn.getEntitiesWithinAABB(entityClass, axisAlignedBB, and2));
            }
         } else if (type.equals("a")) {
            newArrayList.addAll(worldIn.getPlayers(entityClass, and));
         } else if (type.equals("p") || type.equals("r") && !flag2) {
            newArrayList.addAll(worldIn.getPlayers(entityClass, and2));
         } else {
            newArrayList.addAll(worldIn.getEntities(entityClass, and2));
         }
      } else if (type.equals("a")) {
         newArrayList.addAll(worldIn.getPlayers(entityClass, and));
      } else if (type.equals("p") || type.equals("r") && !flag2) {
         newArrayList.addAll(worldIn.getPlayers(entityClass, and2));
      } else {
         newArrayList.addAll(worldIn.getEntities(entityClass, and2));
      }

      return newArrayList;
   }

   public static <T extends Entity> List<T> findList(
      List<T> list, Map<String, String> map, ICommandSender commandSender, Class<? extends T> clazz, String str, final BlockPos blockPos
   ) {
      int parseIntWithDefault2 = parseIntWithDefault(map, "c", !str.equals("a") && !str.equals("e") ? 1 : 0);
      if (!str.equals("p") && !str.equals("a") && !str.equals("e")) {
         if (str.equals("r")) {
            Collections.shuffle(list);
         }
      } else if (blockPos != null) {
         Collections.sort(list, new Comparator<Entity>() {
            public int compare(Entity entity, Entity entity2) {
               return ComparisonChain.start().compare(entity.getDistanceSq(blockPos), entity2.getDistanceSq(blockPos)).result();
            }
         });
      }

      Entity commandSenderEntity = commandSender.getCommandSenderEntity();
      if (commandSenderEntity != null && clazz.isAssignableFrom(commandSenderEntity.getClass()) && parseIntWithDefault2 == 1 && list.contains(commandSenderEntity) && !"r".equals(str)) {
         list = (List)Lists.newArrayList(new Entity[]{commandSenderEntity});
      }

      if (parseIntWithDefault2 != 0) {
         if (parseIntWithDefault2 < 0) {
            Collections.reverse(list);
         }

         list = list.subList(0, Math.min(Math.abs(parseIntWithDefault2), list.size()));
      }

      return list;
   }

   public static AxisAlignedBB createAxisAlignedBB(BlockPos blockPos, int n, int n2, int n3) {
      boolean flag = n < 0;
      boolean flag2 = n2 < 0;
      boolean flag3 = n3 < 0;
      int n4 = blockPos.getX() + (flag ? n : 0);
      int n5 = blockPos.getY() + (flag2 ? n2 : 0);
      int n6 = blockPos.getZ() + (flag3 ? n3 : 0);
      int n7 = blockPos.getX() + (flag ? 0 : n) + 1;
      int n8 = blockPos.getY() + (flag2 ? 0 : n2) + 1;
      int n9 = blockPos.getZ() + (flag3 ? 0 : n3) + 1;
      return new AxisAlignedBB(n4, n5, n6, n7, n8, n9);
   }

   public static int computeInt(int n) {
      n %= 360;
      if (n >= 160) {
         n -= 360;
      }

      if (n < 0) {
         n += 360;
      }

      return n;
   }

   public static BlockPos createBlockPos(Map<String, String> map, BlockPos blockPos) {
      return new BlockPos(parseIntWithDefault(map, "x", blockPos.getX()), parseIntWithDefault(map, "y", blockPos.getY()), parseIntWithDefault(map, "z", blockPos.getZ()));
   }

   public static boolean checkMap(Map<String, String> map) {
      for (String str : WORLD_BINDING_ARGS) {
         if (map.containsKey(str)) {
            return true;
         }
      }

      return false;
   }

   public static int parseIntWithDefault(Map<String, String> map, String str, int n) {
      return map.containsKey(str) ? MathHelper.parseIntWithDefault((String)map.get(str), n) : n;
   }

   public static String get(Map<String, String> map, String str) {
      return (String)map.get(str);
   }

   public static Map<String, Integer> findMap(Map<String, String> map) {
      HashMap newHashMap = Maps.newHashMap();

      for (String str : map.keySet()) {
         if (str.startsWith("score_") && str.length() > "score_".length()) {
            newHashMap.put(str.substring("score_".length()), MathHelper.parseIntWithDefault((String)map.get(str), 1));
         }
      }

      return newHashMap;
   }

   public static boolean matchesMultiplePlayers(String str) {
      Matcher matcher = tokenPattern.matcher(str);
      if (!matcher.matches()) {
         return false;
      }

      Map argumentMap = getArgumentMap(matcher.group(2));
      String group = matcher.group(1);
      int n = !"a".equals(group) && !"e".equals(group) ? 1 : 0;
      return parseIntWithDefault(argumentMap, "c", n) != 1;
   }

   public static boolean hasArguments(String str) {
      return tokenPattern.matcher(str).matches();
   }

   public static Map<String, String> getArgumentMap(String argumentString) {
      HashMap newHashMap = Maps.newHashMap();
      if (argumentString == null) {
         return newHashMap;
      }

      int n = 0;
      int n2 = -1;

      for (Matcher matcher = intListPattern.matcher(argumentString); matcher.find(); n2 = matcher.end()) {
         String str = null;
         switch (n++) {
            case 0:
               str = "x";
               break;
            case 1:
               str = "y";
               break;
            case 2:
               str = "z";
               break;
            case 3:
               str = "r";
         }

         if (str != null && matcher.group(1).length() > 0) {
            newHashMap.put(str, matcher.group(1));
         }
      }

      if (n2 < argumentString.length()) {
         Matcher matcher = keyValueListPattern.matcher(n2 == -1 ? argumentString : argumentString.substring(n2));

         while (matcher.find()) {
            newHashMap.put(matcher.group(1), matcher.group(2));
         }
      }

      return newHashMap;
   }
}
