package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.CustomEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent;
import com.cheatbreaker.client.event.EntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent;
import com.cheatbreaker.client.event.ItemStackStringIntEvent;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import java.util.HashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Items;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class EnchantmentGlintMod extends AbstractMod {
   public HashMap map = new HashMap();
   public final double doubleValue = 1.25;
   public final double doubleValue2 = -0.1;
   public final double doubleValue3 = -0.1;
   public static ResourceLocation enchantedItemGlintPng = new ResourceLocation("textures/misc/enchanted_item_glint.png");
   public EnchantmentGlintMod.GlintSettings inventory;
   public Setting potionOptions;
   public Setting showPotionColor;
   public Setting onlyRenderPotionGlint;
   public Setting excludePotionGlint;
   public Setting shinyPots;
   public Setting renderGlintBehindPotion;
   public Setting shinyPotsBoundary;
   public EnchantmentGlintMod.GlintSettings dropped;
   public EnchantmentGlintMod.GlintSettings thrown;
   public EnchantmentGlintMod.GlintSettings held;
   public EnchantmentGlintMod.GlintSettings armor;

   public EnchantmentGlintMod() {
      super("Enchantment Glint");
      this.setApplyDefaultColorOnEnable(false);
      this.inventory = new EnchantmentGlintMod.GlintSettings("Inventory");
      this.potionOptions = new SettingGroup(this, "Potion Options").changeValue(true);
      this.showPotionColor = new Setting(this, "Show Potion Color").changeValue(false).withHierarchyParents(this.potionOptions);
      this.onlyRenderPotionGlint = new Setting(this, "Only Render Potion Glint").changeValue(false).withHierarchyParents(this.potionOptions);
      this.excludePotionGlint = new Setting(this, "Exclude Potion Glint").changeValue(false).withHierarchyParents(this.potionOptions);
      this.shinyPots = new Setting(this, "Shiny Pots")
         .setDescription("Show a glint box around potions.")
         .changeValue(false)
         .withHierarchyParents(this.potionOptions, this.inventory.glint);
      this.renderGlintBehindPotion = new Setting(this, "Render Glint Behind Potion").changeValue(false).withHierarchyParents(this.shinyPots);
      this.shinyPotsBoundary = new Setting(this, "Shiny Pots Boundary")
         .changeValue(true)
         .setToggleTexts("To Border", "Entire Slot")
         .withHierarchyParents(this.shinyPots);
      this.dropped = new EnchantmentGlintMod.GlintSettings("Dropped");
      this.thrown = new EnchantmentGlintMod.GlintSettings("Thrown");
      this.held = new EnchantmentGlintMod.GlintSettings("Held");
      this.armor = new EnchantmentGlintMod.GlintSettings("Armor");
      this.setIcon(new ResourceLocation("client/icons/mods/ench_sword.png"), 32, 32);
      this.setDescription("Customize the enchantment glint and/or enable shiny pots overlay.");
      this.setCategories(ModCategory.MISC);
      this.addEventHandler(
         CustomEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.class,
         this::onCustomEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent
      );
      this.addEventHandler(ItemStackStringIntEvent.class, this::onItemStackStringIntEvent);
      this.addEventHandler(
         EntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.class,
         this::onEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent
      );
      this.setEnabledByDefault(true);
   }

   public int computeInt(ItemStack itemStack) {
      if (!(Boolean)this.showPotionColor.getValue()) {
         return this.inventory.glintColor.getColorValue();
      }

      int metadata = itemStack.getMetadata();
      return (int)this.map.computeIfAbsent(metadata, obj -> Items.potionitem.getColorFromItemStack(itemStack, 0) | 0xFF000000);
   }

   public void onCustomEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent(
      CustomEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent
   ) {
      if (!customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.renderItemGui()
         || (!(customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getItemStack().getItem() instanceof ItemPotion) || !this.excludePotionGlint.getBooleanValue())
            && (customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getItemStack().getItem() instanceof ItemPotion || !this.onlyRenderPotionGlint.getBooleanValue())) {
         switch (customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getText()) {
            case "inventory":
               if (customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getItemStack().getItem() instanceof ItemPotion && this.shinyPots.getBooleanValue() && this.renderGlintBehindPotion.getBooleanValue()) {
                  customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.setFlag(true);
                  return;
               }

               if (!this.inventory.glint.getBooleanValue()) {
                  customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.setFlag(true);
               }
               break;
            case "dropped":
               if (!this.dropped.glint.getBooleanValue()) {
                  customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.setFlag(true);
               }
               break;
            case "held":
               if (!this.held.glint.getBooleanValue()) {
                  customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.setFlag(true);
               }
               break;
            default:
               if (!this.thrown.glint.getBooleanValue()) {
                  customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.setFlag(true);
               }
         }
      } else {
         customEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.setFlag(true);
      }
   }

   public void handleIBakedModel(IBakedModel bakedModel, int n, RenderItem renderItem) {
      GlStateManager.depthMask(false);
      GlStateManager.disableLighting();
      GlStateManager.blendFunc(768, 1);
      if (CheatBreaker.getInstance().getModManager().enchantmentGlintMod.shinyPotsBoundary.getBooleanValue()) {
         GlStateManager.scale(1.25, 1.25, 1.25);
         GlStateManager.translate(-0.1, -0.1, 0.0);
      }

      this.mc.getTextureManager().bindTexture(enchantedItemGlintPng);
      GlStateManager.matrixMode(5890);
      GlStateManager.pushMatrix();
      GlStateManager.scale(8.0F, 8.0F, 8.0F);
      float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
      GlStateManager.translate(f, 0.0F, 0.0F);
      GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
      renderItem.renderModel(bakedModel, n);
      GlStateManager.popMatrix();
      GlStateManager.pushMatrix();
      GlStateManager.scale(8.0F, 8.0F, 8.0F);
      float f2 = (float)(Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
      GlStateManager.translate(-f2, 0.0F, 0.0F);
      GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
      renderItem.renderModel(bakedModel, n);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.blendFunc(770, 771);
      GlStateManager.enableLighting();
      GlStateManager.depthMask(true);
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
   }

   public void onEntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent(
      EntityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent entityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent
   ) {
      if (this.inventory.glint.getBooleanValue()
         && this.shinyPots.getBooleanValue()
         && entityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.renderItemGui()
         && entityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getItemStack().getItem() != null
         && entityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getItemStack().getItem() instanceof ItemPotion) {
         GlStateManager.pushMatrix();
         this.handleIBakedModel(entityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getBakedModel(), this.computeInt(entityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getItemStack()), entityIBakedModelItemCameraTransformsTransformTypeItemStackRenderItemStringBooleanEvent.getRenderItem());
         GlStateManager.popMatrix();
      }
   }

   public void onItemStackStringIntEvent(ItemStackStringIntEvent itemStackStringIntEvent) {
      if (itemStackStringIntEvent.getItemStack().getItem() instanceof ItemPotion) {
         itemStackStringIntEvent.setIntValue(this.computeInt(itemStackStringIntEvent.getItemStack()));
      } else {
         switch (itemStackStringIntEvent.getText()) {
            case "inventory":
               itemStackStringIntEvent.setIntValue(this.inventory.glintColor.getColorValue());
               break;
            case "dropped":
               itemStackStringIntEvent.setIntValue(this.dropped.glintColor.getColorValue());
               break;
            case "held":
               itemStackStringIntEvent.setIntValue(this.held.glintColor.getColorValue());
               break;
            default:
               itemStackStringIntEvent.setIntValue(this.thrown.glintColor.getColorValue());
         }
      }
   }

   public class GlintSettings {
      public Setting glintSettings;
      public Setting glint;
      public Setting glintColor;

      public GlintSettings(String str) {
         this.glintSettings = new SettingGroup(EnchantmentGlintMod.this, str + " Glint Settings").changeValue(true);
         this.glint = new Setting(EnchantmentGlintMod.this, str + " Glint").changeValue(true).withHierarchyParents(this.glintSettings);
         this.glintColor = new Setting(EnchantmentGlintMod.this, str + " Glint Color")
            .changeValue(-8372020)
            .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
            .withHierarchyParents(this.glint);
      }
   }
}
