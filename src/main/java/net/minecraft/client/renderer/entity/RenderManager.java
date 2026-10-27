package net.minecraft.client.renderer.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.impl.FreeLookHandler;
import com.cheatbreaker.client.mod.impl.HitboxesMod;
import com.cheatbreaker.client.mod.impl.HitboxesMod.HitboxSettings;
import com.cheatbreaker.client.render.RenderUtils;
import com.google.common.collect.Maps;
import java.awt.Color;
import java.util.Collections;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.model.ModelCow;
import net.minecraft.client.model.ModelHorse;
import net.minecraft.client.model.ModelOcelot;
import net.minecraft.client.model.ModelPig;
import net.minecraft.client.model.ModelRabbit;
import net.minecraft.client.model.ModelSheep2;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.model.ModelSquid;
import net.minecraft.client.model.ModelWolf;
import net.minecraft.client.model.ModelZombie;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.RenderEnderCrystal;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.tileentity.RenderWitherSkull;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityMinecartMobSpawner;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityMinecartTNT;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.entity.monster.EntityCaveSpider;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityEndermite;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityGiantZombie;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityMagmaCube;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.EntitySnowman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.optifine.entity.model.CustomEntityModels;
import net.optifine.player.PlayerItemsLayer;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.lwjgl.opengl.GL11;

public class RenderManager {
   public Map<Class, Render> entityRenderMap = Maps.newHashMap();
   public Map<String, RenderPlayer> skinMap = Maps.newHashMap();
   public boolean renderOutlines = false;
   public FontRenderer textRenderer;
   public double renderPosX;
   public double renderPosY;
   public double renderPosZ;
   public boolean renderShadow = true;
   public World worldObj;
   public Entity livingPlayer;
   public Entity pointedEntity;
   public float playerViewY;
   public float playerViewX;
   public GameSettings options;
   public double viewerPosX;
   public double viewerPosY;
   public double viewerPosZ;
   public boolean debugBoundingBox = false;
   public Render renderRender = null;
   public TextureManager renderEngine;
   public RenderPlayer playerRenderer;
   public static RenderManager renderManager;

