package net.minecraft.client.renderer.block.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.util.vector.Vector3f;

public class ItemModelGenerator {
   public static List<String> LAYERS = Lists.newArrayList(new String[]{"layer0", "layer1", "layer2", "layer3", "layer4"});

   public ModelBlock makeItemModel(TextureMap textureMapIn, ModelBlock blockModel) {
      HashMap newHashMap = Maps.newHashMap();
      ArrayList newArrayList = Lists.newArrayList();

      for (int i = 0; i < LAYERS.size(); i++) {
         String str = LAYERS.get(i);
         if (!blockModel.isTexturePresent(str)) {
            break;
         }

         String resolveTextureName = blockModel.resolveTextureName(str);
         newHashMap.put(str, resolveTextureName);
         TextureAtlasSprite atlasSprite = textureMapIn.getAtlasSprite(new ResourceLocation(resolveTextureName).toString());
         newArrayList.addAll(this.findList(i, str, atlasSprite));
      }

      if (newArrayList.isEmpty()) {
         return null;
      }

      newHashMap.put("particle", blockModel.isTexturePresent("particle") ? blockModel.resolveTextureName("particle") : (String)newHashMap.get("layer0"));
      return new ModelBlock(newArrayList, newHashMap, false, false, blockModel.getAllTransforms());
   }

   public List<BlockPart> findList(int n, String str, TextureAtlasSprite textureAtlasSprite) {
      HashMap newHashMap = Maps.newHashMap();
      newHashMap.put(EnumFacing.SOUTH, new BlockPartFace((EnumFacing)null, n, str, new BlockFaceUV(new float[]{0.0F, 0.0F, 16.0F, 16.0F}, 0)));
      newHashMap.put(EnumFacing.NORTH, new BlockPartFace((EnumFacing)null, n, str, new BlockFaceUV(new float[]{16.0F, 0.0F, 0.0F, 16.0F}, 0)));
      ArrayList newArrayList = Lists.newArrayList();
      newArrayList.add(new BlockPart(new Vector3f(0.0F, 0.0F, 7.5F), new Vector3f(16.0F, 16.0F, 8.5F), newHashMap, (BlockPartRotation)null, true));
      newArrayList.addAll(this.findList(textureAtlasSprite, str, n));
      return newArrayList;
   }

   public List<BlockPart> findList(TextureAtlasSprite textureAtlasSprite, String str, int n) {
      float iconWidth = textureAtlasSprite.getIconWidth();
      float iconHeight = textureAtlasSprite.getIconHeight();
      ArrayList newArrayList = Lists.newArrayList();

      for (ItemModelGenerator.Span span : this.findList(textureAtlasSprite)) {
         float f = 0.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;
         float f4 = 0.0F;
         float f5 = 0.0F;
         float f6 = 0.0F;
         float f7 = 0.0F;
         float f8 = 0.0F;
         float f9 = 0.0F;
         float f10 = 0.0F;
         float min = span.getMin();
         float max = span.getMax();
         float level = span.getLevel();
         ItemModelGenerator.SpanFacing side = span.getSide();
         switch (side) {
            case UP:
               f5 = min;
               f = min;
               f3 = f6 = max + 1.0F;
               f7 = level;
               f2 = level;
               f8 = level;
               f4 = level;
               f9 = 16.0F / iconWidth;
               f10 = 16.0F / (iconHeight - 1.0F);
               break;
            case DOWN:
               f8 = level;
               f7 = level;
               f5 = min;
               f = min;
               f3 = f6 = max + 1.0F;
               f2 = level + 1.0F;
               f4 = level + 1.0F;
               f9 = 16.0F / iconWidth;
               f10 = 16.0F / (iconHeight - 1.0F);
               break;
            case LEFT:
               f5 = level;
               f = level;
               f6 = level;
               f3 = level;
               f8 = min;
               f2 = min;
               f4 = f7 = max + 1.0F;
               f9 = 16.0F / (iconWidth - 1.0F);
               f10 = 16.0F / iconHeight;
               break;
            case RIGHT:
               f6 = level;
               f5 = level;
               f = level + 1.0F;
               f3 = level + 1.0F;
               f8 = min;
               f2 = min;
               f4 = f7 = max + 1.0F;
               f9 = 16.0F / (iconWidth - 1.0F);
               f10 = 16.0F / iconHeight;
         }

         float f11 = 16.0F / iconWidth;
         float f12 = 16.0F / iconHeight;
         f *= f11;
         f3 *= f11;
         f2 *= f12;
         f4 *= f12;
         f2 = 16.0F - f2;
         f4 = 16.0F - f4;
         f5 *= f9;
         f6 *= f9;
         f7 *= f10;
         f8 *= f10;
         HashMap newHashMap = Maps.newHashMap();
         newHashMap.put(side.getFacing(), new BlockPartFace((EnumFacing)null, n, str, new BlockFaceUV(new float[]{f5, f7, f6, f8}, 0)));
         switch (side) {
            case UP:
               newArrayList.add(new BlockPart(new Vector3f(f, f2, 7.5F), new Vector3f(f3, f2, 8.5F), newHashMap, (BlockPartRotation)null, true));
               break;
            case DOWN:
               newArrayList.add(new BlockPart(new Vector3f(f, f4, 7.5F), new Vector3f(f3, f4, 8.5F), newHashMap, (BlockPartRotation)null, true));
               break;
            case LEFT:
               newArrayList.add(new BlockPart(new Vector3f(f, f2, 7.5F), new Vector3f(f, f4, 8.5F), newHashMap, (BlockPartRotation)null, true));
               break;
            case RIGHT:
               newArrayList.add(new BlockPart(new Vector3f(f3, f2, 7.5F), new Vector3f(f3, f4, 8.5F), newHashMap, (BlockPartRotation)null, true));
         }
      }

      return newArrayList;
   }

