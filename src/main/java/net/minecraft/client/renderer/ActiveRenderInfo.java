package net.minecraft.client.renderer;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

public class ActiveRenderInfo {
    public static float objectX;
    public static float objectY;
    public static float objectZ;
    public static IntBuffer viewport = GLAllocation.createDirectIntBuffer(16);
    public static FloatBuffer modelview = GLAllocation.createDirectFloatBuffer(16);
    public static FloatBuffer projection = GLAllocation.createDirectFloatBuffer(16);
    public static FloatBuffer objectCoords = GLAllocation.createDirectFloatBuffer(3);
    public static float rotationX;
    public static float rotationXZ;
    public static float rotationZ;
    public static float rotationYZ;
    public static float rotationXY;

    public static void updateRenderInfo(EntityPlayer entityPlayer, boolean flag) {
        GL11.glGetFloat(2982, modelview);
        GL11.glGetFloat(2983, projection);
        GL11.glGetInteger(2978, viewport);
        float f = (viewport.get(0) + viewport.get(2)) / 2;
        float f2 = (viewport.get(1) + viewport.get(3)) / 2;
        GLU.gluUnProject(f, f2, 0.0F, modelview, projection, viewport, objectCoords);
        objectX = objectCoords.get(0);
        objectY = objectCoords.get(1);
        objectZ = objectCoords.get(2);
        float f3 = Minecraft.getMinecraft().currentScreen instanceof GuiContainer ? entityPlayer.rotationPitch : RenderManager.instance.playerViewX;
        float f4 = Minecraft.getMinecraft().currentScreen instanceof GuiContainer ? entityPlayer.rotationYaw : RenderManager.instance.playerViewY;
        rotationX = MathHelper.cos(f4 * (float) Math.PI / 180.0F);
        rotationZ = MathHelper.sin(f4 * (float) Math.PI / 180.0F);
        rotationYZ = -rotationZ * MathHelper.sin(f3 * (float) Math.PI / 180.0F);
        rotationXY = rotationX * MathHelper.sin(f3 * (float) Math.PI / 180.0F);
        rotationXZ = MathHelper.cos(f3 * (float) Math.PI / 180.0F);
    }

    public static Vec3 projectViewFromEntity(EntityLivingBase entityLivingBase, double d) {
        double d2 = entityLivingBase.prevPosX + (entityLivingBase.posX - entityLivingBase.prevPosX) * d;
        double d3 = entityLivingBase.prevPosY + (entityLivingBase.posY - entityLivingBase.prevPosY) * d + entityLivingBase.getEyeHeight();
        double d4 = entityLivingBase.prevPosZ + (entityLivingBase.posZ - entityLivingBase.prevPosZ) * d;
        double d5 = d2 + objectX * 1.0F;
        double d6 = d3 + objectY * 1.0F;
        double d7 = d4 + objectZ * 1.0F;
        return Vec3.createVectorHelper(d5, d6, d7);
    }

    public static Block getBlockAtEntityViewpoint(World world, EntityLivingBase entityLivingBase, float f) {
        Vec3 projectViewFromEntity2 = projectViewFromEntity(entityLivingBase, f);
        ChunkPosition chunkPosition = new ChunkPosition(projectViewFromEntity2);
        Block block = world.getBlock(chunkPosition.chunkPosX, chunkPosition.chunkPosY, chunkPosition.chunkPosZ);
        if (block.getMaterial().isLiquid()) {
            float f2 = BlockLiquid.getLiquidHeightPercent(world.getBlockMetadata(chunkPosition.chunkPosX, chunkPosition.chunkPosY, chunkPosition.chunkPosZ)) - 0.11111111F;
            float f3 = chunkPosition.chunkPosY + 1 - f2;
            if (projectViewFromEntity2.yCoord >= f3) {
                block = world.getBlock(chunkPosition.chunkPosX, chunkPosition.chunkPosY + 1, chunkPosition.chunkPosZ);
            }
        }

        return block;
    }
}