   public RenderManager(TextureManager renderEngineIn, RenderItem itemRendererIn) {
      renderManager = this;
      this.renderEngine = renderEngineIn;
      this.entityRenderMap.put(EntityCaveSpider.class, new RenderCaveSpider(this));
      this.entityRenderMap.put(EntitySpider.class, new RenderSpider(this));
      this.entityRenderMap.put(EntityPig.class, new RenderPig(this, new ModelPig(), 0.7F));
      this.entityRenderMap.put(EntitySheep.class, new RenderSheep(this, new ModelSheep2(), 0.7F));
      this.entityRenderMap.put(EntityCow.class, new RenderCow(this, new ModelCow(), 0.7F));
      this.entityRenderMap.put(EntityMooshroom.class, new RenderMooshroom(this, new ModelCow(), 0.7F));
      this.entityRenderMap.put(EntityWolf.class, new RenderWolf(this, new ModelWolf(), 0.5F));
      this.entityRenderMap.put(EntityChicken.class, new RenderChicken(this, new ModelChicken(), 0.3F));
      this.entityRenderMap.put(EntityOcelot.class, new RenderOcelot(this, new ModelOcelot(), 0.4F));
      this.entityRenderMap.put(EntityRabbit.class, new RenderRabbit(this, new ModelRabbit(), 0.3F));
      this.entityRenderMap.put(EntitySilverfish.class, new RenderSilverfish(this));
      this.entityRenderMap.put(EntityEndermite.class, new RenderEndermite(this));
      this.entityRenderMap.put(EntityCreeper.class, new RenderCreeper(this));
      this.entityRenderMap.put(EntityEnderman.class, new RenderEnderman(this));
      this.entityRenderMap.put(EntitySnowman.class, new RenderSnowMan(this));
      this.entityRenderMap.put(EntitySkeleton.class, new RenderSkeleton(this));
      this.entityRenderMap.put(EntityWitch.class, new RenderWitch(this));
      this.entityRenderMap.put(EntityBlaze.class, new RenderBlaze(this));
      this.entityRenderMap.put(EntityPigZombie.class, new RenderPigZombie(this));
      this.entityRenderMap.put(EntityZombie.class, new RenderZombie(this));
      this.entityRenderMap.put(EntitySlime.class, new RenderSlime(this, new ModelSlime(16), 0.25F));
      this.entityRenderMap.put(EntityMagmaCube.class, new RenderMagmaCube(this));
      this.entityRenderMap.put(EntityGiantZombie.class, new RenderGiantZombie(this, new ModelZombie(), 0.5F, 6.0F));
      this.entityRenderMap.put(EntityGhast.class, new RenderGhast(this));
      this.entityRenderMap.put(EntitySquid.class, new RenderSquid(this, new ModelSquid(), 0.7F));
      this.entityRenderMap.put(EntityVillager.class, new RenderVillager(this));
      this.entityRenderMap.put(EntityIronGolem.class, new RenderIronGolem(this));
      this.entityRenderMap.put(EntityBat.class, new RenderBat(this));
      this.entityRenderMap.put(EntityGuardian.class, new RenderGuardian(this));
      this.entityRenderMap.put(EntityDragon.class, new RenderDragon(this));
      this.entityRenderMap.put(EntityEnderCrystal.class, new RenderEnderCrystal(this));
      this.entityRenderMap.put(EntityWither.class, new RenderWither(this));
      this.entityRenderMap.put(Entity.class, new RenderEntity(this));
      this.entityRenderMap.put(EntityPainting.class, new RenderPainting(this));
      this.entityRenderMap.put(EntityItemFrame.class, new RenderItemFrame(this, itemRendererIn));
      this.entityRenderMap.put(EntityLeashKnot.class, new RenderLeashKnot(this));
      this.entityRenderMap.put(EntityArrow.class, new RenderArrow(this));
      this.entityRenderMap.put(EntitySnowball.class, new RenderSnowball(this, Items.snowball, itemRendererIn));
      this.entityRenderMap.put(EntityEnderPearl.class, new RenderSnowball(this, Items.ender_pearl, itemRendererIn));
      this.entityRenderMap.put(EntityEnderEye.class, new RenderSnowball(this, Items.ender_eye, itemRendererIn));
      this.entityRenderMap.put(EntityEgg.class, new RenderSnowball(this, Items.egg, itemRendererIn));
      this.entityRenderMap.put(EntityPotion.class, new RenderPotion(this, itemRendererIn));
      this.entityRenderMap.put(EntityExpBottle.class, new RenderSnowball(this, Items.experience_bottle, itemRendererIn));
      this.entityRenderMap.put(EntityFireworkRocket.class, new RenderSnowball(this, Items.fireworks, itemRendererIn));
      this.entityRenderMap.put(EntityLargeFireball.class, new RenderFireball(this, 2.0F));
      this.entityRenderMap.put(EntitySmallFireball.class, new RenderFireball(this, 0.5F));
      this.entityRenderMap.put(EntityWitherSkull.class, new RenderWitherSkull(this));
      this.entityRenderMap.put(EntityItem.class, new RenderEntityItem(this, itemRendererIn));
      this.entityRenderMap.put(EntityXPOrb.class, new RenderXPOrb(this));
      this.entityRenderMap.put(EntityTNTPrimed.class, new RenderTNTPrimed(this));
      this.entityRenderMap.put(EntityFallingBlock.class, new RenderFallingBlock(this));
      this.entityRenderMap.put(EntityArmorStand.class, new ArmorStandRenderer(this));
      this.entityRenderMap.put(EntityMinecartTNT.class, new RenderTntMinecart(this));
      this.entityRenderMap.put(EntityMinecartMobSpawner.class, new RenderMinecartMobSpawner(this));
      this.entityRenderMap.put(EntityMinecart.class, new RenderMinecart(this));
      this.entityRenderMap.put(EntityBoat.class, new RenderBoat(this));
      this.entityRenderMap.put(EntityFishHook.class, new RenderFish(this));
      this.entityRenderMap.put(EntityHorse.class, new RenderHorse(this, new ModelHorse(), 0.75F));
      this.entityRenderMap.put(EntityLightningBolt.class, new RenderLightningBolt(this));
      this.playerRenderer = new RenderPlayer(this);
      this.skinMap.put("default", this.playerRenderer);
      this.skinMap.put("slim", new RenderPlayer(this, true));
      PlayerItemsLayer.register(this.skinMap);
      if (Reflector.RenderingRegistry_loadEntityRenderers.exists()) {
         Reflector.call(Reflector.RenderingRegistry_loadEntityRenderers, this, this.entityRenderMap);
      }
   }

   public void setRenderPosition(double renderPosXIn, double renderPosYIn, double renderPosZIn) {
      this.renderPosX = renderPosXIn;
      this.renderPosY = renderPosYIn;
      this.renderPosZ = renderPosZIn;
   }

   public <T extends Entity> Render<T> getEntityClassRenderObject(Class<? extends Entity> entityClass) {
      Render render = this.entityRenderMap.get(entityClass);
      if (render == null && entityClass != Entity.class) {
         render = this.getEntityClassRenderObject((Class<? extends Entity>)entityClass.getSuperclass());
         this.entityRenderMap.put(entityClass, render);
      }

      return render;
   }

