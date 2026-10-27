package net.minecraft.client.renderer;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.EntityPlayerSPItemStackBooleanFloatIntEvent;
import com.cheatbreaker.client.event.ItemRendererItemStackFloatEvent;
import com.cheatbreaker.client.event.SimpleEvent5;
import com.cheatbreaker.client.mod.impl.FreeLookHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.optifine.Config;
import net.optifine.DynamicLights;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

public class ItemRenderer {
   public static ResourceLocation RES_MAP_BACKGROUND = new ResourceLocation("textures/map/map_background.png");
   public static ResourceLocation RES_UNDERWATER_OVERLAY = new ResourceLocation("textures/misc/underwater.png");
   public int equippedItemSlot = -1;
   public ItemStack itemToRender;
   public float equippedProgress;
   public float prevEquippedProgress;
   public Minecraft mc;
   public RenderManager renderManager;
   public RenderItem itemRenderer;

   public ItemRenderer(Minecraft mcIn) {
      this.mc = mcIn;
      this.renderManager = mcIn.getRenderManager();
      this.itemRenderer = mcIn.getRenderItem();
   }

   public void renderItem(EntityLivingBase entityIn, ItemStack heldStack, TransformType transform) {
      if (heldStack != null) {
         Item item = heldStack.getItem();
         Block blockFromItem = Block.getBlockFromItem(item);
         GlStateManager.pushMatrix();
         if (this.itemRenderer.shouldRenderItemIn3D(heldStack)) {
            GlStateManager.scale(2.0F, 2.0F, 2.0F);
            if (this.isBlockTranslucent(blockFromItem) && (!Config.isShaders() || !Shaders.renderItemKeepDepthMask)) {
               GlStateManager.depthMask(false);
            }
         }

         this.itemRenderer.renderItemModelForEntity(heldStack, entityIn, transform);
         if (this.isBlockTranslucent(blockFromItem)) {
            GlStateManager.depthMask(true);
         }

         GlStateManager.popMatrix();
      }
   }

   public boolean isBlockTranslucent(Block blockIn) {
      return blockIn != null && blockIn.getBlockLayer() == EnumWorldBlockLayer.TRANSLUCENT;
   }

   public void rotateArroundXAndY(float angle, float angleY) {
      GlStateManager.pushMatrix();
      GlStateManager.rotate(angle, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(angleY, 0.0F, 1.0F, 0.0F);
      RenderHelper.enableStandardItemLighting();
      GlStateManager.popMatrix();
   }

   public void setLightMapFromPlayer(AbstractClientPlayer clientPlayer) {
      int combinedLight = this.mc.theWorld.getCombinedLight(new BlockPos(clientPlayer.posX, clientPlayer.posY + clientPlayer.getEyeHeight(), clientPlayer.posZ), 0);
      if (Config.isDynamicLights()) {
         combinedLight = DynamicLights.getCombinedLight(this.mc.getRenderViewEntity(), combinedLight);
      }

      float f = combinedLight & 65535;
      float f2 = combinedLight >> 16;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, f, f2);
   }

