package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityAIFollowOwner extends EntityAIBase {
   public EntityTameable thePet;
   public EntityLivingBase theOwner;
   public World theWorld;
   public double followSpeed;
   public PathNavigate petPathfinder;
   public int updateCountdownTicks;
   public float minDist;
   public float maxDist;
   public boolean flag;

   public EntityAIFollowOwner(EntityTameable thePetIn, double followSpeedIn, float minDistIn, float maxDistIn) {
      this.thePet = thePetIn;
      this.theWorld = thePetIn.worldObj;
      this.followSpeed = followSpeedIn;
      this.petPathfinder = thePetIn.getNavigator();
      this.minDist = minDistIn;
      this.maxDist = maxDistIn;
      this.setMutexBits(3);
      if (!(thePetIn.getNavigator() instanceof PathNavigateGround)) {
         throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
      }
   }

   @Override
   public boolean shouldExecute() {
      EntityLivingBase owner = this.thePet.getOwner();
      if (owner == null) {
         return false;
      }

      if (owner instanceof EntityPlayer && ((EntityPlayer)owner).isSpectator()) {
         return false;
      }

      if (this.thePet.isSitting()) {
         return false;
      }

      if (this.thePet.getDistanceSqToEntity(owner) < this.minDist * this.minDist) {
         return false;
      }

      this.theOwner = owner;
      return true;
   }

   @Override
   public boolean continueExecuting() {
      return !this.petPathfinder.noPath() && this.thePet.getDistanceSqToEntity(this.theOwner) > this.maxDist * this.maxDist && !this.thePet.isSitting();
   }

   @Override
   public void startExecuting() {
      this.updateCountdownTicks = 0;
      this.flag = ((PathNavigateGround)this.thePet.getNavigator()).getAvoidsWater();
      ((PathNavigateGround)this.thePet.getNavigator()).setAvoidsWater(false);
   }

   @Override
   public void resetTask() {
      this.theOwner = null;
      this.petPathfinder.clearPathEntity();
      ((PathNavigateGround)this.thePet.getNavigator()).setAvoidsWater(true);
   }

   public boolean canTeleportTo(BlockPos blockPos) {
      IBlockState blockState = this.theWorld.getBlockState(blockPos);
      Block block = blockState.getBlock();
      return block == Blocks.air ? true : !block.isFullCube();
   }

   @Override
   public void updateTask() {
      this.thePet.getLookHelper().setLookPositionWithEntity(this.theOwner, 10.0F, this.thePet.getVerticalFaceSpeed());
      if (!this.thePet.isSitting() && --this.updateCountdownTicks <= 0) {
         this.updateCountdownTicks = 10;
         if (!this.petPathfinder.tryMoveToEntityLiving(this.theOwner, this.followSpeed)
            && !this.thePet.getLeashed()
            && this.thePet.getDistanceSqToEntity(this.theOwner) >= 144.0) {
            int n = MathHelper.floor_double(this.theOwner.posX) - 2;
            int n2 = MathHelper.floor_double(this.theOwner.posZ) - 2;
            int n3 = MathHelper.floor_double(this.theOwner.getEntityBoundingBox().minY);

            for (int i = 0; i <= 4; i++) {
               for (int j = 0; j <= 4; j++) {
                  if ((i < 1 || j < 1 || i > 3 || j > 3)
                     && World.doesBlockHaveSolidTopSurface(this.theWorld, new BlockPos(n + i, n3 - 1, n2 + j))
                     && this.canTeleportTo(new BlockPos(n + i, n3, n2 + j))
                     && this.canTeleportTo(new BlockPos(n + i, n3 + 1, n2 + j))) {
                     this.thePet.setLocationAndAngles(n + i + 0.5F, n3, n2 + j + 0.5F, this.thePet.rotationYaw, this.thePet.rotationPitch);
                     this.petPathfinder.clearPathEntity();
                     return;
                  }
               }
            }
         }
      }
   }
}
