package net.optifine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGlass;
import net.minecraft.block.BlockPane;
import net.minecraft.block.BlockQuartz;
import net.minecraft.block.BlockRotatedPillar;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.config.Matches;
import net.optifine.model.BlockModelUtils;
import net.optifine.model.ListQuadsOverlay;
import net.optifine.reflect.Reflector;
import net.optifine.render.RenderEnv;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;
import net.optifine.util.TileEntityUtils;

public class ConnectedTextures {
   public static Map[] spriteQuadMaps = null;
   public static Map[] spriteQuadFullMaps = null;
   public static Map[][] spriteQuadCompactMaps = (Map[][])null;
   public static ConnectedProperties[][] blockProperties = (ConnectedProperties[][])null;
   public static ConnectedProperties[][] tileProperties = (ConnectedProperties[][])null;
   public static boolean multipass = false;
   public static final int UNKNOWN = -1;
   public static final int Z_POS_SOUTH = 3;
   public static final int X_NEG_WEST = 4;
   public static final int X_POS_EAST = 5;
   public static IBlockState AIR_DEFAULT_STATE = Blocks.air.getDefaultState();
   public static TextureAtlasSprite emptySprite = null;
   public static BlockDir[] SIDES_Y_NEG_DOWN = new BlockDir[]{BlockDir.WEST, BlockDir.EAST, BlockDir.NORTH, BlockDir.SOUTH};
   public static BlockDir[] SIDES_Y_POS_UP = new BlockDir[]{BlockDir.WEST, BlockDir.EAST, BlockDir.SOUTH, BlockDir.NORTH};
   public static BlockDir[] SIDES_Z_NEG_NORTH = new BlockDir[]{BlockDir.EAST, BlockDir.WEST, BlockDir.DOWN, BlockDir.UP};
   public static BlockDir[] SIDES_Z_POS_SOUTH = new BlockDir[]{BlockDir.WEST, BlockDir.EAST, BlockDir.DOWN, BlockDir.UP};
   public static BlockDir[] SIDES_X_NEG_WEST = new BlockDir[]{BlockDir.NORTH, BlockDir.SOUTH, BlockDir.DOWN, BlockDir.UP};
   public static BlockDir[] SIDES_X_POS_EAST = new BlockDir[]{BlockDir.SOUTH, BlockDir.NORTH, BlockDir.DOWN, BlockDir.UP};
   public static BlockDir[] SIDES_Z_NEG_NORTH_Z_AXIS = new BlockDir[]{BlockDir.WEST, BlockDir.EAST, BlockDir.UP, BlockDir.DOWN};
   public static BlockDir[] SIDES_X_POS_EAST_X_AXIS = new BlockDir[]{BlockDir.NORTH, BlockDir.SOUTH, BlockDir.UP, BlockDir.DOWN};
   public static BlockDir[] EDGES_Y_NEG_DOWN = new BlockDir[]{BlockDir.NORTH_EAST, BlockDir.NORTH_WEST, BlockDir.SOUTH_EAST, BlockDir.SOUTH_WEST};
   public static BlockDir[] EDGES_Y_POS_UP = new BlockDir[]{BlockDir.SOUTH_EAST, BlockDir.SOUTH_WEST, BlockDir.NORTH_EAST, BlockDir.NORTH_WEST};
   public static BlockDir[] EDGES_Z_NEG_NORTH = new BlockDir[]{BlockDir.DOWN_WEST, BlockDir.DOWN_EAST, BlockDir.UP_WEST, BlockDir.UP_EAST};
   public static BlockDir[] EDGES_Z_POS_SOUTH = new BlockDir[]{BlockDir.DOWN_EAST, BlockDir.DOWN_WEST, BlockDir.UP_EAST, BlockDir.UP_WEST};
   public static BlockDir[] EDGES_X_NEG_WEST = new BlockDir[]{BlockDir.DOWN_SOUTH, BlockDir.DOWN_NORTH, BlockDir.UP_SOUTH, BlockDir.UP_NORTH};
   public static BlockDir[] EDGES_X_POS_EAST = new BlockDir[]{BlockDir.DOWN_NORTH, BlockDir.DOWN_SOUTH, BlockDir.UP_NORTH, BlockDir.UP_SOUTH};
   public static BlockDir[] EDGES_Z_NEG_NORTH_Z_AXIS = new BlockDir[]{BlockDir.UP_EAST, BlockDir.UP_WEST, BlockDir.DOWN_EAST, BlockDir.DOWN_WEST};
   public static BlockDir[] EDGES_X_POS_EAST_X_AXIS = new BlockDir[]{BlockDir.UP_SOUTH, BlockDir.UP_NORTH, BlockDir.DOWN_SOUTH, BlockDir.DOWN_NORTH};
   public static TextureAtlasSprite SPRITE_DEFAULT = new TextureAtlasSprite("<default>");
   public static final int CONSTANT_1 = 1;
   public static final int CONSTANT_0 = 0;
   public static final int CONSTANT_2 = 2;
   public static final int CONSTANT_1_2 = 1;
   public static final int CONSTANT_2_2 = 2;
   public static final int CONSTANT_0_2 = 0;

   public static BakedQuad[] getConnectedTexture(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, BakedQuad bakedQuad, RenderEnv renderEnv) {
      TextureAtlasSprite sprite = bakedQuad.getSprite();
      if (sprite == null) {
         return renderEnv.getArrayQuadsCtm(bakedQuad);
      } else {
         Block block = blockState.getBlock();
         if (skipConnectedTexture(blockAccess, blockState, blockPos, bakedQuad, renderEnv)) {
            bakedQuad = getQuad(emptySprite, bakedQuad);
            return renderEnv.getArrayQuadsCtm(bakedQuad);
         } else {
            EnumFacing face = bakedQuad.getFace();
            return getConnectedTextureMultiPass(blockAccess, blockState, blockPos, face, bakedQuad, renderEnv);
         }
      }
   }

