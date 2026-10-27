package net.minecraft.command.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team.EnumVisible;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;

public class CommandScoreboard extends CommandBase {
   @Override
   public String getCommandName() {
      return "scoreboard";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.scoreboard.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (!this.checkICommandSender(sender, args)) {
         if (args.length < 1) {
            throw new WrongUsageException("commands.scoreboard.usage");
         }

         if (args[0].equalsIgnoreCase("objectives")) {
            if (args.length == 1) {
               throw new WrongUsageException("commands.scoreboard.objectives.usage");
            }

            if (args[1].equalsIgnoreCase("list")) {
               this.listObjectives(sender);
            } else if (args[1].equalsIgnoreCase("add")) {
               if (args.length < 4) {
                  throw new WrongUsageException("commands.scoreboard.objectives.add.usage");
               }

               this.addObjective(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("remove")) {
               if (args.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.objectives.remove.usage");
               }

               this.removeObjective(sender, args[2]);
            } else {
               if (!args[1].equalsIgnoreCase("setdisplay")) {
                  throw new WrongUsageException("commands.scoreboard.objectives.usage");
               }

               if (args.length != 3 && args.length != 4) {
                  throw new WrongUsageException("commands.scoreboard.objectives.setdisplay.usage");
               }

               this.setObjectiveDisplay(sender, args, 2);
            }
         } else if (args[0].equalsIgnoreCase("players")) {
            if (args.length == 1) {
               throw new WrongUsageException("commands.scoreboard.players.usage");
            }

            if (args[1].equalsIgnoreCase("list")) {
               if (args.length > 3) {
                  throw new WrongUsageException("commands.scoreboard.players.list.usage");
               }

               this.listPlayers(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("add")) {
               if (args.length < 5) {
                  throw new WrongUsageException("commands.scoreboard.players.add.usage");
               }

               this.setPlayer(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("remove")) {
               if (args.length < 5) {
                  throw new WrongUsageException("commands.scoreboard.players.remove.usage");
               }

               this.setPlayer(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("set")) {
               if (args.length < 5) {
                  throw new WrongUsageException("commands.scoreboard.players.set.usage");
               }

               this.setPlayer(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("reset")) {
               if (args.length != 3 && args.length != 4) {
                  throw new WrongUsageException("commands.scoreboard.players.reset.usage");
               }

               this.resetPlayers(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("enable")) {
               if (args.length != 4) {
                  throw new WrongUsageException("commands.scoreboard.players.enable.usage");
               }

               this.handleICommandSender(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("test")) {
               if (args.length != 5 && args.length != 6) {
                  throw new WrongUsageException("commands.scoreboard.players.test.usage");
               }

               this.handleICommandSender2(sender, args, 2);
            } else {
               if (!args[1].equalsIgnoreCase("operation")) {
                  throw new WrongUsageException("commands.scoreboard.players.usage");
               }

               if (args.length != 7) {
                  throw new WrongUsageException("commands.scoreboard.players.operation.usage");
               }

               this.handleICommandSender3(sender, args, 2);
            }
         } else {
            if (!args[0].equalsIgnoreCase("teams")) {
               throw new WrongUsageException("commands.scoreboard.usage");
            }

            if (args.length == 1) {
               throw new WrongUsageException("commands.scoreboard.teams.usage");
            }

            if (args[1].equalsIgnoreCase("list")) {
               if (args.length > 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.list.usage");
               }

               this.listTeams(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("add")) {
               if (args.length < 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.add.usage");
               }

               this.addTeam(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("remove")) {
               if (args.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.remove.usage");
               }

               this.removeTeam(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("empty")) {
               if (args.length != 3) {
                  throw new WrongUsageException("commands.scoreboard.teams.empty.usage");
               }

               this.emptyTeam(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("join")) {
               if (args.length < 4 && (args.length != 3 || !(sender instanceof EntityPlayer))) {
                  throw new WrongUsageException("commands.scoreboard.teams.join.usage");
               }

               this.joinTeam(sender, args, 2);
            } else if (args[1].equalsIgnoreCase("leave")) {
               if (args.length < 3 && !(sender instanceof EntityPlayer)) {
                  throw new WrongUsageException("commands.scoreboard.teams.leave.usage");
               }

               this.leaveTeam(sender, args, 2);
            } else {
               if (!args[1].equalsIgnoreCase("option")) {
                  throw new WrongUsageException("commands.scoreboard.teams.usage");
               }

               if (args.length != 4 && args.length != 5) {
                  throw new WrongUsageException("commands.scoreboard.teams.option.usage");
               }

               this.setTeamOption(sender, args, 2);
            }
         }
      }
   }

   public boolean checkICommandSender(ICommandSender commandSender, String[] strs) throws CommandException, WrongUsageException {
      int n = -1;

      for (int i = 0; i < strs.length; i++) {
         if (this.isUsernameIndex(strs, i) && "*".equals(strs[i])) {
            if (n >= 0) {
               throw new CommandException("commands.scoreboard.noMultiWildcard");
            }

            n = i;
         }
      }

      if (n < 0) {
         return false;
      }

      ArrayList newArrayList = Lists.newArrayList(this.getScoreboard().getObjectiveNames());
      String str = strs[n];
      ArrayList newArrayList2 = Lists.newArrayList();

      for (String str2 : (Iterable<String>)newArrayList) {
         strs[n] = str2;

         try {
            this.processCommand(commandSender, strs);
            newArrayList2.add(str2);
         } catch (CommandException e) {
            ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation(e.getMessage(), e.getErrorObjects());
            chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
            commandSender.addChatMessage(chatComponentTranslation);
         }
      }

      strs[n] = str;
      commandSender.setCommandStat(Type.AFFECTED_ENTITIES, newArrayList2.size());
      if (newArrayList2.size() == 0) {
         throw new WrongUsageException("commands.scoreboard.allMatchesFailed");
      } else {
         return true;
      }
   }

   public Scoreboard getScoreboard() {
      return MinecraftServer.getServer().worldServerForDimension(0).getScoreboard();
   }

   public ScoreObjective getObjective(String name, boolean edit) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard();
      ScoreObjective objective = scoreboard.getObjective(name);
      if (objective == null) {
         throw new CommandException("commands.scoreboard.objectiveNotFound", name);
      } else if (edit && objective.getCriteria().isReadOnly()) {
         throw new CommandException("commands.scoreboard.objectiveReadOnly", name);
      } else {
         return objective;
      }
   }

   public ScorePlayerTeam getTeam(String name) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard();
      ScorePlayerTeam team = scoreboard.getTeam(name);
      if (team == null) {
         throw new CommandException("commands.scoreboard.teamNotFound", name);
      } else {
         return team;
      }
   }

   public void addObjective(ICommandSender sender, String[] args, int index) throws CommandException {
      String str = args[index++];
      String str2 = args[index++];
      Scoreboard scoreboard = this.getScoreboard();
      IScoreObjectiveCriteria scoreObjectiveCriteria = IScoreObjectiveCriteria.INSTANCES.get(str2);
      if (scoreObjectiveCriteria == null) {
         throw new WrongUsageException("commands.scoreboard.objectives.add.wrongType", str2);
      }

      if (scoreboard.getObjective(str) != null) {
         throw new CommandException("commands.scoreboard.objectives.add.alreadyExists", str);
      }

      if (str.length() > 16) {
         throw new SyntaxErrorException("commands.scoreboard.objectives.add.tooLong", str, 16);
      }

      if (str.length() == 0) {
         throw new WrongUsageException("commands.scoreboard.objectives.add.usage");
      }

      if (args.length > index) {
         String unformattedText = getChatComponentFromNthArg(sender, args, index).getUnformattedText();
         if (unformattedText.length() > 32) {
            throw new SyntaxErrorException("commands.scoreboard.objectives.add.displayTooLong", unformattedText, 32);
         }

         if (unformattedText.length() > 0) {
            scoreboard.addScoreObjective(str, scoreObjectiveCriteria).setDisplayName(unformattedText);
         } else {
            scoreboard.addScoreObjective(str, scoreObjectiveCriteria);
         }
      } else {
         scoreboard.addScoreObjective(str, scoreObjectiveCriteria);
      }

      notifyOperators(sender, this, "commands.scoreboard.objectives.add.success", str);
   }

   public void addTeam(ICommandSender sender, String[] args, int index) throws CommandException {
      String str = args[index++];
      Scoreboard scoreboard = this.getScoreboard();
      if (scoreboard.getTeam(str) != null) {
         throw new CommandException("commands.scoreboard.teams.add.alreadyExists", str);
      }

      if (str.length() > 16) {
         throw new SyntaxErrorException("commands.scoreboard.teams.add.tooLong", str, 16);
      }

      if (str.length() == 0) {
         throw new WrongUsageException("commands.scoreboard.teams.add.usage");
      }

      if (args.length > index) {
         String unformattedText = getChatComponentFromNthArg(sender, args, index).getUnformattedText();
         if (unformattedText.length() > 32) {
            throw new SyntaxErrorException("commands.scoreboard.teams.add.displayTooLong", unformattedText, 32);
         }

         if (unformattedText.length() > 0) {
            scoreboard.createTeam(str).setTeamName(unformattedText);
         } else {
            scoreboard.createTeam(str);
         }
      } else {
         scoreboard.createTeam(str);
      }

      notifyOperators(sender, this, "commands.scoreboard.teams.add.success", str);
   }

   public void setTeamOption(ICommandSender sender, String[] args, int index) throws CommandException {
      ScorePlayerTeam team = this.getTeam(args[index++]);
      if (team != null) {
         String toLowerCase = args[index++].toLowerCase();
         if (!toLowerCase.equalsIgnoreCase("color")
            && !toLowerCase.equalsIgnoreCase("friendlyfire")
            && !toLowerCase.equalsIgnoreCase("seeFriendlyInvisibles")
            && !toLowerCase.equalsIgnoreCase("nametagVisibility")
            && !toLowerCase.equalsIgnoreCase("deathMessageVisibility")) {
            throw new WrongUsageException("commands.scoreboard.teams.option.usage");
         }

         if (args.length == 4) {
            if (toLowerCase.equalsIgnoreCase("color")) {
               throw new WrongUsageException(
                  "commands.scoreboard.teams.option.noValue", toLowerCase, joinNiceStringFromCollection(EnumChatFormatting.getValidValues(true, false))
               );
            }

            if (!toLowerCase.equalsIgnoreCase("friendlyfire") && !toLowerCase.equalsIgnoreCase("seeFriendlyInvisibles")) {
               if (!toLowerCase.equalsIgnoreCase("nametagVisibility") && !toLowerCase.equalsIgnoreCase("deathMessageVisibility")) {
                  throw new WrongUsageException("commands.scoreboard.teams.option.usage");
               }

               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", toLowerCase, joinNiceString(EnumVisible.getValuesAsArray()));
            }

            throw new WrongUsageException("commands.scoreboard.teams.option.noValue", toLowerCase, joinNiceStringFromCollection(Arrays.asList("true", "false")));
         }

         String str = args[index];
         if (toLowerCase.equalsIgnoreCase("color")) {
            EnumChatFormatting valueByName = EnumChatFormatting.getValueByName(str);
            if (valueByName == null || valueByName.isFancyStyling()) {
               throw new WrongUsageException(
                  "commands.scoreboard.teams.option.noValue", toLowerCase, joinNiceStringFromCollection(EnumChatFormatting.getValidValues(true, false))
               );
            }

            team.setChatFormat(valueByName);
            team.setNamePrefix(valueByName.toString());
            team.setNameSuffix(EnumChatFormatting.RESET.toString());
         } else if (toLowerCase.equalsIgnoreCase("friendlyfire")) {
            if (!str.equalsIgnoreCase("true") && !str.equalsIgnoreCase("false")) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", toLowerCase, joinNiceStringFromCollection(Arrays.asList("true", "false")));
            }

            team.setAllowFriendlyFire(str.equalsIgnoreCase("true"));
         } else if (toLowerCase.equalsIgnoreCase("seeFriendlyInvisibles")) {
            if (!str.equalsIgnoreCase("true") && !str.equalsIgnoreCase("false")) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", toLowerCase, joinNiceStringFromCollection(Arrays.asList("true", "false")));
            }

            team.setSeeFriendlyInvisiblesEnabled(str.equalsIgnoreCase("true"));
         } else if (toLowerCase.equalsIgnoreCase("nametagVisibility")) {
            EnumVisible ruleByName = EnumVisible.getRuleByName(str);
            if (ruleByName == null) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", toLowerCase, joinNiceString(EnumVisible.getValuesAsArray()));
            }

            team.setNameTagVisibility(ruleByName);
         } else if (toLowerCase.equalsIgnoreCase("deathMessageVisibility")) {
            EnumVisible ruleByName = EnumVisible.getRuleByName(str);
            if (ruleByName == null) {
               throw new WrongUsageException("commands.scoreboard.teams.option.noValue", toLowerCase, joinNiceString(EnumVisible.getValuesAsArray()));
            }

            team.setDeathMessageVisibility(ruleByName);
         }

         notifyOperators(sender, this, "commands.scoreboard.teams.option.success", toLowerCase, team.getRegisteredName(), str);
      }
   }

   public void removeTeam(ICommandSender commandSender, String[] strs, int n) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard();
      ScorePlayerTeam team = this.getTeam(strs[n]);
      if (team != null) {
         scoreboard.removeTeam(team);
         notifyOperators(commandSender, this, "commands.scoreboard.teams.remove.success", team.getRegisteredName());
      }
   }

   public void listTeams(ICommandSender commandSender, String[] strs, int n) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard();
      if (strs.length > n) {
         ScorePlayerTeam team = this.getTeam(strs[n]);
         if (team == null) {
            return;
         }

         Collection membershipCollection = team.getMembershipCollection();
         commandSender.setCommandStat(Type.QUERY_RESULT, membershipCollection.size());
         if (membershipCollection.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.player.empty", team.getRegisteredName());
         }

         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.scoreboard.teams.list.player.count", membershipCollection.size(), team.getRegisteredName());
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
         commandSender.addChatMessage(chatComponentTranslation);
         commandSender.addChatMessage(new ChatComponentText(joinNiceString(membershipCollection.toArray())));
      } else {
         Collection teams = scoreboard.getTeams();
         commandSender.setCommandStat(Type.QUERY_RESULT, teams.size());
         if (teams.size() <= 0) {
            throw new CommandException("commands.scoreboard.teams.list.empty");
         }

         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.scoreboard.teams.list.count", teams.size());
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
         commandSender.addChatMessage(chatComponentTranslation);

         for (ScorePlayerTeam scorePlayerTeam : (Iterable<ScorePlayerTeam>)teams) {
            commandSender.addChatMessage(
               new ChatComponentTranslation(
                  "commands.scoreboard.teams.list.entry", scorePlayerTeam.getRegisteredName(), scorePlayerTeam.getTeamName(), scorePlayerTeam.getMembershipCollection().size()
               )
            );
         }
      }
   }

   public void joinTeam(ICommandSender commandSender, String[] strs, int n) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard();
      String str = strs[n++];
      HashSet newHashSet = Sets.newHashSet();
      HashSet newHashSet2 = Sets.newHashSet();
      if (commandSender instanceof EntityPlayer && n == strs.length) {
         String name = getCommandSenderAsPlayer(commandSender).getName();
         if (scoreboard.addPlayerToTeam(name, str)) {
            newHashSet.add(name);
         } else {
            newHashSet2.add(name);
         }
      } else {
         while (n < strs.length) {
            String str2 = strs[n++];
            if (str2.startsWith("@")) {
               for (Entity entity : getEntities(commandSender, str2)) {
                  String entityName = getEntityName(commandSender, entity.getUniqueID().toString());
                  if (scoreboard.addPlayerToTeam(entityName, str)) {
                     newHashSet.add(entityName);
                  } else {
                     newHashSet2.add(entityName);
                  }
               }
            } else {
               String entityName = getEntityName(commandSender, str2);
               if (scoreboard.addPlayerToTeam(entityName, str)) {
                  newHashSet.add(entityName);
               } else {
                  newHashSet2.add(entityName);
               }
            }
         }
      }

      if (!newHashSet.isEmpty()) {
         commandSender.setCommandStat(Type.AFFECTED_ENTITIES, newHashSet.size());
         notifyOperators(commandSender, this, "commands.scoreboard.teams.join.success", newHashSet.size(), str, joinNiceString(newHashSet.toArray(new String[newHashSet.size()])));
      }

      if (!newHashSet2.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.join.failure", newHashSet2.size(), str, joinNiceString(newHashSet2.toArray(new String[newHashSet2.size()])));
      }
   }

   public void leaveTeam(ICommandSender commandSender, String[] strs, int n) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard();
      HashSet newHashSet = Sets.newHashSet();
      HashSet newHashSet2 = Sets.newHashSet();
      if (commandSender instanceof EntityPlayer && n == strs.length) {
         String name = getCommandSenderAsPlayer(commandSender).getName();
         if (scoreboard.removePlayerFromTeams(name)) {
            newHashSet.add(name);
         } else {
            newHashSet2.add(name);
         }
      } else {
         while (n < strs.length) {
            String str = strs[n++];
            if (str.startsWith("@")) {
               for (Entity entity : getEntities(commandSender, str)) {
                  String entityName = getEntityName(commandSender, entity.getUniqueID().toString());
                  if (scoreboard.removePlayerFromTeams(entityName)) {
                     newHashSet.add(entityName);
                  } else {
                     newHashSet2.add(entityName);
                  }
               }
            } else {
               String entityName = getEntityName(commandSender, str);
               if (scoreboard.removePlayerFromTeams(entityName)) {
                  newHashSet.add(entityName);
               } else {
                  newHashSet2.add(entityName);
               }
            }
         }
      }

      if (!newHashSet.isEmpty()) {
         commandSender.setCommandStat(Type.AFFECTED_ENTITIES, newHashSet.size());
         notifyOperators(commandSender, this, "commands.scoreboard.teams.leave.success", newHashSet.size(), joinNiceString(newHashSet.toArray(new String[newHashSet.size()])));
      }

      if (!newHashSet2.isEmpty()) {
         throw new CommandException("commands.scoreboard.teams.leave.failure", newHashSet2.size(), joinNiceString(newHashSet2.toArray(new String[newHashSet2.size()])));
      }
   }

   public void emptyTeam(ICommandSender commandSender, String[] strs, int n) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard();
      ScorePlayerTeam team = this.getTeam(strs[n]);
      if (team != null) {
         ArrayList newArrayList = Lists.newArrayList(team.getMembershipCollection());
         commandSender.setCommandStat(Type.AFFECTED_ENTITIES, newArrayList.size());
         if (newArrayList.isEmpty()) {
            throw new CommandException("commands.scoreboard.teams.empty.alreadyEmpty", team.getRegisteredName());
         }

         for (String str : (Iterable<String>)newArrayList) {
            scoreboard.removePlayerFromTeam(str, team);
         }

         notifyOperators(commandSender, this, "commands.scoreboard.teams.empty.success", newArrayList.size(), team.getRegisteredName());
      }
   }

   public void removeObjective(ICommandSender commandSender, String str) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard();
      ScoreObjective objective = this.getObjective(str, false);
      scoreboard.removeObjective(objective);
      notifyOperators(commandSender, this, "commands.scoreboard.objectives.remove.success", str);
   }

