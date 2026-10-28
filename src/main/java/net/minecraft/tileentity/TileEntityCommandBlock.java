package net.minecraft.tileentity;

import io.netty.buffer.ByteBuf;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class TileEntityCommandBlock extends TileEntity {
    public CommandBlockLogic executor = new CommandBlockLogic() {
        @Override
        public ChunkCoordinates getCommandSenderPosition() {
            return new ChunkCoordinates(TileEntityCommandBlock.this.xCoord, TileEntityCommandBlock.this.yCoord, TileEntityCommandBlock.this.zCoord);
        }

        @Override
        public World getEntityWorld() {
            return TileEntityCommandBlock.this.getWorld();
        }

        @Override
        public void setCommand(String str) {
            super.setCommand(str);
            TileEntityCommandBlock.this.markDirty();
        }

        @Override
        public void run() {
            TileEntityCommandBlock.this.getWorld()
                .markBlockForUpdate(TileEntityCommandBlock.this.xCoord, TileEntityCommandBlock.this.yCoord, TileEntityCommandBlock.this.zCoord);
        }

        @Override
        public int constantInt() {
            return 0;
        }

        @Override
        public void write(ByteBuf byteBuf) {
            byteBuf.writeInt(TileEntityCommandBlock.this.xCoord);
            byteBuf.writeInt(TileEntityCommandBlock.this.yCoord);
            byteBuf.writeInt(TileEntityCommandBlock.this.zCoord);
        }
    };

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        this.executor.writeDataToNBT(compound);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.executor.readDataFromNBT(compound);
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbttagCompound = new NBTTagCompound();
        this.writeToNBT(nbttagCompound);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 2, nbttagCompound);
    }

    public CommandBlockLogic getCommandExecutor() {
        return this.executor;
    }
}
