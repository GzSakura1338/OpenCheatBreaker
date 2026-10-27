package net.minecraft.client.particle;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.impl.ParticlesMod;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.optifine.Config;
import net.optifine.reflect.Reflector;

public class EffectRenderer {
   public static ResourceLocation particleTextures = new ResourceLocation("textures/particle/particles.png");
   public List<EntityFX>[][] fxLayers = new List[4][];
   public List<EntityParticleEmitter> particleEmitters = Lists.newArrayList();
   public Random rand = new Random();
   public Map<Integer, IParticleFactory> particleTypes = Maps.newHashMap();
   public World worldObj;
   public TextureManager renderer;

   public EffectRenderer(World worldIn, TextureManager rendererIn) {
      this.worldObj = worldIn;
      this.renderer = rendererIn;

      for (int i = 0; i < 4; i++) {
         this.fxLayers[i] = new List[2];

         for (int j = 0; j < 2; j++) {
            Minecraft.getMinecraft().stream.run11();
            this.fxLayers[i][j] = Lists.newArrayList();
         }
      }

      this.registerVanillaParticles();
   }

   public void registerVanillaParticles() {
      this.registerParticle(EnumParticleTypes.EXPLOSION_NORMAL.getParticleID(), new EntityExplodeFX.Factory());
      this.registerParticle(EnumParticleTypes.WATER_BUBBLE.getParticleID(), new EntityBubbleFX.Factory());
      this.registerParticle(EnumParticleTypes.WATER_SPLASH.getParticleID(), new EntitySplashFX.Factory());
      this.registerParticle(EnumParticleTypes.WATER_WAKE.getParticleID(), new EntityFishWakeFX.Factory());
      this.registerParticle(EnumParticleTypes.WATER_DROP.getParticleID(), new EntityRainFX.Factory());
      this.registerParticle(EnumParticleTypes.SUSPENDED.getParticleID(), new EntitySuspendFX.Factory());
      this.registerParticle(EnumParticleTypes.SUSPENDED_DEPTH.getParticleID(), new EntityAuraFX.Factory());
      this.registerParticle(EnumParticleTypes.CRIT.getParticleID(), new EntityCrit2FX.Factory());
      this.registerParticle(EnumParticleTypes.CRIT_MAGIC.getParticleID(), new EntityCrit2FX.MagicFactory());
      this.registerParticle(EnumParticleTypes.SMOKE_NORMAL.getParticleID(), new EntitySmokeFX.Factory());
      this.registerParticle(EnumParticleTypes.SMOKE_LARGE.getParticleID(), new EntityCritFX.Factory());
      this.registerParticle(EnumParticleTypes.SPELL.getParticleID(), new EntitySpellParticleFX.Factory());
      this.registerParticle(EnumParticleTypes.SPELL_INSTANT.getParticleID(), new EntitySpellParticleFX.InstantFactory());
      this.registerParticle(EnumParticleTypes.SPELL_MOB.getParticleID(), new EntitySpellParticleFX.MobFactory());
      this.registerParticle(EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID(), new EntitySpellParticleFX.AmbientMobFactory());
      this.registerParticle(EnumParticleTypes.SPELL_WITCH.getParticleID(), new EntitySpellParticleFX.WitchFactory());
      this.registerParticle(EnumParticleTypes.DRIP_WATER.getParticleID(), new EntityDropParticleFX.WaterFactory());
      this.registerParticle(EnumParticleTypes.DRIP_LAVA.getParticleID(), new EntityDropParticleFX.LavaFactory());
      this.registerParticle(EnumParticleTypes.VILLAGER_ANGRY.getParticleID(), new EntityHeartFX.AngryVillagerFactory());
      this.registerParticle(EnumParticleTypes.VILLAGER_HAPPY.getParticleID(), new EntityAuraFX.HappyVillagerFactory());
      this.registerParticle(EnumParticleTypes.TOWN_AURA.getParticleID(), new EntityAuraFX.Factory());
      this.registerParticle(EnumParticleTypes.NOTE.getParticleID(), new EntityNoteFX.Factory());
      this.registerParticle(EnumParticleTypes.PORTAL.getParticleID(), new EntityPortalFX.Factory());
      this.registerParticle(EnumParticleTypes.ENCHANTMENT_TABLE.getParticleID(), new EntityEnchantmentTableParticleFX.EnchantmentTable());
      this.registerParticle(EnumParticleTypes.FLAME.getParticleID(), new EntityFlameFX.Factory());
      this.registerParticle(EnumParticleTypes.LAVA.getParticleID(), new EntityLavaFX.Factory());
      this.registerParticle(EnumParticleTypes.FOOTSTEP.getParticleID(), new EntityFootStepFX.Factory());
      this.registerParticle(EnumParticleTypes.CLOUD.getParticleID(), new EntityCloudFX.Factory());
      this.registerParticle(EnumParticleTypes.REDSTONE.getParticleID(), new EntityReddustFX.Factory());
      this.registerParticle(EnumParticleTypes.SNOWBALL.getParticleID(), new EntityBreakingFX.SnowballFactory());
      this.registerParticle(EnumParticleTypes.SNOW_SHOVEL.getParticleID(), new EntitySnowShovelFX.Factory());
      this.registerParticle(EnumParticleTypes.SLIME.getParticleID(), new EntityBreakingFX.SlimeFactory());
      this.registerParticle(EnumParticleTypes.HEART.getParticleID(), new EntityHeartFX.Factory());
      this.registerParticle(EnumParticleTypes.BARRIER.getParticleID(), new Barrier.Factory());
      this.registerParticle(EnumParticleTypes.ITEM_CRACK.getParticleID(), new EntityBreakingFX.Factory());
      this.registerParticle(EnumParticleTypes.BLOCK_CRACK.getParticleID(), new EntityDiggingFX.Factory());
      this.registerParticle(EnumParticleTypes.BLOCK_DUST.getParticleID(), new EntityBlockDustFX.Factory());
      this.registerParticle(EnumParticleTypes.EXPLOSION_HUGE.getParticleID(), new EntityHugeExplodeFX.Factory());
      this.registerParticle(EnumParticleTypes.EXPLOSION_LARGE.getParticleID(), new EntityLargeExplodeFX.Factory());
      this.registerParticle(EnumParticleTypes.FIREWORKS_SPARK.getParticleID(), new EntityFirework.Factory());
      this.registerParticle(EnumParticleTypes.MOB_APPEARANCE.getParticleID(), new MobAppearance.Factory());
   }

