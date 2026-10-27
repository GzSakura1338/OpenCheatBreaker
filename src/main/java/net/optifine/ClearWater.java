package net.optifine;

import net.minecraft.block.BlockAir;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;

public class ClearWater {
   public static void updateWaterOpacity(GameSettings gameSettings, World world) {
      if (gameSettings != null) {
         byte b = 3;
         if (gameSettings.ofClearWater) {
            b = 1;
         }

         BlockAir.setLightOpacity(Blocks.water, b);
         BlockAir.setLightOpacity(Blocks.flowing_water, b);
      }

      if (world != null) {
         IChunkProvider chunkProvider = world.getChunkProvider();
         if (chunkProvider != null) {
            Entity renderViewEntity = Config.getMinecraft().getRenderViewEntity();
            if (renderViewEntity != null) {
               int n = (int)renderViewEntity.posX / 16;
               int n2 = (int)renderViewEntity.posZ / 16;
               int n3 = n - 512;
               int n4 = n + 512;
               int n5 = n2 - 512;
               int n6 = n2 + 512;
               int n7 = 0;

               for (int i = n3; i < n4; i++) {
                  for (int j = n5; j < n6; j++) {
                     if (chunkProvider.chunkExists(i, j)) {
                        Chunk provideChunk = chunkProvider.provideChunk(i, j);
                        if (provideChunk != null && !(provideChunk instanceof EmptyChunk)) {
                           int n8 = i << 4;
                           int n9 = j << 4;
                           int n10 = n8 + 16;
                           int n11 = n9 + 16;
                           BlockPosM blockPosM = new BlockPosM(0, 0, 0);
                           BlockPosM blockPosM2 = new BlockPosM(0, 0, 0);

                           for (int k = n8; k < n10; k++) {
                              for (int m = n9; m < n11; m++) {
                                 blockPosM.setXyz(k, 0, m);
                                 BlockPos precipitationHeight = world.getPrecipitationHeight(blockPosM);

                                 for (int n12 = 0; n12 < precipitationHeight.getY(); n12++) {
                                    blockPosM2.setXyz(k, n12, m);
                                    IBlockState blockState = world.getBlockState(blockPosM2);
                                    if (blockState.getBlock().getMaterial() == Material.water) {
                                       world.markBlocksDirtyVertical(k, m, blockPosM2.getY(), precipitationHeight.getY());
                                       n7++;
                                       break;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               if (n7 > 0) {
                  String str = "server";
                  if (Config.isMinecraftThread()) {
                     str = "client";
                  }

                  Config.dbg("ClearWater (" + str + ") relighted " + n7 + " chunks");
               }
            }
         }
      }
   }
}
