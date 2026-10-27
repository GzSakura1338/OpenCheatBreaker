package net.minecraft.tileentity;

import io.netty.buffer.ByteBuf;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class TileEntityCommandBlock extends TileEntity {
   public CommandBlockLogic commandBlockLogic = new CommandBlockLogic() {
      @Override
      public BlockPos getPosition() {
         return TileEntityCommandBlock.this.pos;
      }

      @Override
      public Vec3 getPositionVector() {
         return new Vec3(
            TileEntityCommandBlock.this.pos.getX() + 0.5, TileEntityCommandBlock.this.pos.getY() + 0.5, TileEntityCommandBlock.this.pos.getZ() + 0.5
         );
      }

      @Override
      public World getEntityWorld() {
         return TileEntityCommandBlock.this.getWorld();
      }

      @Override
      public void setCommand(String command) {
         super.setCommand(command);
         TileEntityCommandBlock.this.markDirty();
      }

      @Override
      public void updateCommand() {
         TileEntityCommandBlock.this.getWorld().markBlockForUpdate(TileEntityCommandBlock.this.pos);
      }

      @Override
      public int constantInt() {
         return 0;
      }

      @Override
      public void write(ByteBuf byteBuf) {
         byteBuf.writeInt(TileEntityCommandBlock.this.pos.getX());
         byteBuf.writeInt(TileEntityCommandBlock.this.pos.getY());
         byteBuf.writeInt(TileEntityCommandBlock.this.pos.getZ());
      }

      @Override
      public Entity getCommandSenderEntity() {
         return null;
      }
   };

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      this.commandBlockLogic.writeDataToNBT(compound);
   }

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.commandBlockLogic.readDataFromNBT(compound);
   }

   @Override
   public Packet getDescriptionPacket() {
      NBTTagCompound nbttagCompound = new NBTTagCompound();
      this.writeToNBT(nbttagCompound);
      return new S35PacketUpdateTileEntity(this.pos, 2, nbttagCompound);
   }

   @Override
   public boolean alwaysFalse() {
      return true;
   }

   public CommandBlockLogic getCommandBlockLogic() {
      return this.commandBlockLogic;
   }

   public CommandResultStats getCommandResultStats() {
      return this.commandBlockLogic.getCommandResultStats();
   }
}
