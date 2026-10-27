package net.minecraft.entity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRedstoneDiode;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.World;
import org.apache.commons.lang3.Validate;

public abstract class EntityHanging extends Entity {
   public int tickCounter1;
   public BlockPos hangingPosition;
   public EnumFacing facingDirection;

   public EntityHanging(World worldIn) {
      super(worldIn);
      this.setSize(0.5F, 0.5F);
   }

   public EntityHanging(World worldIn, BlockPos hangingPositionIn) {
      this(worldIn);
      this.hangingPosition = hangingPositionIn;
   }

   @Override
   public void entityInit() {
   }

   public void updateFacingWithBoundingBox(EnumFacing facingDirectionIn) {
      Validate.notNull(facingDirectionIn);
      Validate.isTrue(facingDirectionIn.getAxis().isHorizontal());
      this.facingDirection = facingDirectionIn;
      this.prevRotationYaw = this.rotationYaw = this.facingDirection.getHorizontalIndex() * 90;
      this.updateBoundingBox();
   }

   public void updateBoundingBox() {
      if (this.facingDirection != null) {
         double d = this.hangingPosition.getX() + 0.5;
         double d2 = this.hangingPosition.getY() + 0.5;
         double d3 = this.hangingPosition.getZ() + 0.5;
         double d4 = 0.46875;
         double computeDouble = this.computeDouble(this.getWidthPixels());
         double computeDouble2 = this.computeDouble(this.getHeightPixels());
         d -= this.facingDirection.getFrontOffsetX() * 0.46875;
         d3 -= this.facingDirection.getFrontOffsetZ() * 0.46875;
         d2 += computeDouble2;
         EnumFacing rotateYCCW = this.facingDirection.rotateYCCW();
         d += computeDouble * rotateYCCW.getFrontOffsetX();
         d3 += computeDouble * rotateYCCW.getFrontOffsetZ();
         this.posX = d;
         this.posY = d2;
         this.posZ = d3;
         double widthPixels = this.getWidthPixels();
         double heightPixels = this.getHeightPixels();
         double widthPixels2 = this.getWidthPixels();
         if (this.facingDirection.getAxis() == Axis.Z) {
            widthPixels2 = 1.0;
         } else {
            widthPixels = 1.0;
         }

         widthPixels /= 32.0;
         heightPixels /= 32.0;
         widthPixels2 /= 32.0;
         this.setEntityBoundingBox(new AxisAlignedBB(d - widthPixels, d2 - heightPixels, d3 - widthPixels2, d + widthPixels, d2 + heightPixels, d3 + widthPixels2));
      }
   }

   public double computeDouble(int n) {
      return n % 32 == 0 ? 0.5 : 0.0;
   }

   @Override
   public void onUpdate() {
      this.prevPosX = this.posX;
      this.prevPosY = this.posY;
      this.prevPosZ = this.posZ;
      if (this.tickCounter1++ == 100 && !this.worldObj.isRemote) {
         this.tickCounter1 = 0;
         if (!this.isDead && !this.onValidSurface()) {
            this.setDead();
            this.onBroken((Entity)null);
         }
      }
   }

   public boolean onValidSurface() {
      if (!this.worldObj.getCollidingBoundingBoxes(this, this.getEntityBoundingBox()).isEmpty()) {
         return false;
      }

      int max = Math.max(1, this.getWidthPixels() / 16);
      int max2 = Math.max(1, this.getHeightPixels() / 16);
      BlockPos offset = this.hangingPosition.offset(this.facingDirection.getOpposite());
      EnumFacing rotateYCCW = this.facingDirection.rotateYCCW();

      for (int i = 0; i < max; i++) {
         for (int j = 0; j < max2; j++) {
            BlockPos up = offset.offset(rotateYCCW, i).up(j);
            Block block = this.worldObj.getBlockState(up).getBlock();
            if (!block.getMaterial().isSolid() && !BlockRedstoneDiode.isRedstoneRepeaterBlockID(block)) {
               return false;
            }
         }
      }

      for (Entity entity : this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.getEntityBoundingBox())) {
         if (entity instanceof EntityHanging) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean canBeCollidedWith() {
      return true;
   }

   @Override
   public boolean hitByEntity(Entity entityIn) {
      return entityIn instanceof EntityPlayer ? this.attackEntityFrom(DamageSource.causePlayerDamage((EntityPlayer)entityIn), 0.0F) : false;
   }

   @Override
   public EnumFacing getHorizontalFacing() {
      return this.facingDirection;
   }

   @Override
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.isEntityInvulnerable(source)) {
         return false;
      }

      if (!this.isDead && !this.worldObj.isRemote) {
         this.setDead();
         this.setBeenAttacked();
         this.onBroken(source.getEntity());
      }

      return true;
   }

   @Override
   public void moveEntity(double x, double y, double z) {
      if (!this.worldObj.isRemote && !this.isDead && x * x + y * y + z * z > 0.0) {
         this.setDead();
         this.onBroken((Entity)null);
      }
   }

   @Override
   public void addVelocity(double x, double y, double z) {
      if (!this.worldObj.isRemote && !this.isDead && x * x + y * y + z * z > 0.0) {
         this.setDead();
         this.onBroken((Entity)null);
      }
   }

   @Override
   public void writeEntityToNBT(NBTTagCompound tagCompound) {
      tagCompound.setByte("Facing", (byte)this.facingDirection.getHorizontalIndex());
      tagCompound.setInteger("TileX", this.getHangingPosition().getX());
      tagCompound.setInteger("TileY", this.getHangingPosition().getY());
      tagCompound.setInteger("TileZ", this.getHangingPosition().getZ());
   }

   @Override
   public void readEntityFromNBT(NBTTagCompound tagCompund) {
      this.hangingPosition = new BlockPos(tagCompund.getInteger("TileX"), tagCompund.getInteger("TileY"), tagCompund.getInteger("TileZ"));
      EnumFacing enumFacing;
      if (tagCompund.hasKey("Direction", 99)) {
         enumFacing = EnumFacing.getHorizontal(tagCompund.getByte("Direction"));
         this.hangingPosition = this.hangingPosition.offset(enumFacing);
      } else if (tagCompund.hasKey("Facing", 99)) {
         enumFacing = EnumFacing.getHorizontal(tagCompund.getByte("Facing"));
      } else {
         enumFacing = EnumFacing.getHorizontal(tagCompund.getByte("Dir"));
      }

      this.updateFacingWithBoundingBox(enumFacing);
   }

   public abstract int getWidthPixels();

   public abstract int getHeightPixels();

   public abstract void onBroken(Entity entity);

   @Override
   public boolean shouldSetPosAfterLoading() {
      return false;
   }

   @Override
   public void setPosition(double x, double y, double z) {
      this.posX = x;
      this.posY = y;
      this.posZ = z;
      BlockPos hangingPosition = this.hangingPosition;
      this.hangingPosition = new BlockPos(x, y, z);
      if (!this.hangingPosition.equals(hangingPosition)) {
         this.updateBoundingBox();
         this.isAirBorne = true;
      }
   }

   public BlockPos getHangingPosition() {
      return this.hangingPosition;
   }
}
