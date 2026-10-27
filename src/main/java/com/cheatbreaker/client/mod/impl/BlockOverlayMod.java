package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.event.AxisAlignedBBDoubleEvent;
import com.cheatbreaker.client.event.BlockEvent;
import com.cheatbreaker.client.event.DoubleEvent2;
import com.cheatbreaker.client.event.FloatEvent8;
import com.cheatbreaker.client.event.SimpleEvent15;
import com.cheatbreaker.client.event.SimpleEvent19;
import com.cheatbreaker.client.event.SimpleEvent2;
import com.cheatbreaker.client.event.SimpleEvent21;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingGroup;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class BlockOverlayMod extends AbstractMod {
   public Setting generalOptions;
   public Setting outline;
   public Setting overlay;
   public Setting dashedLines;
   public Setting lineDistance;
   public Setting lineWidth;
   public Setting gap;
   public Setting renderWithoutDepth;
   public Setting hidePlants;
   public Setting colorOptions;
   public Setting outlineColor;
   public Setting overlayColor;

   public BlockOverlayMod() {
      super("Block Overlay");
      this.setApplyDefaultColorOnEnable(false);
      this.setCategories(ModCategory.HUD);
      this.generalOptions = new SettingGroup(this, "General Options").changeValue(true);
      this.outline = new Setting(this, "Outline").changeValue(true).withParents(this.generalOptions);
      this.overlay = new Setting(this, "Overlay").changeValue(false).withParents(this.generalOptions);
      this.dashedLines = new Setting(this, "Dashed Lines").changeValue(false).setUnit("px").withHierarchyParents(this.outline);
      this.lineDistance = new Setting(this, "Line Distance").changeValue(10).setRange(0, 100).setUnit("px").withHierarchyParents(this.dashedLines);
      this.lineWidth = new Setting(this, "Line Width").changeValue(2.0F).setRange(1.0F, 10.0F).setUnit("px").withHierarchyParents(this.outline);
      this.gap = new Setting(this, "Gap").changeValue(1.0).setRange(1.0, 10.0).withParents(this.generalOptions);
      this.renderWithoutDepth = new Setting(this, "Render without Depth").changeValue(false).withParents(this.generalOptions);
      this.hidePlants = new Setting(this, "Hide Plants").changeValue(false).withParents(this.generalOptions);
      this.colorOptions = new SettingGroup(this, "Color Options").changeValue(true).withOptionalParents(this.outline, this.overlay);
      this.outlineColor = new Setting(this, "Outline Color")
         .changeValue(1711276032)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withParents(this.colorOptions, this.outline);
      this.overlayColor = new Setting(this, "Overlay Color")
         .changeValue(1056964608)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .withHierarchyParents(this.colorOptions, this.overlay);
      this.setIcon(new ResourceLocation("client/icons/mods/overlay.png"), 36, 40);
      this.setDescription("Replace the Vanilla outline with a customizable outline and overlay.");
      this.setAuthors("aycy");
      this.addEventHandler(FloatEvent8.class, floatEvent8 -> floatEvent8.setFloatValue(this.lineWidth.getFloatValue()));
      this.addEventHandler(DoubleEvent2.class, doubleEvent2 -> doubleEvent2.setDoubleValue(this.gap.getDoubleValue() * 0.002F));
      this.addEventHandler(SimpleEvent15.class, simpleEvent15 -> {
         if (this.renderWithoutDepth.getBooleanValue()) {
            GlStateManager.disableDepth();
         }
      });
      this.addEventHandler(BlockEvent.class, blockEvent -> {
         if ((blockEvent.getBlock().getMaterial() == Material.plants || blockEvent.getBlock().getMaterial() == Material.vine) && (Boolean)this.hidePlants.getValue()) {
            blockEvent.setFlag(true);
         }
      });
      this.addEventHandler(SimpleEvent21.class, simpleEvent21 -> simpleEvent21.setFlag(!this.outline.getBooleanValue()));
      this.addEventHandler(SimpleEvent2.class, simpleEvent2 -> {
         int colorValue = this.outlineColor.getColorValue();
         float f = (colorValue >> 24 & 0xFF) / 255.0F;
         float f2 = (colorValue >> 16 & 0xFF) / 255.0F;
         float f3 = (colorValue >> 8 & 0xFF) / 255.0F;
         float f4 = (colorValue & 0xFF) / 255.0F;
         if (this.dashedLines.getBooleanValue()) {
            GL11.glPushAttrib(8192);
            GL11.glLineStipple(this.lineDistance.getIntValue(), (short)-21846);
            GL11.glEnable(2852);
            GL11.glBegin(1);
            GL11.glEnd();
         }

         GlStateManager.color(f2, f3, f4, f);
      });
      this.addEventHandler(SimpleEvent19.class, simpleEvent19 -> {
         if (this.dashedLines.getBooleanValue()) {
            GL11.glPopAttrib();
         }
      });
      this.addEventHandler(
         AxisAlignedBBDoubleEvent.class,
         axisAlignedBBDoubleEvent -> {
            int colorValue = this.overlayColor.getColorValue();
            float f = (colorValue >> 24 & 0xFF) / 255.0F;
            float f2 = (colorValue >> 16 & 0xFF) / 255.0F;
            float f3 = (colorValue >> 8 & 0xFF) / 255.0F;
            float f4 = (colorValue & 0xFF) / 255.0F;
            if (this.overlay.getBooleanValue()) {
               GlStateManager.color(f2, f3, f4, f);
               render(
                  axisAlignedBBDoubleEvent.getAxisAlignedBB()
                     .expand(axisAlignedBBDoubleEvent.getDoubleValue1(), axisAlignedBBDoubleEvent.getDoubleValue1(), axisAlignedBBDoubleEvent.getDoubleValue1())
                     .offset(-axisAlignedBBDoubleEvent.getDoubleValue2(), -axisAlignedBBDoubleEvent.getDoubleValue3(), -axisAlignedBBDoubleEvent.getDoubleValue4())
               );
            }
         }
      );
      this.setEnabledByDefault(true);
   }

   public static void render(AxisAlignedBB axisAlignedBB) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
      instance.draw();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
      instance.draw();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      instance.draw();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      instance.draw();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      instance.draw();
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ).endVertex();
      worldRenderer.pos(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ).endVertex();
      instance.draw();
   }
}
