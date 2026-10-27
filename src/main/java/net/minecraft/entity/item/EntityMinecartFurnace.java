package net.minecraft.entity.item;

import net.minecraft.block.BlockFurnace;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMinecartFurnace extends EntityMinecart {
   public int fuel;
   public double pushX;
   public double pushZ;

   public EntityMinecartFurnace(World worldIn) {
      super(worldIn);
   }

   public EntityMinecartFurnace(World worldIn, double x, double y, double z) {
      super(worldIn, x, y, z);
   }

   @Override
   public EntityMinecart.EnumMinecartType getMinecartType() {
      return EntityMinecart.EnumMinecartType.FURNACE;
   }

   @Override
   public void entityInit() {
      super.entityInit();
      this.dataWatcher.addObject(16, new Byte((byte)0));
   }

   @Override
   public void onUpdate() {
      super.onUpdate();
      if (this.fuel > 0) {
         this.fuel--;
      }

      if (this.fuel <= 0) {
         this.pushX = this.pushZ = 0.0;
      }

      this.setMinecartPowered(this.fuel > 0);
      if (this.isMinecartPowered() && this.rand.nextInt(4) == 0) {
         this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_LARGE, this.posX, this.posY + 0.8, this.posZ, 0.0, 0.0, 0.0);
      }
   }

   @Override
   public double getMaximumSpeed() {
      return 0.2;
   }

   @Override
   public void killMinecart(DamageSource source) {
      super.killMinecart(source);
      if (!source.isExplosion() && this.worldObj.getGameRules().getBoolean("doEntityDrops")) {
         this.entityDropItem(new ItemStack(Blocks.furnace, 1), 0.0F);
      }
   }

   @Override
   public void moveOnRail(BlockPos blockPos, IBlockState blockState) {
      super.moveOnRail(blockPos, blockState);
      double d = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (d > 1.0E-4 && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001) {
         d = MathHelper.sqrt_double(d);
         this.pushX /= d;
         this.pushZ /= d;
         if (this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0) {
            this.pushX = 0.0;
            this.pushZ = 0.0;
         } else {
            double d2 = d / this.getMaximumSpeed();
            this.pushX *= d2;
            this.pushZ *= d2;
         }
      }
   }

   @Override
   public void applyDrag() {
      double d = this.pushX * this.pushX + this.pushZ * this.pushZ;
      if (d > 1.0E-4) {
         d = MathHelper.sqrt_double(d);
         this.pushX /= d;
         this.pushZ /= d;
         double d2 = 1.0;
         this.motionX *= 0.8F;
         this.motionY *= 0.0;
         this.motionZ *= 0.8F;
         this.motionX = this.motionX + this.pushX * d2;
         this.motionZ = this.motionZ + this.pushZ * d2;
      } else {
         this.motionX *= 0.98F;
         this.motionY *= 0.0;
         this.motionZ *= 0.98F;
      }

      super.applyDrag();
   }

   @Override
   public boolean interactFirst(EntityPlayer playerIn) {
      ItemStack currentItem = playerIn.inventory.getCurrentItem();
      if (currentItem != null && currentItem.getItem() == Items.coal) {
         if (!playerIn.capabilities.isCreativeMode && --currentItem.stackSize == 0) {
            playerIn.inventory.setInventorySlotContents(playerIn.inventory.currentItem, (ItemStack)null);
         }

         this.fuel += 3600;
      }

      this.pushX = this.posX - playerIn.posX;
      this.pushZ = this.posZ - playerIn.posZ;
      return true;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setDouble("PushX", this.pushX);
      tagCompound.setDouble("PushZ", this.pushZ);
      tagCompound.setShort("Fuel", (short)this.fuel);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.pushX = tagCompund.getDouble("PushX");
      this.pushZ = tagCompund.getDouble("PushZ");
      this.fuel = tagCompund.getShort("Fuel");
   }

   public boolean isMinecartPowered() {
      return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
   }

   public void setMinecartPowered(boolean flag) {
      if (flag) {
         this.dataWatcher.updateObject(16, (byte)(this.dataWatcher.getWatchableObjectByte(16) | 1));
      } else {
         this.dataWatcher.updateObject(16, (byte)(this.dataWatcher.getWatchableObjectByte(16) & -2));
      }
   }

   @Override
   public IBlockState getDefaultDisplayTile() {
      return (this.isMinecartPowered() ? Blocks.lit_furnace : Blocks.furnace).getDefaultState().withProperty(BlockFurnace.FACING, EnumFacing.NORTH);
   }
}
