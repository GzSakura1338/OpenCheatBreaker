package net.minecraft.client.particle;

import net.minecraft.world.World;

public class EntityCritFX extends EntitySmokeFX {
   public EntityCritFX(World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double d, double d2, double d3) {
      super(worldIn, xCoordIn, yCoordIn, zCoordIn, d, d2, d3, 2.5F);
   }

   public static class Factory implements IParticleFactory {
      @Override
      public EntityFX getEntityFX(
         int particleID, World worldIn, double xCoordIn, double yCoordIn, double zCoordIn, double xSpeedIn, double ySpeedIn, double zSpeedIn, int... ints
      ) {
         return new EntityCritFX(worldIn, xCoordIn, yCoordIn, zCoordIn, xSpeedIn, ySpeedIn, zSpeedIn);
      }
   }
}