   public void registerParticle(int id, IParticleFactory particleFactory) {
      this.particleTypes.put(id, particleFactory);
   }

   public void emitParticleAtEntity(Entity entityIn, EnumParticleTypes particleTypes) {
      this.particleEmitters.add(new EntityParticleEmitter(this.worldObj, entityIn, particleTypes));
   }

   public EntityFX spawnEffectParticle(
      int particleId, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters
   ) {
      IParticleFactory particleFactory = this.particleTypes.get(particleId);
      if (particleFactory != null) {
         EntityFX entityFX = particleFactory.getEntityFX(particleId, this.worldObj, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
         if (entityFX != null) {
            this.addEffect(entityFX);
            return entityFX;
         }
      }

      return null;
   }

   public void addEffect(EntityFX effect) {
      if (effect != null && (!(effect instanceof EntityFirework.SparkFX) || Config.isFireworkParticles())) {
         int fXLayer = effect.getFXLayer();
         int n = effect.getAlpha() != 1.0F ? 0 : 1;
         if (this.fxLayers[fXLayer][n].size() >= 4000) {
            this.fxLayers[fXLayer][n].remove(0);
         }

         this.fxLayers[fXLayer][n].add(effect);
      }
   }

   public void updateEffects() {
      for (int i = 0; i < 4; i++) {
         this.updateEffectLayer(i);
      }

      ArrayList newArrayList = Lists.newArrayList();

      for (EntityParticleEmitter entityParticleEmitter : this.particleEmitters) {
         entityParticleEmitter.onUpdate();
         if (entityParticleEmitter.isDead) {
            newArrayList.add(entityParticleEmitter);
         }
      }

      this.particleEmitters.removeAll(newArrayList);
   }

   public void updateEffectLayer(int layer) {
      for (int i = 0; i < 2; i++) {
         this.updateEffectAlphaLayer(this.fxLayers[layer][i]);
      }
   }

   public void updateEffectAlphaLayer(List<EntityFX> entitiesFX) {
      ArrayList newArrayList = Lists.newArrayList();
      long currentTimeMillis = System.currentTimeMillis();
      int size = entitiesFX.size();

      for (int i = 0; i < entitiesFX.size(); i++) {
         EntityFX entityFX = (EntityFX)entitiesFX.get(i);
         this.tickParticle(entityFX);
         if (entityFX.isDead) {
            newArrayList.add(entityFX);
         }

         size--;
         if (System.currentTimeMillis() > currentTimeMillis + 20L) {
            break;
         }
      }

      if (size > 0) {
         int n = size;

         for (Iterator iterator = entitiesFX.iterator(); iterator.hasNext() && n > 0; n--) {
            EntityFX entityFX = (EntityFX)iterator.next();
            entityFX.setDead();
            iterator.remove();
         }
      }

      entitiesFX.removeAll(newArrayList);
   }

   public void tickParticle(final EntityFX particle) {
      try {
         particle.onUpdate();
      } catch (Throwable e) {
         CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Ticking Particle");
         CrashReportCategory makeCategory = makeCrashReport.makeCategory("Particle being ticked");
         final int fXLayer = particle.getFXLayer();
         makeCategory.addCrashSectionCallable("Particle", new Callable<String>() {
            public String call() throws Exception {
               return particle.toString();
            }
         });
         makeCategory.addCrashSectionCallable("Particle Type", new Callable<String>() {
            public String call() throws Exception {
               return fXLayer == 0 ? "MISC_TEXTURE" : (fXLayer == 1 ? "TERRAIN_TEXTURE" : (fXLayer == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + fXLayer));
            }
         });
         throw new ReportedException(makeCrashReport);
      }
   }

   public void renderParticles(Entity entityIn, float partialTicks) {
      float rotationX = ActiveRenderInfo.getRotationX();
      float rotationZ = ActiveRenderInfo.getRotationZ();
      float rotationYZ = ActiveRenderInfo.getRotationYZ();
      float rotationXY = ActiveRenderInfo.getRotationXY();
      float rotationXZ = ActiveRenderInfo.getRotationXZ();
      EntityFX.interpPosX = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
      EntityFX.interpPosY = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
      EntityFX.interpPosZ = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(770, 771);
      GlStateManager.alphaFunc(516, 0.003921569F);

      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 2; j++) {
            final int n = i;
            if (!this.fxLayers[i][j].isEmpty()) {
               switch (j) {
                  case 0:
                     GlStateManager.depthMask(false);
                     break;
                  case 1:
                     GlStateManager.depthMask(true);
               }

               switch (i) {
                  case 0:
                  default:
                     this.renderer.bindTexture(particleTextures);
                     break;
                  case 1:
                     this.renderer.bindTexture(TextureMap.locationBlocksTexture);
               }

               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               Tessellator instance = Tessellator.getInstance();
               WorldRenderer worldRenderer = instance.getWorldRenderer();
               worldRenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);

               for (int k = 0; k < this.fxLayers[i][j].size(); k++) {
                  final EntityFX entityFX = this.fxLayers[i][j].get(k);

                  try {
                     if (CheatBreaker.getInstance().getKeybindSettings().shouldRenderEntity(entityFX)) {
                        entityFX.renderParticle(worldRenderer, entityIn, partialTicks, rotationX, rotationXZ, rotationZ, rotationYZ, rotationXY);
                     }
                  } catch (Throwable e) {
                     CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Rendering Particle");
                     CrashReportCategory makeCategory = makeCrashReport.makeCategory("Particle being rendered");
                     makeCategory.addCrashSectionCallable("Particle", new Callable<String>() {
                        public String call() throws Exception {
                           return entityFX.toString();
                        }
                     });
                     makeCategory.addCrashSectionCallable(
                        "Particle Type",
                        new Callable<String>() {
                           public String call() throws Exception {
                              return n == 0
                                 ? "MISC_TEXTURE"
                                 : (n == 1 ? "TERRAIN_TEXTURE" : (n == 3 ? "ENTITY_PARTICLE_TEXTURE" : "Unknown - " + n));
                           }
                        }
                     );
                     throw new ReportedException(makeCrashReport);
                  }
               }

               instance.draw();
            }
         }
      }

      GlStateManager.depthMask(true);
      GlStateManager.disableBlend();
      GlStateManager.alphaFunc(516, 0.1F);
   }

   public void renderLitParticles(Entity entityIn, float partialTick) {
      float f = (float) (Math.PI / 180.0);
      float cos = MathHelper.cos(entityIn.rotationYaw * (float) (Math.PI / 180.0));
      float sin = MathHelper.sin(entityIn.rotationYaw * (float) (Math.PI / 180.0));
      float f2 = -sin * MathHelper.sin(entityIn.rotationPitch * (float) (Math.PI / 180.0));
      float f3 = cos * MathHelper.sin(entityIn.rotationPitch * (float) (Math.PI / 180.0));
      float cos2 = MathHelper.cos(entityIn.rotationPitch * (float) (Math.PI / 180.0));

      for (int i = 0; i < 2; i++) {
         List list = this.fxLayers[3][i];
         if (!list.isEmpty()) {
            Tessellator instance = Tessellator.getInstance();
            WorldRenderer worldRenderer = instance.getWorldRenderer();

            for (int j = 0; j < list.size(); j++) {
               EntityFX entityFX = (EntityFX)list.get(j);
               entityFX.renderParticle(worldRenderer, entityIn, partialTick, cos, cos2, sin, f2, f3);
            }
         }
      }
   }

   public void clearEffects(World worldIn) {
      this.worldObj = worldIn;

      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 2; j++) {
            this.fxLayers[i][j].clear();
         }
      }

      this.particleEmitters.clear();
   }

   public void addBlockDestroyEffects(BlockPos pos, IBlockState state) {
      ParticlesMod particlesMod = CheatBreaker.getInstance().getModManager().particlesMod;
      if (!particlesMod.isEnabled() || particlesMod.showBlockBreakingParticles.getBooleanValue()) {
         boolean flag;
         if (Reflector.ForgeBlock_addDestroyEffects.exists() && Reflector.ForgeBlock_isAir.exists()) {
            Block block = state.getBlock();
            flag = !Reflector.callBoolean(block, Reflector.ForgeBlock_isAir, this.worldObj, pos)
               && !Reflector.callBoolean(block, Reflector.ForgeBlock_addDestroyEffects, this.worldObj, pos, this);
         } else {
            flag = state.getBlock().getMaterial() != Material.air;
         }

         if (flag) {
            state = state.getBlock().getActualState(state, this.worldObj, pos);
            byte b = 4;

            for (int i = 0; i < b; i++) {
               for (int j = 0; j < b; j++) {
                  for (int k = 0; k < b; k++) {
                     double d = pos.getX() + (i + 0.5) / b;
                     double d2 = pos.getY() + (j + 0.5) / b;
                     double d3 = pos.getZ() + (k + 0.5) / b;
                     this.addEffect(
                        new EntityDiggingFX(
                              this.worldObj, d, d2, d3, d - pos.getX() - 0.5, d2 - pos.getY() - 0.5, d3 - pos.getZ() - 0.5, state
                           )
                           .setBlockPos(pos)
                     );
                  }
               }
            }
         }
      }
   }

   public void addBlockHitEffects(BlockPos pos, EnumFacing side) {
      ParticlesMod particlesMod = CheatBreaker.getInstance().getModManager().particlesMod;
      if (!particlesMod.isEnabled() || particlesMod.showBlockBreakingParticles.getBooleanValue()) {
         IBlockState blockState = this.worldObj.getBlockState(pos);
         Block block = blockState.getBlock();
         if (block.getRenderType() != -1) {
            int n = pos.getX();
            int n2 = pos.getY();
            int n3 = pos.getZ();
            float f = 0.1F;
            double d = n
               + this.rand.nextDouble() * (block.getBlockBoundsMaxX() - block.getBlockBoundsMinX() - f * 2.0F)
               + f
               + block.getBlockBoundsMinX();
            double d2 = n2
               + this.rand.nextDouble() * (block.getBlockBoundsMaxY() - block.getBlockBoundsMinY() - f * 2.0F)
               + f
               + block.getBlockBoundsMinY();
            double d3 = n3
               + this.rand.nextDouble() * (block.getBlockBoundsMaxZ() - block.getBlockBoundsMinZ() - f * 2.0F)
               + f
               + block.getBlockBoundsMinZ();
            if (side == EnumFacing.DOWN) {
               d2 = n2 + block.getBlockBoundsMinY() - f;
            }

            if (side == EnumFacing.UP) {
               d2 = n2 + block.getBlockBoundsMaxY() + f;
            }

            if (side == EnumFacing.NORTH) {
               d3 = n3 + block.getBlockBoundsMinZ() - f;
            }

            if (side == EnumFacing.SOUTH) {
               d3 = n3 + block.getBlockBoundsMaxZ() + f;
            }

            if (side == EnumFacing.WEST) {
               d = n + block.getBlockBoundsMinX() - f;
            }

            if (side == EnumFacing.EAST) {
               d = n + block.getBlockBoundsMaxX() + f;
            }

            this.addEffect(
               new EntityDiggingFX(this.worldObj, d, d2, d3, 0.0, 0.0, 0.0, blockState)
                  .setBlockPos(pos)
                  .multiplyVelocity(0.2F)
                  .multipleParticleScaleBy(0.6F)
            );
         }
      }
   }

   public void moveToAlphaLayer(EntityFX effect) {
      this.moveToLayer(effect, 1, 0);
   }

   public void moveToNoAlphaLayer(EntityFX effect) {
      this.moveToLayer(effect, 0, 1);
   }

   public void moveToLayer(EntityFX effect, int layerFrom, int layerTo) {
      for (int i = 0; i < 4; i++) {
         if (this.fxLayers[i][layerFrom].contains(effect)) {
            this.fxLayers[i][layerFrom].remove(effect);
            this.fxLayers[i][layerTo].add(effect);
         }
      }
   }

   public String getStatistics() {
      int n = 0;

      for (int i = 0; i < 4; i++) {
         for (int j = 0; j < 2; j++) {
            n += this.fxLayers[i][j].size();
         }
      }

      return "" + n;
   }

   public void addBlockHitEffects(BlockPos blockPos, MovingObjectPosition movingObjectPosition) {
      IBlockState blockState = this.worldObj.getBlockState(blockPos);
      if (blockState != null) {
         boolean callBoolean = Reflector.callBoolean(blockState.getBlock(), Reflector.ForgeBlock_addHitEffects, this.worldObj, movingObjectPosition, this);
         if (blockState != null && !callBoolean) {
            this.addBlockHitEffects(blockPos, movingObjectPosition.sideHit);
         }
      }
   }

   public void handleEntity(Entity entity, EnumParticleTypes enumParticleTypes, float f) {
      this.particleEmitters.add(new EntityParticleEmitter(this.worldObj, entity, enumParticleTypes, f));
   }
}
