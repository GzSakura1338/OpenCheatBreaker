package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ActiveRenderInfo {
   public static IntBuffer VIEWPORT = GLAllocation.createDirectIntBuffer(16);
   public static FloatBuffer MODELVIEW = GLAllocation.createDirectFloatBuffer(16);
   public static FloatBuffer PROJECTION = GLAllocation.createDirectFloatBuffer(16);
   public static FloatBuffer OBJECTCOORDS = GLAllocation.createDirectFloatBuffer(3);
   public static Vec3 position = new Vec3(0.0, 0.0, 0.0);
   public static float rotationX;
   public static float rotationXZ;
   public static float rotationZ;
   public static float rotationYZ;
   public static float rotationXY;

   public static void updateRenderInfo(EntityPlayer entityplayerIn, boolean flag) {
      GlStateManager.getFloat(2982, MODELVIEW);
      GlStateManager.getFloat(2983, PROJECTION);
      GL11.glGetInteger(2978, VIEWPORT);
      float f = (VIEWPORT.get(0) + VIEWPORT.get(2)) / 2;
      float f2 = (VIEWPORT.get(1) + VIEWPORT.get(3)) / 2;
      GLU.gluUnProject(f, f2, 0.0F, MODELVIEW, PROJECTION, VIEWPORT, OBJECTCOORDS);
      position = new Vec3(OBJECTCOORDS.get(0), OBJECTCOORDS.get(1), OBJECTCOORDS.get(2));
      int n = flag ? 1 : 0;
      float rotationPitch = entityplayerIn.rotationPitch;
      float rotationYaw = entityplayerIn.rotationYaw;
      rotationX = MathHelper.cos(rotationYaw * (float) Math.PI / 180.0F) * (1 - n * 2);
      rotationZ = MathHelper.sin(rotationYaw * (float) Math.PI / 180.0F) * (1 - n * 2);
      rotationYZ = -rotationZ * MathHelper.sin(rotationPitch * (float) Math.PI / 180.0F) * (1 - n * 2);
      rotationXY = rotationX * MathHelper.sin(rotationPitch * (float) Math.PI / 180.0F) * (1 - n * 2);
      rotationXZ = MathHelper.cos(rotationPitch * (float) Math.PI / 180.0F);
   }

   public static Vec3 projectViewFromEntity(Entity entity, double d) {
      double d2 = entity.prevPosX + (entity.posX - entity.prevPosX) * d;
      double d3 = entity.prevPosY + (entity.posY - entity.prevPosY) * d;
      double d4 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * d;
      double d5 = d2 + position.xCoord;
      double d6 = d3 + position.yCoord;
      double d7 = d4 + position.zCoord;
      return new Vec3(d5, d6, d7);
   }

   public static Block getBlockAtEntityViewpoint(World worldIn, Entity entity, float f) {
      Vec3 projectViewFromEntity2 = projectViewFromEntity(entity, f);
      BlockPos blockPos = new BlockPos(projectViewFromEntity2);
      IBlockState blockState = worldIn.getBlockState(blockPos);
      Block block = blockState.getBlock();
      if (block.getMaterial().isLiquid()) {
         float f2 = 0.0F;
         if (blockState.getBlock() instanceof BlockLiquid) {
            f2 = BlockLiquid.getLiquidHeightPercent(blockState.getValue(BlockLiquid.LEVEL)) - 0.11111111F;
         }

         float f3 = blockPos.getY() + 1 - f2;
         if (projectViewFromEntity2.yCoord >= f3) {
            block = worldIn.getBlockState(blockPos.up()).getBlock();
         }
      }

      return block;
   }

   public static Vec3 getPosition() {
      return position;
   }

   public static float getRotationX() {
      return rotationX;
   }

   public static float getRotationXZ() {
      return rotationXZ;
   }

   public static float getRotationZ() {
      return rotationZ;
   }

   public static float getRotationYZ() {
      return rotationYZ;
   }

   public static float getRotationXY() {
      return rotationXY;
   }
}
