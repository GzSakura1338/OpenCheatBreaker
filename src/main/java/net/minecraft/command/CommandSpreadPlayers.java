package net.minecraft.command;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
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
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 6) {
            throw new WrongUsageException("commands.spreadplayers.usage");
        }

        byte b = 0;
        int n = b + 1;
        double d = clamp_coord(sender, Double.NaN, args[b]);
        double d2 = clamp_coord(sender, Double.NaN, args[n++]);
        double parseDouble2 = parseDouble(sender, args[n++], 0.0);
        double parseDouble3 = parseDouble(sender, args[n++], parseDouble2 + 1.0);
        boolean parseBoolean2 = parseBoolean(sender, args[n++]);
        ArrayList newArrayList = Lists.newArrayList();

        while (n < args.length) {
            String str = args[n++];
            if (PlayerSelector.hasArguments(str)) {
                EntityPlayerMP[] matchPlayers = PlayerSelector.matchPlayers(sender, str);
                if (matchPlayers == null || matchPlayers.length == 0) {
                    throw new PlayerNotFoundException();
                }

                Collections.addAll(newArrayList, matchPlayers);
            } else {
                EntityPlayerMP playerByUsername = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(str);
                if (playerByUsername == null) {
                    throw new PlayerNotFoundException();
                }

                newArrayList.add(playerByUsername);
            }
        }

        if (newArrayList.isEmpty()) {
            throw new PlayerNotFoundException();
        }

        sender.addChatMessage(
            new ChatComponentTranslation("commands.spreadplayers.spreading." + (parseBoolean2 ? "teams" : "players"), newArrayList.size(), parseDouble3, d, d2, parseDouble2)
        );
        this.handleICommandSender(sender, newArrayList, new CommandSpreadPlayers.Position(d, d2), parseDouble2, parseDouble3, ((EntityLivingBase)newArrayList.get(0)).worldObj, parseBoolean2);
    }

    public void handleICommandSender(ICommandSender commandSender, List list, CommandSpreadPlayers.Position position, double d, double d2, World world, boolean flag) {
        Random random = new Random();
        double d3 = position.doubleValue - d2;
        double d4 = position.doubleValue2 - d2;
        double d5 = position.doubleValue + d2;
        double d6 = position.doubleValue2 + d2;
        CommandSpreadPlayers.Position[] positionArray = this.getPositionArray(random, flag ? this.computeInt(list) : list.size(), d3, d4, d5, d6);
        int computeInt = this.computeInt(position, d, world, random, d3, d4, d5, d6, positionArray, flag);
        double computeDouble = this.computeDouble(list, world, positionArray, flag);
        notifyOperators(commandSender, this, "commands.spreadplayers.success." + (flag ? "teams" : "players"), positionArray.length, position.doubleValue, position.doubleValue2);
        if (positionArray.length > 1) {
            commandSender.addChatMessage(new ChatComponentTranslation("commands.spreadplayers.info." + (flag ? "teams" : "players"), String.format("%.2f", computeDouble), computeInt));
        }
    }

    public int computeInt(List list) {
        HashSet newHashSet = Sets.newHashSet();

        for (EntityLivingBase entityLivingBase : (Iterable<EntityLivingBase>)list) {
            if (entityLivingBase instanceof EntityPlayer) {
                newHashSet.add(entityLivingBase.getTeam());
            } else {
                newHashSet.add(null);
            }
        }

        return newHashSet.size();
    }

    public int computeInt(
        CommandSpreadPlayers.Position position,
        double d,
        World world,
        Random random,
        double d2,
        double d3,
        double d4,
        double d5,
        CommandSpreadPlayers.Position[] positions,
        boolean flag
    ) {
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
                        double sqrt = position2.sqrt(position4);
                        d6 = Math.min(sqrt, d6);
                        if (sqrt < d) {
                            n2++;
                            position3.doubleValue = position3.doubleValue + (position4.doubleValue - position2.doubleValue);
                            position3.doubleValue2 = position3.doubleValue2 + (position4.doubleValue2 - position2.doubleValue2);
                        }
                    }
                }

                if (n2 > 0) {
                    position3.doubleValue /= n2;
                    position3.doubleValue2 /= n2;
                    double d7 = position3.sqrt_double();
                    if (d7 > 0.0) {
                        position3.run();
                        position2.handlePosition(position3);
                    } else {
                        position2.handleRandom(random, d2, d3, d4, d5);
                    }

                    flag2 = true;
                }

                if (position2.check(d2, d3, d4, d5)) {
                    flag2 = true;
                }
            }

            if (!flag2) {
                CommandSpreadPlayers.Position[] positions2 = positions;
                int length = positions.length;

                for (int i = 0; i < length; i++) {
                    CommandSpreadPlayers.Position position2 = positions2[i];
                    if (!position2.checkWorld(world)) {
                        position2.handleRandom(random, d2, d3, d4, d5);
                        flag2 = true;
                    }
                }
            }
        }

        if (n >= 10000) {
            throw new CommandException(
                "commands.spreadplayers.failure." + (flag ? "teams" : "players"), positions.length, position.doubleValue, position.doubleValue2, String.format("%.2f", d6)
            );
        } else {
            return n;
        }
    }

    public double computeDouble(List list, World world, CommandSpreadPlayers.Position[] positions, boolean flag) {
        double d = 0.0;
        int n = 0;
        HashMap newHashMap = Maps.newHashMap();

        for (int i = 0; i < list.size(); i++) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)list.get(i);
            CommandSpreadPlayers.Position position;
            if (flag) {
                Team team = entityLivingBase instanceof EntityPlayer ? entityLivingBase.getTeam() : null;
                if (!newHashMap.containsKey(team)) {
                    newHashMap.put(team, positions[n++]);
                }

                position = (CommandSpreadPlayers.Position)newHashMap.get(team);
            } else {
                position = positions[n++];
            }

            entityLivingBase.setPositionAndUpdate(
                MathHelper.floor_double(position.doubleValue) + 0.5F, position.computeInt(world), MathHelper.floor_double(position.doubleValue2) + 0.5
            );
            double d2 = Double.MAX_VALUE;

            for (int j = 0; j < positions.length; j++) {
                if (position != positions[j]) {
                    double sqrt = position.sqrt(positions[j]);
                    d2 = Math.min(sqrt, d2);
                }
            }

            d += d2;
        }

        return d / list.size();
    }

    public CommandSpreadPlayers.Position[] getPositionArray(Random random, int n, double d, double d2, double d3, double d4) {
        CommandSpreadPlayers.Position[] positions = new CommandSpreadPlayers.Position[n];

        for (int i = 0; i < positions.length; i++) {
            CommandSpreadPlayers.Position position = new CommandSpreadPlayers.Position();
            position.handleRandom(random, d, d2, d3, d4);
            positions[i] = position;
        }

        return positions;
    }

    public static class Position {
        public double doubleValue;
        public double doubleValue2;

        public Position() {
        }

        public Position(double d, double d2) {
            this.doubleValue = d;
            this.doubleValue2 = d2;
        }

        public double sqrt(CommandSpreadPlayers.Position position) {
            double d = this.doubleValue - position.doubleValue;
            double d2 = this.doubleValue2 - position.doubleValue2;
            return Math.sqrt(d * d + d2 * d2);
        }

        public void run() {
            double d = this.sqrt_double();
            this.doubleValue /= d;
            this.doubleValue2 /= d;
        }

        public float sqrt_double() {
            return MathHelper.sqrt_double(this.doubleValue * this.doubleValue + this.doubleValue2 * this.doubleValue2);
        }

        public void handlePosition(CommandSpreadPlayers.Position position) {
            this.doubleValue = this.doubleValue - position.doubleValue;
            this.doubleValue2 = this.doubleValue2 - position.doubleValue2;
        }

        public boolean check(double d, double d2, double d3, double d4) {
            boolean flag = false;
            if (this.doubleValue < d) {
                this.doubleValue = d;
                flag = true;
            } else if (this.doubleValue > d3) {
                this.doubleValue = d3;
                flag = true;
            }

            if (this.doubleValue2 < d2) {
                this.doubleValue2 = d2;
                flag = true;
            } else if (this.doubleValue2 > d4) {
                this.doubleValue2 = d4;
                flag = true;
            }

            return flag;
        }

        public int computeInt(World world) {
            int n = MathHelper.floor_double(this.doubleValue);
            int n2 = MathHelper.floor_double(this.doubleValue2);

            for (int i = 256; i > 0; i--) {
                if (world.getBlock(n, i, n2).getMaterial() != Material.air) {
                    return i + 1;
                }
            }

            return 257;
        }

        public boolean checkWorld(World world) {
            int n = MathHelper.floor_double(this.doubleValue);
            int n2 = MathHelper.floor_double(this.doubleValue2);
            short s = 256;
            if (s <= 0) {
                return false;
            }

            Material material = world.getBlock(n, s, n2).getMaterial();
            return !material.isLiquid() && material != Material.fire;
        }

        public void handleRandom(Random random, double d, double d2, double d3, double d4) {
            this.doubleValue = MathHelper.getRandomDoubleInRange(random, d, d3);
            this.doubleValue2 = MathHelper.getRandomDoubleInRange(random, d2, d4);
        }
    }
}