   public void listObjectives(ICommandSender commandSender) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard();
      Collection scoreObjectives = scoreboard.getScoreObjectives();
      if (scoreObjectives.size() <= 0) {
         throw new CommandException("commands.scoreboard.objectives.list.empty");
      }

      ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.scoreboard.objectives.list.count", scoreObjectives.size());
      chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
      commandSender.addChatMessage(chatComponentTranslation);

      for (ScoreObjective scoreObjective : (Iterable<ScoreObjective>)scoreObjectives) {
         commandSender.addChatMessage(
            new ChatComponentTranslation("commands.scoreboard.objectives.list.entry", scoreObjective.getName(), scoreObjective.getDisplayName(), scoreObjective.getCriteria().getName())
         );
      }
   }

   public void setObjectiveDisplay(ICommandSender commandSender, String[] strs, int n) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard();
      String str = strs[n++];
      int objectiveDisplaySlotNumber = Scoreboard.getObjectiveDisplaySlotNumber(str);
      ScoreObjective scoreObjective = null;
      if (strs.length == 4) {
         scoreObjective = this.getObjective(strs[n], false);
      }

      if (objectiveDisplaySlotNumber < 0) {
         throw new CommandException("commands.scoreboard.objectives.setdisplay.invalidSlot", str);
      }

      scoreboard.setObjectiveInDisplaySlot(objectiveDisplaySlotNumber, scoreObjective);
      if (scoreObjective != null) {
         notifyOperators(commandSender, this, "commands.scoreboard.objectives.setdisplay.successSet", Scoreboard.getObjectiveDisplaySlot(objectiveDisplaySlotNumber), scoreObjective.getName());
      } else {
         notifyOperators(commandSender, this, "commands.scoreboard.objectives.setdisplay.successCleared", Scoreboard.getObjectiveDisplaySlot(objectiveDisplaySlotNumber));
      }
   }

   public void listPlayers(ICommandSender commandSender, String[] strs, int n) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard();
      if (strs.length > n) {
         String entityName = getEntityName(commandSender, strs[n]);
         Map objectivesForEntity = scoreboard.getObjectivesForEntity(entityName);
         commandSender.setCommandStat(Type.QUERY_RESULT, objectivesForEntity.size());
         if (objectivesForEntity.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.player.empty", entityName);
         }

         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.scoreboard.players.list.player.count", objectivesForEntity.size(), entityName);
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
         commandSender.addChatMessage(chatComponentTranslation);

         for (Score score : (Iterable<Score>)objectivesForEntity.values()) {
            commandSender.addChatMessage(
               new ChatComponentTranslation(
                  "commands.scoreboard.players.list.player.entry", score.getScorePoints(), score.getObjective().getDisplayName(), score.getObjective().getName()
               )
            );
         }
      } else {
         Collection objectiveNames = scoreboard.getObjectiveNames();
         commandSender.setCommandStat(Type.QUERY_RESULT, objectiveNames.size());
         if (objectiveNames.size() <= 0) {
            throw new CommandException("commands.scoreboard.players.list.empty");
         }

         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("commands.scoreboard.players.list.count", objectiveNames.size());
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.DARK_GREEN);
         commandSender.addChatMessage(chatComponentTranslation);
         commandSender.addChatMessage(new ChatComponentText(joinNiceString(objectiveNames.toArray())));
      }
   }

   public void setPlayer(ICommandSender commandSender, String[] strs, int n) throws CommandException {
      String str = strs[n - 1];
      int n2 = n;
      String entityName = getEntityName(commandSender, strs[n++]);
      if (entityName.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", entityName, 40);
      }

      ScoreObjective objective = this.getObjective(strs[n++], true);
      int n3 = str.equalsIgnoreCase("set") ? parseInt(strs[n++]) : parseInt(strs[n++], 0);
      if (strs.length > n) {
         Entity entity = getEntity(commandSender, strs[n2]);

         try {
            NBTTagCompound tagFromJson = JsonToNBT.getTagFromJson(buildString(strs, n));
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            entity.writeToNBT(nbttagCompound);
            if (!NBTUtil.matches(tagFromJson, nbttagCompound, true)) {
               throw new CommandException("commands.scoreboard.players.set.tagMismatch", entityName);
            }
         } catch (NBTException e) {
            throw new CommandException("commands.scoreboard.players.set.tagError", e.getMessage());
         }
      }

      Scoreboard scoreboard = this.getScoreboard();
      Score valueFromObjective = scoreboard.getValueFromObjective(entityName, objective);
      if (str.equalsIgnoreCase("set")) {
         valueFromObjective.setScorePoints(n3);
      } else if (str.equalsIgnoreCase("add")) {
         valueFromObjective.increseScore(n3);
      } else {
         valueFromObjective.decreaseScore(n3);
      }

      notifyOperators(commandSender, this, "commands.scoreboard.players.set.success", objective.getName(), entityName, valueFromObjective.getScorePoints());
   }

   public void resetPlayers(ICommandSender commandSender, String[] strs, int n) throws CommandException {
      Scoreboard scoreboard = this.getScoreboard();
      String entityName = getEntityName(commandSender, strs[n++]);
      if (strs.length > n) {
         ScoreObjective objective = this.getObjective(strs[n++], false);
         scoreboard.removeObjectiveFromEntity(entityName, objective);
         notifyOperators(commandSender, this, "commands.scoreboard.players.resetscore.success", objective.getName(), entityName);
      } else {
         scoreboard.removeObjectiveFromEntity(entityName, (ScoreObjective)null);
         notifyOperators(commandSender, this, "commands.scoreboard.players.reset.success", entityName);
      }
   }

   public void handleICommandSender(ICommandSender commandSender, String[] strs, int n) throws net.minecraft.command.PlayerNotFoundException, SyntaxErrorException, CommandException {
      Scoreboard scoreboard = this.getScoreboard();
      String playerName = getPlayerName(commandSender, strs[n++]);
      if (playerName.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", playerName, 40);
      }

      ScoreObjective objective = this.getObjective(strs[n], false);
      if (objective.getCriteria() != IScoreObjectiveCriteria.TRIGGER) {
         throw new CommandException("commands.scoreboard.players.enable.noTrigger", objective.getName());
      }

      Score valueFromObjective = scoreboard.getValueFromObjective(playerName, objective);
      valueFromObjective.setLocked(false);
      notifyOperators(commandSender, this, "commands.scoreboard.players.enable.success", objective.getName(), playerName);
   }

   public void handleICommandSender2(ICommandSender commandSender, String[] strs, int n) throws net.minecraft.command.EntityNotFoundException, SyntaxErrorException, CommandException, net.minecraft.command.NumberInvalidException {
      Scoreboard scoreboard = this.getScoreboard();
      String entityName = getEntityName(commandSender, strs[n++]);
      if (entityName.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", entityName, 40);
      }

      ScoreObjective objective = this.getObjective(strs[n++], false);
      if (!scoreboard.entityHasObjective(entityName, objective)) {
         throw new CommandException("commands.scoreboard.players.test.notFound", objective.getName(), entityName);
      }

      int n2 = strs[n].equals("*") ? Integer.MIN_VALUE : parseInt(strs[n]);
      n++;
      int n3 = n < strs.length && !strs[n].equals("*") ? parseInt(strs[n], n2) : Integer.MAX_VALUE;
      Score valueFromObjective = scoreboard.getValueFromObjective(entityName, objective);
      if (valueFromObjective.getScorePoints() >= n2 && valueFromObjective.getScorePoints() <= n3) {
         notifyOperators(commandSender, this, "commands.scoreboard.players.test.success", valueFromObjective.getScorePoints(), n2, n3);
      } else {
         throw new CommandException("commands.scoreboard.players.test.failed", valueFromObjective.getScorePoints(), n2, n3);
      }
   }

   public void handleICommandSender3(ICommandSender commandSender, String[] strs, int n) throws net.minecraft.command.EntityNotFoundException, CommandException, SyntaxErrorException {
      Scoreboard scoreboard = this.getScoreboard();
      String entityName = getEntityName(commandSender, strs[n++]);
      ScoreObjective objective = this.getObjective(strs[n++], true);
      String str = strs[n++];
      String entityName2 = getEntityName(commandSender, strs[n++]);
      ScoreObjective objective2 = this.getObjective(strs[n], false);
      if (entityName.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", entityName, 40);
      }

      if (entityName2.length() > 40) {
         throw new SyntaxErrorException("commands.scoreboard.players.name.tooLong", entityName2, 40);
      }

      Score valueFromObjective = scoreboard.getValueFromObjective(entityName, objective);
      if (!scoreboard.entityHasObjective(entityName2, objective2)) {
         throw new CommandException("commands.scoreboard.players.operation.notFound", objective2.getName(), entityName2);
      }

      Score valueFromObjective2 = scoreboard.getValueFromObjective(entityName2, objective2);
      if (str.equals("+=")) {
         valueFromObjective.setScorePoints(valueFromObjective.getScorePoints() + valueFromObjective2.getScorePoints());
      } else if (str.equals("-=")) {
         valueFromObjective.setScorePoints(valueFromObjective.getScorePoints() - valueFromObjective2.getScorePoints());
      } else if (str.equals("*=")) {
         valueFromObjective.setScorePoints(valueFromObjective.getScorePoints() * valueFromObjective2.getScorePoints());
      } else if (str.equals("/=")) {
         if (valueFromObjective2.getScorePoints() != 0) {
            valueFromObjective.setScorePoints(valueFromObjective.getScorePoints() / valueFromObjective2.getScorePoints());
         }
      } else if (str.equals("%=")) {
         if (valueFromObjective2.getScorePoints() != 0) {
            valueFromObjective.setScorePoints(valueFromObjective.getScorePoints() % valueFromObjective2.getScorePoints());
         }
      } else if (str.equals("=")) {
         valueFromObjective.setScorePoints(valueFromObjective2.getScorePoints());
      } else if (str.equals("<")) {
         valueFromObjective.setScorePoints(Math.min(valueFromObjective.getScorePoints(), valueFromObjective2.getScorePoints()));
      } else if (str.equals(">")) {
         valueFromObjective.setScorePoints(Math.max(valueFromObjective.getScorePoints(), valueFromObjective2.getScorePoints()));
      } else {
         if (!str.equals("><")) {
            throw new CommandException("commands.scoreboard.players.operation.invalidOperation", str);
         }

         int scorePoints = valueFromObjective.getScorePoints();
         valueFromObjective.setScorePoints(valueFromObjective2.getScorePoints());
         valueFromObjective2.setScorePoints(scorePoints);
      }

      notifyOperators(commandSender, this, "commands.scoreboard.players.operation.success");
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      if (args.length == 1) {
         return getListOfStringsMatchingLastWord(args, "objectives", "players", "teams");
      }

      if (args[0].equalsIgnoreCase("objectives")) {
         if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "list", "add", "remove", "setdisplay");
         }

         if (args[1].equalsIgnoreCase("add")) {
            if (args.length == 4) {
               Set keySet = IScoreObjectiveCriteria.INSTANCES.keySet();
               return getListOfStringsMatchingLastWord(args, keySet);
            }
         } else if (args[1].equalsIgnoreCase("remove")) {
            if (args.length == 3) {
               return getListOfStringsMatchingLastWord(args, this.findList(false));
            }
         } else if (args[1].equalsIgnoreCase("setdisplay")) {
            if (args.length == 3) {
               return getListOfStringsMatchingLastWord(args, Scoreboard.getDisplaySlotStrings());
            }

            if (args.length == 4) {
               return getListOfStringsMatchingLastWord(args, this.findList(false));
            }
         }
      } else if (args[0].equalsIgnoreCase("players")) {
         if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "set", "add", "remove", "reset", "list", "enable", "test", "operation");
         }

         if (!args[1].equalsIgnoreCase("set") && !args[1].equalsIgnoreCase("add") && !args[1].equalsIgnoreCase("remove") && !args[1].equalsIgnoreCase("reset")) {
            if (args[1].equalsIgnoreCase("enable")) {
               if (args.length == 3) {
                  return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
               }

               if (args.length == 4) {
                  return getListOfStringsMatchingLastWord(args, this.getList());
               }
            } else if (!args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("test")) {
               if (args[1].equalsIgnoreCase("operation")) {
                  if (args.length == 3) {
                     return getListOfStringsMatchingLastWord(args, this.getScoreboard().getObjectiveNames());
                  }

                  if (args.length == 4) {
                     return getListOfStringsMatchingLastWord(args, this.findList(true));
                  }

                  if (args.length == 5) {
                     return getListOfStringsMatchingLastWord(args, "+=", "-=", "*=", "/=", "%=", "=", "<", ">", "><");
                  }

                  if (args.length == 6) {
                     return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
                  }

                  if (args.length == 7) {
                     return getListOfStringsMatchingLastWord(args, this.findList(false));
                  }
               }
            } else {
               if (args.length == 3) {
                  return getListOfStringsMatchingLastWord(args, this.getScoreboard().getObjectiveNames());
               }

               if (args.length == 4 && args[1].equalsIgnoreCase("test")) {
                  return getListOfStringsMatchingLastWord(args, this.findList(false));
               }
            }
         } else {
            if (args.length == 3) {
               return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }

            if (args.length == 4) {
               return getListOfStringsMatchingLastWord(args, this.findList(true));
            }
         }
      } else if (args[0].equalsIgnoreCase("teams")) {
         if (args.length == 2) {
            return getListOfStringsMatchingLastWord(args, "add", "remove", "join", "leave", "empty", "list", "option");
         }

         if (args[1].equalsIgnoreCase("join")) {
            if (args.length == 3) {
               return getListOfStringsMatchingLastWord(args, this.getScoreboard().getTeamNames());
            }

            if (args.length >= 4) {
               return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }
         } else {
            if (args[1].equalsIgnoreCase("leave")) {
               return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames());
            }

            if (!args[1].equalsIgnoreCase("empty") && !args[1].equalsIgnoreCase("list") && !args[1].equalsIgnoreCase("remove")) {
               if (args[1].equalsIgnoreCase("option")) {
                  if (args.length == 3) {
                     return getListOfStringsMatchingLastWord(args, this.getScoreboard().getTeamNames());
                  }

                  if (args.length == 4) {
                     return getListOfStringsMatchingLastWord(
                        args, "color", "friendlyfire", "seeFriendlyInvisibles", "nametagVisibility", "deathMessageVisibility"
                     );
                  }

                  if (args.length == 5) {
                     if (args[3].equalsIgnoreCase("color")) {
                        return getListOfStringsMatchingLastWord(args, EnumChatFormatting.getValidValues(true, false));
                     }

                     if (args[3].equalsIgnoreCase("nametagVisibility") || args[3].equalsIgnoreCase("deathMessageVisibility")) {
                        return getListOfStringsMatchingLastWord(args, EnumVisible.getValuesAsArray());
                     }

                     if (args[3].equalsIgnoreCase("friendlyfire") || args[3].equalsIgnoreCase("seeFriendlyInvisibles")) {
                        return getListOfStringsMatchingLastWord(args, "true", "false");
                     }
                  }
               }
            } else if (args.length == 3) {
               return getListOfStringsMatchingLastWord(args, this.getScoreboard().getTeamNames());
            }
         }
      }

      return null;
   }

   public List<String> findList(boolean flag) {
      Collection scoreObjectives = this.getScoreboard().getScoreObjectives();
      ArrayList newArrayList = Lists.newArrayList();

      for (ScoreObjective scoreObjective : (Iterable<ScoreObjective>)scoreObjectives) {
         if (!flag || !scoreObjective.getCriteria().isReadOnly()) {
            newArrayList.add(scoreObjective.getName());
         }
      }

      return newArrayList;
   }

   public List<String> getList() {
      Collection scoreObjectives = this.getScoreboard().getScoreObjectives();
      ArrayList newArrayList = Lists.newArrayList();

      for (ScoreObjective scoreObjective : (Iterable<ScoreObjective>)scoreObjectives) {
         if (scoreObjective.getCriteria() == IScoreObjectiveCriteria.TRIGGER) {
            newArrayList.add(scoreObjective.getName());
         }
      }

      return newArrayList;
   }

   @Override
   public boolean isUsernameIndex(String[] args, int index) {
      return !args[0].equalsIgnoreCase("players")
         ? (args[0].equalsIgnoreCase("teams") ? index == 2 : false)
         : (args.length > 1 && args[1].equalsIgnoreCase("operation") ? index == 2 || index == 5 : index == 2);
   }
}
