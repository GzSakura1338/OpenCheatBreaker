package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldProviderEnd;

public class EntityEnderCrystal extends Entity {
   public int health;
   public int innerRotation;

   public EntityEnderCrystal(World worldIn) {
      super(worldIn);
      this.preventEntitySpawning = true;
      this.setSize(2.0F, 2.0F);
      this.health = 5;
      this.innerRotation = this.rand.nextInt(100000);
   }

   public EntityEnderCrystal(World worldIn, double x, double y, double z) {
      this(worldIn);
      this.setPosition(x, y, z);
   }

   @Override
   public boolean canTriggerWalking() {
      return false;
   }

   @Override
   public void entityInit() {
      this.dataWatcher.addObject(8, this.health);
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      this.innerRotation++;
      this.dataWatcher.updateObject(8, this.health);
      int n = MathHelper.floor_double(this.posX);
      int n2 = MathHelper.floor_double(this.posY);
      int n3 = MathHelper.floor_double(this.posZ);
      if (this.worldObj.provider instanceof WorldProviderEnd && this.worldObj.getBlockState(new BlockPos(n, n2, n3)).getBlock() != Blocks.fire) {
         this.worldObj.setBlockState(new BlockPos(n, n2, n3), Blocks.fire.getDefaultState());
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
   }

   @Override
   public boolean canBeCollidedWith() {
      return true;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (!this.isDead && !this.worldObj.isRemote) {
         this.health = 0;
         if (this.health <= 0) {
            this.setDead();
            if (!this.worldObj.isRemote) {
               this.worldObj.createExplosion((Entity)null, this.posX, this.posY, this.posZ, 6.0F, true);
            }
         }
      }

      return true;
   }
}