   public List<ItemModelGenerator.Span> findList(TextureAtlasSprite textureAtlasSprite) {
      int iconWidth = textureAtlasSprite.getIconWidth();
      int iconHeight = textureAtlasSprite.getIconHeight();
      ArrayList newArrayList = Lists.newArrayList();

      for (int i = 0; i < textureAtlasSprite.getFrameCount(); i++) {
         int[] ints = textureAtlasSprite.getFrameTextureData(i)[0];

         for (int j = 0; j < iconHeight; j++) {
            for (int k = 0; k < iconWidth; k++) {
               boolean flag = !this.check(ints, k, j, iconWidth, iconHeight);
               this.handleSpanFacing(ItemModelGenerator.SpanFacing.UP, newArrayList, ints, k, j, iconWidth, iconHeight, flag);
               this.handleSpanFacing(ItemModelGenerator.SpanFacing.DOWN, newArrayList, ints, k, j, iconWidth, iconHeight, flag);
               this.handleSpanFacing(ItemModelGenerator.SpanFacing.LEFT, newArrayList, ints, k, j, iconWidth, iconHeight, flag);
               this.handleSpanFacing(ItemModelGenerator.SpanFacing.RIGHT, newArrayList, ints, k, j, iconWidth, iconHeight, flag);
            }
         }
      }

      return newArrayList;
   }

   public void handleSpanFacing(
      ItemModelGenerator.SpanFacing spanFacing, List<ItemModelGenerator.Span> list, int[] ints, int n, int n2, int n3, int n4, boolean flag
   ) {
      boolean flag2 = this.check(ints, n + spanFacing.getOffsetX(), n2 + spanFacing.getOffsetY(), n3, n4) && flag;
      if (flag2) {
         this.handleList(list, spanFacing, n, n2);
      }
   }

   public void handleList(List<ItemModelGenerator.Span> list, ItemModelGenerator.SpanFacing spanFacing, int n, int n2) {
      ItemModelGenerator.Span span = null;

      for (ItemModelGenerator.Span span2 : list) {
         if (span2.getSide() == spanFacing) {
            int n3 = spanFacing.isVertical() ? n2 : n;
            if (span2.getLevel() == n3) {
               span = span2;
               break;
            }
         }
      }

      int n3 = spanFacing.isVertical() ? n2 : n;
      int n4 = spanFacing.isVertical() ? n : n2;
      if (span == null) {
         list.add(new ItemModelGenerator.Span(spanFacing, n4, n3));
      } else {
         span.expand(n4);
      }
   }

   public boolean check(int[] ints, int n, int n2, int n3, int n4) {
      return n >= 0 && n2 >= 0 && n < n3 && n2 < n4 ? (ints[n2 * n3 + n] >> 24 & 0xFF) == 0 : true;
   }

   static class Span {
      public ItemModelGenerator.SpanFacing spanFacing;
      public int min;
      public int max;
      public int level;

      public Span(ItemModelGenerator.SpanFacing spanFacingIn, int n, int n2) {
         this.spanFacing = spanFacingIn;
         this.min = n;
         this.max = n;
         this.level = n2;
      }

      public void expand(int n) {
         if (n < this.min) {
            this.min = n;
         } else if (n > this.max) {
            this.max = n;
         }
      }

      public ItemModelGenerator.SpanFacing getSide() {
         return this.spanFacing;
      }

      public int getMin() {
         return this.min;
      }

      public int getMax() {
         return this.max;
      }

      public int getLevel() {
         return this.level;
      }
   }

   enum SpanFacing {
      UP(EnumFacing.UP, 0, -1),
      DOWN(EnumFacing.DOWN, 0, 1),
      LEFT(EnumFacing.EAST, -1, 0),
      RIGHT(EnumFacing.WEST, 1, 0);

      public EnumFacing facing;
      public int offsetX;
      public int offsetY;

      SpanFacing(EnumFacing facing, int n, int n2) {
         this.facing = facing;
         this.offsetX = n;
         this.offsetY = n2;
      }

      public EnumFacing getFacing() {
         return this.facing;
      }

      public int getOffsetX() {
         return this.offsetX;
      }

      public int getOffsetY() {
         return this.offsetY;
      }

      public boolean isVertical() {
         return this == DOWN || this == UP;
      }
   }
}
