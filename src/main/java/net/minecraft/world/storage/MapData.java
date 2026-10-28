package net.minecraft.world.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class MapData extends WorldSavedData {
    public int xCenter;
    public int zCenter;
    public byte dimension;
    public byte scale;
    public byte[] colors = new byte[16384];
    public List playersArrayList = new ArrayList();
    public Map playersHashMap = new HashMap();
    public Map playersVisibleOnMap = new LinkedHashMap();

    public MapData(String str) {
        super(str);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagCompound) {
        this.dimension = nbttagCompound.getByte("dimension");
        this.xCenter = nbttagCompound.getInteger("xCenter");
        this.zCenter = nbttagCompound.getInteger("zCenter");
        this.scale = nbttagCompound.getByte("scale");
        if (this.scale < 0) {
            this.scale = 0;
        }

        if (this.scale > 4) {
            this.scale = 4;
        }

        short short2 = nbttagCompound.getShort("width");
        short short3 = nbttagCompound.getShort("height");
        if (short2 == 128 && short3 == 128) {
            this.colors = nbttagCompound.getByteArray("colors");
        } else {
            byte[] byteArray = nbttagCompound.getByteArray("colors");
            this.colors = new byte[16384];
            int n = (128 - short2) / 2;
            int n2 = (128 - short3) / 2;

            for (int i = 0; i < short3; i++) {
                int n3 = i + n2;
                if (n3 >= 0 || n3 < 128) {
                    for (int j = 0; j < short2; j++) {
                        int n4 = j + n;
                        if (n4 >= 0 || n4 < 128) {
                            this.colors[n4 + n3 * 128] = byteArray[j + i * short2];
                        }
                    }
                }
            }
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagCompound) {
        nbttagCompound.setByte("dimension", this.dimension);
        nbttagCompound.setInteger("xCenter", this.xCenter);
        nbttagCompound.setInteger("zCenter", this.zCenter);
        nbttagCompound.setByte("scale", this.scale);
        nbttagCompound.setShort("width", (short)128);
        nbttagCompound.setShort("height", (short)128);
        nbttagCompound.setByteArray("colors", this.colors);
    }

    public void updateVisiblePlayers(EntityPlayer entityPlayer, ItemStack itemStack) {
        if (!this.playersHashMap.containsKey(entityPlayer)) {
            MapData.MapInfo mapInfo = new MapData.MapInfo(entityPlayer);
            this.playersHashMap.put(entityPlayer, mapInfo);
            this.playersArrayList.add(mapInfo);
        }

        if (!entityPlayer.inventory.hasItemStack(itemStack)) {
            this.playersVisibleOnMap.remove(entityPlayer.getCommandSenderName());
        }

        for (int i = 0; i < this.playersArrayList.size(); i++) {
            MapData.MapInfo mapInfo = (MapData.MapInfo)this.playersArrayList.get(i);
            if (!mapInfo.entityplayerObj.isDead && (mapInfo.entityplayerObj.inventory.hasItemStack(itemStack) || itemStack.isOnItemFrame())) {
                if (!itemStack.isOnItemFrame() && mapInfo.entityplayerObj.dimension == this.dimension) {
                    this.process(
                        0,
                        mapInfo.entityplayerObj.worldObj,
                        mapInfo.entityplayerObj.getCommandSenderName(),
                        mapInfo.entityplayerObj.posX,
                        mapInfo.entityplayerObj.posZ,
                        mapInfo.entityplayerObj.rotationYaw
                    );
                }
            } else {
                this.playersHashMap.remove(mapInfo.entityplayerObj);
                this.playersArrayList.remove(mapInfo);
            }
        }

        if (itemStack.isOnItemFrame()) {
            this.process(
                1,
                entityPlayer.worldObj,
                "frame-" + itemStack.getItemFrame().getEntityId(),
                itemStack.getItemFrame().intValue,
                itemStack.getItemFrame().intValue3,
                itemStack.getItemFrame().hangingDirection * 90
            );
        }
    }

    public void process(int n, World world, String str, double d, double d2, double d3) {
        int n2 = 1 << this.scale;
        float f = (float)(d - this.xCenter) / n2;
        float f2 = (float)(d2 - this.zCenter) / n2;
        byte b = (byte)(f * 2.0F + 0.5);
        byte b2 = (byte)(f2 * 2.0F + 0.5);
        byte b3 = 63;
        byte b4;
        if (f >= -b3 && f2 >= -b3 && f <= b3 && f2 <= b3) {
            d3 += d3 < 0.0 ? -8.0 : 8.0;
            b4 = (byte)(d3 * 16.0 / 360.0);
            if (this.dimension < 0) {
                int n3 = (int)(world.getWorldInfo().getWorldTime() / 10L);
                b4 = (byte)(n3 * n3 * 34187121 + n3 * 121 >> 15 & 15);
            }
        } else {
            if (Math.abs(f) >= 320.0F || Math.abs(f2) >= 320.0F) {
                this.playersVisibleOnMap.remove(str);
                return;
            }

            n = 6;
            b4 = 0;
            if (f <= -b3) {
                b = (byte)(b3 * 2 + 2.5);
            }

            if (f2 <= -b3) {
                b2 = (byte)(b3 * 2 + 2.5);
            }

            if (f >= b3) {
                b = (byte)(b3 * 2 + 1);
            }

            if (f2 >= b3) {
                b2 = (byte)(b3 * 2 + 1);
            }
        }

        this.playersVisibleOnMap.put(str, new MapData.MapCoord((byte)n, b, b2, b4));
    }

    public byte[] getUpdatePacketData(ItemStack itemStack, World world, EntityPlayer entityPlayer) {
        MapData.MapInfo mapInfo = (MapData.MapInfo)this.playersHashMap.get(entityPlayer);
        return mapInfo == null ? null : mapInfo.getPlayersOnMap(itemStack);
    }

    public void setColumnDirty(int n, int n2, int n3) {
        super.markDirty();

        for (int i = 0; i < this.playersArrayList.size(); i++) {
            MapData.MapInfo mapInfo = (MapData.MapInfo)this.playersArrayList.get(i);
            if (mapInfo.intArray[n] < 0 || mapInfo.intArray[n] > n2) {
                mapInfo.intArray[n] = n2;
            }

            if (mapInfo.intArray2[n] < 0 || mapInfo.intArray2[n] < n3) {
                mapInfo.intArray2[n] = n3;
            }
        }
    }

    public void updateMPMapData(byte[] bytes) {
        if (bytes[0] == 0) {
            int n = bytes[1] & 255;
            int n2 = bytes[2] & 255;

            for (int i = 0; i < bytes.length - 3; i++) {
                this.colors[(i + n2) * 128 + n] = bytes[i + 3];
            }

            this.markDirty();
        } else if (bytes[0] == 1) {
            this.playersVisibleOnMap.clear();

            for (int i = 0; i < (bytes.length - 1) / 3; i++) {
                byte b = (byte)(bytes[i * 3 + 1] >> 4);
                byte b2 = bytes[i * 3 + 2];
                byte b3 = bytes[i * 3 + 3];
                byte b4 = (byte)(bytes[i * 3 + 1] & 15);
                this.playersVisibleOnMap.put("icon-" + i, new MapData.MapCoord(b, b2, b3, b4));
            }
        } else if (bytes[0] == 2) {
            this.scale = bytes[1];
        }
    }

    public MapData.MapInfo createMapInfo(EntityPlayer entityPlayer) {
        MapData.MapInfo mapInfo = (MapData.MapInfo)this.playersHashMap.get(entityPlayer);
        if (mapInfo == null) {
            mapInfo = new MapData.MapInfo(entityPlayer);
            this.playersHashMap.put(entityPlayer, mapInfo);
            this.playersArrayList.add(mapInfo);
        }

        return mapInfo;
    }

    public class MapCoord {
        public byte iconSize;
        public byte centerX;
        public byte centerZ;
        public byte iconRotation;

        public MapCoord(byte b, byte b2, byte b3, byte b4) {
            this.iconSize = b;
            this.centerX = b2;
            this.centerZ = b3;
            this.iconRotation = b4;
        }
    }

    public class MapInfo {
        public int[] intArray = new int[128];
        public int[] intArray2 = new int[128];
        public EntityPlayer entityplayerObj;
        public int currentRandomNumber;
        public int ticksUntilPlayerLocationMapUpdate;
        public byte[] lastPlayerLocationOnMap;
        public int intValue;
        public boolean flag;

        public MapInfo(EntityPlayer entityPlayer) {
            this.entityplayerObj = entityPlayer;

            for (int i = 0; i < this.intArray.length; i++) {
                this.intArray[i] = 0;
                this.intArray2[i] = 127;
            }
        }

        public byte[] getPlayersOnMap(ItemStack itemStack) {
            if (!this.flag) {
                byte[] bytes = new byte[]{2, MapData.this.scale};
                this.flag = true;
                return bytes;
            }

            if (--this.ticksUntilPlayerLocationMapUpdate < 0) {
                this.ticksUntilPlayerLocationMapUpdate = 4;
                byte[] bytes = new byte[MapData.this.playersVisibleOnMap.size() * 3 + 1];
                bytes[0] = 1;
                int n = 0;

                for (MapData.MapCoord mapCoord : (Iterable<MapData.MapCoord>)MapData.this.playersVisibleOnMap.values()) {
                    bytes[n * 3 + 1] = (byte)(mapCoord.iconSize << 4 | mapCoord.iconRotation & 15);
                    bytes[n * 3 + 2] = mapCoord.centerX;
                    bytes[n * 3 + 3] = mapCoord.centerZ;
                    n++;
                }

                boolean flag = !itemStack.isOnItemFrame();
                if (this.lastPlayerLocationOnMap != null && this.lastPlayerLocationOnMap.length == bytes.length) {
                    for (int i = 0; i < bytes.length; i++) {
                        if (bytes[i] != this.lastPlayerLocationOnMap[i]) {
                            flag = false;
                            break;
                        }
                    }
                } else {
                    flag = false;
                }

                if (!flag) {
                    this.lastPlayerLocationOnMap = bytes;
                    return bytes;
                }
            }

            for (int i = 0; i < 1; i++) {
                int n = this.currentRandomNumber++ * 11 % 128;
                if (this.intArray[n] >= 0) {
                    int n2 = this.intArray2[n] - this.intArray[n] + 1;
                    int n3 = this.intArray[n];
                    byte[] bytes = new byte[n2 + 3];
                    bytes[0] = 0;
                    bytes[1] = (byte)n;
                    bytes[2] = (byte)n3;

                    for (int j = 0; j < bytes.length - 3; j++) {
                        bytes[j + 3] = MapData.this.colors[(j + n3) * 128 + n];
                    }

                    this.intArray2[n] = -1;
                    this.intArray[n] = -1;
                    return bytes;
                }
            }

            return null;
        }
    }
}
