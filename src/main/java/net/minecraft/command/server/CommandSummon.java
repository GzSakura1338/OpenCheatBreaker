package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class CommandSummon extends CommandBase {
    @Override
    public String getCommandName() {
        return "summon";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.summon.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length < 1) {
            throw new WrongUsageException("commands.summon.usage");
        }

        String str = args[0];
        double d = sender.getCommandSenderPosition().posX + 0.5;
        double posY = sender.getCommandSenderPosition().posY;
        double d2 = sender.getCommandSenderPosition().posZ + 0.5;
        if (args.length >= 4) {
            d = clamp_coord(sender, d, args[1]);
            posY = clamp_coord(sender, posY, args[2]);
            d2 = clamp_coord(sender, d2, args[3]);
        }

        World entityWorld = sender.getEntityWorld();
        if (!entityWorld.blockExists((int)d, (int)posY, (int)d2)) {
            notifyOperators(sender, this, "commands.summon.outOfWorld");
        } else {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            boolean flag = false;
            if (args.length >= 5) {
                IChatComponent chatComponentFromNthArg = getChatComponentFromNthArg(sender, args, 4);

                try {
                    NBTBase createNBTException = JsonToNBT.createNBTException(chatComponentFromNthArg.getUnformattedText());
                    if (!(createNBTException instanceof NBTTagCompound)) {
                        notifyOperators(sender, this, "commands.summon.tagError", "Not a valid tag");
                        return;
                    }

                    nbttagCompound = (NBTTagCompound)createNBTException;
                    flag = true;
                } catch (NBTException e) {
                    notifyOperators(sender, this, "commands.summon.tagError", e.getMessage());
                    return;
                }
            }

            nbttagCompound.setString("id", str);
            Entity createEntityFromNBT = EntityList.createEntityFromNBT(nbttagCompound, entityWorld);
            if (createEntityFromNBT == null) {
                notifyOperators(sender, this, "commands.summon.failed");
            } else {
                createEntityFromNBT.setLocationAndAngles(d, posY, d2, createEntityFromNBT.rotationYaw, createEntityFromNBT.rotationPitch);
                if (!flag && createEntityFromNBT instanceof EntityLiving) {
                    ((EntityLiving)createEntityFromNBT).onSpawnWithEgg(null);
                }

                entityWorld.spawnEntityInWorld(createEntityFromNBT);
                Entity entity = createEntityFromNBT;

                for (NBTTagCompound nbttagCompound2 = nbttagCompound; entity != null && nbttagCompound2.hasKey("Riding", 10); nbttagCompound2 = nbttagCompound2.getCompoundTag("Riding")) {
                    Entity createEntityFromNBT2 = EntityList.createEntityFromNBT(nbttagCompound2.getCompoundTag("Riding"), entityWorld);
                    if (createEntityFromNBT2 != null) {
                        createEntityFromNBT2.setLocationAndAngles(d, posY, d2, createEntityFromNBT2.rotationYaw, createEntityFromNBT2.rotationPitch);
                        entityWorld.spawnEntityInWorld(createEntityFromNBT2);
                        entity.mountEntity(createEntityFromNBT2);
                    }

                    entity = createEntityFromNBT2;
                }

                notifyOperators(sender, this, "commands.summon.success");
            }
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, this.getStringArray()) : null;
    }

    public String[] getStringArray() {
        return (String[])EntityList.getSet().toArray(new String[0]);
    }
}
