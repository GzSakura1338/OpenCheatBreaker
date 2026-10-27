package net.minecraft.client.renderer.entity;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.EntityLivingBaseDoubleEvent1;
import com.cheatbreaker.client.event.EntityLivingBaseFloatEvent2;
import com.cheatbreaker.client.event.EntityLivingBaseFloatEvent3;
import com.cheatbreaker.client.mod.impl.NametagMod;
import com.google.common.collect.Lists;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSpider;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.scoreboard.Team.EnumVisible;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.optifine.Config;
import net.optifine.EmissiveTextures;
import net.optifine.entity.model.CustomEntityModels;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public abstract class RendererLivingEntity<T extends EntityLivingBase> extends Render<T> {
   public static Logger logger = LogManager.getLogger();
   public static DynamicTexture textureBrightness = new DynamicTexture(16, 16);
   public FloatBuffer brightnessBuffer = GLAllocation.createDirectFloatBuffer(4);
   public List<LayerRenderer<T>> layerRenderers = Lists.newArrayList();
   public boolean renderOutlines = false;
   public ModelBase mainModel;
   public static float NAME_TAG_RANGE = 64.0F;
   public static float NAME_TAG_RANGE_SNEAK = 32.0F;
   public EntityLivingBase renderEntity;
   public float renderLimbSwing;
   public float renderLimbSwingAmount;
   public float renderAgeInTicks;
   public float renderHeadYaw;
   public float renderHeadPitch;
   public float renderScaleFactor;
   public float renderPartialTicks;
   public boolean renderModelPushMatrix;
   public boolean renderLayersPushMatrix;
   public static boolean animateModelLiving = Boolean.getBoolean("animate.model.living");

   public RendererLivingEntity(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
      super(renderManagerIn);
      this.mainModel = modelBaseIn;
      this.shadowSize = shadowSizeIn;
      this.renderModelPushMatrix = this.mainModel instanceof ModelSpider;
   }

   public <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean addLayer(U layer) {
      return this.layerRenderers.add((LayerRenderer)layer);
   }

   public <V extends EntityLivingBase, U extends LayerRenderer<V>> boolean removeLayer(U layer) {
      return this.layerRenderers.remove(layer);
   }

   public ModelBase getMainModel() {
      return this.mainModel;
   }

   public float interpolateRotation(float par1, float par2, float par3) {
      float f = par2 - par1;

      while (f < -180.0F) {
         f += 360.0F;
      }

      while (f >= 180.0F) {
         f -= 360.0F;
      }

      return par1 + par3 * f;
   }

   public void transformHeldFull3DItemLayer() {
   }

   public void doRender(T entity, double x, double y, double z, float entityYaw, float partialTicks) {
      if (CheatBreaker.getInstance().getKeybindSettings().shouldRenderEntity(entity)) {
         if (!Reflector.RenderLivingEvent_Pre_Constructor.exists()
            || !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Pre_Constructor, entity, this, x, y, z)) {
            if (animateModelLiving) {
               entity.limbSwingAmount = 1.0F;
            }

            GlStateManager.pushMatrix();
            GlStateManager.disableCull();
            this.mainModel.swingProgress = this.getSwingProgress((T)entity, partialTicks);
            this.mainModel.isRiding = entity.isRiding();
            if (Reflector.ForgeEntity_shouldRiderSit.exists()) {
               this.mainModel.isRiding = entity.isRiding()
                  && entity.ridingEntity != null
                  && Reflector.callBoolean(entity.ridingEntity, Reflector.ForgeEntity_shouldRiderSit);
            }

            this.mainModel.isChild = entity.isChild();

            try {
               float interpolateRotation = this.interpolateRotation(entity.prevRenderYawOffset, entity.renderYawOffset, partialTicks);
               float interpolateRotation2 = this.interpolateRotation(entity.prevRotationYawHead, entity.rotationYawHead, partialTicks);
               float f = interpolateRotation2 - interpolateRotation;
               if (this.mainModel.isRiding && entity.ridingEntity instanceof EntityLivingBase) {
                  EntityLivingBase ridingEntity = (EntityLivingBase)entity.ridingEntity;
                  interpolateRotation = this.interpolateRotation(ridingEntity.prevRenderYawOffset, ridingEntity.renderYawOffset, partialTicks);
                  f = interpolateRotation2 - interpolateRotation;
                  float f2 = MathHelper.wrapAngleTo180_float(f);
                  if (f2 < -85.0F) {
                     f2 = -85.0F;
                  }

                  if (f2 >= 85.0F) {
                     f2 = 85.0F;
                  }

                  interpolateRotation = interpolateRotation2 - f2;
                  if (f2 * f2 > 2500.0F) {
                     interpolateRotation += f2 * 0.2F;
                  }

                  f = interpolateRotation2 - interpolateRotation;
               }

               float f2 = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;
               this.renderLivingAt((T)entity, x, y, z);
               float handleRotationFloat = this.handleRotationFloat((T)entity, partialTicks);
               this.rotateCorpse((T)entity, handleRotationFloat, interpolateRotation, partialTicks);
               GlStateManager.enableRescaleNormal();
               GlStateManager.scale(-1.0F, -1.0F, 1.0F);
               this.preRenderCallback((T)entity, partialTicks);
               float f3 = 0.0625F;
               GlStateManager.translate(0.0F, -1.5078125F, 0.0F);
               float f4 = entity.prevLimbSwingAmount + (entity.limbSwingAmount - entity.prevLimbSwingAmount) * partialTicks;
               float f5 = entity.limbSwing - entity.limbSwingAmount * (1.0F - partialTicks);
               if (entity.isChild()) {
                  f5 *= 3.0F;
               }

               if (f4 > 1.0F) {
                  f4 = 1.0F;
               }

               GlStateManager.enableAlpha();
               this.mainModel.setLivingAnimations(entity, f5, f4, partialTicks);
               this.mainModel.setRotationAngles(f5, f4, handleRotationFloat, f, f2, 0.0625F, entity);
               if (CustomEntityModels.isActive()) {
                  this.renderEntity = entity;
                  this.renderLimbSwing = f5;
                  this.renderLimbSwingAmount = f4;
                  this.renderAgeInTicks = handleRotationFloat;
                  this.renderHeadYaw = f;
                  this.renderHeadPitch = f2;
                  this.renderScaleFactor = f3;
                  this.renderPartialTicks = partialTicks;
               }

               if (this.renderOutlines) {
                  boolean setScoreTeamColor = this.setScoreTeamColor((T)entity);
                  this.renderModel((T)entity, f5, f4, handleRotationFloat, f, f2, 0.0625F);
                  if (setScoreTeamColor) {
                     this.unsetScoreTeamColor();
                  }
               } else {
                  boolean setDoRenderBrightness = this.setDoRenderBrightness((T)entity, partialTicks);
                  if (EmissiveTextures.isActive()) {
                     EmissiveTextures.beginRender();
                  }

                  if (this.renderModelPushMatrix) {
                     GlStateManager.pushMatrix();
                  }

                  this.renderModel((T)entity, f5, f4, handleRotationFloat, f, f2, 0.0625F);
                  if (this.renderModelPushMatrix) {
                     GlStateManager.popMatrix();
                  }

                  if (EmissiveTextures.isActive()) {
                     if (EmissiveTextures.hasEmissive()) {
                        this.renderModelPushMatrix = true;
                        EmissiveTextures.beginRenderEmissive();
                        GlStateManager.pushMatrix();
                        this.renderModel((T)entity, f5, f4, handleRotationFloat, f, f2, f3);
                        GlStateManager.popMatrix();
                        EmissiveTextures.endRenderEmissive();
                     }

                     EmissiveTextures.endRender();
                  }

                  if (setDoRenderBrightness) {
                     this.unsetBrightness();
                  }

                  GlStateManager.depthMask(true);
                  if (!(entity instanceof EntityPlayer) || !((EntityPlayer)entity).isSpectator()) {
                     this.renderLayers((T)entity, f5, f4, partialTicks, handleRotationFloat, f, f2, 0.0625F);
                  }
               }

               if (CustomEntityModels.isActive()) {
                  this.renderEntity = null;
               }

               GlStateManager.disableRescaleNormal();
            } catch (Exception e) {
               logger.error("Couldn't render entity", e);
            }

            GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
            GlStateManager.enableTexture2D();
            GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
            GlStateManager.enableCull();
            GlStateManager.popMatrix();
            if (!this.renderOutlines) {
               super.doRender((T)entity, x, y, z, entityYaw, partialTicks);
            }

            if (Reflector.RenderLivingEvent_Post_Constructor.exists()) {
               Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Post_Constructor, entity, this, x, y, z);
            }
         }
      }
   }

   public boolean setScoreTeamColor(T entityLivingBaseIn) {
      int n = 16777215;
      if (entityLivingBaseIn instanceof EntityPlayer) {
         ScorePlayerTeam team = (ScorePlayerTeam)entityLivingBaseIn.getTeam();
         if (team != null) {
            String formatFromString = FontRenderer.getFormatFromString(team.getColorPrefix());
            if (formatFromString.length() >= 2) {
               n = this.getFontRendererFromRenderManager().getColorCode(formatFromString.charAt(1));
            }
         }
      }

      float f = (n >> 16 & 0xFF) / 255.0F;
      float f2 = (n >> 8 & 0xFF) / 255.0F;
      float f3 = (n & 0xFF) / 255.0F;
      GlStateManager.disableLighting();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.color(f, f2, f3, 1.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      return true;
   }

   public void unsetScoreTeamColor() {
      GlStateManager.enableLighting();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.enableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.enableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
   }

   public void renderModel(T entitylivingbaseIn, float f, float f2, float f3, float f4, float f5, float scaleFactor) {
      boolean flag = !entitylivingbaseIn.isInvisible();
      boolean flag2 = !flag && !entitylivingbaseIn.isInvisibleToPlayer(Minecraft.getMinecraft().thePlayer);
      if (flag || flag2) {
         if (!this.bindEntityTexture((T)entitylivingbaseIn)) {
            return;
         }

         if (flag2) {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.15F);
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.alphaFunc(516, 0.003921569F);
         }

         this.mainModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scaleFactor);
         if (flag2) {
            GlStateManager.disableBlend();
            GlStateManager.alphaFunc(516, 0.1F);
            GlStateManager.popMatrix();
            GlStateManager.depthMask(true);
         }
      }
   }

   public boolean setDoRenderBrightness(T entityLivingBaseIn, float partialTicks) {
      return this.setBrightness((T)entityLivingBaseIn, partialTicks, true);
   }

   public boolean setBrightness(T entitylivingbaseIn, float partialTicks, boolean combineTextures) {
      float brightness = entitylivingbaseIn.getBrightness(partialTicks);
      int colorMultiplier = this.getColorMultiplier((T)entitylivingbaseIn, brightness, partialTicks);
      boolean flag = (colorMultiplier >> 24 & 0xFF) > 0;
      boolean flag2 = entitylivingbaseIn.hurtTime > 0 || entitylivingbaseIn.deathTime > 0;
      if (!flag && !flag2) {
         return false;
      }

      if (!flag && !combineTextures) {
         return false;
      }

      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.enableTexture2D();
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.enableTexture2D();
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, OpenGlHelper.GL_INTERPOLATE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_CONSTANT);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE2_RGB, OpenGlHelper.GL_CONSTANT);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND2_RGB, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      ((Buffer)this.brightnessBuffer).position(0);
      EntityLivingBaseFloatEvent2 entityLivingBaseFloatEvent2 = new EntityLivingBaseFloatEvent2(1.0F, 0.0F, 0.0F, 0.3F, entitylivingbaseIn, brightness);
      CheatBreaker.getInstance().getEventBus().handleEvent(entityLivingBaseFloatEvent2);
      if (flag2) {
         this.brightnessBuffer.put(entityLivingBaseFloatEvent2.getFloatValue1());
         this.brightnessBuffer.put(entityLivingBaseFloatEvent2.getFloatValue2());
         this.brightnessBuffer.put(entityLivingBaseFloatEvent2.getFloatValue3());
         this.brightnessBuffer.put(entityLivingBaseFloatEvent2.getFloatValue4());
         if (Config.isShaders()) {
            Shaders.setEntityColor(1.0F, 0.0F, 0.0F, 0.3F);
         }
      } else {
         float f = (colorMultiplier >> 24 & 0xFF) / 255.0F;
         float f2 = (colorMultiplier >> 16 & 0xFF) / 255.0F;
         float f3 = (colorMultiplier >> 8 & 0xFF) / 255.0F;
         float f4 = (colorMultiplier & 0xFF) / 255.0F;
         this.brightnessBuffer.put(f2);
         this.brightnessBuffer.put(f3);
         this.brightnessBuffer.put(f4);
         this.brightnessBuffer.put(1.0F - f);
         if (Config.isShaders()) {
            Shaders.setEntityColor(f2, f3, f4, 1.0F - f);
         }
      }

      ((Buffer)this.brightnessBuffer).flip();
      GL11.glTexEnv(8960, 8705, this.brightnessBuffer);
      GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
      GlStateManager.enableTexture2D();
      GlStateManager.bindTexture(textureBrightness.getGlTextureId());
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.lightmapTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 7681);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      return true;
   }

   public void unsetBrightness() {
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      GlStateManager.enableTexture2D();
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, OpenGlHelper.defaultTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PRIMARY_COLOR);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, OpenGlHelper.defaultTexUnit);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_ALPHA, OpenGlHelper.GL_PRIMARY_COLOR);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_ALPHA, 770);
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.setActiveTexture(OpenGlHelper.GL_TEXTURE2);
      GlStateManager.disableTexture2D();
      GlStateManager.bindTexture(0);
      GL11.glTexEnvi(8960, 8704, OpenGlHelper.GL_COMBINE);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_RGB, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND1_RGB, 768);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_RGB, 5890);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE1_RGB, OpenGlHelper.GL_PREVIOUS);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_COMBINE_ALPHA, 8448);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_OPERAND0_ALPHA, 770);
      GL11.glTexEnvi(8960, OpenGlHelper.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      if (Config.isShaders()) {
         Shaders.setEntityColor(0.0F, 0.0F, 0.0F, 0.0F);
      }
   }

   public void renderLivingAt(T entityLivingBaseIn, double x, double y, double z) {
      GlStateManager.translate((float)x, (float)y, (float)z);
   }

   public void rotateCorpse(T bat, float f, float f2, float partialTicks) {
      GlStateManager.rotate(180.0F - f2, 0.0F, 1.0F, 0.0F);
      if (bat.deathTime > 0) {
         float f3 = (bat.deathTime + partialTicks - 1.0F) / 20.0F * 1.6F;
         f3 = MathHelper.sqrt_float(f3);
         if (f3 > 1.0F) {
            f3 = 1.0F;
         }

         GlStateManager.rotate(f3 * this.getDeathMaxRotation((T)bat), 0.0F, 0.0F, 1.0F);
      } else {
         String textWithoutFormattingCodes = EnumChatFormatting.getTextWithoutFormattingCodes(bat.getName());
         if (textWithoutFormattingCodes != null
            && (textWithoutFormattingCodes.equals("Dinnerbone") || textWithoutFormattingCodes.equals("Grumm"))
            && (!(bat instanceof EntityPlayer) || ((EntityPlayer)bat).isWearing(EnumPlayerModelParts.CAPE))) {
            GlStateManager.translate(0.0F, bat.height + 0.1F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
         }
      }
   }

   public float getSwingProgress(T livingBase, float partialTickTime) {
      EntityLivingBaseFloatEvent3 entityLivingBaseFloatEvent3 = new EntityLivingBaseFloatEvent3(livingBase.getSwingProgress(partialTickTime), livingBase, partialTickTime);
      CheatBreaker.getInstance().getEventBus().handleEvent(entityLivingBaseFloatEvent3);
      return entityLivingBaseFloatEvent3.getFloatValue1();
   }

   public float handleRotationFloat(T livingBase, float partialTicks) {
      return livingBase.ticksExisted + partialTicks;
   }

   public void renderLayers(T entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float f6) {
      for (LayerRenderer layerRenderer : this.layerRenderers) {
         boolean setBrightness = this.setBrightness((T)entitylivingbaseIn, partialTicks, layerRenderer.shouldCombineTextures());
         if (EmissiveTextures.isActive()) {
            EmissiveTextures.beginRender();
         }

         if (this.renderLayersPushMatrix) {
            GlStateManager.pushMatrix();
         }

         layerRenderer.doRenderLayer(entitylivingbaseIn, f, f2, partialTicks, f3, f4, f5, f6);
         if (this.renderLayersPushMatrix) {
            GlStateManager.popMatrix();
         }

         if (EmissiveTextures.isActive()) {
            if (EmissiveTextures.hasEmissive()) {
               this.renderLayersPushMatrix = true;
               EmissiveTextures.beginRenderEmissive();
               GlStateManager.pushMatrix();
               layerRenderer.doRenderLayer(entitylivingbaseIn, f, f2, partialTicks, f3, f4, f5, f6);
               GlStateManager.popMatrix();
               EmissiveTextures.endRenderEmissive();
            }

            EmissiveTextures.endRender();
         }

         if (setBrightness) {
            this.unsetBrightness();
         }
      }
   }

   public float getDeathMaxRotation(T entityLivingBaseIn) {
      return 90.0F;
   }

   public int getColorMultiplier(T entitylivingbaseIn, float lightBrightness, float partialTickTime) {
      return 0;
   }

   public void preRenderCallback(T entitylivingbaseIn, float partialTickTime) {
   }

   public void renderName(T entity, double x, double y, double z) {
      EntityLivingBaseDoubleEvent1 entityLivingBaseDoubleEvent1 = new EntityLivingBaseDoubleEvent1(entity, x, y, z);
      CheatBreaker.getInstance().getEventBus().handleEvent(entityLivingBaseDoubleEvent1);
      if (!entityLivingBaseDoubleEvent1.isFlag()) {
         NametagMod nametagMod = CheatBreaker.getInstance().getModManager().nametagMod;
         if (nametagMod.isEnabled() && nametagMod.shownOwnNametag.getBooleanValue() || !entity.equals(Minecraft.getMinecraft().thePlayer)) {
            if (!Reflector.RenderLivingEvent_Specials_Pre_Constructor.exists()
               || !Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Specials_Pre_Constructor, entity, this, x, y, z)) {
               if (this.canRenderName((T)entity)) {
                  double distanceSqToEntity = entity.getDistanceSqToEntity(this.renderManager.livingPlayer);
                  float f = entity.isSneaking() ? NAME_TAG_RANGE_SNEAK : NAME_TAG_RANGE;
                  if (distanceSqToEntity < f * f && !nametagMod.hidden) {
                     String formattedText = entity.getDisplayName().getFormattedText();
                     GlStateManager.alphaFunc(516, 0.1F);
                     if (entity.isSneaking()) {
                        FontRenderer fontRendererFromRenderManager = this.getFontRendererFromRenderManager();
                        GlStateManager.pushMatrix();
                        GlStateManager.translate((float)x, (float)y + entity.height + 0.5F - (entity.isChild() ? entity.height / 2.0F : 0.0F), (float)z);
                        GL11.glNormal3f(0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(-this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
                        GlStateManager.rotate(this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
                        GlStateManager.scale(-0.02666667F, -0.02666667F, 0.02666667F);
                        GlStateManager.translate(0.0F, 9.374999F, 0.0F);
                        GlStateManager.disableLighting();
                        GlStateManager.depthMask(false);
                        GlStateManager.enableBlend();
                        GlStateManager.disableTexture2D();
                        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
                        int n = fontRendererFromRenderManager.getStringWidth(formattedText) / 2;
                        Tessellator instance = Tessellator.getInstance();
                        WorldRenderer worldRenderer = instance.getWorldRenderer();
                        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                        worldRenderer.pos(-n - 1, -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                        worldRenderer.pos(-n - 1, 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                        worldRenderer.pos(n + 1, 8.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                        worldRenderer.pos(n + 1, -1.0, 0.0).color(0.0F, 0.0F, 0.0F, 0.25F).endVertex();
                        instance.draw();
                        GlStateManager.enableTexture2D();
                        GlStateManager.depthMask(true);
                        fontRendererFromRenderManager.drawString(formattedText, -fontRendererFromRenderManager.getStringWidth(formattedText) / 2, 0.0F, 553648127, nametagMod.isEnabled() && nametagMod.textShadow.getBooleanValue());
                        GlStateManager.enableLighting();
                        GlStateManager.disableBlend();
                        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                        GlStateManager.popMatrix();
                     } else {
                        this.renderOffsetLivingLabel((T)entity, x, y - (entity.isChild() ? entity.height / 2.0F : 0.0), z, formattedText, 0.02666667F, distanceSqToEntity);
                     }
                  }
               }

               if (Reflector.RenderLivingEvent_Specials_Post_Constructor.exists()) {
                  Reflector.postForgeBusEvent(Reflector.RenderLivingEvent_Specials_Post_Constructor, entity, this, x, y, z);
               }
            }
         }
      }
   }

   public boolean canRenderName(T entity) {
      EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
      if (entity instanceof EntityPlayer) {
         Team team = entity.getTeam();
         Team team2 = thePlayer.getTeam();
         if (team != null) {
            EnumVisible nameTagVisibility = team.getNameTagVisibility();
            switch (nameTagVisibility) {
               case NEVER:
                  return false;
               case HIDE_FOR_OTHER_TEAMS:
                  return team2 == null || team.isSameTeam(team2);
               case HIDE_FOR_OWN_TEAM:
                  return team2 == null || !team.isSameTeam(team2);
               default:
                  return true;
            }
         }
      }

      return Minecraft.isGuiEnabled() && entity != this.renderManager.livingPlayer && !entity.isInvisibleToPlayer(thePlayer) && entity.riddenByEntity == null;
   }

   public void setRenderOutlines(boolean renderOutlinesIn) {
      this.renderOutlines = renderOutlinesIn;
   }

   public List<LayerRenderer<T>> getLayerRenderers() {
      return this.layerRenderers;
   }

   static {
      int[] textureData = textureBrightness.getTextureData();

      for (int i = 0; i < 256; i++) {
         textureData[i] = -1;
      }

      textureBrightness.updateDynamicTexture();
   }
}
