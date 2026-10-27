package net.minecraft.client.renderer.tileentity;

import com.cheatbreaker.client.CheatBreaker;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityEnchantmentTable;
import net.minecraft.tileentity.TileEntityEndPortal;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import net.optifine.EmissiveTextures;
import net.optifine.reflect.Reflector;

public class TileEntityRendererDispatcher {
   public Map<Class, TileEntitySpecialRenderer> mapSpecialRenderers = Maps.newHashMap();
   public static TileEntityRendererDispatcher instance = new TileEntityRendererDispatcher();
   public FontRenderer fontRenderer;
   public static double staticPlayerX;
   public static double staticPlayerY;
   public static double staticPlayerZ;
   public TextureManager renderEngine;
   public World worldObj;
   public Entity entity;
   public float entityYaw;
   public float entityPitch;
   public double entityX;
   public double entityY;
   public double entityZ;
   public TileEntity tileEntityRendered;
   public Tessellator batchBuffer = new Tessellator(2097152);
   public boolean drawingBatch = false;

   public TileEntityRendererDispatcher() {
      this.mapSpecialRenderers.put(TileEntitySign.class, new TileEntitySignRenderer());
      this.mapSpecialRenderers.put(TileEntityMobSpawner.class, new TileEntityMobSpawnerRenderer());
      this.mapSpecialRenderers.put(TileEntityPiston.class, new TileEntityPistonRenderer());
      this.mapSpecialRenderers.put(TileEntityChest.class, new TileEntityChestRenderer());
      this.mapSpecialRenderers.put(TileEntityEnderChest.class, new TileEntityEnderChestRenderer());
      this.mapSpecialRenderers.put(TileEntityEnchantmentTable.class, new TileEntityEnchantmentTableRenderer());
      this.mapSpecialRenderers.put(TileEntityEndPortal.class, new TileEntityEndPortalRenderer());
      this.mapSpecialRenderers.put(TileEntityBeacon.class, new TileEntityBeaconRenderer());
      this.mapSpecialRenderers.put(TileEntitySkull.class, new TileEntitySkullRenderer());
      this.mapSpecialRenderers.put(TileEntityBanner.class, new TileEntityBannerRenderer());

      for (TileEntitySpecialRenderer tileEntitySpecialRenderer : this.mapSpecialRenderers.values()) {
         tileEntitySpecialRenderer.setRendererDispatcher(this);
      }
   }

   public <T extends TileEntity> TileEntitySpecialRenderer<T> getSpecialRendererByClass(Class<? extends TileEntity> teClass) {
      TileEntitySpecialRenderer tileEntitySpecialRenderer = this.mapSpecialRenderers.get(teClass);
      if (tileEntitySpecialRenderer == null && teClass != TileEntity.class) {
         tileEntitySpecialRenderer = this.getSpecialRendererByClass((Class<? extends TileEntity>)teClass.getSuperclass());
         this.mapSpecialRenderers.put(teClass, tileEntitySpecialRenderer);
      }

      return tileEntitySpecialRenderer;
   }

   public <T extends TileEntity> TileEntitySpecialRenderer<T> getSpecialRenderer(TileEntity tileEntityIn) {
      return tileEntityIn != null && !tileEntityIn.isInvalid() ? this.getSpecialRendererByClass((Class<? extends TileEntity>)tileEntityIn.getClass()) : null;
   }

