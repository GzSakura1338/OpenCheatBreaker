package net.minecraft.command.server;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class CommandSetBlock extends CommandBase {
    @Override
    public String getCommandName() {
        return "setblock";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.setblock.usage";
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
            Block blockByText = CommandBase.getBlockByText(sender, args[3]);
            int n = 0;
            if (args.length >= 5) {
                n = parseIntBounded(sender, args[4], 0, 15);
            }

            World entityWorld = sender.getEntityWorld();
            if (!entityWorld.blockExists(posX, posY, posZ)) {
                throw new CommandException("commands.setblock.outOfWorld");
            }

            NBTTagCompound nbttagCompound = new NBTTagCompound();
            boolean flag = false;
            if (args.length >= 7 && blockByText.hasTileEntity()) {
                String unformattedText = getChatComponentFromNthArg(sender, args, 6).getUnformattedText();

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

            if (args.length >= 6) {
                if (args[5].equals("destroy")) {
                    entityWorld.breakBlock(posX, posY, posZ, true);
                } else if (args[5].equals("keep") && !entityWorld.isAirBlock(posX, posY, posZ)) {
                    throw new CommandException("commands.setblock.noChange");
                }
            }

            if (!entityWorld.setBlock(posX, posY, posZ, blockByText, n, 3)) {
                throw new CommandException("commands.setblock.noChange");
            }

            if (flag) {
                TileEntity tileEntity = entityWorld.getTileEntity(posX, posY, posZ);
                if (tileEntity != null) {
                    nbttagCompound.setInteger("x", posX);
                    nbttagCompound.setInteger("y", posY);
                    nbttagCompound.setInteger("z", posZ);
                    tileEntity.readFromNBT(nbttagCompound);
                }
            }

            notifyOperators(sender, this, "commands.setblock.success");
        } else {
            throw new WrongUsageException("commands.setblock.usage");
        }
    }

    @Override
    public List addTabCompletionOptions(ICommandSender sender, String[] args) {
        return args.length == 4
            ? getListOfStringsFromIterableMatchingLastWord(args, Block.blockRegistry.getKeys())
            : (args.length == 6 ? getListOfStringsMatchingLastWord(args, "replace", "destroy", "keep") : null);
    }
}
