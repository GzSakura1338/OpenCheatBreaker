package net.minecraft.entity;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.util.IChatComponent.Serializer;
import net.minecraft.world.World;

public class EntityMinecartCommandBlock extends EntityMinecart {
    public CommandBlockLogic executor = new CommandBlockLogic() {
        @Override
        public void run() {
            EntityMinecartCommandBlock.this.getDataWatcher().updateObject(23, this.getCustomName());
            EntityMinecartCommandBlock.this.getDataWatcher().updateObject(24, Serializer.componentToJson(this.getLastOutput()));
        }

        @Override
        public int constantInt() {
            return 1;
        }

        @Override
        public void write(ByteBuf byteBuf) {
            byteBuf.writeInt(EntityMinecartCommandBlock.this.getEntityId());
        }

        @Override
        public ChunkCoordinates getCommandSenderPosition() {
            return new ChunkCoordinates(
                MathHelper.floor_double(EntityMinecartCommandBlock.this.posX),
                MathHelper.floor_double(EntityMinecartCommandBlock.this.posY + 0.5),
                MathHelper.floor_double(EntityMinecartCommandBlock.this.posZ)
            );
        }

        @Override
        public World getEntityWorld() {
            return EntityMinecartCommandBlock.this.worldObj;
        }
    };
    public int lastExecuted = 0;

    public EntityMinecartCommandBlock(World world) {
        super(world);
    }

    public EntityMinecartCommandBlock(World world, double d, double d2, double d3) {
        super(world, d, d2, d3);
    }

    @Override
    public void entityInit() {
        super.entityInit();
        this.getDataWatcher().addObject(23, "");
        this.getDataWatcher().addObject(24, "");
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound tagCompund) {
        super.readEntityFromNBT(tagCompund);
        this.executor.readDataFromNBT(tagCompund);
        this.getDataWatcher().updateObject(23, this.getCommandExecutor().getCustomName());
        this.getDataWatcher().updateObject(24, Serializer.componentToJson(this.getCommandExecutor().getLastOutput()));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound tagCompound) {
        super.writeEntityToNBT(tagCompound);
        this.executor.writeDataToNBT(tagCompound);
    }

    @Override
    public int getMinecartType() {
        return 6;
    }

    @Override
    public Block getDefaultDisplayTile() {
        return Blocks.command_block;
    }

    public CommandBlockLogic getCommandExecutor() {
        return this.executor;
    }

    @Override
    public void onActivatorRailPass(int n, int n2, int n3, boolean flag) {
        if (flag && this.ticksExisted - this.lastExecuted >= 4) {
            this.getCommandExecutor().execute(this.worldObj);
            this.lastExecuted = this.ticksExisted;
        }
    }

    @Override
    public boolean interactFirst(EntityPlayer player) throws java.io.IOException {
        if (this.worldObj.isRemote) {
            player.handleCommandBlockLogic(this.getCommandExecutor());
        }

        return super.interactFirst(player);
    }

    @Override
    public void process(int n) {
        super.process(n);
        if (n == 24) {
            try {
                this.executor.setLastOutput(Serializer.jsonToComponent(this.getDataWatcher().getWatchableObjectString(24)));
            } catch (Throwable e) {
            }
        } else if (n == 23) {
            this.executor.setCommand(this.getDataWatcher().getWatchableObjectString(23));
        }
    }
}
