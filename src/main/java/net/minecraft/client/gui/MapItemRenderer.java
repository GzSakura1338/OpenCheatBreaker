package net.minecraft.client.gui;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapData.MapCoord;
import org.lwjgl.opengl.GL11;

public class MapItemRenderer {
    public static ResourceLocation mapIcons = new ResourceLocation("textures/map/map_icons.png");
    public Map loadedMaps = Maps.newHashMap();
    public TextureManager textureManager;

    public MapItemRenderer(TextureManager textureManager) {
        this.textureManager = textureManager;
    }

    public void updateTexture(MapData mapData) {
        this.createInstance(mapData).run();
    }

    public void draw(MapData mapData, boolean flag) {
        this.createInstance(mapData).render(flag);
    }

    public MapItemRenderer.Instance createInstance(MapData mapData) {
        MapItemRenderer.Instance instance = (MapItemRenderer.Instance)this.loadedMaps.get(mapData.mapName);
        if (instance == null) {
            instance = new MapItemRenderer.Instance(mapData, null);
            this.loadedMaps.put(mapData.mapName, instance);
        }

        return instance;
    }

    public void clearStateTextures() {
        for (MapItemRenderer.Instance instance : (Iterable<MapItemRenderer.Instance>)this.loadedMaps.values()) {
            this.textureManager.deleteTexture(instance.resourceLocation);
        }

        this.loadedMaps.clear();
    }

    public class Instance {
        public MapData mapData;
        public DynamicTexture dynamicTexture;
        public int[] intArray;
        public ResourceLocation resourceLocation;

        public Instance(MapData mapData) {
            this.mapData = mapData;
            this.dynamicTexture = new DynamicTexture(128, 128);
            this.intArray = this.dynamicTexture.getTextureData();
            this.resourceLocation = MapItemRenderer.this.textureManager.getDynamicTextureLocation("map/" + mapData.mapName, this.dynamicTexture);

            for (int i = 0; i < this.intArray.length; i++) {
                this.intArray[i] = 0;
            }
        }

        public void run() {
            for (int i = 0; i < 16384; i++) {
                int n = this.mapData.colors[i] & 255;
                if (n / 4 == 0) {
                    this.intArray[i] = (i + i / 128 & 1) * 8 + 16 << 24;
                } else {
                    this.intArray[i] = MapColor.mapColorArray[n / 4].getRenderColor(n & 3);
                }
            }

            this.dynamicTexture.updateDynamicTexture();
        }

        public void render(boolean flag) {
            byte b = 0;
            byte b2 = 0;
            Tessellator instance = Tessellator.instance;
            float f = 0.0F;
            MapItemRenderer.this.textureManager.bindTexture(this.resourceLocation);
            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(1, 771, 0, 1);
            GL11.glDisable(3008);
            instance.startDrawingQuads();
            instance.addVertexWithUV(b + 0 + f, b2 + 128 - f, -0.01F, 0.0, 1.0);
            instance.addVertexWithUV(b + 128 - f, b2 + 128 - f, -0.01F, 1.0, 1.0);
            instance.addVertexWithUV(b + 128 - f, b2 + 0 + f, -0.01F, 1.0, 0.0);
            instance.addVertexWithUV(b + 0 + f, b2 + 0 + f, -0.01F, 0.0, 0.0);
            instance.draw();
            GL11.glEnable(3008);
            GL11.glDisable(3042);
            MapItemRenderer.this.textureManager.bindTexture(MapItemRenderer.mapIcons);
            int n = 0;

            for (MapCoord mapCoord : (Iterable<MapCoord>)this.mapData.playersVisibleOnMap.values()) {
                if (!flag || mapCoord.iconSize == 1) {
                    GL11.glPushMatrix();
                    GL11.glTranslatef(b + mapCoord.centerX / 2.0F + 64.0F, b2 + mapCoord.centerZ / 2.0F + 64.0F, -0.02F);
                    GL11.glRotatef(mapCoord.iconRotation * 360 / 16.0F, 0.0F, 0.0F, 1.0F);
                    GL11.glScalef(4.0F, 4.0F, 3.0F);
                    GL11.glTranslatef(-0.125F, 0.125F, 0.0F);
                    float f2 = (mapCoord.iconSize % 4 + 0) / 4.0F;
                    float f3 = (mapCoord.iconSize / 4 + 0) / 4.0F;
                    float f4 = (mapCoord.iconSize % 4 + 1) / 4.0F;
                    float f5 = (mapCoord.iconSize / 4 + 1) / 4.0F;
                    instance.startDrawingQuads();
                    instance.addVertexWithUV(-1.0, 1.0, n * 0.001F, f2, f3);
                    instance.addVertexWithUV(1.0, 1.0, n * 0.001F, f4, f3);
                    instance.addVertexWithUV(1.0, -1.0, n * 0.001F, f4, f5);
                    instance.addVertexWithUV(-1.0, -1.0, n * 0.001F, f2, f5);
                    instance.draw();
                    GL11.glPopMatrix();
                    n++;
                }
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, 0.0F, -0.04F);
            GL11.glScalef(1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }

        public Instance(MapData mapData, Object obj) {
            this(mapData);
        }
    }
}
