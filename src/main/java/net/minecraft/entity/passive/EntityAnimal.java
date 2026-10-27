package net.minecraft.entity.passive;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public abstract class EntityAnimal extends EntityAgeable implements IAnimals {
   public Block spawnableBlock = Blocks.grass;
   public int inLove;
   public EntityPlayer playerInLove;

   public EntityAnimal(World worldIn) {
      super(worldIn);
   }

   @Override
   public void updateAITasks() {
      if (this.getGrowingAge() != 0) {
         this.inLove = 0;
      }

      super.updateAITasks();
   }

   @Override
   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getGrowingAge() != 0) {
         this.inLove = 0;
      }

      if (this.inLove > 0) {
         this.inLove--;
         if (this.inLove % 10 == 0) {
            double d = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            double d3 = this.rand.nextGaussian() * 0.02;
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.HEART,
                  this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  this.posY + 0.5 + this.rand.nextFloat() * this.height,
                  this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  d,
                  d2,
                  d3
               );
         }
      }
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      this.inLove = 0;
      return super.attackEntityFrom(source, amount);
   }

   @Override
   public float getBlockPathWeight(BlockPos pos) {
      return this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.grass ? 10.0F : this.worldObj.getLightBrightness(pos) - 0.5F;
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      super.writeEntityToNBT(tagCompound);
      tagCompound.setInteger("InLove", this.inLove);
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      super.readEntityFromNBT(tagCompund);
      this.inLove = tagCompund.getInteger("InLove");
   }

   @Override
   public boolean getCanSpawnHere() {
      int n = MathHelper.floor_double(this.posX);
      int n2 = MathHelper.floor_double(this.getEntityBoundingBox().minY);
      int n3 = MathHelper.floor_double(this.posZ);
      BlockPos blockPos = new BlockPos(n, n2, n3);
      return this.worldObj.getBlockState(blockPos.down()).getBlock() == this.spawnableBlock && this.worldObj.getLight(blockPos) > 8 && super.getCanSpawnHere();
   }

   @Override
   public int getTalkInterval() {
      return 120;
   }

   @Override
   public boolean canDespawn() {
      return false;
   }

   @Override
   public int getExperiencePoints(EntityPlayer player) {
      return 1 + this.worldObj.rand.nextInt(3);
   }

   public boolean isBreedingItem(ItemStack stack) {
      return stack == null ? false : stack.getItem() == Items.wheat;
   }

   @Override
   public boolean interact(EntityPlayer player) {
      ItemStack currentItem = player.inventory.getCurrentItem();
      if (currentItem != null) {
         if (this.isBreedingItem(currentItem) && this.getGrowingAge() == 0 && this.inLove <= 0) {
            this.consumeItemFromStack(player, currentItem);
            this.setInLove(player);
            return true;
         }

         if (this.isChild() && this.isBreedingItem(currentItem)) {
            this.consumeItemFromStack(player, currentItem);
            this.process((int)(-this.getGrowingAge() / 20 * 0.1F), true);
            return true;
         }
      }

      return super.interact(player);
   }

   public void consumeItemFromStack(EntityPlayer player, ItemStack stack) {
      if (!player.capabilities.isCreativeMode) {
         stack.stackSize--;
         if (stack.stackSize <= 0) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, (ItemStack)null);
         }
      }
   }

   public void setInLove(EntityPlayer player) {
      this.inLove = 600;
      this.playerInLove = player;
      this.worldObj.setEntityState(this, (byte)18);
   }

   public EntityPlayer getPlayerInLove() {
      return this.playerInLove;
   }

   public boolean isInLove() {
      return this.inLove > 0;
   }

   public void resetInLove() {
      this.inLove = 0;
   }

   public boolean canMateWith(EntityAnimal otherAnimal) {
      return otherAnimal == this ? false : (otherAnimal.getClass() != this.getClass() ? false : this.isInLove() && otherAnimal.isInLove());
   }

   @Override
   public void handleStatusUpdate(byte id) {
      if (id == 18) {
         for (int i = 0; i < 7; i++) {
            double d = this.rand.nextGaussian() * 0.02;
            double d2 = this.rand.nextGaussian() * 0.02;
            double d3 = this.rand.nextGaussian() * 0.02;
            this.worldObj
               .spawnParticle(
                  EnumParticleTypes.HEART,
                  this.posX + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  this.posY + 0.5 + this.rand.nextFloat() * this.height,
                  this.posZ + this.rand.nextFloat() * this.width * 2.0F - this.width,
                  d,
                  d2,
                  d3
               );
         }
      } else {
         super.handleStatusUpdate(id);
      }
   }
}
