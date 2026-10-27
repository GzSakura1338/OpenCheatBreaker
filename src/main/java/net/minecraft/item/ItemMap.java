package net.minecraft.item;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multisets;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapData.MapInfo;

public class ItemMap extends ItemMapBase {
   public ItemMap() {
      this.setHasSubtypes(true);
   }

   public static MapData loadMapData(int mapId, World worldIn) {
      String str = "map_" + mapId;
      MapData loadItemData = (MapData)worldIn.loadItemData(MapData.class, str);
      if (loadItemData == null) {
         loadItemData = new MapData(str);
         worldIn.setItemData(str, loadItemData);
      }

      return loadItemData;
   }

   public MapData getMapData(ItemStack stack, World worldIn) {
      String str = "map_" + stack.getMetadata();
      MapData loadItemData = (MapData)worldIn.loadItemData(MapData.class, str);
      if (loadItemData == null && !worldIn.isRemote) {
         stack.setItemDamage(worldIn.getUniqueDataId("map"));
         str = "map_" + stack.getMetadata();
         loadItemData = new MapData(str);
         loadItemData.scale = 3;
         loadItemData.calculateMapCenter(worldIn.getWorldInfo().getSpawnX(), worldIn.getWorldInfo().getSpawnZ(), loadItemData.scale);
         loadItemData.dimension = (byte)worldIn.provider.getDimensionId();
         loadItemData.markDirty();
         worldIn.setItemData(str, loadItemData);
      }

      return loadItemData;
   }

