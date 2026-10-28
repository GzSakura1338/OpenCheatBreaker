package net.minecraft.item;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multisets;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapData.MapInfo;

public class ItemMap extends ItemMapBase {
    public ItemMap() {
        this.setHasSubtypes(true);
    }

    public static MapData loadMapData(int n, World world) {
        String str = "map_" + n;
        MapData loadItemData = (MapData)world.loadItemData(MapData.class, str);
        if (loadItemData == null) {
            loadItemData = new MapData(str);
            world.setItemData(str, loadItemData);
        }

        return loadItemData;
    }

    public MapData getMapData(ItemStack itemStack, World world) {
        String str = "map_" + itemStack.getMetadata();
        MapData loadItemData = (MapData)world.loadItemData(MapData.class, str);
        if (loadItemData == null && !world.isRemote) {
            itemStack.setMetadata(world.getUniqueDataId("map"));
            str = "map_" + itemStack.getMetadata();
            loadItemData = new MapData(str);
            loadItemData.scale = 3;
            int n = 128 * (1 << loadItemData.scale);
            loadItemData.xCenter = Math.round((float)world.getWorldInfo().getSpawnX() / n) * n;
            loadItemData.zCenter = Math.round(world.getWorldInfo().getSpawnZ() / n) * n;
            loadItemData.dimension = (byte)world.provider.dimensionId;
            loadItemData.markDirty();
            world.setItemData(str, loadItemData);
        }

        return loadItemData;
    }

