package net.optifine;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.optifine.model.BlockModelUtils;
import net.optifine.util.PropertiesOrdered;

public class BetterGrass {
   public static boolean betterGrass = true;
   public static boolean betterMycelium = true;
   public static boolean betterPodzol = true;
   public static boolean betterGrassSnow = true;
   public static boolean betterMyceliumSnow = true;
   public static boolean betterPodzolSnow = true;
   public static boolean grassMultilayer = false;
   public static TextureAtlasSprite spriteGrass = null;
   public static TextureAtlasSprite spriteGrassSide = null;
   public static TextureAtlasSprite spriteMycelium = null;
   public static TextureAtlasSprite spritePodzol = null;
   public static TextureAtlasSprite spriteSnow = null;
   public static boolean spritesLoaded = false;
   public static IBakedModel modelCubeGrass = null;
   public static IBakedModel modelCubeMycelium = null;
   public static IBakedModel modelCubePodzol = null;
   public static IBakedModel modelCubeSnow = null;
   public static boolean modelsLoaded = false;
   public static final String TEXTURE_GRASS_DEFAULT = "blocks/grass_top";
   public static final String TEXTURE_GRASS_SIDE_DEFAULT = "blocks/grass_side";
   public static final String TEXTURE_MYCELIUM_DEFAULT = "blocks/mycelium_top";
   public static final String TEXTURE_PODZOL_DEFAULT = "blocks/dirt_podzol_top";
   public static final String TEXTURE_SNOW_DEFAULT = "blocks/snow";

   public static void updateIcons(TextureMap textureMap) {
      spritesLoaded = false;
      modelsLoaded = false;
      loadProperties(textureMap);
   }

   public static void update() {
      if (spritesLoaded) {
         modelCubeGrass = BlockModelUtils.makeModelCube(spriteGrass, 0);
         if (grassMultilayer) {
            IBakedModel makeModelCube = BlockModelUtils.makeModelCube(spriteGrassSide, -1);
            modelCubeGrass = BlockModelUtils.joinModelsCube(makeModelCube, modelCubeGrass);
         }

         modelCubeMycelium = BlockModelUtils.makeModelCube(spriteMycelium, -1);
         modelCubePodzol = BlockModelUtils.makeModelCube(spritePodzol, 0);
         modelCubeSnow = BlockModelUtils.makeModelCube(spriteSnow, -1);
         modelsLoaded = true;
      }
   }

   public static void loadProperties(TextureMap textureMap) {
      betterGrass = true;
      betterMycelium = true;
      betterPodzol = true;
      betterGrassSnow = true;
      betterMyceliumSnow = true;
      betterPodzolSnow = true;
      spriteGrass = textureMap.registerSprite(new ResourceLocation("blocks/grass_top"));
      spriteGrassSide = textureMap.registerSprite(new ResourceLocation("blocks/grass_side"));
      spriteMycelium = textureMap.registerSprite(new ResourceLocation("blocks/mycelium_top"));
      spritePodzol = textureMap.registerSprite(new ResourceLocation("blocks/dirt_podzol_top"));
      spriteSnow = textureMap.registerSprite(new ResourceLocation("blocks/snow"));
      spritesLoaded = true;
      String str = "optifine/bettergrass.properties";

      try {
         ResourceLocation resourceLocation = new ResourceLocation(str);
         if (!Config.hasResource(resourceLocation)) {
            return;
         }

         InputStream resourceStream = Config.getResourceStream(resourceLocation);
         if (resourceStream == null) {
            return;
         }

         boolean isFromDefaultResourcePack = Config.isFromDefaultResourcePack(resourceLocation);
         if (isFromDefaultResourcePack) {
            Config.dbg("BetterGrass: Parsing default configuration " + str);
         } else {
            Config.dbg("BetterGrass: Parsing configuration " + str);
         }

         PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
         propertiesOrdered.load(resourceStream);
         resourceStream.close();
         betterGrass = getBoolean(propertiesOrdered, "grass", true);
         betterMycelium = getBoolean(propertiesOrdered, "mycelium", true);
         betterPodzol = getBoolean(propertiesOrdered, "podzol", true);
         betterGrassSnow = getBoolean(propertiesOrdered, "grass.snow", true);
         betterMyceliumSnow = getBoolean(propertiesOrdered, "mycelium.snow", true);
         betterPodzolSnow = getBoolean(propertiesOrdered, "podzol.snow", true);
         grassMultilayer = getBoolean(propertiesOrdered, "grass.multilayer", false);
         spriteGrass = registerSprite(propertiesOrdered, "texture.grass", "blocks/grass_top", textureMap);
         spriteGrassSide = registerSprite(propertiesOrdered, "texture.grass_side", "blocks/grass_side", textureMap);
         spriteMycelium = registerSprite(propertiesOrdered, "texture.mycelium", "blocks/mycelium_top", textureMap);
         spritePodzol = registerSprite(propertiesOrdered, "texture.podzol", "blocks/dirt_podzol_top", textureMap);
         spriteSnow = registerSprite(propertiesOrdered, "texture.snow", "blocks/snow", textureMap);
      } catch (IOException e) {
         Config.warn("Error reading: " + str + ", " + e.getClass().getName() + ": " + e.getMessage());
      }
   }

