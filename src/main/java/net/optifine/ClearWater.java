package net.optifine;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
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

            BlockUtils.setLightOpacity(Blocks.water, b);
            BlockUtils.setLightOpacity(Blocks.flowing_water, b);
        }

        if (world != null) {
            IChunkProvider chunkProvider = world.getChunkProvider();
            if (chunkProvider != null) {
                EntityLivingBase renderViewEntity = Config.getMinecraft().renderViewEntity;
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

                                    for (int k = n8; k < n10; k++) {
                                        for (int m = n9; m < n11; m++) {
                                            int precipitationHeight = world.getPrecipitationHeight(k, m);

                                            for (int n12 = 0; n12 < precipitationHeight; n12++) {
                                                Block block = world.getBlock(k, n12, m);
                                                if (block.getMaterial() == Material.water) {
                                                    world.markBlocksDirtyVertical(k, m, n12, precipitationHeight);
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
