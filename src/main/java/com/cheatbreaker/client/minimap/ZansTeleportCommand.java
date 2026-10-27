package com.cheatbreaker.client.minimap;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ZansTeleportCommand extends CommandBase {
   public IWaypointManager waypointManager;

   public ZansTeleportCommand(IWaypointManager waypointManager) {
      this.waypointManager = waypointManager;
   }

   public boolean checkWorld(World world, int n, int n2, int n3) {
      Block block = world.getBlockState(new BlockPos(n, n2, n3)).getBlock();
      return block.getMaterial() == Material.air ? !(block instanceof BlockFence) : block == null;
   }

   @Override
   public void processCommand(ICommandSender commandSender, String[] strs) throws WrongUsageException, PlayerNotFoundException, net.minecraft.command.NumberInvalidException {
      if (strs.length < 1) {
         throw new WrongUsageException("/ztp [waypointName]");
      }

      MinecraftServer server = MinecraftServer.getServer();
      EntityPlayerMP entityPlayerMP = null;
      if (entityPlayerMP == null) {
         entityPlayerMP = getCommandSenderAsPlayer(commandSender);
      }

      if (entityPlayerMP == null) {
         throw new PlayerNotFoundException();
      }

      String str = strs[0];

      for (int i = 1; i < strs.length; i++) {
         str = str + " ";
         str = str + strs[i];
      }

      ArrayList waypoints = this.waypointManager.getWaypoints();
      Waypoint waypoint = null;

      for (Waypoint waypoint2 : (Iterable<Waypoint>)waypoints) {
         if (waypoint2.name.equalsIgnoreCase(str)) {
            waypoint = waypoint2;
         }
      }

      boolean flag = entityPlayerMP.dimension == -1;
      if (waypoint != null && entityPlayerMP.worldObj != null) {
         int n = 30000000;
         int parseInt2 = parseInt("" + waypoint.getX(), -n, n);
         int parseInt3 = parseInt("" + waypoint.getZ(), -n, n);
         int n2 = waypoint.getY();
         if (!flag) {
            if (waypoint.getY() == -1) {
               n2 = entityPlayerMP.worldObj.getTopSolidOrLiquidBlock(new BlockPos(parseInt2, 0, parseInt3)).getY();
            }

            if (n2 == 0) {
               Chunk chunkFromBlockCoords = entityPlayerMP.worldObj.getChunkFromBlockCoords(new BlockPos(parseInt2, 0, parseInt3));
               n2 = entityPlayerMP.worldObj.getTopSolidOrLiquidBlock(new BlockPos(parseInt2, 0, parseInt3)).getY();
            }
         } else {
            Chunk chunkFromBlockCoords = entityPlayerMP.worldObj.getChunkFromBlockCoords(new BlockPos(parseInt2, 0, parseInt3));
            int n3 = -1;

            for (int i = 0; i < 127; i++) {
               if (n2 + i < 127
                  && this.checkWorld2(entityPlayerMP.worldObj, parseInt2, n2 + i, parseInt3)
                  && this.checkWorld(entityPlayerMP.worldObj, parseInt2, n2 + i + 1, parseInt3)
                  && this.checkWorld(entityPlayerMP.worldObj, parseInt2, n2 + i + 2, parseInt3)) {
                  n3 = n2 + i + 1;
                  i = 128;
               }

               if (n2 - i > 0
                  && this.checkWorld2(entityPlayerMP.worldObj, parseInt2, n2 - i, parseInt3)
                  && this.checkWorld(entityPlayerMP.worldObj, parseInt2, n2 - i + 1, parseInt3)
                  && this.checkWorld(entityPlayerMP.worldObj, parseInt2, n2 - i + 2, parseInt3)) {
                  n3 = n2 - i + 1;
                  i = 128;
               }
            }

            if (n3 == -1) {
               return;
            }

            n2 = n3;
         }

         entityPlayerMP.setPositionAndUpdate(parseInt2 + 0.5F, n2, parseInt3 + 0.5F);
      }
   }

   public List findList(ICommandSender commandSender, String[] strs) {
      return strs.length != 1 && strs.length != 2 ? null : getListOfStringsMatchingLastWord(strs, MinecraftServer.getServer().getAllUsernames());
   }

   public boolean checkWorld2(World world, int n, int n2, int n3) {
      Block block = world.getBlockState(new BlockPos(n, n2, n3)).getBlock();
      return block.getMaterial() == Material.air ? block instanceof BlockFence : block != null && block.getMaterial().isOpaque();
   }

   @Override
   public String getCommandUsage(ICommandSender commandSender) {
      return "/ztp [waypointName]";
   }

   @Override
   public String getCommandName() {
      return "ztp";
   }
}
