package net.optifine;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import net.minecraft.block.state.BlockStateBase;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumWorldBlockLayer;
import net.optifine.config.ConnectedParser;
import net.optifine.config.MatchBlock;
import net.optifine.shaders.BlockAliases;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.ResUtils;

public class CustomBlockLayers {
   public static EnumWorldBlockLayer[] renderLayers = null;
   public static boolean active = false;

   public static EnumWorldBlockLayer getRenderLayer(IBlockState blockState) {
      if (renderLayers == null) {
         return null;
      }

      if (blockState.getBlock().isOpaqueCube()) {
         return null;
      }

      if (!(blockState instanceof BlockStateBase)) {
         return null;
      }

      BlockStateBase blockStateBase = (BlockStateBase)blockState;
      int blockId = blockStateBase.getBlockId();
      return blockId > 0 && blockId < renderLayers.length ? renderLayers[blockId] : null;
   }

   public static void update() {
      renderLayers = null;
      active = false;
      ArrayList list = new ArrayList();
      String str = "optifine/block.properties";
      Properties readProperties = ResUtils.readProperties(str, "CustomBlockLayers");
      if (readProperties != null) {
         readLayers(str, readProperties, list);
      }

      if (Config.isShaders()) {
         PropertiesOrdered blockLayerPropertes = BlockAliases.getBlockLayerPropertes();
         if (blockLayerPropertes != null) {
            String str2 = "shaders/block.properties";
            readLayers(str2, blockLayerPropertes, list);
         }
      }

      if (!list.isEmpty()) {
         renderLayers = (EnumWorldBlockLayer[])list.toArray(new EnumWorldBlockLayer[list.size()]);
         active = true;
      }
   }

   public static void readLayers(String str, Properties properties, List<EnumWorldBlockLayer> list) {
      Config.dbg("CustomBlockLayers: " + str);
      readLayer("solid", EnumWorldBlockLayer.SOLID, properties, list);
      readLayer("cutout", EnumWorldBlockLayer.CUTOUT, properties, list);
      readLayer("cutout_mipped", EnumWorldBlockLayer.CUTOUT_MIPPED, properties, list);
      readLayer("translucent", EnumWorldBlockLayer.TRANSLUCENT, properties, list);
   }

   public static void readLayer(String str, EnumWorldBlockLayer enumWorldBlockLayer, Properties properties, List<EnumWorldBlockLayer> list) {
      String str2 = "layer." + str;
      String property = properties.getProperty(str2);
      if (property != null) {
         ConnectedParser connectedParser = new ConnectedParser("CustomBlockLayers");
         MatchBlock[] parseMatchBlocks = connectedParser.parseMatchBlocks(property);
         if (parseMatchBlocks != null) {
            for (int i = 0; i < parseMatchBlocks.length; i++) {
               MatchBlock matchBlock = parseMatchBlocks[i];
               int blockId = matchBlock.getBlockId();
               if (blockId > 0) {
                  while (list.size() < blockId + 1) {
                     list.add(null);
                  }

                  if (list.get(blockId) != null) {
                     Config.warn("CustomBlockLayers: Block layer is already set, block: " + blockId + ", layer: " + str);
                  }

                  list.set(blockId, enumWorldBlockLayer);
               }
            }
         }
      }
   }

   public static boolean isActive() {
      return active;
   }
}
