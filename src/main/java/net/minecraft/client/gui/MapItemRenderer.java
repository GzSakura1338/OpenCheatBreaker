package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec4b;
import net.minecraft.world.storage.MapData;

public class MapItemRenderer {
   public static ResourceLocation mapIcons = new ResourceLocation("textures/map/map_icons.png");
   public Map<String, MapItemRenderer.Instance> loadedMaps = Maps.newHashMap();
   public TextureManager textureManager;

   public MapItemRenderer(TextureManager textureManagerIn) {
      this.textureManager = textureManagerIn;
   }

   public void updateMapTexture(MapData mapdataIn) {
      this.getMapRendererInstance(mapdataIn).updateMapTexture();
   }

   public void renderMap(MapData mapdataIn, boolean flag) {
      this.getMapRendererInstance(mapdataIn).render(flag);
   }

   public MapItemRenderer.Instance getMapRendererInstance(MapData mapdataIn) {
      MapItemRenderer.Instance instance = this.loadedMaps.get(mapdataIn.mapName);
      if (instance == null) {
         instance = new MapItemRenderer.Instance(mapdataIn);
         this.loadedMaps.put(mapdataIn.mapName, instance);
      }

      return instance;
   }

   public void clearLoadedMaps() {
      for (MapItemRenderer.Instance instance : this.loadedMaps.values()) {
         this.textureManager.deleteTexture(instance.location);
      }

      this.loadedMaps.clear();
   }

   class Instance {
      public MapData mapData;
      public DynamicTexture mapTexture;
      public int[] mapTextureData;
      public ResourceLocation location;

      public Instance(MapData mapdataIn) {
         this.mapData = mapdataIn;
         this.mapTexture = new DynamicTexture(128, 128);
         this.mapTextureData = this.mapTexture.getTextureData();
         this.location = MapItemRenderer.this.textureManager.getDynamicTextureLocation("map/" + mapdataIn.mapName, this.mapTexture);

         for (int i = 0; i < this.mapTextureData.length; i++) {
            this.mapTextureData[i] = 0;
         }
      }

      public void updateMapTexture() {
         for (int i = 0; i < 16384; i++) {
            int n = this.mapData.colors[i] & 255;
            if (n / 4 == 0) {
               this.mapTextureData[i] = (i + i / 128 & 1) * 8 + 16 << 24;
            } else {
               this.mapTextureData[i] = MapColor.mapColorArray[n / 4].getMapColor(n & 3);
            }
         }

         this.mapTexture.updateDynamicTexture();
      }

      public void render(boolean noOverlayRendering) {
         byte b = 0;
         byte b2 = 0;
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         float f = 0.0F;
         MapItemRenderer.this.textureManager.bindTexture(this.location);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(1, 771, 0, 1);
         GlStateManager.disableAlpha();
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.pos(b + 0 + f, b2 + 128 - f, -0.01F).tex(0.0, 1.0).endVertex();
         worldRenderer.pos(b + 128 - f, b2 + 128 - f, -0.01F).tex(1.0, 1.0).endVertex();
         worldRenderer.pos(b + 128 - f, b2 + 0 + f, -0.01F).tex(1.0, 0.0).endVertex();
         worldRenderer.pos(b + 0 + f, b2 + 0 + f, -0.01F).tex(0.0, 0.0).endVertex();
         instance.draw();
         GlStateManager.enableAlpha();
         GlStateManager.disableBlend();
         MapItemRenderer.this.textureManager.bindTexture(MapItemRenderer.mapIcons);
         int n = 0;

         for (Vec4b vec4b : this.mapData.mapDecorations.values()) {
            if (!noOverlayRendering || vec4b.getTypeId() == 1) {
               GlStateManager.pushMatrix();
               GlStateManager.translate(b + vec4b.getX() / 2.0F + 64.0F, b2 + vec4b.getY() / 2.0F + 64.0F, -0.02F);
               GlStateManager.rotate(vec4b.getRotation() * 360 / 16.0F, 0.0F, 0.0F, 1.0F);
               GlStateManager.scale(4.0F, 4.0F, 3.0F);
               GlStateManager.translate(-0.125F, 0.125F, 0.0F);
               byte typeId = vec4b.getTypeId();
               float f2 = (typeId % 4 + 0) / 4.0F;
               float f3 = (typeId / 4 + 0) / 4.0F;
               float f4 = (typeId % 4 + 1) / 4.0F;
               float f5 = (typeId / 4 + 1) / 4.0F;
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
               float f6 = -0.001F;
               worldRenderer.pos(-1.0, 1.0, n * -0.001F).tex(f2, f3).endVertex();
               worldRenderer.pos(1.0, 1.0, n * -0.001F).tex(f4, f3).endVertex();
               worldRenderer.pos(1.0, -1.0, n * -0.001F).tex(f4, f5).endVertex();
               worldRenderer.pos(-1.0, -1.0, n * -0.001F).tex(f2, f5).endVertex();
               instance.draw();
               GlStateManager.popMatrix();
               n++;
            }
         }

         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, 0.0F, -0.04F);
         GlStateManager.scale(1.0F, 1.0F, 1.0F);
         GlStateManager.popMatrix();
      }
   }
}