   public void cacheActiveRenderInfo(World worldIn, TextureManager textureManagerIn, FontRenderer fontrendererIn, Entity entityIn, float partialTicks) {
      if (this.worldObj != worldIn) {
         this.setWorld(worldIn);
      }

      this.renderEngine = textureManagerIn;
      this.entity = entityIn;
      this.fontRenderer = fontrendererIn;
      this.entityYaw = entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks;
      this.entityPitch = entityIn.prevRotationPitch + (entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTicks;
      this.entityX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
      this.entityY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
      this.entityZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
   }

   public void renderTileEntity(TileEntity tileentityIn, float partialTicks, int destroyStage) {
      if (CheatBreaker.getInstance().getKeybindSettings().shouldRenderTileEntity(tileentityIn)) {
         if (tileentityIn.getDistanceSq(this.entityX, this.entityY, this.entityZ) < tileentityIn.getMaxRenderDistanceSquared()) {
            boolean flag = true;
            if (Reflector.ForgeTileEntity_hasFastRenderer.exists()) {
               flag = !this.drawingBatch || !Reflector.callBoolean(tileentityIn, Reflector.ForgeTileEntity_hasFastRenderer);
            }

            if (flag) {
               RenderHelper.enableStandardItemLighting();
               int combinedLight = this.worldObj.getCombinedLight(tileentityIn.getPos(), 0);
               int n = combinedLight % 65536;
               int n2 = combinedLight / 65536;
               OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            }

            BlockPos pos = tileentityIn.getPos();
            if (!this.worldObj.isBlockLoaded(pos, false)) {
               return;
            }

            if (EmissiveTextures.isActive()) {
               EmissiveTextures.beginRender();
            }

            this.renderTileEntityAt(
               tileentityIn, pos.getX() - staticPlayerX, pos.getY() - staticPlayerY, pos.getZ() - staticPlayerZ, partialTicks, destroyStage
            );
            if (EmissiveTextures.isActive()) {
               if (EmissiveTextures.hasEmissive()) {
                  EmissiveTextures.beginRenderEmissive();
                  this.renderTileEntityAt(
                     tileentityIn, pos.getX() - staticPlayerX, pos.getY() - staticPlayerY, pos.getZ() - staticPlayerZ, partialTicks, destroyStage
                  );
                  EmissiveTextures.endRenderEmissive();
               }

               EmissiveTextures.endRender();
            }
         }
      }
   }

   public void renderTileEntityAt(TileEntity tileEntityIn, double x, double y, double z, float partialTicks) {
      this.renderTileEntityAt(tileEntityIn, x, y, z, partialTicks, -1);
   }

   public void renderTileEntityAt(TileEntity tileEntityIn, double x, double y, double z, float partialTicks, int destroyStage) {
      TileEntitySpecialRenderer specialRenderer = this.getSpecialRenderer(tileEntityIn);
      if (specialRenderer != null) {
         try {
            this.tileEntityRendered = tileEntityIn;
            if (this.drawingBatch && Reflector.callBoolean(tileEntityIn, Reflector.ForgeTileEntity_hasFastRenderer)) {
               specialRenderer.renderTileEntityFast(tileEntityIn, x, y, z, partialTicks, destroyStage, this.batchBuffer.getWorldRenderer());
            } else {
               specialRenderer.renderTileEntityAt(tileEntityIn, x, y, z, partialTicks, destroyStage);
            }

            this.tileEntityRendered = null;
         } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Rendering Block Entity");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Block Entity Details");
            tileEntityIn.addInfoToCrashReport(makeCategory);
            throw new ReportedException(makeCrashReport);
         }
      }
   }

   public void setWorld(World worldIn) {
      this.worldObj = worldIn;
   }

   public FontRenderer getFontRenderer() {
      return this.fontRenderer;
   }

   public void preDrawBatch() {
      this.batchBuffer.getWorldRenderer().begin(7, DefaultVertexFormats.BLOCK);
      this.drawingBatch = true;
   }

   public void drawBatch(int n) {
      this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
      RenderHelper.disableStandardItemLighting();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.enableBlend();
      GlStateManager.disableCull();
      if (Minecraft.isAmbientOcclusionEnabled()) {
         GlStateManager.shadeModel(7425);
      } else {
         GlStateManager.shadeModel(7424);
      }

      if (n > 0) {
         this.batchBuffer.getWorldRenderer().sortVertexData((float)staticPlayerX, (float)staticPlayerY, (float)staticPlayerZ);
      }

      this.batchBuffer.draw();
      RenderHelper.enableStandardItemLighting();
      this.drawingBatch = false;
   }
}
