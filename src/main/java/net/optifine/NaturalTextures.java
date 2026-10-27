package net.optifine;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.optifine.util.TextureUtils;

public class NaturalTextures {
   public static NaturalProperties[] propertiesByIndex = new NaturalProperties[0];

   public static void update() {
      propertiesByIndex = new NaturalProperties[0];
      if (Config.isNaturalTextures()) {
         String str = "optifine/natural.properties";

         try {
            ResourceLocation resourceLocation = new ResourceLocation(str);
            if (!Config.hasResource(resourceLocation)) {
               Config.dbg("NaturalTextures: configuration \"" + str + "\" not found");
               return;
            }

            boolean isFromDefaultResourcePack = Config.isFromDefaultResourcePack(resourceLocation);
            InputStream resourceStream = Config.getResourceStream(resourceLocation);
            ArrayList list = new ArrayList(256);
            String readInputStream = Config.readInputStream(resourceStream);
            resourceStream.close();
            String[] tokenize = Config.tokenize(readInputStream, "\n\r");
            if (isFromDefaultResourcePack) {
               Config.dbg("Natural Textures: Parsing default configuration \"" + str + "\"");
               Config.dbg("Natural Textures: Valid only for textures from default resource pack");
            } else {
               Config.dbg("Natural Textures: Parsing configuration \"" + str + "\"");
            }

            TextureMap textureMapBlocks = TextureUtils.getTextureMapBlocks();

            for (int i = 0; i < tokenize.length; i++) {
               String trim = tokenize[i].trim();
               if (!trim.startsWith("#")) {
                  String[] tokenize2 = Config.tokenize(trim, "=");
                  if (tokenize2.length != 2) {
                     Config.warn("Natural Textures: Invalid \"" + str + "\" line: " + trim);
                  } else {
                     String trim2 = tokenize2[0].trim();
                     String trim3 = tokenize2[1].trim();
                     TextureAtlasSprite spriteSafe = textureMapBlocks.getSpriteSafe("minecraft:blocks/" + trim2);
                     if (spriteSafe == null) {
                        Config.warn("Natural Textures: Texture not found: \"" + str + "\" line: " + trim);
                     } else {
                        int indexInMap = spriteSafe.getIndexInMap();
                        if (indexInMap < 0) {
                           Config.warn("Natural Textures: Invalid \"" + str + "\" line: " + trim);
                        } else {
                           if (isFromDefaultResourcePack && !Config.isFromDefaultResourcePack(new ResourceLocation("textures/blocks/" + trim2 + ".png"))) {
                              return;
                           }

                           NaturalProperties naturalProperties = new NaturalProperties(trim3);
                           if (naturalProperties.isValid()) {
                              while (list.size() <= indexInMap) {
                                 list.add(null);
                              }

                              list.set(indexInMap, naturalProperties);
                              Config.dbg("NaturalTextures: " + trim2 + " = " + trim3);
                           }
                        }
                     }
                  }
               }
            }

            propertiesByIndex = (NaturalProperties[])list.toArray(new NaturalProperties[list.size()]);
         } catch (FileNotFoundException e) {
            Config.warn("NaturalTextures: configuration \"" + str + "\" not found");
            return;
         } catch (Exception e) {
            e.printStackTrace();
         }
      }
   }

   public static BakedQuad getNaturalTexture(BlockPos blockPos, BakedQuad bakedQuad) {
      TextureAtlasSprite sprite = bakedQuad.getSprite();
      if (sprite == null) {
         return bakedQuad;
      }

      NaturalProperties naturalProperties = getNaturalProperties(sprite);
      if (naturalProperties == null) {
         return bakedQuad;
      }

      int side = ConnectedTextures.getSide(bakedQuad.getFace());
      int random = Config.getRandom(blockPos, side);
      int n = 0;
      boolean flag = false;
      if (naturalProperties.rotation > 1) {
         n = random & 3;
      }

      if (naturalProperties.rotation == 2) {
         n = n / 2 * 2;
      }

      if (naturalProperties.flip) {
         flag = (random & 4) != 0;
      }

      return naturalProperties.getQuad(bakedQuad, n, flag);
   }

   public static NaturalProperties getNaturalProperties(TextureAtlasSprite textureAtlasSprite) {
      if (!(textureAtlasSprite instanceof TextureAtlasSprite)) {
         return null;
      }

      int indexInMap = textureAtlasSprite.getIndexInMap();
      return indexInMap >= 0 && indexInMap < propertiesByIndex.length ? propertiesByIndex[indexInMap] : null;
   }
}