   public <T extends Entity> Render<T> getEntityRenderObject(Entity entityIn) {
      if (entityIn instanceof AbstractClientPlayer) {
         String skinType = ((AbstractClientPlayer)entityIn).getSkinType();
         RenderPlayer renderPlayer = this.skinMap.get(skinType);
         return (Render<T>)(Render)(renderPlayer != null ? renderPlayer : this.playerRenderer);
      } else {
         return this.getEntityClassRenderObject((Class<? extends Entity>)entityIn.getClass());
      }
   }

   public void cacheActiveRenderInfo(
      World worldIn, FontRenderer textRendererIn, Entity livingPlayerIn, Entity pointedEntityIn, GameSettings optionsIn, float partialTicks
   ) {
      this.worldObj = worldIn;
      this.options = optionsIn;
      this.livingPlayer = livingPlayerIn;
      this.pointedEntity = pointedEntityIn;
      this.textRenderer = textRendererIn;
      if (livingPlayerIn instanceof EntityLivingBase && ((EntityLivingBase)livingPlayerIn).isPlayerSleeping()) {
         IBlockState blockState = worldIn.getBlockState(new BlockPos(livingPlayerIn));
         Block block = blockState.getBlock();
         if (Reflector.callBoolean(block, Reflector.ForgeBlock_isBed, blockState, worldIn, new BlockPos(livingPlayerIn), livingPlayerIn)) {
            EnumFacing enumFacing = (EnumFacing)Reflector.call(block, Reflector.ForgeBlock_getBedDirection, blockState, worldIn, new BlockPos(livingPlayerIn));
            int horizontalIndex = enumFacing.getHorizontalIndex();
            this.playerViewY = horizontalIndex * 90 + 180;
            this.playerViewX = 0.0F;
         } else if (block == Blocks.bed) {
            int horizontalIndex = blockState.getValue(BlockBed.FACING).getHorizontalIndex();
            this.playerViewY = horizontalIndex * 90 + 180;
            this.playerViewX = 0.0F;
         }
      } else {
         FreeLookHandler freeLookHandler = CheatBreaker.getInstance().getModManager().freeLookHandler;
         if (freeLookHandler.active) {
            this.playerViewY = freeLookHandler.prevYaw + (freeLookHandler.yaw - freeLookHandler.prevYaw) * partialTicks;
            this.playerViewX = freeLookHandler.prevPitch + (freeLookHandler.pitch - freeLookHandler.prevPitch) * partialTicks;
         } else {
            this.playerViewY = livingPlayerIn.prevRotationYaw + (livingPlayerIn.rotationYaw - livingPlayerIn.prevRotationYaw) * partialTicks;
            this.playerViewX = livingPlayerIn.prevRotationPitch + (livingPlayerIn.rotationPitch - livingPlayerIn.prevRotationPitch) * partialTicks;
         }
      }

      if (optionsIn.thirdPersonView == 2) {
         this.playerViewY += 180.0F;
         this.playerViewX = -this.playerViewX;
      }

      this.viewerPosX = livingPlayerIn.lastTickPosX + (livingPlayerIn.posX - livingPlayerIn.lastTickPosX) * partialTicks;
      this.viewerPosY = livingPlayerIn.lastTickPosY + (livingPlayerIn.posY - livingPlayerIn.lastTickPosY) * partialTicks;
      this.viewerPosZ = livingPlayerIn.lastTickPosZ + (livingPlayerIn.posZ - livingPlayerIn.lastTickPosZ) * partialTicks;
   }

   public void setPlayerViewY(float playerViewYIn) {
      this.playerViewY = playerViewYIn;
   }

   public boolean isRenderShadow() {
      return this.renderShadow;
   }

   public void setRenderShadow(boolean renderShadowIn) {
      this.renderShadow = renderShadowIn;
   }

   public void setDebugBoundingBox(boolean debugBoundingBoxIn) {
      this.debugBoundingBox = debugBoundingBoxIn;
   }

   public boolean isDebugBoundingBox() {
      return this.debugBoundingBox;
   }

   public boolean renderEntitySimple(Entity entityIn, float partialTicks) {
      return this.renderEntityStatic(entityIn, partialTicks, false);
   }

   public boolean shouldRender(Entity entityIn, ICamera camera, double camX, double camY, double camZ) {
      Render entityRenderObject = this.getEntityRenderObject(entityIn);
      return entityRenderObject != null && entityRenderObject.shouldRender(entityIn, camera, camX, camY, camZ);
   }

