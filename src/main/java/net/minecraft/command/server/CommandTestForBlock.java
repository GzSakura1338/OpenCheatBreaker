package net.minecraft.command.server;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandTestForBlock extends CommandBase {
    @Override
    public String getCommandName() {
        return "testforblock";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.testforblock.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length >= 4) {
            int posX = sender.getCommandSenderPosition().posX;
            int posY = sender.getCommandSenderPosition().posY;
            int posZ = sender.getCommandSenderPosition().posZ;
            posX = MathHelper.floor_double(clamp_coord(sender, posX, args[0]));
            posY = MathHelper.floor_double(clamp_coord(sender, posY, args[1]));
            posZ = MathHelper.floor_double(clamp_coord(sender, posZ, args[2]));
            Block blockFromName = Block.getBlockFromName(args[3]);
            if (blockFromName == null) {
                throw new NumberInvalidException("commands.setblock.notFound", args[3]);
            }

            int n = -1;
            if (args.length >= 5) {
                n = parseIntBounded(sender, args[4], -1, 15);
            }

            World entityWorld = sender.getEntityWorld();
            if (!entityWorld.blockExists(posX, posY, posZ)) {
                throw new CommandException("commands.testforblock.outOfWorld");
            }

            NBTTagCompound nbttagCompound = new NBTTagCompound();
            boolean flag = false;
            if (args.length >= 6 && blockFromName.hasTileEntity()) {
                String unformattedText = getChatComponentFromNthArg(sender, args, 5).getUnformattedText();

                try {
                    NBTBase createNBTException = JsonToNBT.createNBTException(unformattedText);
                    if (!(createNBTException instanceof NBTTagCompound)) {
                        throw new CommandException("commands.setblock.tagError", "Not a valid tag");
                    }

                    nbttagCompound = (NBTTagCompound)createNBTException;
                    flag = true;
                } catch (NBTException e) {
                    throw new CommandException("commands.setblock.tagError", e.getMessage());
                }
            }

            Block block = entityWorld.getBlock(posX, posY, posZ);
            if (block != blockFromName) {
                throw new CommandException("commands.testforblock.failed.tile", posX, posY, posZ, block.getLocalizedName(), blockFromName.getLocalizedName());
            }

            if (n > -1) {
                int blockMetadata = entityWorld.getBlockMetadata(posX, posY, posZ);
                if (blockMetadata != n) {
                    throw new CommandException("commands.testforblock.failed.data", posX, posY, posZ, blockMetadata, n);
                }
            }

            if (flag) {
                TileEntity tileEntity = entityWorld.getTileEntity(posX, posY, posZ);
                if (tileEntity == null) {
                    throw new CommandException("commands.testforblock.failed.tileEntity", posX, posY, posZ);
                }

                NBTTagCompound nbttagCompound2 = new NBTTagCompound();
                tileEntity.writeToNBT(nbttagCompound2);
                if (!this.checkNBTBase(nbttagCompound, nbttagCompound2)) {
                    throw new CommandException("commands.testforblock.failed.nbt", posX, posY, posZ);
                }
            }

            sender.addChatMessage(new ChatComponentTranslation("commands.testforblock.success", posX, posY, posZ));
        } else {
            throw new WrongUsageException("commands.testforblock.usage");
        }
    }

    public boolean checkNBTBase(NBTBase nbtbase, NBTBase nbtbase2) {
        if (nbtbase == nbtbase2) {
            return true;
        }

        if (nbtbase == null) {
            return true;
        }

        if (nbtbase2 == null) {
            return false;
        }

        if (!nbtbase.getClass().equals(nbtbase2.getClass())) {
            return false;
        }

        if (!(nbtbase instanceof NBTTagCompound)) {
            return nbtbase.equals(nbtbase2);
        }

        NBTTagCompound nbttagCompound = (NBTTagCompound)nbtbase;
        NBTTagCompound nbttagCompound2 = (NBTTagCompound)nbtbase2;

        for (String str : (Iterable<String>)nbttagCompound.getKeySet()) {
            NBTBase tag = nbttagCompound.getTag(str);
            if (!this.checkNBTBase(tag, nbttagCompound2.getTag(str))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 4 ? getListOfStringsFromIterableMatchingLastWord(args, Block.blockRegistry.getKeys()) : null;
    }
}
