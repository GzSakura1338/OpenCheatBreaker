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
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ZansTeleportCommand extends CommandBase {
    public IWaypointManager waypointManager;

    @Override
    public void processCommand(ICommandSender commandSender, String[] strs) {
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
            int parseIntBounded2 = parseIntBounded(commandSender, "" + waypoint.getX(), -n, n);
            int parseIntBounded3 = parseIntBounded(commandSender, "" + waypoint.getZ(), -n, n);
            int n2 = waypoint.getY();
            if (!flag) {
                if (waypoint.getY() == -1) {
                    n2 = entityPlayerMP.worldObj.getHeightValue(parseIntBounded2, parseIntBounded3);
                }

                if (n2 == 0) {
                    Chunk chunkFromBlockCoords = entityPlayerMP.worldObj.getChunkFromBlockCoords(parseIntBounded2, parseIntBounded3);
                    n2 = entityPlayerMP.worldObj.getHeightValue(parseIntBounded2, parseIntBounded3);
                }
            } else {
                Chunk chunkFromBlockCoords = entityPlayerMP.worldObj.getChunkFromBlockCoords(parseIntBounded2, parseIntBounded3);
                entityPlayerMP.worldObj.getChunkProvider().loadChunk(parseIntBounded2, parseIntBounded3);
                int n3 = -1;

                for (int i = 0; i < 127; i++) {
                    if (n2 + i < 127
                        && this.checkWorld(entityPlayerMP.worldObj, parseIntBounded2, n2 + i, parseIntBounded3)
                        && this.checkWorld2(entityPlayerMP.worldObj, parseIntBounded2, n2 + i + 1, parseIntBounded3)
                        && this.checkWorld2(entityPlayerMP.worldObj, parseIntBounded2, n2 + i + 2, parseIntBounded3)) {
                        n3 = n2 + i + 1;
                        i = 128;
                    }

                    if (n2 - i > 0
                        && this.checkWorld(entityPlayerMP.worldObj, parseIntBounded2, n2 - i, parseIntBounded3)
                        && this.checkWorld2(entityPlayerMP.worldObj, parseIntBounded2, n2 - i + 1, parseIntBounded3)
                        && this.checkWorld2(entityPlayerMP.worldObj, parseIntBounded2, n2 - i + 2, parseIntBounded3)) {
                        n3 = n2 - i + 1;
                        i = 128;
                    }
                }

                if (n3 == -1) {
                    return;
                }

                n2 = n3;
            }

            entityPlayerMP.setPositionAndUpdate(parseIntBounded2 + 0.5F, n2, parseIntBounded3 + 0.5F);
        }
    }

    public boolean checkWorld(World world, int n, int n2, int n3) {
        Block block = world.getBlock(n, n2, n3);
        return block.getMaterial() == Material.air ? block instanceof BlockFence : block != null && block.getMaterial().isOpaque();
    }

    public ZansTeleportCommand(IWaypointManager waypointManager) {
        this.waypointManager = waypointManager;
    }

    @Override
    public String getCommandName() {
        return "ztp";
    }

    @Override
    public String getCommandUsage(ICommandSender commandSender) {
        return "/ztp [waypointName]";
    }

    @Override
    public List addTabCompletionOptions(ICommandSender commandSender, String[] strs) {
        return strs.length != 1 && strs.length != 2 ? null : getListOfStringsMatchingLastWord(strs, MinecraftServer.getServer().getAllUsernames());
    }

    public boolean checkWorld2(World world, int n, int n2, int n3) {
        Block block = world.getBlock(n, n2, n3);
        return block.getMaterial() == Material.air ? !(block instanceof BlockFence) : block == null;
    }
}
