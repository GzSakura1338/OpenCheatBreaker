package net.minecraft.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.optifine.Config;
import net.optifine.shaders.Program;
import net.optifine.shaders.Shaders;

public class EntityPickupFX extends EntityFX {
   public RenderManager entityRenderManager = Minecraft.getMinecraft().getRenderManager();
   public Entity itemEntity;
   public int age;
   public Entity interactingEntity;
   public int maxAge;
   public float floatValue;

   public EntityPickupFX(World worldIn, Entity entity, Entity entity2, float f) {
      super(worldIn, entity.posX, entity.posY, entity.posZ, entity.motionX, entity.motionY, entity.motionZ);
      this.itemEntity = entity;
      this.interactingEntity = entity2;
      this.maxAge = 3;
      this.floatValue = f;
   }

   @Override
   public void renderParticle(
      WorldRenderer worldRendererIn,
      Entity entityIn,
      float partialTicks,
      float rotationX,
      float rotationZ,
      float rotationYZ,
      float rotationXY,
      float rotationXZ
   ) {
      Program program = null;
      if (Config.isShaders()) {
         program = Shaders.activeProgram;
         Shaders.nextEntity(this.itemEntity);
      }

      float f = (this.age + partialTicks) / this.maxAge;
      f *= f;
      double posX = this.itemEntity.posX;
      double posY = this.itemEntity.posY;
      double posZ = this.itemEntity.posZ;
      double d = this.interactingEntity.lastTickPosX + (this.interactingEntity.posX - this.interactingEntity.lastTickPosX) * partialTicks;
      double d2 = this.interactingEntity.lastTickPosY + (this.interactingEntity.posY - this.interactingEntity.lastTickPosY) * partialTicks + this.floatValue;
      double d3 = this.interactingEntity.lastTickPosZ + (this.interactingEntity.posZ - this.interactingEntity.lastTickPosZ) * partialTicks;
      double d4 = posX + (d - posX) * f;
      double d5 = posY + (d2 - posY) * f;
      double d6 = posZ + (d3 - posZ) * f;
      int brightnessForRender = this.getBrightnessForRender(partialTicks);
      int n = brightnessForRender % 65536;
      int n2 = brightnessForRender / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      d4 -= interpPosX;
      d5 -= interpPosY;
      d6 -= interpPosZ;
      this.entityRenderManager.renderEntityWithPosYaw(this.itemEntity, (float)d4, (float)d5, (float)d6, this.itemEntity.rotationYaw, partialTicks);
      if (Config.isShaders()) {
         Shaders.setEntityId((Entity)null);
         Shaders.useProgram(program);
      }
   }

   @Override
   public void onUpdate() {
      this.age++;
      if (this.age == this.maxAge) {
         this.setDead();
      }
   }

   @Override
   public int getFXLayer() {
      return 3;
   }
}
