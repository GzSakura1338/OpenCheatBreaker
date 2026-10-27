package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.BlockIntEvent;
import com.cheatbreaker.client.event.GuiScreenEvent;
import com.cheatbreaker.client.event.IntEvent3;
import com.cheatbreaker.client.event.SimpleEvent31;
import com.cheatbreaker.client.event.SimpleEvent5;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.border.WorldBorder;
import net.optifine.Config;
import org.lwjgl.opengl.GL11;

public class PackTweaksMod extends AbstractMod {
   public boolean flag = false;
   public float floatValue = 1.0F;
   public Setting glassOptions;
   public Setting clearGlass;
   public Setting stringOptions;
   public Setting colorPlacedString;
   public Setting rodDashedLines;
   public Setting rodLineWidth;
   public Setting rodLineDistance;
   public Setting placedStringColor;
   public Setting rodStringColor;
   public Setting fireOptions;
   public Setting firstPersonFire;
   public Setting fireOpacity;
   public Setting fireHeight;
   public Setting fireOnEntities;
   public Setting vignetteOptions;
   public Setting vignetteType;
   public static ImmutableList<String> immutableList = ImmutableList.of("gold_ingot", "gold_nugget", "gold_ore", "golden_apple", "skull_char");
   public Setting vignetteOpacity;
   public Setting vignetteMinimumOpacity;
   public Setting vignetteMaximumOpacity;
   public Setting vignetteOpacityMultiplier;
   public Setting lowHealthOptions;
   public Setting lowHealthOverlay;
   public Setting lowHealthStart;
   public Setting lowHealthOpacity;
   public Setting pumpkinOverlayOptions;
   public Setting showPumpkinOverlay;
   public Setting pumpkinOverlayOpacity;
   public Setting inventoryOptions;
   public Setting transparentInventory;
   public Setting showInventoryTooltips;
   public Setting droppedItemOptions;
   public Setting facePlayer;
   public Setting rotateTowardsCamera;
   public Setting droppedItemScaleOptions;
   public Setting goldIngotScale;
   public Setting goldNuggetScale;
   public Setting goldOreScale;
   public Setting goldAppleScale;
   public Setting skullScale;

   @Override
   public void onDisable() {
      this.run();
      super.onDisable();
   }

