package net.minecraft.client.renderer.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.FloatEvent4;
import com.cheatbreaker.client.mod.impl.NametagMod;
import com.cheatbreaker.client.mod.impl.PackTweaksMod;
import com.cheatbreaker.client.network.PlayerData;
import com.cheatbreaker.client.network.PlayerDataManager;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.PreviewPlayer;
import java.awt.Color;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.Config;
import net.optifine.entity.model.IEntityRenderer;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

public abstract class Render<T extends Entity> implements IEntityRenderer {
   public static ResourceLocation shadowTextures = new ResourceLocation("textures/misc/shadow.png");
   public float shadowOpaque = 1.0F;
   public float shadowSize;
   public Class entityClass = null;
   public ResourceLocation locationTextureCustom = null;
   public Pattern pattern = Pattern.compile("(?i)§[0-689A-E]");
   public RenderManager renderManager;

   public Render(RenderManager renderManager) {
      this.renderManager = renderManager;
   }

   public boolean shouldRender(T livingEntity, ICamera camera, double camX, double camY, double camZ) {
      AxisAlignedBB entityBoundingBox = livingEntity.getEntityBoundingBox();
      if (entityBoundingBox.hasNaN() || entityBoundingBox.getAverageEdgeLength() == 0.0) {
         entityBoundingBox = new AxisAlignedBB(
            livingEntity.posX - 2.0,
            livingEntity.posY - 2.0,
            livingEntity.posZ - 2.0,
            livingEntity.posX + 2.0,
            livingEntity.posY + 2.0,
            livingEntity.posZ + 2.0
         );
      }

      return livingEntity.isInRangeToRender3d(camX, camY, camZ) && (livingEntity.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(entityBoundingBox));
   }

   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      this.renderName((T)entity, x, y, z);
   }

   public void renderName(T entity, double x, double y, double z) {
      if (this.canRenderName((T)entity)) {
         this.renderLivingLabel((T)entity, entity.getDisplayName().getFormattedText(), x, y, z, 64);
      }
   }

   public boolean canRenderName(T entity) {
      return entity.getAlwaysRenderNameTagForRender() && entity.hasCustomName();
   }

   public void renderOffsetLivingLabel(T entityIn, double x, double y, double z, String str, float f, double d) {
      if (!(entityIn instanceof PreviewPlayer)) {
         this.renderLivingLabel((T)entityIn, str, x, y, z, 64);
      }
   }

   public abstract ResourceLocation getEntityTexture(T value);

   public boolean bindEntityTexture(T entity) {
      ResourceLocation entityTexture = this.getEntityTexture((T)entity);
      if (this.locationTextureCustom != null) {
         entityTexture = this.locationTextureCustom;
      }

      if (entityTexture == null) {
         return false;
      }

      this.bindTexture(entityTexture);
      return true;
   }

   public void bindTexture(ResourceLocation location) {
      this.renderManager.renderEngine.bindTexture(location);
   }

   public void renderEntityOnFire(Entity entity, double x, double y, double z, float partialTicks) {
      GlStateManager.disableLighting();
      TextureMap textureMapBlocks = Minecraft.getMinecraft().getTextureMapBlocks();
      TextureAtlasSprite atlasSprite = textureMapBlocks.getAtlasSprite("minecraft:blocks/fire_layer_0");
      TextureAtlasSprite atlasSprite2 = textureMapBlocks.getAtlasSprite("minecraft:blocks/fire_layer_1");
      GlStateManager.pushMatrix();
      GlStateManager.translate((float)x, (float)y, (float)z);
      float f = entity.width * 1.4F;
      GlStateManager.scale(f, f, f);
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      float f2 = 0.5F;
      float f3 = 0.0F;
      float f4 = entity.height / f;
      float f5 = (float)(entity.posY - entity.getEntityBoundingBox().minY);
      GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
      GlStateManager.translate(0.0F, 0.0F, -0.3F + (int)f4 * 0.02F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      float f6 = 0.0F;
      int n = 0;
      boolean isMultiTexture = Config.isMultiTexture();
      if (isMultiTexture) {
         worldRenderer.setBlockLayer(EnumWorldBlockLayer.SOLID);
      }

      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);

      while (f4 > 0.0F) {
         TextureAtlasSprite textureAtlasSprite = n % 2 == 0 ? atlasSprite : atlasSprite2;
         worldRenderer.setSprite(textureAtlasSprite);
         this.bindTexture(TextureMap.locationBlocksTexture);
         float minU = textureAtlasSprite.getMinU();
         float minV = textureAtlasSprite.getMinV();
         float maxU = textureAtlasSprite.getMaxU();
         float maxV = textureAtlasSprite.getMaxV();
         if (n / 2 % 2 == 0) {
            float f7 = maxU;
            maxU = minU;
            minU = f7;
         }

         worldRenderer.pos(f2 - f3, 0.0F - f5, f6).tex(maxU, maxV).endVertex();
         worldRenderer.pos(-f2 - f3, 0.0F - f5, f6).tex(minU, maxV).endVertex();
         worldRenderer.pos(-f2 - f3, 1.4F - f5, f6).tex(minU, minV).endVertex();
         worldRenderer.pos(f2 - f3, 1.4F - f5, f6).tex(maxU, minV).endVertex();
         f4 -= 0.45F;
         f5 -= 0.45F;
         f2 *= 0.9F;
         f6 += 0.03F;
         n++;
      }

      instance.draw();
      if (isMultiTexture) {
         worldRenderer.setBlockLayer(null);
         GlStateManager.bindCurrentTexture();
      }

      GlStateManager.popMatrix();
      GlStateManager.enableLighting();
   }

   public void renderShadow(Entity entityIn, double x, double y, double z, float shadowAlpha, float partialTicks) {
      if (!Config.isShaders() || !Shaders.shouldSkipDefaultShadow) {
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
         this.renderManager.renderEngine.bindTexture(shadowTextures);
         World worldFromRenderManager = this.getWorldFromRenderManager();
         GlStateManager.depthMask(false);
         float shadowSize = this.shadowSize;
         if (entityIn instanceof EntityLiving) {
            EntityLiving entityIn2 = (EntityLiving)entityIn;
            shadowSize *= entityIn2.getRenderSizeModifier();
            if (entityIn2.isChild()) {
               shadowSize *= 0.5F;
            }
         }

         double d = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
         double d2 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
         double d3 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
         int n = MathHelper.floor_double(d - shadowSize);
         int n2 = MathHelper.floor_double(d + shadowSize);
         int n3 = MathHelper.floor_double(d2 - shadowSize);
         int n4 = MathHelper.floor_double(d2);
         int n5 = MathHelper.floor_double(d3 - shadowSize);
         int n6 = MathHelper.floor_double(d3 + shadowSize);
         double d4 = x - d;
         double d5 = y - d2;
         double d6 = z - d3;
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

         for (BlockPos blockPos : BlockPos.getAllInBoxMutable(new BlockPos(n, n3, n5), new BlockPos(n2, n4, n6))) {
            Block block = worldFromRenderManager.getBlockState(blockPos.down()).getBlock();
            if (block.getRenderType() != -1 && worldFromRenderManager.getLightFromNeighbors(blockPos) > 3) {
               this.renderShadowBlock(block, x, y, z, blockPos, shadowAlpha, shadowSize, d4, d5, d6);
            }
         }

         instance.draw();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.depthMask(true);
      }
   }

   public World getWorldFromRenderManager() {
      return this.renderManager.worldObj;
   }

   public void renderShadowBlock(
      Block blockIn, double d, double d2, double d3, BlockPos pos, float f, float f2, double d4, double d5, double d6
   ) {
      if (blockIn.isFullCube()) {
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         double d7 = (f - (d2 - (pos.getY() + d5)) / 2.0) * 0.5 * this.getWorldFromRenderManager().getLightBrightness(pos);
         if (d7 >= 0.0) {
            if (d7 > 1.0) {
               d7 = 1.0;
            }

            double d8 = pos.getX() + blockIn.getBlockBoundsMinX() + d4;
            double d9 = pos.getX() + blockIn.getBlockBoundsMaxX() + d4;
            double d10 = pos.getY() + blockIn.getBlockBoundsMinY() + d5 + 0.015625;
            double d11 = pos.getZ() + blockIn.getBlockBoundsMinZ() + d6;
            double d12 = pos.getZ() + blockIn.getBlockBoundsMaxZ() + d6;
            float f3 = (float)((d - d8) / 2.0 / f2 + 0.5);
            float f4 = (float)((d - d9) / 2.0 / f2 + 0.5);
            float f5 = (float)((d3 - d11) / 2.0 / f2 + 0.5);
            float f6 = (float)((d3 - d12) / 2.0 / f2 + 0.5);
            worldRenderer.pos(d8, d10, d11).tex(f3, f5).color(1.0F, 1.0F, 1.0F, (float)d7).endVertex();
            worldRenderer.pos(d8, d10, d12).tex(f3, f6).color(1.0F, 1.0F, 1.0F, (float)d7).endVertex();
            worldRenderer.pos(d9, d10, d12).tex(f4, f6).color(1.0F, 1.0F, 1.0F, (float)d7).endVertex();
            worldRenderer.pos(d9, d10, d11).tex(f4, f5).color(1.0F, 1.0F, 1.0F, (float)d7).endVertex();
         }
      }
   }

   public static void renderOffsetAABB(AxisAlignedBB boundingBox, double x, double y, double z) {
      GlStateManager.disableTexture2D();
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      worldRenderer.setTranslation(x, y, z);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_NORMAL);
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(0.0F, 0.0F, -1.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 0.0F, 1.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(0.0F, -1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(0.0F, 1.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).normal(-1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).normal(1.0F, 0.0F, 0.0F).endVertex();
      instance.draw();
      worldRenderer.setTranslation(0.0, 0.0, 0.0);
      GlStateManager.enableTexture2D();
   }

   public void doRenderShadowAndFire(Entity entityIn, double x, double y, double z, float yaw, float partialTicks) {
      if (this.renderManager.options != null) {
         if (this.renderManager.options.entityShadows && this.shadowSize > 0.0F && !entityIn.isInvisible() && this.renderManager.isRenderShadow()) {
            double distanceToCamera = this.renderManager.getDistanceToCamera(entityIn.posX, entityIn.posY, entityIn.posZ);
            float f = (float)((1.0 - distanceToCamera / 256.0) * this.shadowOpaque);
            if (f > 0.0F) {
               this.renderShadow(entityIn, x, y, z, f, partialTicks);
            }
         }

         PackTweaksMod packTweaksMod = CheatBreaker.getInstance().getModManager().packTweaksMod;
         boolean isEnabled = packTweaksMod.isEnabled();
         boolean flag = isEnabled ? (Boolean)packTweaksMod.fireOnEntities.getValue() : true;
         if (entityIn.canRenderOnFire() && flag && (!(entityIn instanceof EntityPlayer) || !((EntityPlayer)entityIn).isSpectator())) {
            this.renderEntityOnFire(entityIn, x, y, z, partialTicks);
         }
      }
   }

   public FontRenderer getFontRendererFromRenderManager() {
      return this.renderManager.getFontRenderer();
   }

   public void renderLivingLabel(T entityIn, String str, double x, double y, double z, int maxDistance) {
      NametagMod nametagMod = CheatBreaker.getInstance().getModManager().nametagMod;
      PlayerDataManager playerDataManager = CheatBreaker.getInstance().getPlayerDataManager();
      PlayerData findPlayerData2 = playerDataManager.findPlayerData2(entityIn.getUniqueID().toString());
      boolean isEnabled = nametagMod.isEnabled();
      boolean flag = findPlayerData2 != null
         && isEnabled
         && (Boolean)nametagMod.showCheatbreakerLogo.getValue()
         && (nametagMod.contains(str) || playerDataManager.checkString(entityIn.getUniqueID().toString(), false))
         && playerDataManager.checkString(str, true);
      boolean value = (Boolean)nametagMod.cheatbreakerLogoType.getValue();
      if (isEnabled && nametagMod.shownOwnNametag.getBooleanValue() || !entityIn.equals(Minecraft.getMinecraft().thePlayer)) {
         double distanceSqToEntity = entityIn.getDistanceSqToEntity(this.renderManager.livingPlayer);
         if (!CheatBreaker.getInstance().getModManager().nametagMod.flag && distanceSqToEntity <= maxDistance * maxDistance) {
            FontRenderer fontRendererFromRenderManager = this.getFontRendererFromRenderManager();
            float f = 1.6F;
            float f2 = 0.016666668F * f;
            GlStateManager.pushMatrix();
            GlStateManager.translate((float)x + 0.0F, (float)y + entityIn.height + 0.5F, (float)z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
            GlStateManager.scale(-f2, -f2, f2);
            GlStateManager.disableLighting();
            GlStateManager.depthMask(false);
            GlStateManager.disableDepth();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            Tessellator instance = Tessellator.getInstance();
            WorldRenderer worldRenderer = instance.getWorldRenderer();
            boolean flag2 = (Boolean)nametagMod.stripColorFormatting.getValue() && isEnabled;
            String str2 = flag2 ? this.formatText(str) : str;
            byte b = 0;
            if (str.equals("deadmau5")) {
               b = -10;
            }

            int n = fontRendererFromRenderManager.getStringWidth(str) / 2;
            int n2 = flag ? (str2.contains(" ") ? 6 : 5) : 0;
            int n3 = flag ? (str2.contains(" ") ? 8 : 7) : 0;
            n = value ? n + n2 : n + n3;
            GlStateManager.disableTexture2D();
            if (!isEnabled || (Boolean)nametagMod.showBackground.getValue()) {
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
               FloatEvent4 floatEvent4 = new FloatEvent4(0.0F, 0.0F, 0.0F, 0.25F);
               CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent4);
               float floatValue1 = floatEvent4.getFloatValue1();
               float floatValue2 = floatEvent4.getFloatValue2();
               float floatValue3 = floatEvent4.getFloatValue3();
               float floatValue4 = floatEvent4.getFloatValue4();
               worldRenderer.pos(-n - 1, -1 + b, 0.0).color(floatValue1, floatValue2, floatValue3, floatValue4).endVertex();
               worldRenderer.pos(-n - 1, 8 + b, 0.0).color(floatValue1, floatValue2, floatValue3, floatValue4).endVertex();
               worldRenderer.pos(n + 1, 8 + b, 0.0).color(floatValue1, floatValue2, floatValue3, floatValue4).endVertex();
               worldRenderer.pos(n + 1, -1 + b, 0.0).color(floatValue1, floatValue2, floatValue3, floatValue4).endVertex();
               instance.draw();
            }

            GlStateManager.enableTexture2D();
            this.render(n, b, n2, n3, fontRendererFromRenderManager, str2, findPlayerData2, nametagMod, flag, value, 32);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            this.render(n, b, n2, n3, fontRendererFromRenderManager, str2, findPlayerData2, nametagMod, flag, value, 255);
            GlStateManager.enableLighting();
            GlStateManager.disableBlend();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
         }
      }
   }

   public RenderManager getRenderManager() {
      return this.renderManager;
   }

   public boolean isMultipass() {
      return false;
   }

   public void renderMultipass(T value, double d, double d2, double d3, float f, float f2) {
   }

   @Override
   public Class getEntityClass() {
      return this.entityClass;
   }

   @Override
   public void setEntityClass(Class clazz) {
      this.entityClass = clazz;
   }

   @Override
   public ResourceLocation getLocationTextureCustom() {
      return this.locationTextureCustom;
   }

   @Override
   public void setLocationTextureCustom(ResourceLocation resourceLocation) {
      this.locationTextureCustom = resourceLocation;
   }

   public static void setModelBipedMain(RenderBiped renderBiped, ModelBiped modelBiped) {
      renderBiped.modelBipedMain = modelBiped;
   }

   public void render(
      int n, int n2, int n3, int n4, FontRenderer fontRenderer, String str, PlayerData playerData, NametagMod nametagMod, boolean flag, boolean flag2, int n5
   ) {
      fontRenderer.drawString(
         str,
         -(fontRenderer.getStringWidth(str) / 2 - (flag2 ? n3 : n4)),
         n2,
         nametagMod.stripColorFormatting.getBooleanValue()
            ? new Color(
                  nametagMod.textColor.getColorValue() >> 16 & 0xFF,
                  nametagMod.textColor.getColorValue() >> 8 & 0xFF,
                  nametagMod.textColor.getColorValue() & 0xFF,
                  Math.min(n5, nametagMod.textColor.getColorValue() >> 24 & 0xFF)
               )
               .getRGB()
            : new Color(255, 255, 255, n5).getRGB(),
         nametagMod.isEnabled() && nametagMod.textShadow.getBooleanValue()
      );
      if (playerData != null) {
         if (flag) {
            for (int i = 0; i < playerData.getIntegerList().size(); i++) {
               float f = (playerData.getIntegerList().get(i) >> 16 & 0xFF) / 255.0F;
               float f2 = (playerData.getIntegerList().get(i) >> 8 & 0xFF) / 255.0F;
               float f3 = (playerData.getIntegerList().get(i) & 0xFF) / 255.0F;
               if (nametagMod.textShadow.getBooleanValue()) {
                  if (flag2) {
                     GL11.glColor4f(f / 4.0F, f2 / 4.0F, f3 / 4.0F, n5 / 255.0F);
                     RenderUtils.drawSquareTextureViaRenderEngine(
                        new ResourceLocation("client/icons/nametag/logo_circle_" + i + ".png"), 3.6F, -(n * 2) / 2.0F + 1.0F, 0.0F
                     );
                  } else {
                     GL11.glColor4f(f / 4.0F, f2 / 4.0F, f3 / 4.0F, n5 / 255.0F);
                     RenderUtils.drawTexture(
                        new ResourceLocation("client/icons/nametag/logo_white_" + i + ".png"), -(n * 2) / 2.0F + 1.0F, 1.0F, 13.0F, 7.0F
                     );
                  }
               }

               if (flag2) {
                  GL11.glColor4f(f, f2, f3, n5 / 255.0F);
                  RenderUtils.drawSquareTextureViaRenderEngine(
                     new ResourceLocation("client/icons/nametag/logo_circle_" + i + ".png"), 3.6F, -(n * 2) / 2.0F + 1.0F, 0.0F
                  );
               } else {
                  GL11.glColor4f(f, f2, f3, n5 / 255.0F);
                  RenderUtils.drawTexture(new ResourceLocation("client/icons/nametag/logo_white_" + i + ".png"), -(n * 2) / 2.0F, 0.0F, 13.0F, 7.0F);
               }
            }

            if (nametagMod.showSubIcons.getBooleanValue() && !playerData.getText2().equalsIgnoreCase("")) {
               if (nametagMod.textShadow.getBooleanValue()) {
                  GL11.glColor4f(0.25F, 0.25F, 0.25F, n5 / 255.0F);
                  RenderUtils.drawTexture(
                     new ResourceLocation("client/icons/nametag/subicons/" + playerData.getText2() + ".png"),
                     -(n * 2) / 2.0F + 10.0F + 0.33333334F,
                     -1.6666666F,
                     4.3333335F,
                     4.3333335F
                  );
               }

               GL11.glColor4f(1.0F, 1.0F, 1.0F, n5 / 255.0F);
               RenderUtils.drawTexture(
                  new ResourceLocation("client/icons/nametag/subicons/" + playerData.getText2() + ".png"), -(n * 2) / 2.0F + 10.0F, -2.0F, 4.3333335F, 4.3333335F
               );
            }
         }
      }
   }

   public String formatText(String str) {
      return str == null ? null : this.pattern.matcher(str).replaceAll("§r");
   }
}