   public static boolean skipConnectedTexture(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, BakedQuad bakedQuad, RenderEnv renderEnv) {
      Block block = blockState.getBlock();
      if (block instanceof BlockPane) {
         TextureAtlasSprite sprite = bakedQuad.getSprite();
         if (sprite.getIconName().startsWith("minecraft:blocks/glass_pane_top")) {
            IBlockState blockState2 = blockAccess.getBlockState(blockPos.offset(bakedQuad.getFace()));
            return blockState2 == blockState;
         }
      }

      if (block instanceof BlockPane) {
         EnumFacing face = bakedQuad.getFace();
         if (face != EnumFacing.UP && face != EnumFacing.DOWN) {
            return false;
         }

         if (!bakedQuad.isFaceQuad()) {
            return false;
         }

         BlockPos offset = blockPos.offset(bakedQuad.getFace());
         IBlockState blockState2 = blockAccess.getBlockState(offset);
         if (blockState2.getBlock() != block) {
            return false;
         }

         if (block == Blocks.stained_glass_pane && blockState2.getValue(BlockStainedGlassPane.COLOR) != blockState.getValue(BlockStainedGlassPane.COLOR)) {
            return false;
         }

         blockState2 = blockState2.getBlock().getActualState(blockState2, blockAccess, offset);
         double midX = bakedQuad.getMidX();
         if (midX < 0.4) {
            if (blockState2.getValue(BlockPane.WEST)) {
               return true;
            }
         } else if (midX > 0.6) {
            if (blockState2.getValue(BlockPane.EAST)) {
               return true;
            }
         } else {
            double midZ = bakedQuad.getMidZ();
            if (midZ < 0.4) {
               if (blockState2.getValue(BlockPane.NORTH)) {
                  return true;
               }
            } else {
               if (midZ <= 0.6) {
                  return true;
               }

               if (blockState2.getValue(BlockPane.SOUTH)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static BakedQuad[] getQuads(TextureAtlasSprite textureAtlasSprite, BakedQuad bakedQuad, RenderEnv renderEnv) {
      if (textureAtlasSprite == null) {
         return null;
      }

      if (textureAtlasSprite == SPRITE_DEFAULT) {
         return renderEnv.getArrayQuadsCtm(bakedQuad);
      }

      BakedQuad quad = getQuad(textureAtlasSprite, bakedQuad);
      return renderEnv.getArrayQuadsCtm(quad);
   }

   public static synchronized BakedQuad getQuad(TextureAtlasSprite textureAtlasSprite, BakedQuad bakedQuad) {
      if (spriteQuadMaps == null) {
         return bakedQuad;
      }

      int indexInMap = textureAtlasSprite.getIndexInMap();
      if (indexInMap >= 0 && indexInMap < spriteQuadMaps.length) {
         Map map = spriteQuadMaps[indexInMap];
         if (map == null) {
            map = new IdentityHashMap(1);
            spriteQuadMaps[indexInMap] = map;
         }

         BakedQuad bakedQuad2 = (BakedQuad)map.get(bakedQuad);
         if (bakedQuad2 == null) {
            bakedQuad2 = makeSpriteQuad(bakedQuad, textureAtlasSprite);
            map.put(bakedQuad, bakedQuad2);
         }

         return bakedQuad2;
      } else {
         return bakedQuad;
      }
   }

   public static synchronized BakedQuad getQuadFull(TextureAtlasSprite textureAtlasSprite, BakedQuad bakedQuad, int n) {
      if (spriteQuadFullMaps == null) {
         return null;
      }

      if (textureAtlasSprite == null) {
         return null;
      }

      int indexInMap = textureAtlasSprite.getIndexInMap();
      if (indexInMap >= 0 && indexInMap < spriteQuadFullMaps.length) {
         Map map = spriteQuadFullMaps[indexInMap];
         if (map == null) {
            map = new EnumMap<>(EnumFacing.class);
            spriteQuadFullMaps[indexInMap] = map;
         }

         EnumFacing face = bakedQuad.getFace();
         BakedQuad bakedQuad2 = (BakedQuad)map.get(face);
         if (bakedQuad2 == null) {
            bakedQuad2 = BlockModelUtils.makeBakedQuad(face, textureAtlasSprite, n);
            map.put(face, bakedQuad2);
         }

         return bakedQuad2;
      } else {
         return null;
      }
   }

   public static BakedQuad makeSpriteQuad(BakedQuad bakedQuad, TextureAtlasSprite textureAtlasSprite) {
      int[] ints = (int[])bakedQuad.getVertexData().clone();
      TextureAtlasSprite sprite = bakedQuad.getSprite();

      for (int i = 0; i < 4; i++) {
         fixVertex(ints, i, sprite, textureAtlasSprite);
      }

      return new BakedQuad(ints, bakedQuad.getTintIndex(), bakedQuad.getFace(), textureAtlasSprite);
   }

   public static void fixVertex(int[] ints, int n, TextureAtlasSprite textureAtlasSprite, TextureAtlasSprite textureAtlasSprite2) {
      int n2 = ints.length / 4;
      int n3 = n2 * n;
      float intBitsToFloat = Float.intBitsToFloat(ints[n3 + 4]);
      float intBitsToFloat2 = Float.intBitsToFloat(ints[n3 + 4 + 1]);
      double spriteU16 = textureAtlasSprite.getSpriteU16(intBitsToFloat);
      double spriteV16 = textureAtlasSprite.getSpriteV16(intBitsToFloat2);
      ints[n3 + 4] = Float.floatToRawIntBits(textureAtlasSprite2.getInterpolatedU(spriteU16));
      ints[n3 + 4 + 1] = Float.floatToRawIntBits(textureAtlasSprite2.getInterpolatedV(spriteV16));
   }

   public static BakedQuad[] getConnectedTextureMultiPass(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing enumFacing, BakedQuad bakedQuad, RenderEnv renderEnv) {
      BakedQuad[] connectedTextureSingle = getConnectedTextureSingle(blockAccess, blockState, blockPos, enumFacing, bakedQuad, true, 0, renderEnv);
      if (!multipass) {
         return connectedTextureSingle;
      }

      if (connectedTextureSingle.length == 1 && connectedTextureSingle[0] == bakedQuad) {
         return connectedTextureSingle;
      }

      List listQuadsCtmMultipass = renderEnv.getListQuadsCtmMultipass(connectedTextureSingle);

      for (int i = 0; i < listQuadsCtmMultipass.size(); i++) {
         BakedQuad bakedQuad2 = (BakedQuad)listQuadsCtmMultipass.get(i);
         BakedQuad bakedQuad3 = bakedQuad2;

         for (int j = 0; j < 3; j++) {
            BakedQuad[] connectedTextureSingle2 = getConnectedTextureSingle(blockAccess, blockState, blockPos, enumFacing, bakedQuad3, false, j + 1, renderEnv);
            if (connectedTextureSingle2.length != 1 || connectedTextureSingle2[0] == bakedQuad3) {
               break;
            }

            bakedQuad3 = connectedTextureSingle2[0];
         }

         listQuadsCtmMultipass.set(i, bakedQuad3);
      }

      for (int i = 0; i < connectedTextureSingle.length; i++) {
         connectedTextureSingle[i] = (BakedQuad)listQuadsCtmMultipass.get(i);
      }

      return connectedTextureSingle;
   }

   public static BakedQuad[] getConnectedTextureSingle(
      IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing enumFacing, BakedQuad bakedQuad, boolean flag, int n, RenderEnv renderEnv
   ) {
      Block block = blockState.getBlock();
      if (!(blockState instanceof BlockStateBase)) {
         return renderEnv.getArrayQuadsCtm(bakedQuad);
      }

      BlockStateBase blockStateBase = (BlockStateBase)blockState;
      TextureAtlasSprite sprite = bakedQuad.getSprite();
      if (tileProperties != null) {
         int indexInMap = sprite.getIndexInMap();
         if (indexInMap >= 0 && indexInMap < tileProperties.length) {
            ConnectedProperties[] connectedPropertieses = tileProperties[indexInMap];
            if (connectedPropertieses != null) {
               int side = getSide(enumFacing);

               for (int i = 0; i < connectedPropertieses.length; i++) {
                  ConnectedProperties connectedProperties = connectedPropertieses[i];
                  if (connectedProperties != null && connectedProperties.matchesBlockId(blockStateBase.getBlockId())) {
                     BakedQuad[] connectedTexture = getConnectedTexture(connectedProperties, blockAccess, blockStateBase, blockPos, side, bakedQuad, n, renderEnv);
                     if (connectedTexture != null) {
                        return connectedTexture;
                     }
                  }
               }
            }
         }
      }

      if (blockProperties != null && flag) {
         int blockId = renderEnv.getBlockId();
         if (blockId >= 0 && blockId < blockProperties.length) {
            ConnectedProperties[] connectedPropertieses = blockProperties[blockId];
            if (connectedPropertieses != null) {
               int side = getSide(enumFacing);

               for (int i = 0; i < connectedPropertieses.length; i++) {
                  ConnectedProperties connectedProperties = connectedPropertieses[i];
                  if (connectedProperties != null && connectedProperties.matchesIcon(sprite)) {
                     BakedQuad[] connectedTexture = getConnectedTexture(connectedProperties, blockAccess, blockStateBase, blockPos, side, bakedQuad, n, renderEnv);
                     if (connectedTexture != null) {
                        return connectedTexture;
                     }
                  }
               }
            }
         }
      }

      return renderEnv.getArrayQuadsCtm(bakedQuad);
   }

   public static int getSide(EnumFacing enumFacing) {
      if (enumFacing == null) {
         return -1;
      }

      switch (enumFacing) {
         case DOWN:
            return 0;
         case UP:
            return 1;
         case EAST:
            return 5;
         case WEST:
            return 4;
         case NORTH:
            return 2;
         case SOUTH:
            return 3;
         default:
            return -1;
      }
   }

   public static EnumFacing getFacing(int n) {
      switch (n) {
         case 0:
            return EnumFacing.DOWN;
         case 1:
            return EnumFacing.UP;
         case 2:
            return EnumFacing.NORTH;
         case 3:
            return EnumFacing.SOUTH;
         case 4:
            return EnumFacing.WEST;
         case 5:
            return EnumFacing.EAST;
         default:
            return EnumFacing.UP;
      }
   }

   public static BakedQuad[] getConnectedTexture(
      ConnectedProperties connectedProperties, IBlockAccess blockAccess, BlockStateBase blockStateBase, BlockPos blockPos, int n, BakedQuad bakedQuad, int n2, RenderEnv renderEnv
   ) {
      int n3 = 0;
      int metadata = blockStateBase.getMetadata();
      int n4 = metadata;
      Block block = blockStateBase.getBlock();
      if (block instanceof BlockRotatedPillar) {
         n3 = getWoodAxis(n, metadata);
         if (connectedProperties.getMetadataMax() <= 3) {
            n4 = metadata & 3;
         }
      }

      if (block instanceof BlockQuartz) {
         n3 = getQuartzAxis(n, metadata);
         if (connectedProperties.getMetadataMax() <= 2 && n4 > 2) {
            n4 = 2;
         }
      }

      if (!connectedProperties.matchesBlock(blockStateBase.getBlockId(), n4)) {
         return null;
      }

      if (n >= 0 && connectedProperties.faces != 63) {
         int n5 = n;
         if (n3 != 0) {
            n5 = fixSideByAxis(n, n3);
         }

         if ((1 << n5 & connectedProperties.faces) == 0) {
            return null;
         }
      }

      int n5 = blockPos.getY();
      if (connectedProperties.heights != null && !connectedProperties.heights.isInRange(n5)) {
         return null;
      }

      if (connectedProperties.biomes != null) {
         BiomeGenBase biomeGenForCoords = blockAccess.getBiomeGenForCoords(blockPos);
         if (!connectedProperties.matchesBiome(biomeGenForCoords)) {
            return null;
         }
      }

      if (connectedProperties.nbtName != null) {
         String tileEntityName = TileEntityUtils.getTileEntityName(blockAccess, blockPos);
         if (!connectedProperties.nbtName.matchesValue(tileEntityName)) {
            return null;
         }
      }

      TextureAtlasSprite sprite = bakedQuad.getSprite();
      switch (connectedProperties.method) {
         case 1:
            return getQuads(getConnectedTextureCtm(connectedProperties, blockAccess, blockStateBase, blockPos, n3, n, sprite, metadata, renderEnv), bakedQuad, renderEnv);
         case 2:
            return getQuads(getConnectedTextureHorizontal(connectedProperties, blockAccess, blockStateBase, blockPos, n3, n, sprite, metadata), bakedQuad, renderEnv);
         case 3:
            return getQuads(getConnectedTextureTop(connectedProperties, blockAccess, blockStateBase, blockPos, n3, n, sprite, metadata), bakedQuad, renderEnv);
         case 4:
            return getQuads(getConnectedTextureRandom(connectedProperties, blockAccess, blockStateBase, blockPos, n), bakedQuad, renderEnv);
         case 5:
            return getQuads(getConnectedTextureRepeat(connectedProperties, blockPos, n), bakedQuad, renderEnv);
         case 6:
            return getQuads(getConnectedTextureVertical(connectedProperties, blockAccess, blockStateBase, blockPos, n3, n, sprite, metadata), bakedQuad, renderEnv);
         case 7:
            return getQuads(getConnectedTextureFixed(connectedProperties), bakedQuad, renderEnv);
         case 8:
            return getQuads(getConnectedTextureHorizontalVertical(connectedProperties, blockAccess, blockStateBase, blockPos, n3, n, sprite, metadata), bakedQuad, renderEnv);
         case 9:
            return getQuads(getConnectedTextureVerticalHorizontal(connectedProperties, blockAccess, blockStateBase, blockPos, n3, n, sprite, metadata), bakedQuad, renderEnv);
         case 10:
            if (n2 == 0) {
               return getConnectedTextureCtmCompact(connectedProperties, blockAccess, blockStateBase, blockPos, n3, n, bakedQuad, metadata, renderEnv);
            }
         default:
            return null;
         case 11:
            return getConnectedTextureOverlay(connectedProperties, blockAccess, blockStateBase, blockPos, n3, n, bakedQuad, metadata, renderEnv);
         case 12:
            return getConnectedTextureOverlayFixed(connectedProperties, bakedQuad, renderEnv);
         case 13:
            return getConnectedTextureOverlayRandom(connectedProperties, blockAccess, blockStateBase, blockPos, n, bakedQuad, renderEnv);
         case 14:
            return getConnectedTextureOverlayRepeat(connectedProperties, blockPos, n, bakedQuad, renderEnv);
         case 15:
            return getConnectedTextureOverlayCtm(connectedProperties, blockAccess, blockStateBase, blockPos, n3, n, bakedQuad, metadata, renderEnv);
      }
   }

   public static int fixSideByAxis(int n, int n2) {
      switch (n2) {
         case 0:
            return n;
         case 1:
            switch (n) {
               case 0:
                  return 2;
               case 1:
                  return 3;
               case 2:
                  return 1;
               case 3:
                  return 0;
               default:
                  return n;
            }
         case 2:
            switch (n) {
               case 0:
                  return 4;
               case 1:
                  return 5;
               case 2:
               case 3:
               default:
                  return n;
               case 4:
                  return 1;
               case 5:
                  return 0;
            }
         default:
            return n;
      }
   }

   public static int getWoodAxis(int n, int n2) {
      int n3 = (n2 & 12) >> 2;
      switch (n3) {
         case 1:
            return 2;
         case 2:
            return 1;
         default:
            return 0;
      }
   }

   public static int getQuartzAxis(int n, int n2) {
      switch (n2) {
         case 3:
            return 2;
         case 4:
            return 1;
         default:
            return 0;
      }
   }

   public static TextureAtlasSprite getConnectedTextureRandom(ConnectedProperties connectedProperties, IBlockAccess blockAccess, BlockStateBase blockStateBase, BlockPos blockPos, int n) {
      if (connectedProperties.tileIcons.length == 1) {
         return connectedProperties.tileIcons[0];
      }

      int n2 = n / connectedProperties.symmetry * connectedProperties.symmetry;
      if (connectedProperties.linked) {
         BlockPos down = blockPos.down();

         for (IBlockState blockState = blockAccess.getBlockState(down); blockState.getBlock() == blockStateBase.getBlock(); blockState = blockAccess.getBlockState(down)) {
            blockPos = down;
            down = down.down();
            if (down.getY() < 0) {
               break;
            }
         }
      }

      int n3 = Config.getRandom(blockPos, n2) & 2147483647;

      for (int i = 0; i < connectedProperties.randomLoops; i++) {
         n3 = Config.intHash(n3);
      }

      int n4 = 0;
      if (connectedProperties.weights == null) {
         n4 = n3 % connectedProperties.tileIcons.length;
      } else {
         int n5 = n3 % connectedProperties.sumAllWeights;
         int[] sumWeights = connectedProperties.sumWeights;

         for (int i = 0; i < sumWeights.length; i++) {
            if (n5 < sumWeights[i]) {
               n4 = i;
               break;
            }
         }
      }

      return connectedProperties.tileIcons[n4];
   }

   public static TextureAtlasSprite getConnectedTextureFixed(ConnectedProperties connectedProperties) {
      return connectedProperties.tileIcons[0];
   }

   public static TextureAtlasSprite getConnectedTextureRepeat(ConnectedProperties connectedProperties, BlockPos blockPos, int n) {
      if (connectedProperties.tileIcons.length == 1) {
         return connectedProperties.tileIcons[0];
      }

      int n2 = blockPos.getX();
      int n3 = blockPos.getY();
      int n4 = blockPos.getZ();
      int n5 = 0;
      int n6 = 0;
      switch (n) {
         case 0:
            n5 = n2;
            n6 = -n4 - 1;
            break;
         case 1:
            n5 = n2;
            n6 = n4;
            break;
         case 2:
            n5 = -n2 - 1;
            n6 = -n3;
            break;
         case 3:
            n5 = n2;
            n6 = -n3;
            break;
         case 4:
            n5 = n4;
            n6 = -n3;
            break;
         case 5:
            n5 = -n4 - 1;
            n6 = -n3;
      }

      n5 %= connectedProperties.width;
      n6 %= connectedProperties.height;
      if (n5 < 0) {
         n5 += connectedProperties.width;
      }

      if (n6 < 0) {
         n6 += connectedProperties.height;
      }

      int n7 = n6 * connectedProperties.width + n5;
      return connectedProperties.tileIcons[n7];
   }

   public static TextureAtlasSprite getConnectedTextureCtm(
      ConnectedProperties connectedProperties, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int n, int n2, TextureAtlasSprite textureAtlasSprite, int n3, RenderEnv renderEnv
   ) {
      int connectedTextureCtmIndex = getConnectedTextureCtmIndex(connectedProperties, blockAccess, blockState, blockPos, n, n2, textureAtlasSprite, n3, renderEnv);
      return connectedProperties.tileIcons[connectedTextureCtmIndex];
   }

   public static synchronized BakedQuad[] getConnectedTextureCtmCompact(
      ConnectedProperties connectedProperties, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int n, int n2, BakedQuad bakedQuad, int n3, RenderEnv renderEnv
   ) {
      TextureAtlasSprite sprite = bakedQuad.getSprite();
      int connectedTextureCtmIndex = getConnectedTextureCtmIndex(connectedProperties, blockAccess, blockState, blockPos, n, n2, sprite, n3, renderEnv);
      return ConnectedTexturesCompact.getConnectedTextureCtmCompact(connectedTextureCtmIndex, connectedProperties, n2, bakedQuad, renderEnv);
   }

   public static BakedQuad[] getConnectedTextureOverlay(
      ConnectedProperties connectedProperties, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int n, int n2, BakedQuad bakedQuad, int n3, RenderEnv renderEnv
   ) {
      if (!bakedQuad.isFullQuad()) {
         return null;
      }

      TextureAtlasSprite sprite = bakedQuad.getSprite();
      BlockDir[] sideDirections = getSideDirections(n2, n);
      boolean[] borderFlags = renderEnv.getBorderFlags();

      for (int i = 0; i < 4; i++) {
         borderFlags[i] = isNeighbourOverlay(connectedProperties, blockAccess, blockState, sideDirections[i].offset(blockPos), n2, sprite, n3);
      }

      ListQuadsOverlay listQuadsOverlay = renderEnv.getListQuadsOverlay(connectedProperties.layer);

      Object obj;
      try {
         if (!borderFlags[0] || !borderFlags[1] || !borderFlags[2] || !borderFlags[3]) {
            if (borderFlags[0] && borderFlags[1] && borderFlags[2]) {
               listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[5], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
               obj = null;
               return (BakedQuad[])obj;
            }

            if (borderFlags[0] && borderFlags[2] && borderFlags[3]) {
               listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[6], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
               obj = null;
               return (BakedQuad[])obj;
            }

            if (borderFlags[1] && borderFlags[2] && borderFlags[3]) {
               listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[12], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
               obj = null;
               return (BakedQuad[])obj;
            }

            if (borderFlags[0] && borderFlags[1] && borderFlags[3]) {
               listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[13], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
               obj = null;
               return (BakedQuad[])obj;
            }

            BlockDir[] edgeDirections = getEdgeDirections(n2, n);
            boolean[] borderFlags2 = renderEnv.getBorderFlags2();

            for (int i = 0; i < 4; i++) {
               borderFlags2[i] = isNeighbourOverlay(connectedProperties, blockAccess, blockState, edgeDirections[i].offset(blockPos), n2, sprite, n3);
            }

            if (borderFlags[1] && borderFlags[2]) {
               listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[3], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
               if (borderFlags2[3]) {
                  listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[16], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
               }

               Object obj2 = null;
               return (BakedQuad[])obj2;
            }

            if (borderFlags[0] && borderFlags[2]) {
               listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[4], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
               if (borderFlags2[2]) {
                  listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[14], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
               }

               Object obj2 = null;
               return (BakedQuad[])obj2;
            }

            if (borderFlags[1] && borderFlags[3]) {
               listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[10], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
               if (borderFlags2[1]) {
                  listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[2], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
               }

               Object obj2 = null;
               return (BakedQuad[])obj2;
            }

            if (borderFlags[0] && borderFlags[3]) {
               listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[11], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
               if (borderFlags2[0]) {
                  listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[0], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
               }

               Object obj2 = null;
               return (BakedQuad[])obj2;
            }

            boolean[] borderFlags3 = renderEnv.getBorderFlags3();

            for (int i = 0; i < 4; i++) {
               borderFlags3[i] = isNeighbourMatching(connectedProperties, blockAccess, blockState, sideDirections[i].offset(blockPos), n2, sprite, n3);
            }

            if (borderFlags[0]) {
               listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[9], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
            }

            if (borderFlags[1]) {
               listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[7], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
            }

            if (borderFlags[2]) {
               listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[1], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
            }

            if (borderFlags[3]) {
               listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[15], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
            }

            if (borderFlags2[0] && (borderFlags3[1] || borderFlags3[2]) && !borderFlags[1] && !borderFlags[2]) {
               listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[0], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
            }

            if (borderFlags2[1] && (borderFlags3[0] || borderFlags3[2]) && !borderFlags[0] && !borderFlags[2]) {
               listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[2], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
            }

            if (borderFlags2[2] && (borderFlags3[1] || borderFlags3[3]) && !borderFlags[1] && !borderFlags[3]) {
               listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[14], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
            }

            if (borderFlags2[3] && (borderFlags3[0] || borderFlags3[3]) && !borderFlags[0] && !borderFlags[3]) {
               listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[16], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
            }

            Object obj2 = null;
            return (BakedQuad[])obj2;
         }

         listQuadsOverlay.addQuad(getQuadFull(connectedProperties.tileIcons[8], bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
         obj = null;
      } finally {
         if (listQuadsOverlay.size() > 0) {
            renderEnv.setOverlaysRendered(true);
         }
      }

      return (BakedQuad[])obj;
   }

   public static BakedQuad[] getConnectedTextureOverlayFixed(ConnectedProperties connectedProperties, BakedQuad bakedQuad, RenderEnv renderEnv) {
      if (!bakedQuad.isFullQuad()) {
         return null;
      }

      ListQuadsOverlay listQuadsOverlay = renderEnv.getListQuadsOverlay(connectedProperties.layer);

      Object obj;
      try {
         TextureAtlasSprite connectedTextureFixed = getConnectedTextureFixed(connectedProperties);
         if (connectedTextureFixed != null) {
            listQuadsOverlay.addQuad(getQuadFull(connectedTextureFixed, bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
         }

         obj = null;
      } finally {
         if (listQuadsOverlay.size() > 0) {
            renderEnv.setOverlaysRendered(true);
         }
      }

      return (BakedQuad[])obj;
   }

   public static BakedQuad[] getConnectedTextureOverlayRandom(
      ConnectedProperties connectedProperties, IBlockAccess blockAccess, BlockStateBase blockStateBase, BlockPos blockPos, int n, BakedQuad bakedQuad, RenderEnv renderEnv
   ) {
      if (!bakedQuad.isFullQuad()) {
         return null;
      }

      ListQuadsOverlay listQuadsOverlay = renderEnv.getListQuadsOverlay(connectedProperties.layer);

      Object obj;
      try {
         TextureAtlasSprite connectedTextureRandom = getConnectedTextureRandom(connectedProperties, blockAccess, blockStateBase, blockPos, n);
         if (connectedTextureRandom != null) {
            listQuadsOverlay.addQuad(getQuadFull(connectedTextureRandom, bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
         }

         obj = null;
      } finally {
         if (listQuadsOverlay.size() > 0) {
            renderEnv.setOverlaysRendered(true);
         }
      }

      return (BakedQuad[])obj;
   }

   public static BakedQuad[] getConnectedTextureOverlayRepeat(ConnectedProperties connectedProperties, BlockPos blockPos, int n, BakedQuad bakedQuad, RenderEnv renderEnv) {
      if (!bakedQuad.isFullQuad()) {
         return null;
      }

      ListQuadsOverlay listQuadsOverlay = renderEnv.getListQuadsOverlay(connectedProperties.layer);

      Object obj;
      try {
         TextureAtlasSprite connectedTextureRepeat = getConnectedTextureRepeat(connectedProperties, blockPos, n);
         if (connectedTextureRepeat != null) {
            listQuadsOverlay.addQuad(getQuadFull(connectedTextureRepeat, bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
         }

         obj = null;
      } finally {
         if (listQuadsOverlay.size() > 0) {
            renderEnv.setOverlaysRendered(true);
         }
      }

      return (BakedQuad[])obj;
   }

   public static BakedQuad[] getConnectedTextureOverlayCtm(
      ConnectedProperties connectedProperties, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int n, int n2, BakedQuad bakedQuad, int n3, RenderEnv renderEnv
   ) {
      if (!bakedQuad.isFullQuad()) {
         return null;
      }

      ListQuadsOverlay listQuadsOverlay = renderEnv.getListQuadsOverlay(connectedProperties.layer);

      Object obj;
      try {
         TextureAtlasSprite connectedTextureCtm = getConnectedTextureCtm(connectedProperties, blockAccess, blockState, blockPos, n, n2, bakedQuad.getSprite(), n3, renderEnv);
         if (connectedTextureCtm != null) {
            listQuadsOverlay.addQuad(getQuadFull(connectedTextureCtm, bakedQuad, connectedProperties.tintIndex), connectedProperties.tintBlockState);
         }

         obj = null;
      } finally {
         if (listQuadsOverlay.size() > 0) {
            renderEnv.setOverlaysRendered(true);
         }
      }

      return (BakedQuad[])obj;
   }

   public static BlockDir[] getSideDirections(int n, int n2) {
      switch (n) {
         case 0:
            return SIDES_Y_NEG_DOWN;
         case 1:
            return SIDES_Y_POS_UP;
         case 2:
            if (n2 == 1) {
               return SIDES_Z_NEG_NORTH_Z_AXIS;
            }

            return SIDES_Z_NEG_NORTH;
         case 3:
            return SIDES_Z_POS_SOUTH;
         case 4:
            return SIDES_X_NEG_WEST;
         case 5:
            if (n2 == 2) {
               return SIDES_X_POS_EAST_X_AXIS;
            }

            return SIDES_X_POS_EAST;
         default:
            throw new IllegalArgumentException("Unknown side: " + n);
      }
   }

   public static BlockDir[] getEdgeDirections(int n, int n2) {
      switch (n) {
         case 0:
            return EDGES_Y_NEG_DOWN;
         case 1:
            return EDGES_Y_POS_UP;
         case 2:
            if (n2 == 1) {
               return EDGES_Z_NEG_NORTH_Z_AXIS;
            }

            return EDGES_Z_NEG_NORTH;
         case 3:
            return EDGES_Z_POS_SOUTH;
         case 4:
            return EDGES_X_NEG_WEST;
         case 5:
            if (n2 == 2) {
               return EDGES_X_POS_EAST_X_AXIS;
            }

            return EDGES_X_POS_EAST;
         default:
            throw new IllegalArgumentException("Unknown side: " + n);
      }
   }

   public static Map[][] getSpriteQuadCompactMaps() {
      return spriteQuadCompactMaps;
   }

   public static int getConnectedTextureCtmIndex(
      ConnectedProperties connectedProperties, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int n, int n2, TextureAtlasSprite textureAtlasSprite, int n3, RenderEnv renderEnv
   ) {
      boolean[] borderFlags = renderEnv.getBorderFlags();
      switch (n2) {
         case 0:
            borderFlags[0] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west(), n2, textureAtlasSprite, n3);
            borderFlags[1] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east(), n2, textureAtlasSprite, n3);
            borderFlags[2] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.north(), n2, textureAtlasSprite, n3);
            borderFlags[3] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.south(), n2, textureAtlasSprite, n3);
            if (connectedProperties.innerSeams) {
               BlockPos down = blockPos.down();
               borderFlags[0] = borderFlags[0] && !isNeighbour(connectedProperties, blockAccess, blockState, down.west(), n2, textureAtlasSprite, n3);
               borderFlags[1] = borderFlags[1] && !isNeighbour(connectedProperties, blockAccess, blockState, down.east(), n2, textureAtlasSprite, n3);
               borderFlags[2] = borderFlags[2] && !isNeighbour(connectedProperties, blockAccess, blockState, down.north(), n2, textureAtlasSprite, n3);
               borderFlags[3] = borderFlags[3] && !isNeighbour(connectedProperties, blockAccess, blockState, down.south(), n2, textureAtlasSprite, n3);
            }
            break;
         case 1:
            borderFlags[0] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west(), n2, textureAtlasSprite, n3);
            borderFlags[1] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east(), n2, textureAtlasSprite, n3);
            borderFlags[2] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.south(), n2, textureAtlasSprite, n3);
            borderFlags[3] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.north(), n2, textureAtlasSprite, n3);
            if (connectedProperties.innerSeams) {
               BlockPos up = blockPos.up();
               borderFlags[0] = borderFlags[0] && !isNeighbour(connectedProperties, blockAccess, blockState, up.west(), n2, textureAtlasSprite, n3);
               borderFlags[1] = borderFlags[1] && !isNeighbour(connectedProperties, blockAccess, blockState, up.east(), n2, textureAtlasSprite, n3);
               borderFlags[2] = borderFlags[2] && !isNeighbour(connectedProperties, blockAccess, blockState, up.south(), n2, textureAtlasSprite, n3);
               borderFlags[3] = borderFlags[3] && !isNeighbour(connectedProperties, blockAccess, blockState, up.north(), n2, textureAtlasSprite, n3);
            }
            break;
         case 2:
            borderFlags[0] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east(), n2, textureAtlasSprite, n3);
            borderFlags[1] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west(), n2, textureAtlasSprite, n3);
            borderFlags[2] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down(), n2, textureAtlasSprite, n3);
            borderFlags[3] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up(), n2, textureAtlasSprite, n3);
            if (connectedProperties.innerSeams) {
               BlockPos north = blockPos.north();
               borderFlags[0] = borderFlags[0] && !isNeighbour(connectedProperties, blockAccess, blockState, north.east(), n2, textureAtlasSprite, n3);
               borderFlags[1] = borderFlags[1] && !isNeighbour(connectedProperties, blockAccess, blockState, north.west(), n2, textureAtlasSprite, n3);
               borderFlags[2] = borderFlags[2] && !isNeighbour(connectedProperties, blockAccess, blockState, north.down(), n2, textureAtlasSprite, n3);
               borderFlags[3] = borderFlags[3] && !isNeighbour(connectedProperties, blockAccess, blockState, north.up(), n2, textureAtlasSprite, n3);
            }

            if (n == 1) {
               switchValues(0, 1, borderFlags);
               switchValues(2, 3, borderFlags);
            }
            break;
         case 3:
            borderFlags[0] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west(), n2, textureAtlasSprite, n3);
            borderFlags[1] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east(), n2, textureAtlasSprite, n3);
            borderFlags[2] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down(), n2, textureAtlasSprite, n3);
            borderFlags[3] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up(), n2, textureAtlasSprite, n3);
            if (connectedProperties.innerSeams) {
               BlockPos south = blockPos.south();
               borderFlags[0] = borderFlags[0] && !isNeighbour(connectedProperties, blockAccess, blockState, south.west(), n2, textureAtlasSprite, n3);
               borderFlags[1] = borderFlags[1] && !isNeighbour(connectedProperties, blockAccess, blockState, south.east(), n2, textureAtlasSprite, n3);
               borderFlags[2] = borderFlags[2] && !isNeighbour(connectedProperties, blockAccess, blockState, south.down(), n2, textureAtlasSprite, n3);
               borderFlags[3] = borderFlags[3] && !isNeighbour(connectedProperties, blockAccess, blockState, south.up(), n2, textureAtlasSprite, n3);
            }
            break;
         case 4:
            borderFlags[0] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.north(), n2, textureAtlasSprite, n3);
            borderFlags[1] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.south(), n2, textureAtlasSprite, n3);
            borderFlags[2] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down(), n2, textureAtlasSprite, n3);
            borderFlags[3] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up(), n2, textureAtlasSprite, n3);
            if (connectedProperties.innerSeams) {
               BlockPos west = blockPos.west();
               borderFlags[0] = borderFlags[0] && !isNeighbour(connectedProperties, blockAccess, blockState, west.north(), n2, textureAtlasSprite, n3);
               borderFlags[1] = borderFlags[1] && !isNeighbour(connectedProperties, blockAccess, blockState, west.south(), n2, textureAtlasSprite, n3);
               borderFlags[2] = borderFlags[2] && !isNeighbour(connectedProperties, blockAccess, blockState, west.down(), n2, textureAtlasSprite, n3);
               borderFlags[3] = borderFlags[3] && !isNeighbour(connectedProperties, blockAccess, blockState, west.up(), n2, textureAtlasSprite, n3);
            }
            break;
         case 5:
            borderFlags[0] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.south(), n2, textureAtlasSprite, n3);
            borderFlags[1] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.north(), n2, textureAtlasSprite, n3);
            borderFlags[2] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down(), n2, textureAtlasSprite, n3);
            borderFlags[3] = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up(), n2, textureAtlasSprite, n3);
            if (connectedProperties.innerSeams) {
               BlockPos east = blockPos.east();
               borderFlags[0] = borderFlags[0] && !isNeighbour(connectedProperties, blockAccess, blockState, east.south(), n2, textureAtlasSprite, n3);
               borderFlags[1] = borderFlags[1] && !isNeighbour(connectedProperties, blockAccess, blockState, east.north(), n2, textureAtlasSprite, n3);
               borderFlags[2] = borderFlags[2] && !isNeighbour(connectedProperties, blockAccess, blockState, east.down(), n2, textureAtlasSprite, n3);
               borderFlags[3] = borderFlags[3] && !isNeighbour(connectedProperties, blockAccess, blockState, east.up(), n2, textureAtlasSprite, n3);
            }

            if (n == 2) {
               switchValues(0, 1, borderFlags);
               switchValues(2, 3, borderFlags);
            }
      }

      byte b = 0;
      if (borderFlags[0] & !borderFlags[1] & !borderFlags[2] & !borderFlags[3]) {
         b = 3;
      } else if (!borderFlags[0] & borderFlags[1] & !borderFlags[2] & !borderFlags[3]) {
         b = 1;
      } else if (!borderFlags[0] & !borderFlags[1] & borderFlags[2] & !borderFlags[3]) {
         b = 12;
      } else if (!borderFlags[0] & !borderFlags[1] & !borderFlags[2] & borderFlags[3]) {
         b = 36;
      } else if (borderFlags[0] & borderFlags[1] & !borderFlags[2] & !borderFlags[3]) {
         b = 2;
      } else if (!borderFlags[0] & !borderFlags[1] & borderFlags[2] & borderFlags[3]) {
         b = 24;
      } else if (borderFlags[0] & !borderFlags[1] & borderFlags[2] & !borderFlags[3]) {
         b = 15;
      } else if (borderFlags[0] & !borderFlags[1] & !borderFlags[2] & borderFlags[3]) {
         b = 39;
      } else if (!borderFlags[0] & borderFlags[1] & borderFlags[2] & !borderFlags[3]) {
         b = 13;
      } else if (!borderFlags[0] & borderFlags[1] & !borderFlags[2] & borderFlags[3]) {
         b = 37;
      } else if (!borderFlags[0] & borderFlags[1] & borderFlags[2] & borderFlags[3]) {
         b = 25;
      } else if (borderFlags[0] & !borderFlags[1] & borderFlags[2] & borderFlags[3]) {
         b = 27;
      } else if (borderFlags[0] & borderFlags[1] & !borderFlags[2] & borderFlags[3]) {
         b = 38;
      } else if (borderFlags[0] & borderFlags[1] & borderFlags[2] & !borderFlags[3]) {
         b = 14;
      } else if (borderFlags[0] & borderFlags[1] & borderFlags[2] & borderFlags[3]) {
         b = 26;
      }

      if (b == 0) {
         return b;
      }

      if (!Config.isConnectedTexturesFancy()) {
         return b;
      }

      switch (n2) {
         case 0:
            borderFlags[0] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east().north(), n2, textureAtlasSprite, n3);
            borderFlags[1] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west().north(), n2, textureAtlasSprite, n3);
            borderFlags[2] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east().south(), n2, textureAtlasSprite, n3);
            borderFlags[3] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west().south(), n2, textureAtlasSprite, n3);
            if (connectedProperties.innerSeams) {
               BlockPos down = blockPos.down();
               borderFlags[0] = borderFlags[0] || isNeighbour(connectedProperties, blockAccess, blockState, down.east().north(), n2, textureAtlasSprite, n3);
               borderFlags[1] = borderFlags[1] || isNeighbour(connectedProperties, blockAccess, blockState, down.west().north(), n2, textureAtlasSprite, n3);
               borderFlags[2] = borderFlags[2] || isNeighbour(connectedProperties, blockAccess, blockState, down.east().south(), n2, textureAtlasSprite, n3);
               borderFlags[3] = borderFlags[3] || isNeighbour(connectedProperties, blockAccess, blockState, down.west().south(), n2, textureAtlasSprite, n3);
            }
            break;
         case 1:
            borderFlags[0] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east().south(), n2, textureAtlasSprite, n3);
            borderFlags[1] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west().south(), n2, textureAtlasSprite, n3);
            borderFlags[2] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east().north(), n2, textureAtlasSprite, n3);
            borderFlags[3] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west().north(), n2, textureAtlasSprite, n3);
            if (connectedProperties.innerSeams) {
               BlockPos up = blockPos.up();
               borderFlags[0] = borderFlags[0] || isNeighbour(connectedProperties, blockAccess, blockState, up.east().south(), n2, textureAtlasSprite, n3);
               borderFlags[1] = borderFlags[1] || isNeighbour(connectedProperties, blockAccess, blockState, up.west().south(), n2, textureAtlasSprite, n3);
               borderFlags[2] = borderFlags[2] || isNeighbour(connectedProperties, blockAccess, blockState, up.east().north(), n2, textureAtlasSprite, n3);
               borderFlags[3] = borderFlags[3] || isNeighbour(connectedProperties, blockAccess, blockState, up.west().north(), n2, textureAtlasSprite, n3);
            }
            break;
         case 2:
            borderFlags[0] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west().down(), n2, textureAtlasSprite, n3);
            borderFlags[1] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east().down(), n2, textureAtlasSprite, n3);
            borderFlags[2] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west().up(), n2, textureAtlasSprite, n3);
            borderFlags[3] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east().up(), n2, textureAtlasSprite, n3);
            if (connectedProperties.innerSeams) {
               BlockPos north = blockPos.north();
               borderFlags[0] = borderFlags[0] || isNeighbour(connectedProperties, blockAccess, blockState, north.west().down(), n2, textureAtlasSprite, n3);
               borderFlags[1] = borderFlags[1] || isNeighbour(connectedProperties, blockAccess, blockState, north.east().down(), n2, textureAtlasSprite, n3);
               borderFlags[2] = borderFlags[2] || isNeighbour(connectedProperties, blockAccess, blockState, north.west().up(), n2, textureAtlasSprite, n3);
               borderFlags[3] = borderFlags[3] || isNeighbour(connectedProperties, blockAccess, blockState, north.east().up(), n2, textureAtlasSprite, n3);
            }

            if (n == 1) {
               switchValues(0, 3, borderFlags);
               switchValues(1, 2, borderFlags);
            }
            break;
         case 3:
            borderFlags[0] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east().down(), n2, textureAtlasSprite, n3);
            borderFlags[1] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west().down(), n2, textureAtlasSprite, n3);
            borderFlags[2] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east().up(), n2, textureAtlasSprite, n3);
            borderFlags[3] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west().up(), n2, textureAtlasSprite, n3);
            if (connectedProperties.innerSeams) {
               BlockPos south = blockPos.south();
               borderFlags[0] = borderFlags[0] || isNeighbour(connectedProperties, blockAccess, blockState, south.east().down(), n2, textureAtlasSprite, n3);
               borderFlags[1] = borderFlags[1] || isNeighbour(connectedProperties, blockAccess, blockState, south.west().down(), n2, textureAtlasSprite, n3);
               borderFlags[2] = borderFlags[2] || isNeighbour(connectedProperties, blockAccess, blockState, south.east().up(), n2, textureAtlasSprite, n3);
               borderFlags[3] = borderFlags[3] || isNeighbour(connectedProperties, blockAccess, blockState, south.west().up(), n2, textureAtlasSprite, n3);
            }
            break;
         case 4:
            borderFlags[0] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down().south(), n2, textureAtlasSprite, n3);
            borderFlags[1] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down().north(), n2, textureAtlasSprite, n3);
            borderFlags[2] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up().south(), n2, textureAtlasSprite, n3);
            borderFlags[3] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up().north(), n2, textureAtlasSprite, n3);
            if (connectedProperties.innerSeams) {
               BlockPos west = blockPos.west();
               borderFlags[0] = borderFlags[0] || isNeighbour(connectedProperties, blockAccess, blockState, west.down().south(), n2, textureAtlasSprite, n3);
               borderFlags[1] = borderFlags[1] || isNeighbour(connectedProperties, blockAccess, blockState, west.down().north(), n2, textureAtlasSprite, n3);
               borderFlags[2] = borderFlags[2] || isNeighbour(connectedProperties, blockAccess, blockState, west.up().south(), n2, textureAtlasSprite, n3);
               borderFlags[3] = borderFlags[3] || isNeighbour(connectedProperties, blockAccess, blockState, west.up().north(), n2, textureAtlasSprite, n3);
            }
            break;
         case 5:
            borderFlags[0] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down().north(), n2, textureAtlasSprite, n3);
            borderFlags[1] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down().south(), n2, textureAtlasSprite, n3);
            borderFlags[2] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up().north(), n2, textureAtlasSprite, n3);
            borderFlags[3] = !isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up().south(), n2, textureAtlasSprite, n3);
            if (connectedProperties.innerSeams) {
               BlockPos east = blockPos.east();
               borderFlags[0] = borderFlags[0] || isNeighbour(connectedProperties, blockAccess, blockState, east.down().north(), n2, textureAtlasSprite, n3);
               borderFlags[1] = borderFlags[1] || isNeighbour(connectedProperties, blockAccess, blockState, east.down().south(), n2, textureAtlasSprite, n3);
               borderFlags[2] = borderFlags[2] || isNeighbour(connectedProperties, blockAccess, blockState, east.up().north(), n2, textureAtlasSprite, n3);
               borderFlags[3] = borderFlags[3] || isNeighbour(connectedProperties, blockAccess, blockState, east.up().south(), n2, textureAtlasSprite, n3);
            }

            if (n == 2) {
               switchValues(0, 3, borderFlags);
               switchValues(1, 2, borderFlags);
            }
      }

      if (b == 13 && borderFlags[0]) {
         b = 4;
      } else if (b == 15 && borderFlags[1]) {
         b = 5;
      } else if (b == 37 && borderFlags[2]) {
         b = 16;
      } else if (b == 39 && borderFlags[3]) {
         b = 17;
      } else if (b == 14 && borderFlags[0] && borderFlags[1]) {
         b = 7;
      } else if (b == 25 && borderFlags[0] && borderFlags[2]) {
         b = 6;
      } else if (b == 27 && borderFlags[3] && borderFlags[1]) {
         b = 19;
      } else if (b == 38 && borderFlags[3] && borderFlags[2]) {
         b = 18;
      } else if (b == 14 && !borderFlags[0] && borderFlags[1]) {
         b = 31;
      } else if (b == 25 && borderFlags[0] && !borderFlags[2]) {
         b = 30;
      } else if (b == 27 && !borderFlags[3] && borderFlags[1]) {
         b = 41;
      } else if (b == 38 && borderFlags[3] && !borderFlags[2]) {
         b = 40;
      } else if (b == 14 && borderFlags[0] && !borderFlags[1]) {
         b = 29;
      } else if (b == 25 && !borderFlags[0] && borderFlags[2]) {
         b = 28;
      } else if (b == 27 && borderFlags[3] && !borderFlags[1]) {
         b = 43;
      } else if (b == 38 && !borderFlags[3] && borderFlags[2]) {
         b = 42;
      } else if (b == 26 && borderFlags[0] && borderFlags[1] && borderFlags[2] && borderFlags[3]) {
         b = 46;
      } else if (b == 26 && !borderFlags[0] && borderFlags[1] && borderFlags[2] && borderFlags[3]) {
         b = 9;
      } else if (b == 26 && borderFlags[0] && !borderFlags[1] && borderFlags[2] && borderFlags[3]) {
         b = 21;
      } else if (b == 26 && borderFlags[0] && borderFlags[1] && !borderFlags[2] && borderFlags[3]) {
         b = 8;
      } else if (b == 26 && borderFlags[0] && borderFlags[1] && borderFlags[2] && !borderFlags[3]) {
         b = 20;
      } else if (b == 26 && borderFlags[0] && borderFlags[1] && !borderFlags[2] && !borderFlags[3]) {
         b = 11;
      } else if (b == 26 && !borderFlags[0] && !borderFlags[1] && borderFlags[2] && borderFlags[3]) {
         b = 22;
      } else if (b == 26 && !borderFlags[0] && borderFlags[1] && !borderFlags[2] && borderFlags[3]) {
         b = 23;
      } else if (b == 26 && borderFlags[0] && !borderFlags[1] && borderFlags[2] && !borderFlags[3]) {
         b = 10;
      } else if (b == 26 && borderFlags[0] && !borderFlags[1] && !borderFlags[2] && borderFlags[3]) {
         b = 34;
      } else if (b == 26 && !borderFlags[0] && borderFlags[1] && borderFlags[2] && !borderFlags[3]) {
         b = 35;
      } else if (b == 26 && borderFlags[0] && !borderFlags[1] && !borderFlags[2] && !borderFlags[3]) {
         b = 32;
      } else if (b == 26 && !borderFlags[0] && borderFlags[1] && !borderFlags[2] && !borderFlags[3]) {
         b = 33;
      } else if (b == 26 && !borderFlags[0] && !borderFlags[1] && borderFlags[2] && !borderFlags[3]) {
         b = 44;
      } else if (b == 26 && !borderFlags[0] && !borderFlags[1] && !borderFlags[2] && borderFlags[3]) {
         b = 45;
      }

      return b;
   }

   public static void switchValues(int n, int n2, boolean[] flags) {
      boolean flag = flags[n];
      flags[n] = flags[n2];
      flags[n2] = flag;
   }

   public static boolean isNeighbourOverlay(
      ConnectedProperties connectedProperties, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int n, TextureAtlasSprite textureAtlasSprite, int n2
   ) {
      IBlockState blockState2 = blockAccess.getBlockState(blockPos);
      if (!isFullCubeModel(blockState2)) {
         return false;
      }

      if (connectedProperties.connectBlocks != null) {
         BlockStateBase blockStateBase = (BlockStateBase)blockState2;
         if (!Matches.block(blockStateBase.getBlockId(), blockStateBase.getMetadata(), connectedProperties.connectBlocks)) {
            return false;
         }
      }

      if (connectedProperties.connectTileIcons != null) {
         TextureAtlasSprite neighbourIcon = getNeighbourIcon(blockAccess, blockState, blockPos, blockState2, n);
         if (!Config.isSameOne(neighbourIcon, connectedProperties.connectTileIcons)) {
            return false;
         }
      }

      IBlockState blockState3 = blockAccess.getBlockState(blockPos.offset(getFacing(n)));
      return blockState3.getBlock().isOpaqueCube()
         ? false
         : (n == 1 && blockState3.getBlock() == Blocks.snow_layer ? false : !isNeighbour(connectedProperties, blockAccess, blockState, blockPos, blockState2, n, textureAtlasSprite, n2));
   }

   public static boolean isFullCubeModel(IBlockState blockState) {
      if (blockState.getBlock().isFullCube()) {
         return true;
      }

      Block block = blockState.getBlock();
      return block instanceof BlockGlass ? true : block instanceof BlockStainedGlass;
   }

   public static boolean isNeighbourMatching(
      ConnectedProperties connectedProperties, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int n, TextureAtlasSprite textureAtlasSprite, int n2
   ) {
      IBlockState blockState2 = blockAccess.getBlockState(blockPos);
      if (blockState2 == AIR_DEFAULT_STATE) {
         return false;
      }

      if (connectedProperties.matchBlocks != null && blockState2 instanceof BlockStateBase) {
         BlockStateBase blockStateBase = (BlockStateBase)blockState2;
         if (!connectedProperties.matchesBlock(blockStateBase.getBlockId(), blockStateBase.getMetadata())) {
            return false;
         }
      }

      if (connectedProperties.matchTileIcons != null) {
         TextureAtlasSprite neighbourIcon = getNeighbourIcon(blockAccess, blockState, blockPos, blockState2, n);
         if (neighbourIcon != textureAtlasSprite) {
            return false;
         }
      }

      IBlockState blockState3 = blockAccess.getBlockState(blockPos.offset(getFacing(n)));
      return blockState3.getBlock().isOpaqueCube() ? false : n != 1 || blockState3.getBlock() != Blocks.snow_layer;
   }

   public static boolean isNeighbour(ConnectedProperties connectedProperties, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int n, TextureAtlasSprite textureAtlasSprite, int n2) {
      IBlockState blockState2 = blockAccess.getBlockState(blockPos);
      return isNeighbour(connectedProperties, blockAccess, blockState, blockPos, blockState2, n, textureAtlasSprite, n2);
   }

   public static boolean isNeighbour(
      ConnectedProperties connectedProperties, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, IBlockState blockState2, int n, TextureAtlasSprite textureAtlasSprite, int n2
   ) {
      if (blockState == blockState2) {
         return true;
      }

      if (connectedProperties.connect == 2) {
         if (blockState2 == null) {
            return false;
         }

         if (blockState2 == AIR_DEFAULT_STATE) {
            return false;
         }

         TextureAtlasSprite neighbourIcon = getNeighbourIcon(blockAccess, blockState, blockPos, blockState2, n);
         return neighbourIcon == textureAtlasSprite;
      } else {
         if (connectedProperties.connect == 3) {
            return blockState2 == null ? false : (blockState2 == AIR_DEFAULT_STATE ? false : blockState2.getBlock().getMaterial() == blockState.getBlock().getMaterial());
         }

         if (!(blockState2 instanceof BlockStateBase)) {
            return false;
         }

         BlockStateBase blockStateBase = (BlockStateBase)blockState2;
         Block block = blockStateBase.getBlock();
         int metadata = blockStateBase.getMetadata();
         return block == blockState.getBlock() && metadata == n2;
      }
   }

   public static TextureAtlasSprite getNeighbourIcon(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, IBlockState blockState2, int n) {
      blockState2 = blockState2.getBlock().getActualState(blockState2, blockAccess, blockPos);
      IBakedModel modelForState = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelForState(blockState2);
      if (modelForState == null) {
         return null;
      }

      if (Reflector.ForgeBlock_getExtendedState.exists()) {
         blockState2 = (IBlockState)Reflector.call(blockState2.getBlock(), Reflector.ForgeBlock_getExtendedState, blockState2, blockAccess, blockPos);
      }

      EnumFacing facing = getFacing(n);
      List faceQuads = modelForState.getFaceQuads(facing);
      if (faceQuads == null) {
         return null;
      }

      if (Config.isBetterGrass()) {
         faceQuads = BetterGrass.getFaceQuads(blockAccess, blockState2, blockPos, facing, faceQuads);
      }

      if (faceQuads.size() > 0) {
         BakedQuad bakedQuad = (BakedQuad)faceQuads.get(0);
         return bakedQuad.getSprite();
      }

      List generalQuads = modelForState.getGeneralQuads();
      if (generalQuads == null) {
         return null;
      }

      for (int i = 0; i < generalQuads.size(); i++) {
         BakedQuad bakedQuad = (BakedQuad)generalQuads.get(i);
         if (bakedQuad.getFace() == facing) {
            return bakedQuad.getSprite();
         }
      }

      return null;
   }

   public static TextureAtlasSprite getConnectedTextureHorizontal(
      ConnectedProperties connectedProperties, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int n, int n2, TextureAtlasSprite textureAtlasSprite, int n3
   ) {
      boolean flag;
      boolean flag2;
      flag = false;
      flag2 = false;
      label46:
      switch (n) {
         case 0:
            switch (n2) {
               case 0:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east(), n2, textureAtlasSprite, n3);
                  break label46;
               case 1:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east(), n2, textureAtlasSprite, n3);
                  break label46;
               case 2:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west(), n2, textureAtlasSprite, n3);
                  break label46;
               case 3:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east(), n2, textureAtlasSprite, n3);
                  break label46;
               case 4:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.north(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.south(), n2, textureAtlasSprite, n3);
                  break label46;
               case 5:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.south(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.north(), n2, textureAtlasSprite, n3);
               default:
                  break label46;
            }
         case 1:
            switch (n2) {
               case 0:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west(), n2, textureAtlasSprite, n3);
                  break label46;
               case 1:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east(), n2, textureAtlasSprite, n3);
                  break label46;
               case 2:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east(), n2, textureAtlasSprite, n3);
                  break label46;
               case 3:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east(), n2, textureAtlasSprite, n3);
                  break label46;
               case 4:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up(), n2, textureAtlasSprite, n3);
                  break label46;
               case 5:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down(), n2, textureAtlasSprite, n3);
               default:
                  break label46;
            }
         case 2:
            switch (n2) {
               case 0:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.south(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.north(), n2, textureAtlasSprite, n3);
                  break;
               case 1:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.north(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.south(), n2, textureAtlasSprite, n3);
                  break;
               case 2:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up(), n2, textureAtlasSprite, n3);
                  break;
               case 3:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down(), n2, textureAtlasSprite, n3);
                  break;
               case 4:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.north(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.south(), n2, textureAtlasSprite, n3);
                  break;
               case 5:
                  flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.north(), n2, textureAtlasSprite, n3);
                  flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.south(), n2, textureAtlasSprite, n3);
            }
      }

      byte b = 3;
      if (flag) {
         if (flag2) {
            b = 1;
         } else {
            b = 2;
         }
      } else if (flag2) {
         b = 0;
      } else {
         b = 3;
      }

      return connectedProperties.tileIcons[b];
   }

   public static TextureAtlasSprite getConnectedTextureVertical(
      ConnectedProperties connectedProperties, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int n, int n2, TextureAtlasSprite textureAtlasSprite, int n3
   ) {
      boolean flag = false;
      boolean flag2 = false;
      switch (n) {
         case 0:
            if (n2 == 1) {
               flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.south(), n2, textureAtlasSprite, n3);
               flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.north(), n2, textureAtlasSprite, n3);
            } else if (n2 == 0) {
               flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.north(), n2, textureAtlasSprite, n3);
               flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.south(), n2, textureAtlasSprite, n3);
            } else {
               flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down(), n2, textureAtlasSprite, n3);
               flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up(), n2, textureAtlasSprite, n3);
            }
            break;
         case 1:
            if (n2 == 3) {
               flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down(), n2, textureAtlasSprite, n3);
               flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up(), n2, textureAtlasSprite, n3);
            } else if (n2 == 2) {
               flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up(), n2, textureAtlasSprite, n3);
               flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down(), n2, textureAtlasSprite, n3);
            } else {
               flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.south(), n2, textureAtlasSprite, n3);
               flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.north(), n2, textureAtlasSprite, n3);
            }
            break;
         case 2:
            if (n2 == 5) {
               flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up(), n2, textureAtlasSprite, n3);
               flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down(), n2, textureAtlasSprite, n3);
            } else if (n2 == 4) {
               flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.down(), n2, textureAtlasSprite, n3);
               flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up(), n2, textureAtlasSprite, n3);
            } else {
               flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.west(), n2, textureAtlasSprite, n3);
               flag2 = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east(), n2, textureAtlasSprite, n3);
            }
      }

      byte b = 3;
      if (flag) {
         if (flag2) {
            b = 1;
         } else {
            b = 2;
         }
      } else if (flag2) {
         b = 0;
      } else {
         b = 3;
      }

      return connectedProperties.tileIcons[b];
   }

   public static TextureAtlasSprite getConnectedTextureHorizontalVertical(
      ConnectedProperties connectedProperties, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int n, int n2, TextureAtlasSprite textureAtlasSprite, int n3
   ) {
      TextureAtlasSprite[] tileIcons = connectedProperties.tileIcons;
      TextureAtlasSprite connectedTextureHorizontal = getConnectedTextureHorizontal(connectedProperties, blockAccess, blockState, blockPos, n, n2, textureAtlasSprite, n3);
      if (connectedTextureHorizontal != null && connectedTextureHorizontal != textureAtlasSprite && connectedTextureHorizontal != tileIcons[3]) {
         return connectedTextureHorizontal;
      }

      TextureAtlasSprite connectedTextureVertical = getConnectedTextureVertical(connectedProperties, blockAccess, blockState, blockPos, n, n2, textureAtlasSprite, n3);
      return connectedTextureVertical == tileIcons[0] ? tileIcons[4] : (connectedTextureVertical == tileIcons[1] ? tileIcons[5] : (connectedTextureVertical == tileIcons[2] ? tileIcons[6] : connectedTextureVertical));
   }

   public static TextureAtlasSprite getConnectedTextureVerticalHorizontal(
      ConnectedProperties connectedProperties, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int n, int n2, TextureAtlasSprite textureAtlasSprite, int n3
   ) {
      TextureAtlasSprite[] tileIcons = connectedProperties.tileIcons;
      TextureAtlasSprite connectedTextureVertical = getConnectedTextureVertical(connectedProperties, blockAccess, blockState, blockPos, n, n2, textureAtlasSprite, n3);
      if (connectedTextureVertical != null && connectedTextureVertical != textureAtlasSprite && connectedTextureVertical != tileIcons[3]) {
         return connectedTextureVertical;
      }

      TextureAtlasSprite connectedTextureHorizontal = getConnectedTextureHorizontal(connectedProperties, blockAccess, blockState, blockPos, n, n2, textureAtlasSprite, n3);
      return connectedTextureHorizontal == tileIcons[0] ? tileIcons[4] : (connectedTextureHorizontal == tileIcons[1] ? tileIcons[5] : (connectedTextureHorizontal == tileIcons[2] ? tileIcons[6] : connectedTextureHorizontal));
   }

   public static TextureAtlasSprite getConnectedTextureTop(
      ConnectedProperties connectedProperties, IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, int n, int n2, TextureAtlasSprite textureAtlasSprite, int n3
   ) {
      boolean flag = false;
      switch (n) {
         case 0:
            if (n2 == 1 || n2 == 0) {
               return null;
            }

            flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.up(), n2, textureAtlasSprite, n3);
            break;
         case 1:
            if (n2 == 3 || n2 == 2) {
               return null;
            }

            flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.south(), n2, textureAtlasSprite, n3);
            break;
         case 2:
            if (n2 == 5 || n2 == 4) {
               return null;
            }

            flag = isNeighbour(connectedProperties, blockAccess, blockState, blockPos.east(), n2, textureAtlasSprite, n3);
      }

      return flag ? connectedProperties.tileIcons[0] : null;
   }

   public static void updateIcons(TextureMap textureMap) {
      blockProperties = (ConnectedProperties[][])null;
      tileProperties = (ConnectedProperties[][])null;
      spriteQuadMaps = null;
      spriteQuadCompactMaps = (Map[][])null;
      if (Config.isConnectedTextures()) {
         IResourcePack[] resourcePacks = Config.getResourcePacks();

         for (int i = resourcePacks.length - 1; i >= 0; i--) {
            IResourcePack resourcePack = resourcePacks[i];
            updateIcons(textureMap, resourcePack);
         }

         updateIcons(textureMap, Config.getDefaultResourcePack());
         ResourceLocation resourceLocation = new ResourceLocation("mcpatcher/ctm/default/empty");
         emptySprite = textureMap.registerSprite(resourceLocation);
         spriteQuadMaps = new Map[textureMap.getCountRegisteredSprites() + 1];
         spriteQuadFullMaps = new Map[textureMap.getCountRegisteredSprites() + 1];
         spriteQuadCompactMaps = new Map[textureMap.getCountRegisteredSprites() + 1][];
         if (blockProperties.length <= 0) {
            blockProperties = (ConnectedProperties[][])null;
         }

         if (tileProperties.length <= 0) {
            tileProperties = (ConnectedProperties[][])null;
         }
      }
   }

   public static void updateIconEmpty(TextureMap textureMap) {
   }

   public static void updateIcons(TextureMap textureMap, IResourcePack resourcePack) {
      String[] collectFiles = ResUtils.collectFiles(resourcePack, "mcpatcher/ctm/", ".properties", getDefaultCtmPaths());
      Arrays.sort(collectFiles);
      List makePropertyList2 = makePropertyList(tileProperties);
      List makePropertyList3 = makePropertyList(blockProperties);

      for (int i = 0; i < collectFiles.length; i++) {
         String str = collectFiles[i];
         Config.dbg("ConnectedTextures: " + str);

         try {
            ResourceLocation resourceLocation = new ResourceLocation(str);
            InputStream inputStream = resourcePack.getInputStream(resourceLocation);
            if (inputStream == null) {
               Config.warn("ConnectedTextures file not found: " + str);
            } else {
               PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
               propertiesOrdered.load(inputStream);
               inputStream.close();
               ConnectedProperties connectedProperties = new ConnectedProperties(propertiesOrdered, str);
               if (connectedProperties.isValid(str)) {
                  connectedProperties.updateIcons(textureMap);
                  addToTileList(connectedProperties, makePropertyList2);
                  addToBlockList(connectedProperties, makePropertyList3);
               }
            }
         } catch (FileNotFoundException e) {
            Config.warn("ConnectedTextures file not found: " + str);
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

      blockProperties = propertyListToArray(makePropertyList3);
      tileProperties = propertyListToArray(makePropertyList2);
      multipass = detectMultipass();
      Config.dbg("Multipass connected textures: " + multipass);
   }

   public static List makePropertyList(ConnectedProperties[][] connectedPropertieseses) {
      ArrayList list = new ArrayList();
      if (connectedPropertieseses != null) {
         for (int i = 0; i < connectedPropertieseses.length; i++) {
            ConnectedProperties[] connectedPropertieses = connectedPropertieseses[i];
            ArrayList list2 = null;
            if (connectedPropertieses != null) {
               list2 = new ArrayList<>(Arrays.asList(connectedPropertieses));
            }

            list.add(list2);
         }
      }

      return list;
   }

   public static boolean detectMultipass() {
      ArrayList list = new ArrayList();

      for (int i = 0; i < tileProperties.length; i++) {
         ConnectedProperties[] connectedPropertieses = tileProperties[i];
         if (connectedPropertieses != null) {
            list.addAll(Arrays.asList(connectedPropertieses));
         }
      }

      for (int i = 0; i < blockProperties.length; i++) {
         ConnectedProperties[] connectedPropertieses = blockProperties[i];
         if (connectedPropertieses != null) {
            list.addAll(Arrays.asList(connectedPropertieses));
         }
      }

      ConnectedProperties[] toArray = (ConnectedProperties[])list.toArray(new ConnectedProperties[list.size()]);
      HashSet set = new HashSet();
      HashSet set2 = new HashSet();

      for (int i = 0; i < toArray.length; i++) {
         ConnectedProperties connectedProperties = toArray[i];
         if (connectedProperties.matchTileIcons != null) {
            set.addAll(Arrays.asList(connectedProperties.matchTileIcons));
         }

         if (connectedProperties.tileIcons != null) {
            set2.addAll(Arrays.asList(connectedProperties.tileIcons));
         }
      }

      set.retainAll(set2);
      return !set.isEmpty();
   }

   public static ConnectedProperties[][] propertyListToArray(List list) {
      ConnectedProperties[][] connectedPropertieseses = new ConnectedProperties[list.size()][];

      for (int i = 0; i < list.size(); i++) {
         List list2 = (List)list.get(i);
         if (list2 != null) {
            ConnectedProperties[] toArray = (ConnectedProperties[])list2.toArray(new ConnectedProperties[list2.size()]);
            connectedPropertieseses[i] = toArray;
         }
      }

      return connectedPropertieseses;
   }

   public static void addToTileList(ConnectedProperties connectedProperties, List list) {
      if (connectedProperties.matchTileIcons != null) {
         for (int i = 0; i < connectedProperties.matchTileIcons.length; i++) {
            TextureAtlasSprite textureAtlasSprite = connectedProperties.matchTileIcons[i];
            if (!(textureAtlasSprite instanceof TextureAtlasSprite)) {
               Config.warn("TextureAtlasSprite is not TextureAtlasSprite: " + textureAtlasSprite + ", name: " + textureAtlasSprite.getIconName());
            } else {
               int indexInMap = textureAtlasSprite.getIndexInMap();
               if (indexInMap < 0) {
                  Config.warn("Invalid tile ID: " + indexInMap + ", icon: " + textureAtlasSprite.getIconName());
               } else {
                  addToList(connectedProperties, list, indexInMap);
               }
            }
         }
      }
   }

   public static void addToBlockList(ConnectedProperties connectedProperties, List list) {
      if (connectedProperties.matchBlocks != null) {
         for (int i = 0; i < connectedProperties.matchBlocks.length; i++) {
            int blockId = connectedProperties.matchBlocks[i].getBlockId();
            if (blockId < 0) {
               Config.warn("Invalid block ID: " + blockId);
            } else {
               addToList(connectedProperties, list, blockId);
            }
         }
      }
   }

   public static void addToList(ConnectedProperties connectedProperties, List list, int n) {
      while (n >= list.size()) {
         list.add(null);
      }

      List list2 = (List)list.get(n);
      if (list2 == null) {
         list2 = new ArrayList();
         list.set(n, list2);
      }

      list2.add(connectedProperties);
   }

   public static String[] getDefaultCtmPaths() {
      ArrayList list = new ArrayList();
      String str = "mcpatcher/ctm/default/";
      if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass.png"))) {
         list.add(str + "glass.properties");
         list.add(str + "glasspane.properties");
      }

      if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/bookshelf.png"))) {
         list.add(str + "bookshelf.properties");
      }

      if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/sandstone_normal.png"))) {
         list.add(str + "sandstone.properties");
      }

      String[] strs = new String[]{
         "white", "orange", "magenta", "light_blue", "yellow", "lime", "pink", "gray", "silver", "cyan", "purple", "blue", "brown", "green", "red", "black"
      };

      for (int i = 0; i < strs.length; i++) {
         String str2 = strs[i];
         if (Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/glass_" + str2 + ".png"))) {
            list.add(str + i + "_glass_" + str2 + "/glass_" + str2 + ".properties");
            list.add(str + i + "_glass_" + str2 + "/glass_pane_" + str2 + ".properties");
         }
      }

      return (String[])list.toArray(new String[list.size()]);
   }
}