   public void process(float f, ScaledResolution scaledResolution) {
      f = this.mc.thePlayer.getHealth() / 2.0F <= this.lowHealthStart.getFloatValue() ? f : 0.0F;
      f = MathHelper.clamp_float(f, 0.0F, 1.0F);
      WorldBorder worldBorder = this.mc.theWorld.getWorldBorder();
      float closestDistance = (float)worldBorder.getClosestDistance(this.mc.thePlayer);
      double min = Math.min(worldBorder.getResizeSpeed() * worldBorder.getWarningTime() * 1000.0, Math.abs(worldBorder.getTargetSize() - worldBorder.getDiameter()));
      double max = Math.max(worldBorder.getWarningDistance(), min);
      if (closestDistance < max) {
         closestDistance = 1.0F - (float)(closestDistance / max);
      } else {
         closestDistance = 0.0F;
      }

      this.floatValue = (float)(this.floatValue + (f - this.floatValue) * 0.01);
      GlStateManager.disableDepth();
      GlStateManager.depthMask(false);
      GlStateManager.tryBlendFuncSeparate(0, 769, 1, 0);
      if (closestDistance > 0.0F) {
         GlStateManager.color(0.0F, closestDistance, closestDistance, 1.0F);
      } else {
         GlStateManager.color(0.0F, this.floatValue, this.floatValue, 1.0F);
      }

      this.mc.getTextureManager().bindTexture(new ResourceLocation("textures/misc/vignette.png"));
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldRenderer.pos(0.0, scaledResolution.getScaledHeight(), -90.0).tex(0.0, 1.0).endVertex();
      worldRenderer.pos(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), -90.0).tex(1.0, 1.0).endVertex();
      worldRenderer.pos(scaledResolution.getScaledWidth(), 0.0, -90.0).tex(1.0, 0.0).endVertex();
      worldRenderer.pos(0.0, 0.0, -90.0).tex(0.0, 0.0).endVertex();
      instance.draw();
      GlStateManager.depthMask(true);
      GlStateManager.enableDepth();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
   }

   public PackTweaksMod() {
      super("Pack Tweaks");
      this.setDescription("Change how specific textures appear.");
      this.setCategories(ModCategory.HUD);
      this.setApplyDefaultColorOnEnable(false);
      this.glassOptions = new SettingGroup(this, "Glass Options").changeValue(true);
      this.clearGlass = new Setting(this, "Clear Glass")
         .setDescription(
            "Removes the glass texture.\n\n§bOFF:§r No glass textures are removed.\n§bREGULAR:§r Only normal glass texture is removed.\n§bALL:§r Normal and stained glass textures are removed."
         )
         .changeValue("OFF")
         .setStringValues("OFF", "REGULAR", "ALL")
         .setOnUpdate(obj -> this.mc.renderGlobal.loadRenderers())
         .withParents(this.glassOptions);
      this.stringOptions = new SettingGroup(this, "String Options").changeValue(true);
      this.colorPlacedString = new Setting(this, "Color Placed String")
         .setDescription("Show a solid color line instead of the texture pack's string texture.")
         .changeValue(false)
         .setOnUpdate(obj -> this.mc.renderGlobal.loadRenderers())
         .withParents(this.stringOptions);
      this.rodDashedLines = new Setting(this, "Rod Dashed Lines").changeValue(false).setUnit("px");
      this.rodLineWidth = new Setting(this, "Rod Line Width").changeValue(1).setRange(1, 10).setUnit("px");
      this.rodLineDistance = new Setting(this, "Rod Line Distance").changeValue(10).setRange(0, 100).setUnit("px").withParents(this.rodDashedLines);
      this.placedStringColor = new Setting(this, "Placed String Color")
         .changeValue(-65536)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.colorPlacedString);
      this.rodStringColor = new Setting(this, "Rod String Color").changeValue(-16777216).setRange(Integer.MIN_VALUE, Integer.MAX_VALUE);
      this.fireOptions = new SettingGroup(this, "Fire Options").changeValue(true);
      this.firstPersonFire = new Setting(this, "First Person Fire").changeValue(true).withParents(this.fireOptions);
      this.fireOpacity = new Setting(this, "Fire Opacity")
         .changeValue(90.0F)
         .setRange(0.0F, 100.0F)
         .setUnit("%")
         .setIconNames("circle-hollow-64", "circle-64")
         .setIconScales(7.5F, 7.5F)
         .setIconXOffsets(-0.25F, -2.75F)
         .setIconYOffsets(-0.75F, -0.75F)
         .withHierarchyParents(this.firstPersonFire);
      this.fireHeight = new Setting(this, "Fire Height").changeValue(1.0F).setRange(0.0F, 2.0F).withHierarchyParents(this.firstPersonFire);
      this.fireOnEntities = new Setting(this, "Fire on Entities").changeValue(true).withParents(this.fireOptions);
      this.vignetteOptions = new SettingGroup(this, "Vignette Options").changeValue(true);
      this.vignetteType = new Setting(this, "Vignette Type")
         .changeValue("Vanilla")
         .setStringValues("Vanilla", "Static", "Amplified")
         .withParents(this.vignetteOptions);
      this.vignetteOpacity = new Setting(this, "Vignette Opacity")
         .changeValue(75.0F)
         .setRange(0.0F, 100.0F)
         .setUnit("%")
         .setIconNames("circle-hollow-64", "circle-64")
         .setIconScales(7.5F, 7.5F)
         .setIconXOffsets(-0.25F, -2.75F)
         .setIconYOffsets(-0.75F, -0.75F)
         .setCondition(() -> this.vignetteType.getValue().equals("Static"))
         .withParents(this.vignetteOptions);
      this.vignetteMinimumOpacity = new Setting(this, "Vignette Minimum Opacity")
         .changeValue(0.0F)
         .setRange(0.0F, 100.0F)
         .setUnit("%")
         .setCondition(() -> this.vignetteType.getValue().equals("Amplified"))
         .withParents(this.vignetteOptions);
      this.vignetteMaximumOpacity = new Setting(this, "Vignette Maximum Opacity")
         .changeValue(100.0F)
         .setRange(0.0F, 100.0F)
         .setUnit("%")
         .setIconNames("circle-hollow-64", "circle-64")
         .setIconScales(7.5F, 7.5F)
         .setIconXOffsets(-0.25F, -2.75F)
         .setIconYOffsets(-0.75F, -0.75F)
         .setCondition(() -> this.vignetteType.getValue().equals("Amplified"))
         .withParents(this.vignetteOptions);
      this.vignetteOpacityMultiplier = new Setting(this, "Vignette Opacity Multiplier")
         .changeValue(1.0F)
         .setRange(0.1F, 10.0F)
         .setUnit("x")
         .setCondition(() -> this.vignetteType.getValue().equals("Amplified"))
         .withParents(this.vignetteOptions);
      this.lowHealthOptions = new SettingGroup(this, "Low Health Options").changeValue(true);
      this.lowHealthOverlay = new Setting(this, "Low Health Overlay").changeValue(false).withParents(this.lowHealthOptions);
      this.lowHealthStart = new Setting(this, "Low Health Start")
         .changeValue(3.5F)
         .setRange(1.0F, 10.0F)
         .setUnit(" hearts")
         .withHierarchyParents(this.lowHealthOverlay);
      this.lowHealthOpacity = new Setting(this, "Low Health Opacity")
         .changeValue(75.0F)
         .setRange(0.0F, 100.0F)
         .setUnit("%")
         .setIconNames("circle-hollow-64", "circle-64")
         .setIconScales(7.5F, 7.5F)
         .setIconXOffsets(-0.25F, -2.75F)
         .setIconYOffsets(-0.75F, -0.75F)
         .withHierarchyParents(this.lowHealthOverlay);
      this.pumpkinOverlayOptions = new SettingGroup(this, "Pumpkin Overlay Options").changeValue(true);
      this.showPumpkinOverlay = new Setting(this, "Show Pumpkin Overlay").changeValue(true).withParents(this.pumpkinOverlayOptions);
      this.pumpkinOverlayOpacity = new Setting(this, "Pumpkin Overlay Opacity")
         .changeValue(100.0F)
         .setRange(0.0F, 100.0F)
         .setUnit("%")
         .setIconNames("circle-hollow-64", "circle-64")
         .setIconScales(7.5F, 7.5F)
         .setIconXOffsets(-0.25F, -2.75F)
         .setIconYOffsets(-0.75F, -0.75F)
         .withHierarchyParents(this.showPumpkinOverlay);
      this.inventoryOptions = new SettingGroup(this, "Inventory Options").changeValue(true);
      this.transparentInventory = new Setting(this, "Transparent Inventory").changeValue(false).withParents(this.inventoryOptions);
      this.showInventoryTooltips = new Setting(this, "Show Inventory Tooltips").changeValue(true).withParents(this.inventoryOptions);
      this.droppedItemOptions = new SettingGroup(this, "Dropped Item Options").changeValue(true);
      this.facePlayer = new Setting(this, "Face player").changeValue(false).setCondition(Config::isDroppedItemsFancy).withParents(this.droppedItemOptions);
      this.rotateTowardsCamera = new Setting(this, "Rotate towards camera")
         .changeValue(false)
         .setCondition(() -> !Config.isDroppedItemsFancy() || this.facePlayer.getBooleanValue())
         .withParents(this.droppedItemOptions);
      this.droppedItemScaleOptions = new SettingGroup(this, "Dropped Item Scale Options").changeValue(true);
      this.goldIngotScale = new Setting(this, "Gold Ingot Scale")
         .changeValue(2.5F)
         .setRange(1.0F, 5.0F)
         .setOnUpdate(obj -> this.run())
         .withParents(this.droppedItemScaleOptions);
      this.goldNuggetScale = new Setting(this, "Gold Nugget Scale")
         .changeValue(2.5F)
         .setRange(1.0F, 5.0F)
         .setOnUpdate(obj -> this.run())
         .withParents(this.droppedItemScaleOptions);
      this.goldOreScale = new Setting(this, "Gold Ore Scale")
         .changeValue(2.5F)
         .setRange(1.0F, 5.0F)
         .setOnUpdate(obj -> this.run())
         .withParents(this.droppedItemScaleOptions);
      this.goldAppleScale = new Setting(this, "Gold Apple Scale")
         .changeValue(2.5F)
         .setRange(1.0F, 5.0F)
         .setOnUpdate(obj -> this.run())
         .withParents(this.droppedItemScaleOptions);
      this.skullScale = new Setting(this, "Skull Scale")
         .changeValue(2.5F)
         .setRange(1.0F, 5.0F)
         .setOnUpdate(obj -> this.run())
         .withParents(this.droppedItemScaleOptions);
      this.setIcon(new ResourceLocation("client/icons/mods/packtweaks.png"), 32, 32);
      this.addEventHandler(BlockIntEvent.class, this::onBlockIntEvent);
      this.addEventHandler(GuiScreenEvent.class, guiScreenEvent -> {
         if (this.flag) {
            if (this.mc.theWorld != null) {
               this.mc.refreshResources();
            }

            this.flag = false;
         }
      });
      this.addEventHandler(SimpleEvent5.class, simpleEvent5 -> {
         if (!(Boolean)this.firstPersonFire.getValue()) {
            simpleEvent5.setFlag(true);
         }
      });
      this.addEventHandler(IntEvent3.class, intEvent3 -> {
         intEvent3.setIntValue(this.rodStringColor.getColorValue());
         GL11.glLineWidth(this.rodLineWidth.getIntValue());
         if (this.rodDashedLines.getBooleanValue()) {
            GL11.glPushAttrib(8192);
            GL11.glLineStipple(this.rodLineDistance.getIntValue(), (short)-21846);
            GL11.glEnable(2852);
            GL11.glBegin(1);
            GL11.glEnd();
         }
      });
      this.addEventHandler(SimpleEvent31.class, simpleEvent31 -> {
         if (this.rodDashedLines.getBooleanValue()) {
            GL11.glPopAttrib();
         }
      });
      this.setEnabledByDefault(true);
   }

   public void onBlockIntEvent(BlockIntEvent blockIntEvent) {
      String stringValue = this.clearGlass.getStringValue();
      if (!"OFF".equals(stringValue)) {
         boolean flag = blockIntEvent.getBlock() instanceof BlockGlass;
         boolean flag2 = blockIntEvent.getBlock() instanceof BlockStainedGlass;
         if (flag || flag2 && "ALL".equals(stringValue)) {
            blockIntEvent.setFlag(true);
         }
      }
   }

   public float computeFloat(String str) {
      if (!this.checkString(str)) {
         return 0.0F;
      }

      str = str.replace("minecraft:item/", "");
      str = str.replace("minecraft:models/item/", "");
      switch (str.toLowerCase()) {
         case "gold_ingot":
            return this.goldIngotScale.getFloatValue();
         case "gold_nugget":
            return this.goldNuggetScale.getFloatValue();
         case "gold_ore":
            return this.goldOreScale.getFloatValue();
         case "golden_apple":
            return this.goldAppleScale.getFloatValue();
         case "skull_char":
            return this.skullScale.getFloatValue();
         default:
            return 0.0F;
      }
   }

   public boolean checkString(String str) {
      if (!str.toLowerCase().contains(":item/")) {
         return false;
      }

      UnmodifiableIterator unmodifiableIterator = immutableList.iterator();

      while (unmodifiableIterator.hasNext()) {
         if (str.contains((CharSequence)unmodifiableIterator.next())) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void onEnable() {
      this.run();
      super.onEnable();
   }

   public void run() {
      if (this.mc.theWorld != null) {
         this.flag = true;
      }
   }
}
