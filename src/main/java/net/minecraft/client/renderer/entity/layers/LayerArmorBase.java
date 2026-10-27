package net.minecraft.client.renderer.entity.layers;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.CustomEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent;
import com.cheatbreaker.client.mod.HudElementType;
import com.cheatbreaker.client.mod.impl.EnchantmentGlintMod;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.CustomItems;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;

public abstract class LayerArmorBase<T extends ModelBase> implements LayerRenderer<EntityLivingBase> {
   public static ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   public T modelLeggings;
   public T modelArmor;
   public float alpha = 1.0F;
   public float colorR = 1.0F;
   public float colorG = 1.0F;
   public float colorB = 1.0F;
   public RendererLivingEntity<?> renderer;
   public boolean skipRenderGlint;
   public static Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

   public LayerArmorBase(RendererLivingEntity<?> rendererIn) {
      this.renderer = rendererIn;
      this.initArmor();
   }

   @Override
   public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale) {
      this.renderLayer(entitylivingbaseIn, f, f2, partialTicks, f3, f4, f5, scale, 4);
      this.renderLayer(entitylivingbaseIn, f, f2, partialTicks, f3, f4, f5, scale, 3);
      this.renderLayer(entitylivingbaseIn, f, f2, partialTicks, f3, f4, f5, scale, 2);
      this.renderLayer(entitylivingbaseIn, f, f2, partialTicks, f3, f4, f5, scale, 1);
   }

   @Override
   public boolean shouldCombineTextures() {
      return CheatBreaker.getInstance().getModManager().hitColorMod.affectArmor.getBooleanValue()
         && CheatBreaker.getInstance().getModManager().hitColorMod.isEnabled();
   }

   public void renderLayer(
      EntityLivingBase entitylivingbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale, int armorSlot
   ) {
      ItemStack currentArmor = this.getCurrentArmor(entitylivingbaseIn, armorSlot);
      if (currentArmor != null && currentArmor.getItem() instanceof ItemArmor) {
         ItemArmor item = (ItemArmor)currentArmor.getItem();
         ModelBase armorModel = this.getArmorModel(armorSlot);
         armorModel.setModelAttributes(this.renderer.getMainModel());
         armorModel.setLivingAnimations(entitylivingbaseIn, f, f2, partialTicks);
         if (Reflector.ForgeHooksClient.exists()) {
            armorModel = this.getArmorModelHook(entitylivingbaseIn, currentArmor, armorSlot, (T)armorModel);
         }

         this.setModelPartVisible((T)armorModel, armorSlot);
         boolean isSlotForLeggings = this.isSlotForLeggings(armorSlot);
         if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(currentArmor, isSlotForLeggings ? 2 : 1, (String)null)) {
            if (Reflector.ForgeHooksClient_getArmorTexture.exists()) {
               this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, currentArmor, isSlotForLeggings ? 2 : 1, (String)null));
            } else {
               this.renderer.bindTexture(this.getArmorResource(item, isSlotForLeggings));
            }
         }

         if (Reflector.ForgeHooksClient_getArmorTexture.exists()) {
            if (ReflectorForge.armorHasOverlay(item, currentArmor)) {
               int color = item.getColor(currentArmor);
               float f6 = (color >> 16 & 0xFF) / 255.0F;
               float f7 = (color >> 8 & 0xFF) / 255.0F;
               float f8 = (color & 0xFF) / 255.0F;
               GlStateManager.color(this.colorR * f6, this.colorG * f7, this.colorB * f8, this.alpha);
               armorModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
               if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(currentArmor, isSlotForLeggings ? 2 : 1, "overlay")) {
                  this.renderer.bindTexture(this.getArmorResource(entitylivingbaseIn, currentArmor, isSlotForLeggings ? 2 : 1, "overlay"));
               }
            }

            GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
            armorModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
            if (!this.skipRenderGlint
               && currentArmor.hasEffect()
               && (
                  !Config.isCustomItems()
                     || !CustomItems.renderCustomArmorEffect(entitylivingbaseIn, currentArmor, armorModel, f, f2, partialTicks, f3, f4, f5, scale)
               )) {
               this.renderGlint(entitylivingbaseIn, (T)armorModel, f, f2, partialTicks, f3, f4, f5, scale);
            }

            return;
         }

         switch (item.getArmorMaterial()) {
            case LEATHER:
               int color = item.getColor(currentArmor);
               float f6 = (color >> 16 & 0xFF) / 255.0F;
               float f7 = (color >> 8 & 0xFF) / 255.0F;
               float f8 = (color & 0xFF) / 255.0F;
               GlStateManager.color(this.colorR * f6, this.colorG * f7, this.colorB * f8, this.alpha);
               armorModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
               if (!Config.isCustomItems() || !CustomItems.bindCustomArmorTexture(currentArmor, isSlotForLeggings ? 2 : 1, "overlay")) {
                  this.renderer.bindTexture(this.getArmorResource(item, isSlotForLeggings, "overlay"));
               }
            case CHAIN:
            case IRON:
            case GOLD:
            case DIAMOND:
               GlStateManager.color(this.colorR, this.colorG, this.colorB, this.alpha);
               armorModel.render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
         }

         if (!this.skipRenderGlint
            && currentArmor.isItemEnchanted()
            && (
               !Config.isCustomItems()
                  || !CustomItems.renderCustomArmorEffect(entitylivingbaseIn, currentArmor, armorModel, f, f2, partialTicks, f3, f4, f5, scale)
            )) {
            this.renderGlint(entitylivingbaseIn, (T)armorModel, f, f2, partialTicks, f3, f4, f5, scale);
         }
      }
   }

   public ItemStack getCurrentArmor(EntityLivingBase entitylivingbaseIn, int armorSlot) {
      return entitylivingbaseIn.getCurrentArmor(armorSlot - 1);
   }

   public T getArmorModel(int armorSlot) {
      return this.isSlotForLeggings(armorSlot) ? this.modelLeggings : this.modelArmor;
   }

   public boolean isSlotForLeggings(int armorSlot) {
      return armorSlot == 2;
   }

   public void renderGlint(
      EntityLivingBase entitylivingbaseIn, T modelbaseIn, float f, float f2, float partialTicks, float f3, float f4, float f5, float scale
   ) {
      CustomEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent = new CustomEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent(
         null, HudElementType.ARMOR, null, null, entitylivingbaseIn, "armor", null, false
      );
      CheatBreaker.getInstance().getEventBus().handleEvent(customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent);
      if (!customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.isFlag()) {
         EnchantmentGlintMod enchantmentGlintMod = CheatBreaker.getInstance().getModManager().enchantmentGlintMod;
         if ((!Config.isShaders() || !Shaders.isShadowPass) && enchantmentGlintMod.isEnabled() && enchantmentGlintMod.armor.glint.getBooleanValue()) {
            int colorValue = enchantmentGlintMod.armor.glintColor.getColorValue();
            float f6 = (colorValue >> 24 & 0xFF) / 255.0F;
            float f7 = (colorValue >> 16 & 0xFF) / 255.0F;
            float f8 = (colorValue >> 8 & 0xFF) / 255.0F;
            float f9 = (colorValue & 0xFF) / 255.0F;
            float f10 = entitylivingbaseIn.ticksExisted + partialTicks;
            this.renderer.bindTexture(ENCHANTED_ITEM_GLINT_RES);
            if (Config.isShaders()) {
               ShadersRender.renderEnchantedGlintBegin();
            }

            GlStateManager.enableBlend();
            GlStateManager.depthFunc(514);
            GlStateManager.depthMask(false);
            GlStateManager.color(f7, f8, f9, f6);

            for (int i = 0; i < 2; i++) {
               GlStateManager.disableLighting();
               GlStateManager.blendFunc(768, 1);
               GlStateManager.color(f7, f8, f9, f6);
               GlStateManager.matrixMode(5890);
               GlStateManager.loadIdentity();
               float f11 = 0.33333334F;
               GlStateManager.scale(f11, f11, f11);
               GlStateManager.rotate(30.0F - i * 60.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.translate(0.0F, f10 * (0.001F + i * 0.003F) * 20.0F, 0.0F);
               GlStateManager.matrixMode(5888);
               modelbaseIn.render(entitylivingbaseIn, f, f2, f3, f4, f5, scale);
            }

            GlStateManager.matrixMode(5890);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            GlStateManager.depthFunc(515);
            GlStateManager.disableBlend();
            if (Config.isShaders()) {
               ShadersRender.renderEnchantedGlintEnd();
            }
         }
      }
   }

   public ResourceLocation getArmorResource(ItemArmor itemArmor, boolean flag) {
      return this.getArmorResource(itemArmor, flag, (String)null);
   }

   public ResourceLocation getArmorResource(ItemArmor itemArmor, boolean flag, String str) {
      String format = String.format(
         "textures/models/armor/%s_layer_%d%s.png", itemArmor.getArmorMaterial().getName(), flag ? 2 : 1, str == null ? "" : String.format("_%s", str)
      );
      ResourceLocation resourceLocation = ARMOR_TEXTURE_RES_MAP.get(format);
      if (resourceLocation == null) {
         resourceLocation = new ResourceLocation(format);
         ARMOR_TEXTURE_RES_MAP.put(format, resourceLocation);
      }

      return resourceLocation;
   }

   public abstract void initArmor();

   public abstract void setModelPartVisible(T value, int n);

   public T getArmorModelHook(EntityLivingBase entityLivingBase, ItemStack itemStack, int n, T value) {
      return (T)value;
   }

   public ResourceLocation getArmorResource(Entity entity, ItemStack itemStack, int n, String str) {
      ItemArmor item = (ItemArmor)itemStack.getItem();
      String name = item.getArmorMaterial().getName();
      String str2 = "minecraft";
      int indexOf = name.indexOf(58);
      if (indexOf != -1) {
         str2 = name.substring(0, indexOf);
         name = name.substring(indexOf + 1);
      }

      String format = String.format(
         "%s:textures/models/armor/%s_layer_%d%s.png", str2, name, this.isSlotForLeggings(n) ? 2 : 1, str == null ? "" : String.format("_%s", str)
      );
      format = Reflector.callString(Reflector.ForgeHooksClient_getArmorTexture, entity, itemStack, format, n, str);
      ResourceLocation resourceLocation = ARMOR_TEXTURE_RES_MAP.get(format);
      if (resourceLocation == null) {
         resourceLocation = new ResourceLocation(format);
         ARMOR_TEXTURE_RES_MAP.put(format, resourceLocation);
      }

      return resourceLocation;
   }
}