   public static TextureAtlasSprite registerSprite(Properties properties, String str, String str2, TextureMap textureMap) {
      String property = properties.getProperty(str);
      if (property == null) {
         property = str2;
      }

      ResourceLocation resourceLocation = new ResourceLocation("textures/" + property + ".png");
      if (!Config.hasResource(resourceLocation)) {
         Config.warn("BetterGrass texture not found: " + resourceLocation);
         property = str2;
      }

      ResourceLocation resourceLocation2 = new ResourceLocation(property);
      return textureMap.registerSprite(resourceLocation2);
   }

   public static List getFaceQuads(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing enumFacing, List list) {
      if (enumFacing == EnumFacing.UP || enumFacing == EnumFacing.DOWN) {
         return list;
      }

      if (!modelsLoaded) {
         return list;
      }

      Block block = blockState.getBlock();
      return block instanceof BlockMycelium
         ? getFaceQuadsMycelium(blockAccess, blockState, blockPos, enumFacing, list)
         : (
            block instanceof BlockDirt
               ? getFaceQuadsDirt(blockAccess, blockState, blockPos, enumFacing, list)
               : (block instanceof BlockGrass ? getFaceQuadsGrass(blockAccess, blockState, blockPos, enumFacing, list) : list)
         );
   }

   public static List getFaceQuadsMycelium(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing enumFacing, List list) {
      Block block = blockAccess.getBlockState(blockPos.up()).getBlock();
      boolean flag = block == Blocks.snow || block == Blocks.snow_layer;
      if (Config.isBetterGrassFancy()) {
         if (flag) {
            if (betterMyceliumSnow && getBlockAt(blockPos, enumFacing, blockAccess) == Blocks.snow_layer) {
               return modelCubeSnow.getFaceQuads(enumFacing);
            }
         } else if (betterMycelium && getBlockAt(blockPos.down(), enumFacing, blockAccess) == Blocks.mycelium) {
            return modelCubeMycelium.getFaceQuads(enumFacing);
         }
      } else if (flag) {
         if (betterMyceliumSnow) {
            return modelCubeSnow.getFaceQuads(enumFacing);
         }
      } else if (betterMycelium) {
         return modelCubeMycelium.getFaceQuads(enumFacing);
      }

      return list;
   }

   public static List getFaceQuadsDirt(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing enumFacing, List list) {
      Block blockAt = getBlockAt(blockPos, EnumFacing.UP, blockAccess);
      if (blockState.getValue(BlockDirt.VARIANT) != DirtType.PODZOL) {
         return list;
      }

      boolean flag = blockAt == Blocks.snow || blockAt == Blocks.snow_layer;
      if (Config.isBetterGrassFancy()) {
         if (flag) {
            if (betterPodzolSnow && getBlockAt(blockPos, enumFacing, blockAccess) == Blocks.snow_layer) {
               return modelCubeSnow.getFaceQuads(enumFacing);
            }
         } else if (betterPodzol) {
            BlockPos offset = blockPos.down().offset(enumFacing);
            IBlockState blockState2 = blockAccess.getBlockState(offset);
            if (blockState2.getBlock() == Blocks.dirt && blockState2.getValue(BlockDirt.VARIANT) == DirtType.PODZOL) {
               return modelCubePodzol.getFaceQuads(enumFacing);
            }
         }
      } else if (flag) {
         if (betterPodzolSnow) {
            return modelCubeSnow.getFaceQuads(enumFacing);
         }
      } else if (betterPodzol) {
         return modelCubePodzol.getFaceQuads(enumFacing);
      }

      return list;
   }

   public static List getFaceQuadsGrass(IBlockAccess blockAccess, IBlockState blockState, BlockPos blockPos, EnumFacing enumFacing, List list) {
      Block block = blockAccess.getBlockState(blockPos.up()).getBlock();
      boolean flag = block == Blocks.snow || block == Blocks.snow_layer;
      if (Config.isBetterGrassFancy()) {
         if (flag) {
            if (betterGrassSnow && getBlockAt(blockPos, enumFacing, blockAccess) == Blocks.snow_layer) {
               return modelCubeSnow.getFaceQuads(enumFacing);
            }
         } else if (betterGrass && getBlockAt(blockPos.down(), enumFacing, blockAccess) == Blocks.grass) {
            return modelCubeGrass.getFaceQuads(enumFacing);
         }
      } else if (flag) {
         if (betterGrassSnow) {
            return modelCubeSnow.getFaceQuads(enumFacing);
         }
      } else if (betterGrass) {
         return modelCubeGrass.getFaceQuads(enumFacing);
      }

      return list;
   }

   public static Block getBlockAt(BlockPos blockPos, EnumFacing enumFacing, IBlockAccess blockAccess) {
      BlockPos offset = blockPos.offset(enumFacing);
      return blockAccess.getBlockState(offset).getBlock();
   }

   public static boolean getBoolean(Properties properties, String str, boolean flag) {
      String property = properties.getProperty(str);
      return property == null ? flag : Boolean.parseBoolean(property);
   }
}
