package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandSpreadPlayers extends CommandBase {
   @Override
   public String getCommandName() {
      return "spreadplayers";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.spreadplayers.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length < 6) {
         throw new WrongUsageException("commands.spreadplayers.usage");
      }

      int n = 0;
      BlockPos position = sender.getPosition();
      double parseDouble2 = parseDouble(position.getX(), args[n++], true);
      double parseDouble3 = parseDouble(position.getZ(), args[n++], true);
      double parseDouble4 = parseDouble(args[n++], 0.0);
      double parseDouble5 = parseDouble(args[n++], parseDouble4 + 1.0);
      boolean parseBoolean2 = parseBoolean(args[n++]);
      ArrayList newArrayList = Lists.newArrayList();

      while (n < args.length) {
         String str = args[n++];
         if (PlayerSelector.hasArguments(str)) {
            List matchEntities = PlayerSelector.matchEntities(sender, str, Entity.class);
            if (matchEntities.size() == 0) {
               throw new EntityNotFoundException();
            }

            newArrayList.addAll(matchEntities);
         } else {
            EntityPlayerMP playerByUsername = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(str);
            if (playerByUsername == null) {
               throw new PlayerNotFoundException();
            }

            newArrayList.add(playerByUsername);
         }
      }

      sender.setCommandStat(CommandResultStats.Type.AFFECTED_ENTITIES, newArrayList.size());
      if (newArrayList.isEmpty()) {
         throw new EntityNotFoundException();
      }

      sender.addChatMessage(
         new ChatComponentTranslation("commands.spreadplayers.spreading." + (parseBoolean2 ? "teams" : "players"), newArrayList.size(), parseDouble5, parseDouble2, parseDouble3, parseDouble4)
      );
      this.handleICommandSender(sender, newArrayList, new CommandSpreadPlayers.Position(parseDouble2, parseDouble3), parseDouble4, parseDouble5, ((Entity)newArrayList.get(0)).worldObj, parseBoolean2);
   }

   public void handleICommandSender(
      ICommandSender commandSender, List<Entity> list, CommandSpreadPlayers.Position position, double d, double d2, World worldIn, boolean flag
   ) throws CommandException {
      Random random = new Random();
      double d3 = position.x - d2;
      double d4 = position.z - d2;
      double d5 = position.x + d2;
      double d6 = position.z + d2;
      CommandSpreadPlayers.Position[] makePiles = this.makePiles(random, flag ? this.computeInt(list) : list.size(), d3, d4, d5, d6);
      int computeInt = this.computeInt(position, d, worldIn, random, d3, d4, d5, d6, makePiles, flag);
      double computeDouble = this.computeDouble(list, worldIn, makePiles, flag);
      notifyOperators(commandSender, this, "commands.spreadplayers.success." + (flag ? "teams" : "players"), makePiles.length, position.x, position.z);
      if (makePiles.length > 1) {
         commandSender.addChatMessage(new ChatComponentTranslation("commands.spreadplayers.info." + (flag ? "teams" : "players"), String.format("%.2f", computeDouble), computeInt));
      }
   }

   public int computeInt(List<Entity> list) {
      HashSet newHashSet = Sets.newHashSet();

      for (Entity entity : list) {
         if (entity instanceof EntityPlayer) {
            newHashSet.add(((EntityPlayer)entity).getTeam());
         } else {
            newHashSet.add((Team)null);
         }
      }

      return newHashSet.size();
   }

   public int computeInt(
      CommandSpreadPlayers.Position position,
      double d,
      World worldIn,
      Random random,
      double d2,
      double d3,
      double d4,
      double d5,
      CommandSpreadPlayers.Position[] positions,
      boolean flag
   ) throws CommandException {
      boolean flag2 = true;
      double d6 = Float.MAX_VALUE;

      int n;
      for (n = 0; n < 10000 && flag2; n++) {
         flag2 = false;
         d6 = Float.MAX_VALUE;

         for (int i = 0; i < positions.length; i++) {
            CommandSpreadPlayers.Position position2 = positions[i];
            int n2 = 0;
            CommandSpreadPlayers.Position position3 = new CommandSpreadPlayers.Position();

            for (int j = 0; j < positions.length; j++) {
               if (i != j) {
                  CommandSpreadPlayers.Position position4 = positions[j];
                  double distance = position2.getDistance(position4);
                  d6 = Math.min(distance, d6);
                  if (distance < d) {
                     n2++;
                     position3.x = position3.x + (position4.x - position2.x);
                     position3.z = position3.z + (position4.z - position2.z);
                  }
               }
            }

            if (n2 > 0) {
               position3.x /= n2;
               position3.z /= n2;
               double absolute = position3.absolute();
               if (absolute > 0.0) {
                  position3.normalize();
                  position2.subtract(position3);
               } else {
                  position2.setPileLocation(random, d2, d3, d4, d5);
               }

               flag2 = true;
            }

            if (position2.clamp(d2, d3, d4, d5)) {
               flag2 = true;
            }
         }

         if (!flag2) {
            for (CommandSpreadPlayers.Position position2 : positions) {
               if (!position2.checkWorld(worldIn)) {
                  position2.setPileLocation(random, d2, d3, d4, d5);
                  flag2 = true;
               }
            }
         }
      }

      if (n >= 10000) {
         throw new CommandException(
            "commands.spreadplayers.failure." + (flag ? "teams" : "players"), positions.length, position.x, position.z, String.format("%.2f", d6)
         );
      } else {
         return n;
      }
   }

   public double computeDouble(List<Entity> list, World worldIn, CommandSpreadPlayers.Position[] positions, boolean flag) {
      double d = 0.0;
      int n = 0;
      HashMap newHashMap = Maps.newHashMap();

      for (int i = 0; i < list.size(); i++) {
         Entity entity = (Entity)list.get(i);
         CommandSpreadPlayers.Position position;
         if (flag) {
            Team team = entity instanceof EntityPlayer ? ((EntityPlayer)entity).getTeam() : null;
            if (!newHashMap.containsKey(team)) {
               newHashMap.put(team, positions[n++]);
            }

            position = (CommandSpreadPlayers.Position)newHashMap.get(team);
         } else {
            position = positions[n++];
         }

         entity.setPositionAndUpdate(MathHelper.floor_double(position.x) + 0.5F, position.computeInt(worldIn), MathHelper.floor_double(position.z) + 0.5);
         double d2 = Double.MAX_VALUE;

         for (int j = 0; j < positions.length; j++) {
            if (position != positions[j]) {
               double distance = position.getDistance(positions[j]);
               d2 = Math.min(distance, d2);
            }
         }

         d += d2;
      }

      return d / list.size();
   }

   public CommandSpreadPlayers.Position[] makePiles(Random random, int n, double d, double d2, double d3, double d4) {
      CommandSpreadPlayers.Position[] positions = new CommandSpreadPlayers.Position[n];

      for (int i = 0; i < positions.length; i++) {
         CommandSpreadPlayers.Position position = new CommandSpreadPlayers.Position();
         position.setPileLocation(random, d, d2, d3, d4);
         positions[i] = position;
      }

      return positions;
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length >= 1 && args.length <= 2 ? findList2(args, 0, pos) : null;
   }

   static class Position {
      public double x;
      public double z;

      public Position() {
      }

      public Position(double d, double d2) {
         this.x = d;
         this.z = d2;
      }

      public double getDistance(CommandSpreadPlayers.Position position) {
         double d = this.x - position.x;
         double d2 = this.z - position.z;
         return Math.sqrt(d * d + d2 * d2);
      }

      public void normalize() {
         double absolute = this.absolute();
         this.x /= absolute;
         this.z /= absolute;
      }

      public float absolute() {
         return MathHelper.sqrt_double(this.x * this.x + this.z * this.z);
      }

      public void subtract(CommandSpreadPlayers.Position position) {
         this.x = this.x - position.x;
         this.z = this.z - position.z;
      }

      public boolean clamp(double d, double d2, double d3, double d4) {
         boolean flag = false;
         if (this.x < d) {
            this.x = d;
            flag = true;
         } else if (this.x > d3) {
            this.x = d3;
            flag = true;
         }

         if (this.z < d2) {
            this.z = d2;
            flag = true;
         } else if (this.z > d4) {
            this.z = d4;
            flag = true;
         }

         return flag;
      }

      public int computeInt(World worldIn) {
         BlockPos blockPos = new BlockPos(this.x, 256.0, this.z);

         while (blockPos.getY() > 0) {
            blockPos = blockPos.down();
            if (worldIn.getBlockState(blockPos).getBlock().getMaterial() != Material.air) {
               return blockPos.getY() + 1;
            }
         }

         return 257;
      }

      public boolean checkWorld(World worldIn) {
         BlockPos blockPos = new BlockPos(this.x, 256.0, this.z);

         while (blockPos.getY() > 0) {
            blockPos = blockPos.down();
            Material material = worldIn.getBlockState(blockPos).getBlock().getMaterial();
            if (material != Material.air) {
               return !material.isLiquid() && material != Material.fire;
            }
         }

         return false;
      }

      public void setPileLocation(Random random, double d, double d2, double d3, double d4) {
         this.x = MathHelper.getRandomDoubleInRange(random, d, d3);
         this.z = MathHelper.getRandomDoubleInRange(random, d2, d4);
      }
   }
}
