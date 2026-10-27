package net.minecraft.entity.ai;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecart.EnumMinecartType;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class EntityMinecartMobSpawner extends EntityMinecart {
   public MobSpawnerBaseLogic mobSpawnerLogic = new MobSpawnerBaseLogic() {
      @Override
      public void addBlockEvent(int id) {
         EntityMinecartMobSpawner.this.worldObj.setEntityState(EntityMinecartMobSpawner.this, (byte)id);
      }

      @Override
      public World getSpawnerWorld() {
         return EntityMinecartMobSpawner.this.worldObj;
      }

      @Override
      public BlockPos getSpawnerPosition() {
         return new BlockPos(EntityMinecartMobSpawner.this);
      }
   };

   public EntityMinecartMobSpawner(World worldIn) {
      super(worldIn);
   }

   public EntityMinecartMobSpawner(World worldIn, double d, double d2, double d3) {
      super(worldIn, d, d2, d3);
   }

   @Override
   public EnumMinecartType getMinecartType() {
      return EnumMinecartType.SPAWNER;
   }

   @Override
   public IBlockState getDefaultDisplayTile() {
      return Blocks.mob_spawner.getDefaultState();
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.mobSpawnerLogic.readFromNBT(tagCompund);
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      this.mobSpawnerLogic.writeToNBT(tagCompound);
   }

   @Override
   public void handleStatusUpdate(byte id) {
      this.mobSpawnerLogic.setDelayToMin(id);
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      this.mobSpawnerLogic.updateSpawner();
   }

   public MobSpawnerBaseLogic getSpawnerBehavior() {
      return this.mobSpawnerLogic;
   }
}