   public void updateMapData(World worldIn, Entity viewer, MapData data) {
      if (worldIn.provider.getDimensionId() == data.dimension && viewer instanceof EntityPlayer) {
         int n = 1 << data.scale;
         int xCenter = data.xCenter;
         int zCenter = data.zCenter;
         int n2 = MathHelper.floor_double(viewer.posX - xCenter) / n + 64;
         int n3 = MathHelper.floor_double(viewer.posZ - zCenter) / n + 64;
         int n4 = 128 / n;
         if (worldIn.provider.getHasNoSky()) {
            n4 /= 2;
         }

         MapInfo mapInfo = data.getMapInfo((EntityPlayer)viewer);
         mapInfo.intValue++;
         boolean flag = false;

         for (int i = n2 - n4 + 1; i < n2 + n4; i++) {
            if ((i & 15) == (mapInfo.intValue & 15) || flag) {
               flag = false;
               double d = 0.0;

               for (int j = n3 - n4 - 1; j < n3 + n4; j++) {
                  if (i >= 0 && j >= -1 && i < 128 && j < 128) {
                     int n5 = i - n2;
                     int n6 = j - n3;
                     boolean flag2 = n5 * n5 + n6 * n6 > (n4 - 2) * (n4 - 2);
                     int n7 = (xCenter / n + i - 64) * n;
                     int n8 = (zCenter / n + j - 64) * n;
                     HashMultiset hashMultiset = HashMultiset.create();
                     Chunk chunkFromBlockCoords = worldIn.getChunkFromBlockCoords(new BlockPos(n7, 0, n8));
                     if (!chunkFromBlockCoords.isEmpty()) {
                        int n9 = n7 & 15;
                        int n10 = n8 & 15;
                        int n11 = 0;
                        double d2 = 0.0;
                        if (worldIn.provider.getHasNoSky()) {
                           int n12 = n7 + n8 * 231871;
                           n12 = n12 * n12 * 31287121 + n12 * 11;
                           if ((n12 >> 20 & 1) == 0) {
                              hashMultiset.add(Blocks.dirt.getMapColor(Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.DIRT)), 10);
                           } else {
                              hashMultiset.add(
                                 Blocks.stone.getMapColor(Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.STONE)), 100
                              );
                           }

                           d2 = 100.0;
                        } else {
                           MutableBlockPos mutableBlockPos = new MutableBlockPos();

                           for (int k = 0; k < n; k++) {
                              for (int m = 0; m < n; m++) {
                                 int n12 = chunkFromBlockCoords.getHeightValue(k + n9, m + n10) + 1;
                                 IBlockState defaultState = Blocks.air.getDefaultState();
                                 if (n12 > 1) {
                                    do {
                                       defaultState = chunkFromBlockCoords.getBlockState(mutableBlockPos.set(k + n9, --n12, m + n10));
                                    } while (defaultState.getBlock().getMapColor(defaultState) == MapColor.airColor && n12 > 0);

                                    if (n12 > 0 && defaultState.getBlock().getMaterial().isLiquid()) {
                                       int n13 = n12 - 1;

                                       Block block;
                                       do {
                                          block = chunkFromBlockCoords.getBlock(k + n9, n13--, m + n10);
                                          n11++;
                                       } while (n13 > 0 && block.getMaterial().isLiquid());
                                    }
                                 }

                                 d2 += (double)n12 / (n * n);
                                 hashMultiset.add(defaultState.getBlock().getMapColor(defaultState));
                              }
                           }
                        }

                        n11 /= n * n;
                        double d3 = (d2 - d) * 4.0 / (n + 4) + ((i + j & 1) - 0.5) * 0.4;
                        byte b = 1;
                        if (d3 > 0.6) {
                           b = 2;
                        }

                        if (d3 < -0.6) {
                           b = 0;
                        }

                        MapColor first = (MapColor)Iterables.getFirst(Multisets.copyHighestCountFirst(hashMultiset), MapColor.airColor);
                        if (first == MapColor.waterColor) {
                           d3 = n11 * 0.1 + (i + j & 1) * 0.2;
                           b = 1;
                           if (d3 < 0.5) {
                              b = 2;
                           }

                           if (d3 > 0.9) {
                              b = 0;
                           }
                        }

                        d = d2;
                        if (j >= 0 && n5 * n5 + n6 * n6 < n4 * n4 && (!flag2 || (i + j & 1) != 0)) {
                           byte b2 = data.colors[i + j * 128];
                           byte b3 = (byte)(first.colorIndex * 4 + b);
                           if (b2 != b3) {
                              data.colors[i + j * 128] = b3;
                              data.updateMapData(i, j);
                              flag = true;
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   @Override
   public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
      if (!worldIn.isRemote) {
         MapData mapData = this.getMapData(stack, worldIn);
         if (entityIn instanceof EntityPlayer) {
            EntityPlayer entityIn2 = (EntityPlayer)entityIn;
            mapData.updateVisiblePlayers(entityIn2, stack);
         }

         if (isSelected) {
            this.updateMapData(worldIn, entityIn, mapData);
         }
      }
   }

   @Override
   public Packet createMapDataPacket(ItemStack stack, World worldIn, EntityPlayer player) {
      return this.getMapData(stack, worldIn).getMapPacket(stack, worldIn, player);
   }

   @Override
   public void onCreated(ItemStack stack, World worldIn, EntityPlayer playerIn) {
      if (stack.hasTagCompound() && stack.getTagCompound().getBoolean("map_is_scaling")) {
         MapData mapData = Items.filled_map.getMapData(stack, worldIn);
         stack.setItemDamage(worldIn.getUniqueDataId("map"));
         MapData mapData2 = new MapData("map_" + stack.getMetadata());
         mapData2.scale = (byte)(mapData.scale + 1);
         if (mapData2.scale > 4) {
            mapData2.scale = 4;
         }

         mapData2.calculateMapCenter(mapData.xCenter, mapData.zCenter, mapData2.scale);
         mapData2.dimension = mapData.dimension;
         mapData2.markDirty();
         worldIn.setItemData("map_" + stack.getMetadata(), mapData2);
      }
   }

   @Override
   public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
      MapData mapData = this.getMapData(stack, playerIn.worldObj);
      if (advanced) {
         if (mapData == null) {
            tooltip.add("Unknown map");
         } else {
            tooltip.add("Scaling at 1:" + (1 << mapData.scale));
            tooltip.add("(Level " + mapData.scale + "/" + 4 + ")");
         }
      }
   }
}
