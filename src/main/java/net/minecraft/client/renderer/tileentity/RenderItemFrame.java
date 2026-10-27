package net.minecraft.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureCompass;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemSkull;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.optifine.Config;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

public class RenderItemFrame extends Render<EntityItemFrame> {
   public static ResourceLocation mapBackgroundTextures = new ResourceLocation("textures/map/map_background.png");
   public Minecraft mc = Minecraft.getMinecraft();
   public ModelResourceLocation itemFrameModel = new ModelResourceLocation("item_frame", "normal");
   public ModelResourceLocation mapModel = new ModelResourceLocation("item_frame", "map");
   public RenderItem itemRenderer;
   public static double itemRenderDistanceSq = 4096.0;

   public RenderItemFrame(RenderManager renderManagerIn, RenderItem itemRendererIn) {
      super(renderManagerIn);
      this.itemRenderer = itemRendererIn;
   }

   public void doRender(EntityItemFrame entity, double x, double y, double z, float entityYaw, float partialTicks) {
      GlStateManager.pushMatrix();
      BlockPos hangingPosition = entity.getHangingPosition();
      double d = hangingPosition.getX() - entity.posX + x;
      double d2 = hangingPosition.getY() - entity.posY + y;
      double d3 = hangingPosition.getZ() - entity.posZ + z;
      GlStateManager.translate(d + 0.5, d2 + 0.5, d3 + 0.5);
      GlStateManager.rotate(180.0F - entity.rotationYaw, 0.0F, 1.0F, 0.0F);
      this.renderManager.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
      BlockRendererDispatcher blockRendererDispatcher = this.mc.getBlockRendererDispatcher();
      ModelManager modelManager = blockRendererDispatcher.getBlockModelShapes().getModelManager();
      IBakedModel bakedModel;
      if (entity.getDisplayedItem() != null && entity.getDisplayedItem().getItem() == Items.filled_map) {
         bakedModel = modelManager.getModel(this.mapModel);
      } else {
         bakedModel = modelManager.getModel(this.itemFrameModel);
      }

      GlStateManager.pushMatrix();
      GlStateManager.translate(-0.5F, -0.5F, -0.5F);
      blockRendererDispatcher.getBlockModelRenderer().renderModelBrightnessColor(bakedModel, 1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
      GlStateManager.translate(0.0F, 0.0F, 0.4375F);
      this.renderItem(entity);
      GlStateManager.popMatrix();
      this.renderName(entity, x + entity.facingDirection.getFrontOffsetX() * 0.3F, y - 0.25, z + entity.facingDirection.getFrontOffsetZ() * 0.3F);
   }

   public ResourceLocation getEntityTexture(EntityItemFrame entity) {
      return null;
   }

   public void renderItem(EntityItemFrame itemFrame) {
      ItemStack displayedItem = itemFrame.getDisplayedItem();
      if (displayedItem != null) {
         if (!this.isRenderItem(itemFrame)) {
            return;
         }

         if (!Config.zoomMode) {
            EntityPlayerSP thePlayer = this.mc.thePlayer;
            double distanceSq = itemFrame.getDistanceSq(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
            if (distanceSq > 4096.0) {
               return;
            }
         }

         EntityItem entityItem = new EntityItem(itemFrame.worldObj, 0.0, 0.0, 0.0, displayedItem);
         Item item = entityItem.getEntityItem().getItem();
         entityItem.getEntityItem().stackSize = 1;
         entityItem.hoverStart = 0.0F;
         GlStateManager.pushMatrix();
         GlStateManager.disableLighting();
         int rotation = itemFrame.getRotation();
         if (item instanceof ItemMap) {
            rotation = rotation % 4 * 2;
         }

         GlStateManager.rotate(rotation * 360.0F / 8.0F, 0.0F, 0.0F, 1.0F);
         if (!Reflector.postForgeBusEvent(Reflector.RenderItemInFrameEvent_Constructor, itemFrame, this)) {
            if (item instanceof ItemMap) {
               this.renderManager.renderEngine.bindTexture(mapBackgroundTextures);
               GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
               float f = 0.0078125F;
               GlStateManager.scale(f, f, f);
               GlStateManager.translate(-64.0F, -64.0F, 0.0F);
               MapData mapData = Items.filled_map.getMapData(entityItem.getEntityItem(), itemFrame.worldObj);
               GlStateManager.translate(0.0F, 0.0F, -1.0F);
               if (mapData != null) {
                  this.mc.entityRenderer.getMapItemRenderer().renderMap(mapData, true);
               }
            } else {
               TextureAtlasSprite textureAtlasSprite = null;
               if (item == Items.compass) {
                  textureAtlasSprite = this.mc.getTextureMapBlocks().getAtlasSprite(TextureCompass.locationSprite);
                  this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                  if (textureAtlasSprite instanceof TextureCompass) {
                     TextureCompass textureCompass = (TextureCompass)textureAtlasSprite;
                     double currentAngle = textureCompass.currentAngle;
                     double angleDelta = textureCompass.angleDelta;
                     textureCompass.currentAngle = 0.0;
                     textureCompass.angleDelta = 0.0;
                     textureCompass.updateCompass(
                        itemFrame.worldObj,
                        itemFrame.posX,
                        itemFrame.posZ,
                        MathHelper.wrapAngleTo180_float(180 + itemFrame.facingDirection.getHorizontalIndex() * 90),
                        false,
                        true
                     );
                     textureCompass.currentAngle = currentAngle;
                     textureCompass.angleDelta = angleDelta;
                  } else {
                     textureAtlasSprite = null;
                  }
               }

               GlStateManager.scale(0.5F, 0.5F, 0.5F);
               if (!this.itemRenderer.shouldRenderItemIn3D(entityItem.getEntityItem()) || item instanceof ItemSkull) {
                  GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
               }

               GlStateManager.pushAttrib();
               RenderHelper.enableStandardItemLighting();
               this.itemRenderer.renderItem(entityItem.getEntityItem(), TransformType.FIXED);
               RenderHelper.disableStandardItemLighting();
               GlStateManager.popAttrib();
               if (textureAtlasSprite != null && textureAtlasSprite.getFrameCount() > 0) {
                  textureAtlasSprite.updateAnimation();
               }
            }
         }

         GlStateManager.enableLighting();
         GlStateManager.popMatrix();
      }
   }

   public void renderName(EntityItemFrame entity, double x, double y, double z) {
      if (Minecraft.isGuiEnabled()
         && entity.getDisplayedItem() != null
         && entity.getDisplayedItem().hasDisplayName()
         && this.renderManager.pointedEntity == entity) {
         float f = 1.6F;
         float f2 = 0.016666668F * f;
         double distanceSqToEntity = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
         float f3 = entity.isSneaking() ? 32.0F : 64.0F;
         if (distanceSqToEntity < f3 * f3) {
            String displayName = entity.getDisplayedItem().getDisplayName();
            if (entity.isSneaking()) {
               FontRenderer fontRendererFromRenderManager = this.getFontRendererFromRenderManager();
               GlStateManager.pushMatrix();
               GlStateManager.translate((float)x + 0.0F, (float)y + entity.height + 0.5F, (float)z);
               GL11.glNormal3f(0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
               GlStateManager.scale(-f2, -f2, f2);
               GlStateManager.disableLighting();
               GlStateManager.translate(0.0F, 0.25F / f2, 0.0F);
               GlStateManager.depthMask(false);
               GlStateManager.enableBlend();
               GlStateManager.blendFunc(770, 771);
               Tessellator instance = Tessellator.getInstance();
               WorldRenderer worldRenderer = instance.getWorldRenderer();
               int n = fontRendererFromRenderManager.getStringWidth(displayName) / 2;
               GlStateManager.disableTexture2D();
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
               worldRenderer.pos(-n - 1, -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldRenderer.pos(-n - 1, 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldRenderer.pos(n + 1, 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               worldRenderer.pos(n + 1, -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
               instance.draw();
               GlStateManager.enableTexture2D();
               GlStateManager.depthMask(true);
               fontRendererFromRenderManager.drawString(displayName, -fontRendererFromRenderManager.getStringWidth(displayName) / 2, 0.0F, 553648127);
               GlStateManager.enableLighting();
               GlStateManager.disableBlend();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.popMatrix();
            } else {
               this.renderLivingLabel(entity, displayName, x, y, z, 64);
            }
         }
      }
   }

   public boolean isRenderItem(EntityItemFrame entityItemFrame) {
      if (Shaders.isShadowPass) {
         return false;
      }

      if (!Config.zoomMode) {
         Entity renderViewEntity = this.mc.getRenderViewEntity();
         double distanceSq = entityItemFrame.getDistanceSq(renderViewEntity.posX, renderViewEntity.posY, renderViewEntity.posZ);
         if (distanceSq > itemRenderDistanceSq) {
            return false;
         }
      }

      return true;
   }

   public static void updateItemRenderDistance() {
      Minecraft minecraft = Config.getMinecraft();
      double limit = Config.limit(minecraft.gameSettings.fovSetting, 1.0F, 120.0F);
      double max = Math.max(6.0 * minecraft.displayHeight / limit, 16.0);
      itemRenderDistanceSq = max * max;
   }
}