    public void updateMapData(World world, Entity entity, MapData mapData) {
        if (world.provider.dimensionId == mapData.dimension && entity instanceof EntityPlayer) {
            int n = 1 << mapData.scale;
            int xCenter = mapData.xCenter;
            int zCenter = mapData.zCenter;
            int n2 = MathHelper.floor_double(entity.posX - xCenter) / n + 64;
            int n3 = MathHelper.floor_double(entity.posZ - zCenter) / n + 64;
            int n4 = 128 / n;
            if (world.provider.hasNoSky) {
                n4 /= 2;
            }

            MapInfo createMapInfo = mapData.createMapInfo((EntityPlayer)entity);
            createMapInfo.intValue++;

            for (int i = n2 - n4 + 1; i < n2 + n4; i++) {
                if ((i & 15) == (createMapInfo.intValue & 15)) {
                    int n5 = 255;
                    int n6 = 0;
                    double d = 0.0;

                    for (int j = n3 - n4 - 1; j < n3 + n4; j++) {
                        if (i >= 0 && j >= -1 && i < 128 && j < 128) {
                            int n7 = i - n2;
                            int n8 = j - n3;
                            boolean flag = n7 * n7 + n8 * n8 > (n4 - 2) * (n4 - 2);
                            int n9 = (xCenter / n + i - 64) * n;
                            int n10 = (zCenter / n + j - 64) * n;
                            HashMultiset hashMultiset = HashMultiset.create();
                            Chunk chunkFromBlockCoords = world.getChunkFromBlockCoords(n9, n10);
                            if (!chunkFromBlockCoords.isEmpty()) {
                                int n11 = n9 & 15;
                                int n12 = n10 & 15;
                                int n13 = 0;
                                double d2 = 0.0;
                                if (world.provider.hasNoSky) {
                                    int n14 = n9 + n10 * 231871;
                                    n14 = n14 * n14 * 31287121 + n14 * 11;
                                    if ((n14 >> 20 & 1) == 0) {
                                        hashMultiset.add(Blocks.dirt.getMapColor(0), 10);
                                    } else {
                                        hashMultiset.add(Blocks.stone.getMapColor(0), 100);
                                    }

                                    d2 = 100.0;
                                } else {
                                    for (int k = 0; k < n; k++) {
                                        for (int m = 0; m < n; m++) {
                                            int n14 = chunkFromBlockCoords.getHeightValue(k + n11, m + n12) + 1;
                                            Block air = Blocks.air;
                                            int n15 = 0;
                                            if (n14 > 1) {
                                                do {
                                                    air = chunkFromBlockCoords.getBlock(k + n11, --n14, m + n12);
                                                    n15 = chunkFromBlockCoords.getBlockMetadata(k + n11, n14, m + n12);
                                                } while (air.getMapColor(n15) == MapColor.airColor && n14 > 0);

                                                if (n14 > 0 && air.getMaterial().isLiquid()) {
                                                    int n16 = n14 - 1;

                                                    Block block;
                                                    do {
                                                        block = chunkFromBlockCoords.getBlock(k + n11, n16--, m + n12);
                                                        n13++;
                                                    } while (n16 > 0 && block.getMaterial().isLiquid());
                                                }
                                            }

                                            d2 += (double)n14 / (n * n);
                                            hashMultiset.add(air.getMapColor(n15));
                                        }
                                    }
                                }

                                n13 /= n * n;
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
                                    d3 = n13 * 0.1 + (i + j & 1) * 0.2;
                                    b = 1;
                                    if (d3 < 0.5) {
                                        b = 2;
                                    }

                                    if (d3 > 0.9) {
                                        b = 0;
                                    }
                                }

                                d = d2;
                                if (j >= 0 && n7 * n7 + n8 * n8 < n4 * n4 && (!flag || (i + j & 1) != 0)) {
                                    byte b2 = mapData.colors[i + j * 128];
                                    byte b3 = (byte)(first.colorIndex * 4 + b);
                                    if (b2 != b3) {
                                        if (n5 > j) {
                                            n5 = j;
                                        }

                                        if (n6 < j) {
                                            n6 = j;
                                        }

                                        mapData.colors[i + j * 128] = b3;
                                    }
                                }
                            }
                        }
                    }

                    if (n5 <= n6) {
                        mapData.setColumnDirty(i, n5, n6);
                    }
                }
            }
        }
    }

    @Override
    public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int n, boolean flag) {
        if (!worldIn.isRemote) {
            MapData mapData = this.getMapData(stack, worldIn);
            if (entityIn instanceof EntityPlayer) {
                EntityPlayer entityIn2 = (EntityPlayer)entityIn;
                mapData.updateVisiblePlayers(entityIn2, stack);
            }

            if (flag) {
                this.updateMapData(worldIn, entityIn, mapData);
            }
        }
    }

    @Override
    public Packet createMapDataPacket(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        byte[] updatePacketData = this.getMapData(itemStack, world).getUpdatePacketData(itemStack, world, entityPlayer);
        return updatePacketData == null ? null : new S34PacketMaps(itemStack.getMetadata(), updatePacketData);
    }

    @Override
    public void onCreated(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        if (itemStack.hasTagCompound() && itemStack.getTagCompound().getBoolean("map_is_scaling")) {
            MapData mapData = Items.filled_map.getMapData(itemStack, world);
            itemStack.setMetadata(world.getUniqueDataId("map"));
            MapData mapData2 = new MapData("map_" + itemStack.getMetadata());
            mapData2.scale = (byte)(mapData.scale + 1);
            if (mapData2.scale > 4) {
                mapData2.scale = 4;
            }

            mapData2.xCenter = mapData.xCenter;
            mapData2.zCenter = mapData.zCenter;
            mapData2.dimension = mapData.dimension;
            mapData2.markDirty();
            world.setItemData("map_" + itemStack.getMetadata(), mapData2);
        }
    }

    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean flag) {
        MapData mapData = this.getMapData(itemStack, entityPlayer.worldObj);
        if (flag) {
            if (mapData == null) {
                list.add("Unknown map");
            } else {
                list.add("Scaling at 1:" + (1 << mapData.scale));
                list.add("(Level " + mapData.scale + "/" + 4 + ")");
            }
        }
    }
}