   public void rotateWithPlayerRotations(EntityPlayerSP entityplayerspIn, float partialTicks) {
      float f = entityplayerspIn.prevRenderArmPitch + (entityplayerspIn.renderArmPitch - entityplayerspIn.prevRenderArmPitch) * partialTicks;
      float f2 = entityplayerspIn.prevRenderArmYaw + (entityplayerspIn.renderArmYaw - entityplayerspIn.prevRenderArmYaw) * partialTicks;
      GlStateManager.rotate((entityplayerspIn.rotationPitch - f) * 0.1F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate((entityplayerspIn.rotationYaw - f2) * 0.1F, 0.0F, 1.0F, 0.0F);
   }

   public float getMapAngleFromPitch(float pitch) {
      float f = 1.0F - pitch / 45.0F + 0.1F;
      f = MathHelper.clamp_float(f, 0.0F, 1.0F);
      return -MathHelper.cos(f * (float) Math.PI) * 0.5F + 0.5F;
   }

   public void renderRightArm(RenderPlayer renderPlayerIn) {
      GlStateManager.pushMatrix();
      GlStateManager.rotate(54.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(64.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(-62.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(0.25F, -0.85F, 0.75F);
      renderPlayerIn.renderRightArm(this.mc.thePlayer);
      GlStateManager.popMatrix();
   }

   public void renderLeftArm(RenderPlayer renderPlayerIn) {
      GlStateManager.pushMatrix();
      GlStateManager.rotate(92.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(41.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translate(-0.3F, -1.1F, 0.45F);
      renderPlayerIn.renderLeftArm(this.mc.thePlayer);
      GlStateManager.popMatrix();
   }

   public void renderPlayerArms(AbstractClientPlayer clientPlayer) {
      this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
      Render entityRenderObject = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
      RenderPlayer renderPlayer = (RenderPlayer)entityRenderObject;
      if (!clientPlayer.isInvisible()) {
         GlStateManager.disableCull();
         this.renderRightArm(renderPlayer);
         this.renderLeftArm(renderPlayer);
         GlStateManager.enableCull();
      }
   }

   public void renderItemMap(AbstractClientPlayer clientPlayer, float pitch, float equipmentProgress, float swingProgress) {
      float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      float f2 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F);
      float f3 = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
      GlStateManager.translate(f, f2, f3);
      float mapAngleFromPitch = this.getMapAngleFromPitch(pitch);
      GlStateManager.translate(0.0F, 0.04F, -0.72F);
      GlStateManager.translate(0.0F, equipmentProgress * -1.2F, 0.0F);
      GlStateManager.translate(0.0F, mapAngleFromPitch * -0.5F, 0.0F);
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(mapAngleFromPitch * -85.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
      this.renderPlayerArms(clientPlayer);
      float sin = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
      float sin2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      GlStateManager.rotate(sin * -20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(sin2 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(sin2 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.38F, 0.38F, 0.38F);
      GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(-1.0F, -1.0F, 0.0F);
      GlStateManager.scale(0.015625F, 0.015625F, 0.015625F);
      this.mc.getTextureManager().bindTexture(RES_MAP_BACKGROUND);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GL11.glNormal3f(0.0F, 0.0F, -1.0F);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(-7.0, 135.0, 0.0).tex(0.0, 1.0).endVertex();
      worldRenderer.pos(135.0, 135.0, 0.0).tex(1.0, 1.0).endVertex();
      worldRenderer.pos(135.0, -7.0, 0.0).tex(1.0, 0.0).endVertex();
      worldRenderer.pos(-7.0, -7.0, 0.0).tex(0.0, 0.0).endVertex();
      instance.draw();
      MapData mapData = Items.filled_map.getMapData(this.itemToRender, this.mc.theWorld);
      if (mapData != null) {
         this.mc.entityRenderer.getMapItemRenderer().renderMap(mapData, false);
      }
   }

   public void renderPlayerArm(AbstractClientPlayer clientPlayer, float equipProgress, float swingProgress) {
      float f = -0.3F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      float f2 = 0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F);
      float f3 = -0.4F * MathHelper.sin(swingProgress * (float) Math.PI);
      GlStateManager.translate(f, f2, f3);
      GlStateManager.translate(0.64000005F, -0.6F, -0.71999997F);
      GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
      GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
      float sin = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
      float sin2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      GlStateManager.rotate(sin2 * 70.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(sin * -20.0F, 0.0F, 0.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(clientPlayer.getLocationSkin());
      GlStateManager.translate(-1.0F, 3.6F, 3.5F);
      GlStateManager.rotate(120.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(200.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(-135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.scale(1.0F, 1.0F, 1.0F);
      GlStateManager.translate(5.6F, 0.0F, 0.0F);
      Render entityRenderObject = this.renderManager.getEntityRenderObject(this.mc.thePlayer);
      GlStateManager.disableCull();
      RenderPlayer renderPlayer = (RenderPlayer)entityRenderObject;
      renderPlayer.renderRightArm(this.mc.thePlayer);
      GlStateManager.enableCull();
   }

   public void doItemUsedTransformations(float swingProgress) {
      float f = -0.4F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      float f2 = 0.2F * MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI * 2.0F);
      float f3 = -0.2F * MathHelper.sin(swingProgress * (float) Math.PI);
      GlStateManager.translate(f, f2, f3);
   }

   public void performDrinking(AbstractClientPlayer clientPlayer, float partialTicks) {
      float f = clientPlayer.getItemInUseCount() - partialTicks + 1.0F;
      float f2 = f / this.itemToRender.getMaxItemUseDuration();
      float abs = MathHelper.abs(MathHelper.cos(f / 4.0F * (float) Math.PI) * 0.1F);
      if (f2 >= 0.8F) {
         abs = 0.0F;
      }

      GlStateManager.translate(0.0F, abs, 0.0F);
      float f3 = 1.0F - (float)Math.pow(f2, 27.0);
      GlStateManager.translate(f3 * 0.6F, f3 * -0.5F, f3 * 0.0F);
      GlStateManager.rotate(f3 * 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(f3 * 30.0F, 0.0F, 0.0F, 1.0F);
   }

   public void transformFirstPersonItem(float equipProgress, float swingProgress) {
      GlStateManager.translate(0.56F, -0.52F, -0.71999997F);
      GlStateManager.translate(0.0F, equipProgress * -0.6F, 0.0F);
      GlStateManager.rotate(45.0F, 0.0F, 1.0F, 0.0F);
      float sin = MathHelper.sin(swingProgress * swingProgress * (float) Math.PI);
      float sin2 = MathHelper.sin(MathHelper.sqrt_float(swingProgress) * (float) Math.PI);
      GlStateManager.rotate(sin * -20.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(sin2 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(sin2 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scale(0.4F, 0.4F, 0.4F);
   }

   public void doBowTransformations(float partialTicks, AbstractClientPlayer clientPlayer) {
      GlStateManager.rotate(-18.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotate(-12.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-8.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translate(-0.9F, 0.2F, 0.0F);
      float f = this.itemToRender.getMaxItemUseDuration() - (clientPlayer.getItemInUseCount() - partialTicks + 1.0F);
      float f2 = f / 20.0F;
      f2 = (f2 * f2 + f2 * 2.0F) / 3.0F;
      if (f2 > 1.0F) {
         f2 = 1.0F;
      }

      if (f2 > 0.1F) {
         float sin = MathHelper.sin((f - 0.1F) * 1.3F);
         float f3 = f2 - 0.1F;
         float f4 = sin * f3;
         GlStateManager.translate(f4 * 0.0F, f4 * 0.01F, f4 * 0.0F);
      }

      GlStateManager.translate(f2 * 0.0F, f2 * 0.0F, f2 * 0.1F);
      GlStateManager.scale(1.0F, 1.0F, 1.0F + f2 * 0.2F);
   }

   public void doBlockTransformations() {
      GlStateManager.translate(-0.5F, 0.2F, 0.0F);
      GlStateManager.rotate(30.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(-80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(60.0F, 0.0F, 1.0F, 0.0F);
   }

   public void renderItemInFirstPerson(float partialTicks) {
      ItemRendererItemStackFloatEvent itemRendererItemStackFloatEvent = new ItemRendererItemStackFloatEvent(
         this, this.itemToRender, this.prevEquippedProgress, this.equippedProgress, partialTicks
      );
      CheatBreaker.getInstance().getEventBus().handleEvent(itemRendererItemStackFloatEvent);
      if (!itemRendererItemStackFloatEvent.isFlag()) {
         FreeLookHandler freeLookHandler = CheatBreaker.getInstance().getModManager().freeLookHandler;
         float f = freeLookHandler.active ? this.mc.thePlayer.rotationYaw - freeLookHandler.yaw : 0.0F;
         if (!Config.isShaders() || !Shaders.isSkipRenderHand()) {
            float f2 = 1.0F - (this.prevEquippedProgress + (this.equippedProgress - this.prevEquippedProgress) * partialTicks);
            EntityPlayerSP thePlayer = this.mc.thePlayer;
            float swingProgress = thePlayer.getSwingProgress(partialTicks);
            float f3 = thePlayer.prevRotationPitch + (thePlayer.rotationPitch - thePlayer.prevRotationPitch) * partialTicks;
            float f4 = thePlayer.prevRotationYaw + (thePlayer.rotationYaw - thePlayer.prevRotationYaw) * partialTicks + f * 10.0F;
            this.rotateArroundXAndY(f3, f4);
            this.setLightMapFromPlayer(thePlayer);
            this.rotateWithPlayerRotations(thePlayer, partialTicks);
            GlStateManager.enableRescaleNormal();
            GlStateManager.pushMatrix();
            if (this.itemToRender != null) {
               if (this.itemToRender.getItem() instanceof ItemMap) {
                  this.renderItemMap(thePlayer, f3, f2, swingProgress);
               } else if (thePlayer.getItemInUseCount() > 0) {
                  EnumAction itemUseAction = this.itemToRender.getItemUseAction();
                  switch (itemUseAction) {
                     case NONE:
                        this.transformFirstPersonItem(f2, 0.0F);
                        break;
                     case EAT:
                     case DRINK:
                        this.performDrinking(thePlayer, partialTicks);
                        this.transformFirstPersonItem(f2, 0.0F);
                        break;
                     case BLOCK:
                        this.transformFirstPersonItem(f2, 0.0F);
                        this.doBlockTransformations();
                        break;
                     case BOW:
                        this.transformFirstPersonItem(f2, 0.0F);
                        this.doBowTransformations(partialTicks, thePlayer);
                  }
               } else {
                  this.doItemUsedTransformations(swingProgress);
                  this.transformFirstPersonItem(f2, swingProgress);
               }

               this.renderItem(thePlayer, this.itemToRender, TransformType.FIRST_PERSON);
            } else if (!thePlayer.isInvisible()) {
               this.renderPlayerArm(thePlayer, f2, swingProgress);
            }

            GlStateManager.popMatrix();
            GlStateManager.disableRescaleNormal();
            RenderHelper.disableStandardItemLighting();
         }
      }
   }

   public void renderOverlays(float partialTicks) {
      GlStateManager.disableAlpha();
      if (this.mc.thePlayer.isEntityInsideOpaqueBlock()) {
         IBlockState blockState = this.mc.theWorld.getBlockState(new BlockPos(this.mc.thePlayer));
         BlockPos blockPos = new BlockPos(this.mc.thePlayer);
         EntityPlayerSP thePlayer = this.mc.thePlayer;

         for (int i = 0; i < 8; i++) {
            double d = thePlayer.posX + ((i >> 0) % 2 - 0.5F) * thePlayer.width * 0.8F;
            double d2 = thePlayer.posY + ((i >> 1) % 2 - 0.5F) * 0.1F;
            double d3 = thePlayer.posZ + ((i >> 2) % 2 - 0.5F) * thePlayer.width * 0.8F;
            BlockPos blockPos2 = new BlockPos(d, d2 + thePlayer.getEyeHeight(), d3);
            IBlockState blockState2 = this.mc.theWorld.getBlockState(blockPos2);
            if (blockState2.getBlock().isVisuallyOpaque()) {
               blockState = blockState2;
               blockPos = blockPos2;
            }
         }

         if (blockState.getBlock().getRenderType() != -1) {
            Object fieldValue = Reflector.getFieldValue(Reflector.RenderBlockOverlayEvent_OverlayType_BLOCK);
            if (!Reflector.callBoolean(Reflector.ForgeEventFactory_renderBlockOverlay, this.mc.thePlayer, partialTicks, fieldValue, blockState, blockPos)) {
               this.renderBlockInHand(partialTicks, this.mc.getBlockRendererDispatcher().getBlockModelShapes().getTexture(blockState));
            }
         }
      }

      if (!this.mc.thePlayer.isSpectator()) {
         if (this.mc.thePlayer.isInsideOfMaterial(Material.water)
            && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderWaterOverlay, this.mc.thePlayer, partialTicks)) {
            this.renderWaterOverlayTexture(partialTicks);
         }

         if (this.mc.thePlayer.isBurning() && !Reflector.callBoolean(Reflector.ForgeEventFactory_renderFireOverlay, this.mc.thePlayer, partialTicks)) {
            SimpleEvent5 simpleEvent5 = new SimpleEvent5();
            CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent5);
            if (!simpleEvent5.isFlag()) {
               this.renderFireInFirstPerson(partialTicks);
            }
         }
      }

      GlStateManager.enableAlpha();
   }

   public void renderBlockInHand(float partialTicks, TextureAtlasSprite atlas) {
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      float f = 0.1F;
      GlStateManager.color(0.1F, 0.1F, 0.1F, 0.5F);
      GlStateManager.pushMatrix();
      float minU = atlas.getMinU();
      float maxU = atlas.getMaxU();
      float minV = atlas.getMinV();
      float maxV = atlas.getMaxV();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(-1.0, -1.0, -0.5).tex(maxU, maxV).endVertex();
      worldRenderer.pos(1.0, -1.0, -0.5).tex(minU, maxV).endVertex();
      worldRenderer.pos(1.0, 1.0, -0.5).tex(minU, minV).endVertex();
      worldRenderer.pos(-1.0, 1.0, -0.5).tex(maxU, minV).endVertex();
      instance.draw();
      GlStateManager.popMatrix();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void renderWaterOverlayTexture(float partialTicks) {
      if (!Config.isShaders() || Shaders.isUnderwaterOverlay()) {
         this.mc.getTextureManager().bindTexture(RES_UNDERWATER_OVERLAY);
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         float brightness = this.mc.thePlayer.getBrightness(partialTicks);
         GlStateManager.color(brightness, brightness, brightness, 0.5F);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.pushMatrix();
         float f = -this.mc.thePlayer.rotationYaw / 64.0F;
         float f2 = this.mc.thePlayer.rotationPitch / 64.0F;
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(-1.0, -1.0, -0.5).tex(4.0F + f, 4.0F + f2).endVertex();
         worldRenderer.pos(1.0, -1.0, -0.5).tex(0.0F + f, 4.0F + f2).endVertex();
         worldRenderer.pos(1.0, 1.0, -0.5).tex(0.0F + f, 0.0F + f2).endVertex();
         worldRenderer.pos(-1.0, 1.0, -0.5).tex(4.0F + f, 0.0F + f2).endVertex();
         instance.draw();
         GlStateManager.popMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
      }
   }

   public void renderFireInFirstPerson(float partialTicks) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      if (CheatBreaker.getInstance().getModManager().packTweaksMod.isEnabled()) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, CheatBreaker.getInstance().getModManager().packTweaksMod.fireOpacity.getFloatValue() / 100.0F);
      } else {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
      }

      GlStateManager.depthFunc(519);
      GlStateManager.depthMask(false);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      float f = 1.0F;

      for (int i = 0; i < 2; i++) {
         GlStateManager.pushMatrix();
         TextureAtlasSprite atlasSprite = this.mc.getTextureMapBlocks().getAtlasSprite("minecraft:blocks/fire_layer_1");
         this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
         float minU = atlasSprite.getMinU();
         float maxU = atlasSprite.getMaxU();
         float minV = atlasSprite.getMinV();
         float maxV = atlasSprite.getMaxV();
         float f2 = (0.0F - f) / 2.0F;
         float f3 = f2 + f;
         float f4 = 0.0F - f / 2.0F;
         float f5 = f4 + f;
         float f6 = -0.5F;
         if (CheatBreaker.getInstance().getModManager().packTweaksMod.isEnabled()) {
            GlStateManager.translate(-(i * 2 - 1) * 0.24F, -1.3F + CheatBreaker.getInstance().getModManager().packTweaksMod.fireHeight.getFloatValue(), 0.0F);
         } else {
            GlStateManager.translate(-(i * 2 - 1) * 0.24F, -0.3F, 0.0F);
         }

         GlStateManager.rotate((i * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.setSprite(atlasSprite);
         worldRenderer.pos(f2, f4, f6).tex(maxU, maxV).endVertex();
         worldRenderer.pos(f3, f4, f6).tex(minU, maxV).endVertex();
         worldRenderer.pos(f3, f5, f6).tex(minU, minV).endVertex();
         worldRenderer.pos(f2, f5, f6).tex(maxU, minV).endVertex();
         instance.draw();
         GlStateManager.popMatrix();
      }

      if (CheatBreaker.getInstance().getModManager().packTweaksMod.isEnabled()) {
         GlStateManager.color(1.0F, 1.0F, 1.0F, CheatBreaker.getInstance().getModManager().packTweaksMod.fireOpacity.getFloatValue() / 100.0F);
      } else {
         GlStateManager.color(1.0F, 1.0F, 1.0F, 0.9F);
      }

      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
      GlStateManager.depthFunc(515);
   }

   public void updateEquippedItem() {
      this.prevEquippedProgress = this.equippedProgress;
      EntityPlayerSP thePlayer = this.mc.thePlayer;
      ItemStack currentItem = thePlayer.inventory.getCurrentItem();
      boolean flag = false;
      EntityPlayerSPItemStackBooleanFloatIntEvent entityPlayerSPItemStackBooleanFloatIntEvent = new EntityPlayerSPItemStackBooleanFloatIntEvent(
         flag, thePlayer, currentItem, this.itemToRender, this.prevEquippedProgress, this.equippedProgress, this.equippedItemSlot
      );
      CheatBreaker.getInstance().getEventBus().handleEvent(entityPlayerSPItemStackBooleanFloatIntEvent);
      flag = entityPlayerSPItemStackBooleanFloatIntEvent.isFlag4();
      this.itemToRender = entityPlayerSPItemStackBooleanFloatIntEvent.getItemToRender();
      if (!entityPlayerSPItemStackBooleanFloatIntEvent.isFlag()) {
         if (this.itemToRender != null && currentItem != null) {
            if (!this.itemToRender.getIsItemStackEqual(currentItem)) {
               flag = true;
            }
         } else if (this.itemToRender == null && currentItem == null) {
            flag = false;
         } else {
            flag = true;
         }
      }

      float f = 0.4F;
      float f2 = flag ? 0.0F : 1.0F;
      float f3 = MathHelper.clamp_float(f2 - this.equippedProgress, -f, f);
      this.equippedProgress += f3;
      if (this.equippedProgress < 0.1F) {
         this.itemToRender = currentItem;
         this.equippedItemSlot = thePlayer.inventory.currentItem;
      }
   }

   public void resetEquippedProgress() {
      this.equippedProgress = 0.0F;
   }

   public void resetEquippedProgress2() {
      this.equippedProgress = 0.0F;
   }
}