   public boolean renderEntityStatic(Entity entity, float partialTicks, boolean hideDebugBox) {
      if (entity.ticksExisted == 0) {
         entity.lastTickPosX = entity.posX;
         entity.lastTickPosY = entity.posY;
         entity.lastTickPosZ = entity.posZ;
      }

      double d = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks;
      double d2 = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks;
      double d3 = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks;
      float f = entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks;
      int brightnessForRender = entity.getBrightnessForRender(partialTicks);
      if (entity.isBurning()) {
         brightnessForRender = 15728880;
      }

      int n = brightnessForRender % 65536;
      int n2 = brightnessForRender / 65536;
      OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      return this.doRenderEntity(entity, d - this.renderPosX, d2 - this.renderPosY, d3 - this.renderPosZ, f, partialTicks, hideDebugBox);
   }

   public void renderWitherSkull(Entity entityIn, float partialTicks) {
      double d = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
      double d2 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
      double d3 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
      Render entityRenderObject = this.getEntityRenderObject(entityIn);
      if (entityRenderObject != null && this.renderEngine != null) {
         int brightnessForRender = entityIn.getBrightnessForRender(partialTicks);
         int n = brightnessForRender % 65536;
         int n2 = brightnessForRender / 65536;
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, n / 1.0F, n2 / 1.0F);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         entityRenderObject.renderName(entityIn, d - this.renderPosX, d2 - this.renderPosY, d3 - this.renderPosZ);
      }
   }

   public boolean renderEntityWithPosYaw(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks) {
      return this.doRenderEntity(entityIn, x, y, z, entityYaw, partialTicks, false);
   }

   public boolean doRenderEntity(Entity entity, double x, double y, double z, float entityYaw, float partialTicks, boolean hideDebugBox) {
      Render render = null;

      try {
         render = this.getEntityRenderObject(entity);
         if (render != null && this.renderEngine != null) {
            try {
               if (render instanceof RendererLivingEntity) {
                  ((RendererLivingEntity)render).setRenderOutlines(this.renderOutlines);
               }

               if (CustomEntityModels.isActive()) {
                  this.renderRender = render;
               }

               render.doRender(entity, x, y, z, entityYaw, partialTicks);
            } catch (Throwable e) {
               throw new ReportedException(CrashReport.makeCrashReport(e, "Rendering entity in world"));
            }

            try {
               if (!this.renderOutlines) {
                  render.doRenderShadowAndFire(entity, x, y, z, entityYaw, partialTicks);
               }
            } catch (Throwable e) {
               throw new ReportedException(CrashReport.makeCrashReport(e, "Post-rendering entity in world"));
            }

            if (CheatBreaker.getInstance().getModManager().hitboxesMod.isEnabled() && !entity.isInvisible()) {
               try {
                  this.renderDebugBoundingBox(entity, x, y, z, entityYaw, partialTicks);
               } catch (Throwable e) {
                  throw new ReportedException(CrashReport.makeCrashReport(e, "Rendering entity hitbox in world"));
               }
            }

            return true;
         } else {
            return this.renderEngine == null;
         }
      } catch (Throwable e) {
         CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Rendering entity in world");
         CrashReportCategory makeCategory = makeCrashReport.makeCategory("Entity being rendered");
         entity.addEntityCrashInfo(makeCategory);
         CrashReportCategory makeCategory2 = makeCrashReport.makeCategory("Renderer details");
         makeCategory2.addCrashSection("Assigned renderer", render);
         makeCategory2.addCrashSection("Location", CrashReportCategory.getCoordinateInfo(x, y, z));
         makeCategory2.addCrashSection("Rotation", entityYaw);
         makeCategory2.addCrashSection("Delta", partialTicks);
         throw new ReportedException(makeCrashReport);
      }
   }

   public void renderDebugBoundingBox(Entity entityIn, double x, double y, double z, float entityYaw, float partialTicks) {
      HitboxesMod hitboxesMod = CheatBreaker.getInstance().getModManager().hitboxesMod;
      boolean booleanValue = hitboxesMod.player.showOwnHitbox.getBooleanValue();
      boolean flag = entityIn == Minecraft.getMinecraft().thePlayer;
      HitboxSettings findHitboxSettings = hitboxesMod.findHitboxSettings(entityIn);
      Color toColor = RenderUtils.toColor((flag ? findHitboxSettings.ownHitboxOutlineColor : findHitboxSettings.outlineColor).getColorValue());
      Color toColor2 = RenderUtils.toColor(findHitboxSettings.eyeHeightColor.getColorValue());
      Color toColor3 = RenderUtils.toColor(findHitboxSettings.lookVectorColor.getColorValue());
      if (findHitboxSettings.show.getBooleanValue()) {
         if (booleanValue || !flag) {
            if (!Shaders.isShadowPass) {
               GlStateManager.depthMask(false);
               GlStateManager.disableTexture2D();
               GlStateManager.disableLighting();
               GlStateManager.disableCull();
               GlStateManager.disableBlend();
               float f = entityIn.width / 2.0F;
               AxisAlignedBB entityBoundingBox = entityIn.getEntityBoundingBox();
               GlStateManager.enableBlend();
               if (findHitboxSettings.show2.getBooleanValue()) {
                  if (findHitboxSettings.show6.getBooleanValue()) {
                     GL11.glPushAttrib(8192);
                     GL11.glLineStipple(findHitboxSettings.lineDistance.getIntValue(), (short)-21846);
                     GL11.glEnable(2852);
                     GL11.glBegin(1);
                     GL11.glEnd();
                  }

                  GL11.glLineWidth(findHitboxSettings.lineThickness.getIntValue());
                  AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                     entityBoundingBox.minX - entityIn.posX + x,
                     entityBoundingBox.minY - entityIn.posY + y,
                     entityBoundingBox.minZ - entityIn.posZ + z,
                     entityBoundingBox.maxX - entityIn.posX + x,
                     entityBoundingBox.maxY - entityIn.posY + y,
                     entityBoundingBox.maxZ - entityIn.posZ + z
                  );
                  RenderGlobal.drawOutlinedBoundingBox(axisAlignedBB, toColor.getRed(), toColor.getGreen(), toColor.getBlue(), toColor.getAlpha());
                  if (findHitboxSettings.show6.getBooleanValue()) {
                     GL11.glPopAttrib();
                  }
               }

               GlStateManager.disableBlend();
               if (findHitboxSettings.show4.getBooleanValue()) {
                  AxisAlignedBB axisAlignedBB = new AxisAlignedBB(
                     x - f, y + entityIn.getEyeHeight() - 0.01F, z - f, x + f, y + entityIn.getEyeHeight() + 0.01F, z + f
                  );
                  RenderGlobal.drawOutlinedBoundingBox(axisAlignedBB, toColor2.getRed(), toColor2.getGreen(), toColor2.getBlue(), toColor2.getAlpha());
               }

               if (!findHitboxSettings.show5.getBooleanValue()) {
                  GlStateManager.enableTexture2D();
                  GlStateManager.enableLighting();
                  GlStateManager.enableCull();
                  GlStateManager.disableBlend();
                  GlStateManager.depthMask(true);
                  return;
               }

               Tessellator instance = Tessellator.getInstance();
               WorldRenderer worldRenderer = instance.getWorldRenderer();
               Vec3 look = entityIn.getLook(partialTicks);
               worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
               worldRenderer.pos(x, y + entityIn.getEyeHeight(), z).endVertex();
               worldRenderer.pos(x + look.xCoord * 2.0, y + entityIn.getEyeHeight() + look.yCoord * 2.0, z + look.zCoord * 2.0)
                  .color(toColor3.getRed(), toColor3.getGreen(), toColor3.getBlue(), toColor3.getAlpha())
                  .endVertex();
               instance.draw();
               GlStateManager.enableTexture2D();
               GlStateManager.enableLighting();
               GlStateManager.enableCull();
               GlStateManager.disableBlend();
               GlStateManager.depthMask(true);
            }
         }
      }
   }

   public void set(World worldIn) {
      this.worldObj = worldIn;
   }

   public double getDistanceToCamera(double x, double y, double z) {
      double d = x - this.viewerPosX;
      double d2 = y - this.viewerPosY;
      double d3 = z - this.viewerPosZ;
      return d * d + d2 * d2 + d3 * d3;
   }

   public FontRenderer getFontRenderer() {
      return this.textRenderer;
   }

   public void setRenderOutlines(boolean renderOutlinesIn) {
      this.renderOutlines = renderOutlinesIn;
   }

   public Map<Class, Render> getEntityRenderMap() {
      return this.entityRenderMap;
   }

   public void setEntityRenderMap(Map map) {
      this.entityRenderMap = map;
   }

   public Map<String, RenderPlayer> getSkinMap() {
      return Collections.unmodifiableMap(this.skinMap);
   }

   public double getRenderPosX() {
      return this.renderPosX;
   }

   public static RenderManager getRenderManager() {
      return renderManager;
   }

   public double getRenderPosY() {
      return this.renderPosY;
   }

   public double getRenderPosZ() {
      return this.renderPosZ;
   }
}
