package net.minecraft.tileentity;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.MobSpawnerBaseLogic.WeightedRandomMinecart;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;

public class TileEntityMobSpawner extends TileEntity implements ITickable {
   public MobSpawnerBaseLogic spawnerLogic = new MobSpawnerBaseLogic() {
      @Override
      public void addBlockEvent(int id) {
         TileEntityMobSpawner.this.worldObj.addBlockEvent(TileEntityMobSpawner.this.pos, Blocks.mob_spawner, id, 0);
      }

      @Override
      public World getSpawnerWorld() {
         return TileEntityMobSpawner.this.worldObj;
      }

      @Override
      public BlockPos getSpawnerPosition() {
         return TileEntityMobSpawner.this.pos;
      }

      @Override
      public void setRandomEntity(WeightedRandomMinecart weightedRandomMinecart) {
         super.setRandomEntity(weightedRandomMinecart);
         if (this.getSpawnerWorld() != null) {
            this.getSpawnerWorld().markBlockForUpdate(TileEntityMobSpawner.this.pos);
         }
      }
   };

   @Override
   public void readFromNBT(NBTTagCompound compound) {
      super.readFromNBT(compound);
      this.spawnerLogic.readFromNBT(compound);
   }

   @Override
   public void writeToNBT(NBTTagCompound compound) {
      super.writeToNBT(compound);
      this.spawnerLogic.writeToNBT(compound);
   }

   @Override
   public void update() {
      this.spawnerLogic.updateSpawner();
   }

   @Override
   public Packet getDescriptionPacket() {
      NBTTagCompound nbttagCompound = new NBTTagCompound();
      this.writeToNBT(nbttagCompound);
      nbttagCompound.removeTag("SpawnPotentials");
      return new S35PacketUpdateTileEntity(this.pos, 1, nbttagCompound);
   }

   @Override
   public boolean receiveClientEvent(int id, int type) {
      return this.spawnerLogic.setDelayToMin(id) ? true : super.receiveClientEvent(id, type);
   }

   @Override
   public boolean alwaysFalse() {
      return true;
   }

   public MobSpawnerBaseLogic getSpawnerBaseLogic() {
      return this.spawnerLogic;
   }
}
