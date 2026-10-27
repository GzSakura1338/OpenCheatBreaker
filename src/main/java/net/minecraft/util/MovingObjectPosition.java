package net.minecraft.util;

import net.minecraft.entity.Entity;

public class MovingObjectPosition {
   public MovingObjectPosition.MovingObjectType typeOfHit;
   public BlockPos blockPos;
   public EnumFacing sideHit;
   public Vec3 hitVec;
   public Entity entityHit;

   public MovingObjectPosition(Vec3 hitVecIn, EnumFacing facing, BlockPos blockPosIn) {
      this(MovingObjectPosition.MovingObjectType.BLOCK, hitVecIn, facing, blockPosIn);
   }

   public MovingObjectPosition(Vec3 vec3, EnumFacing facing) {
      this(MovingObjectPosition.MovingObjectType.BLOCK, vec3, facing, BlockPos.ORIGIN);
   }

   public MovingObjectPosition(Entity entityIn) {
      this(entityIn, new Vec3(entityIn.posX, entityIn.posY, entityIn.posZ));
   }

   public MovingObjectPosition(MovingObjectPosition.MovingObjectType typeOfHitIn, Vec3 hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn) {
      this.typeOfHit = typeOfHitIn;
      this.blockPos = blockPosIn;
      this.sideHit = sideHitIn;
      this.hitVec = new Vec3(hitVecIn.xCoord, hitVecIn.yCoord, hitVecIn.zCoord);
   }

   public MovingObjectPosition(Entity entityHitIn, Vec3 hitVecIn) {
      this.typeOfHit = MovingObjectPosition.MovingObjectType.ENTITY;
      this.entityHit = entityHitIn;
      this.hitVec = hitVecIn;
   }

   public BlockPos getBlockPos() {
      return this.blockPos;
   }

   @Override
   public String toString() {
      return "HitResult{type="
         + this.typeOfHit
         + ", blockpos="
         + this.blockPos
         + ", f="
         + this.sideHit
         + ", pos="
         + this.hitVec
         + ", entity="
         + this.entityHit
         + '}';
   }

   public enum MovingObjectType {
      MISS,
      BLOCK,
      ENTITY;
   }
}
