package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.ScaledResolutionEvent7;
import com.cheatbreaker.client.mod.AbstractHudMod;
import com.cheatbreaker.client.mod.HudAlignment;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.cheatbreaker.client.mod.setting.SettingsLevel;
import com.cheatbreaker.client.ui.AnchorPoint;
import com.cheatbreaker.client.ui.AnchorPointUtils;
import com.cheatbreaker.client.ui.screen.ModSettingsScreen;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ArmorStatusMod extends AbstractHudMod {
   public static Setting equippedItem;
   public static Setting damageOverlay;
   public static List<ArmorStatusDamageComparable> armorStatusDamageComparableList = new ArrayList<>();
   public Setting generalOptions;
   public Setting damageOptions;
   public static Setting damageDisplayType;
   public Setting damageDisplay;
   public Setting colorOptions;
   public static Setting nameColor;
   public static List<ArmorStatusEntry> armorStatusEntryList = new ArrayList<>();
   public Setting maxDamageColor;
   public static Setting textShadow;
   public Setting highDamageColor;
   public Setting mediumHighDamageColor;
   public static Setting itemName;
   public static Setting listMode;
   public static Setting itemCount;
   public Setting mediumDamageColor;
   public Setting mediumLowDamageColor;
   public static Setting showDamageAmount;
   public static Setting showItemDamage;
   public static Setting damageThresholdType;
   public static Setting durabilityColor;
   public static Setting showArmorDamage;
   public Setting lowDamageColor;
   public static Setting showMaxDamage;
   public static ScaledResolution scaledResolution;
   public static RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
   public static Setting showWhileTyping;
   public static Setting staticDamageColor;
   public Setting lowestDamageColor;

   @Override
   public void renderPreview(int n, int n2, int n3, int n4) {
      String str = "329/329";
      Minecraft.getMinecraft()
         .fontRendererObj
         .drawStringWithShadow(
            str, (int)(n + 1 + n3 / 2 - Minecraft.getMinecraft().fontRendererObj.getStringWidth(str) / 2.0F), n2 + n4 / 2 - 18, -1
         );
   }

   public void onScaledResolutionEvent3(ScaledResolutionEvent3 scaledResolutionEvent3) {
      if (this.isRenderHud() && (!this.hidden || this.mc.currentScreen instanceof ModsScreen)) {
         if (!(this.mc.currentScreen instanceof ModsScreen)
            && !(this.mc.currentScreen instanceof ModSettingsScreen)
            && (!(this.mc.currentScreen instanceof GuiChat) || (Boolean)showWhileTyping.getValue())) {
            this.handleMinecraft(this.mc);
            if (!armorStatusEntryList.isEmpty()) {
               GlStateManager.pushMatrix();
               GlStateManager.enableBlend();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               scaledResolution = scaledResolutionEvent3.getScaledResolution();
               this.applyHudTransformForSize(scaledResolution);
               this.handleList(armorStatusEntryList);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.disableBlend();
               GlStateManager.popMatrix();
            }
         }
      }
   }

   public void onScaledResolutionEvent7(ScaledResolutionEvent7 scaledResolutionEvent7) {
      if (this.isRenderHud()) {
         ArrayList list = new ArrayList();

         for (int i = 3; i >= 0; i--) {
            ItemStack itemStack = this.mc.thePlayer.inventory.armorInventory[i];
            if (itemStack != null) {
               list.add(new ArmorStatusEntry(itemStack, 16, 16, 2, i > -1));
            }
         }

         if (list.isEmpty()) {
            list.add(new ArmorStatusEntry(new ItemStack(Item.getItemById(310)), 16, 16, 2, true));
            list.add(new ArmorStatusEntry(new ItemStack(Item.getItemById(311)), 16, 16, 2, true));
            list.add(new ArmorStatusEntry(new ItemStack(Item.getItemById(312)), 16, 16, 2, true));
            list.add(new ArmorStatusEntry(new ItemStack(Item.getItemById(313)), 16, 16, 2, true));
         }

         if ((Boolean)equippedItem.getValue() && this.mc.thePlayer.getCurrentEquippedItem() != null) {
            list.add(new ArmorStatusEntry(this.mc.thePlayer.getCurrentEquippedItem(), 16, 16, 2, false));
         } else if ((Boolean)equippedItem.getValue()) {
            list.add(new ArmorStatusEntry(new ItemStack(Item.getItemById(276)), 16, 16, 2, false));
         }

         GlStateManager.pushMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         scaledResolution = scaledResolutionEvent7.getScaledResolution();
         this.applyHudTransformForSize(scaledResolution);
         this.handleList(list);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }
   }

   public void handleMinecraft(Minecraft mc) {
      armorStatusEntryList.clear();

      for (int i = 3; i >= -1; i--) {
         ItemStack itemStack = null;
         if (i == -1 && (Boolean)equippedItem.getValue()) {
            itemStack = mc.thePlayer.getCurrentEquippedItem();
         } else if (i != -1) {
            itemStack = mc.thePlayer.inventory.armorInventory[i];
         }

         if (itemStack != null) {
            armorStatusEntryList.add(new ArmorStatusEntry(itemStack, 16, 16, 2, i > -1));
         }
      }
   }

   public void handleList(List<ArmorStatusEntry> list) {
      if (list.size() > 0) {
         int n = (boolean)itemName.getValue() ? 18 : 16;
         if (((String)listMode.getValue()).equalsIgnoreCase("vertical")) {
            int n2 = 0;
            int n3 = 0;
            boolean flag = AnchorPointUtils.getHorizontalAlignment(this.getAnchorPoint()) == HudAlignment.RIGHT;

            for (ArmorStatusEntry armorStatusEntry : list) {
               armorStatusEntry.render(flag ? this.hudWidth : 0.0F, n2);
               n2 += n;
               if (armorStatusEntry.getWidth() > n3) {
                  n3 = armorStatusEntry.getWidth();
               }
            }

            this.hudWidth = n3;
            this.hudHeight = n2;
         } else if (((String)listMode.getValue()).equalsIgnoreCase("horizontal")) {
            int n2 = 0;
            int n3 = 0;
            boolean flag = AnchorPointUtils.getHorizontalAlignment(this.getAnchorPoint()) == HudAlignment.RIGHT;

            for (ArmorStatusEntry armorStatusEntry : list) {
               if (flag) {
                  n2 += armorStatusEntry.getWidth();
               }

               armorStatusEntry.render(n2, 0.0F);
               if (!flag) {
                  n2 += armorStatusEntry.getWidth();
               }

               if (armorStatusEntry.getHeight() > n3) {
                  n3 += armorStatusEntry.getHeight();
               }
            }

            this.hudWidth = n2;
            this.hudHeight = n3;
         }
      }
   }

   public ArmorStatusMod() {
      super("Armor Status");
      this.setAnchorPointAndDefault(AnchorPoint.RIGHT_BOTTOM);
      this.setEnabledByDefault(false);
      this.setApplyDefaultColorOnEnable(false);
      this.generalOptions = new SettingGroup(this, "General Options").changeValue(true);
      textShadow = new Setting(this, "Text Shadow").changeValue(true).withParents(this.generalOptions);
      listMode = new Setting(this, "List Mode").withParents(this.generalOptions).changeValue("vertical").setStringValues("vertical", "horizontal");
      itemName = new Setting(this, "Item Name").withParents(this.generalOptions).changeValue(false);
      equippedItem = new Setting(this, "Equipped Item").withParents(this.generalOptions).changeValue(true);
      itemCount = new Setting(this, "Item Count").changeValue(true).withHierarchyParents(equippedItem);
      showWhileTyping = new Setting(this, "Show While Typing")
         .withParents(this.generalOptions)
         .changeValue(false)
         .setCustomizationLevel(SettingsLevel.ADVANCED);
      this.damageOptions = new SettingGroup(this, "Damage Options").changeValue(false);
      damageOverlay = new Setting(this, "Damage Overlay").withParents(this.damageOptions).changeValue(true).setCustomizationLevel(SettingsLevel.MEDIUM);
      showDamageAmount = new Setting(this, "Show Damage Amount")
         .withParents(this.damageOptions)
         .changeValue("If Breakable")
         .setStringValues("ON", "If Breakable", "If Damaged");
      showItemDamage = new Setting(this, "Show Item Damage").changeValue(true).withParents(this.damageOptions, equippedItem);
      showArmorDamage = new Setting(this, "Show Armor Damage").withHierarchyParents(this.damageOptions).changeValue(true);
      showMaxDamage = new Setting(this, "Show Max Damage")
         .withHierarchyParents(this.damageOptions)
         .changeValue(false)
         .setCustomizationLevel(SettingsLevel.MEDIUM)
         .withOptionalHierarchyParents(showItemDamage, showArmorDamage);
      this.damageDisplay = new SettingGroup(this, "Damage Display")
         .changeValue(false)
         .setCondition(() -> equippedItem.getBooleanValue() && showItemDamage.getBooleanValue() || showArmorDamage.getBooleanValue());
      damageDisplayType = new Setting(this, "Damage Display Type")
         .withHierarchyParents(this.damageDisplay)
         .changeValue("value")
         .setStringValues("value", "percent", "none")
         .setCondition(() -> equippedItem.getBooleanValue() && showItemDamage.getBooleanValue() || showArmorDamage.getBooleanValue());
      damageThresholdType = new Setting(this, "Damage Threshold Type")
         .withHierarchyParents(this.damageDisplay)
         .changeValue("percent")
         .setStringValues("percent", "value")
         .setCondition(() -> equippedItem.getBooleanValue() && showItemDamage.getBooleanValue() || showArmorDamage.getBooleanValue());
      this.colorOptions = new SettingGroup(this, "Color Options").changeValue(true);
      staticDamageColor = new Setting(this, "Static Damage Color").changeValue(true).withParents(this.colorOptions);
      nameColor = new Setting(this, "Name Color").changeValue(-1).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE).withParents(this.colorOptions);
      durabilityColor = new Setting(this, "Durability Color")
         .changeValue(-1)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(staticDamageColor);
      this.maxDamageColor = new Setting(this, "Max Damage Color")
         .changeValue(-1)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withInvertedHierarchyParents(staticDamageColor);
      this.highDamageColor = new Setting(this, "High Damage Color")
         .changeValue(-11141291)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withInvertedHierarchyParents(staticDamageColor);
      this.mediumHighDamageColor = new Setting(this, "Medium High Damage Color")
         .changeValue(-5592406)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withInvertedHierarchyParents(staticDamageColor);
      this.mediumDamageColor = new Setting(this, "Medium Damage Color")
         .changeValue(-171)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withInvertedHierarchyParents(staticDamageColor);
      this.mediumLowDamageColor = new Setting(this, "Medium Low Damage Color")
         .changeValue(-22016)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withInvertedHierarchyParents(staticDamageColor);
      this.lowDamageColor = new Setting(this, "Low Damage Color")
         .changeValue(-5636096)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withInvertedHierarchyParents(staticDamageColor);
      this.lowestDamageColor = new Setting(this, "Lowest Damage Color")
         .changeValue(-43691)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withInvertedHierarchyParents(staticDamageColor);
      armorStatusDamageComparableList.add(new ArmorStatusDamageComparable(10, this.lowestDamageColor));
      armorStatusDamageComparableList.add(new ArmorStatusDamageComparable(25, this.lowDamageColor));
      armorStatusDamageComparableList.add(new ArmorStatusDamageComparable(40, this.mediumLowDamageColor));
      armorStatusDamageComparableList.add(new ArmorStatusDamageComparable(60, this.mediumDamageColor));
      armorStatusDamageComparableList.add(new ArmorStatusDamageComparable(80, this.mediumHighDamageColor));
      armorStatusDamageComparableList.add(new ArmorStatusDamageComparable(99, this.highDamageColor));
      armorStatusDamageComparableList.add(new ArmorStatusDamageComparable(100, this.maxDamageColor));
      this.setIcon(new ResourceLocation("client/icons/mods/diamond_chestplate.png"), 34, 34);
      this.setIconOffsetY(-10);
      this.setDescription("Displays your current armor and holding item durability.");
      this.setAuthors("bspkrs", "jadedcat");
      this.addEventHandler(ScaledResolutionEvent7.class, this::onScaledResolutionEvent7);
      this.addEventHandler(ScaledResolutionEvent3.class, this::onScaledResolutionEvent3);
   }
}
