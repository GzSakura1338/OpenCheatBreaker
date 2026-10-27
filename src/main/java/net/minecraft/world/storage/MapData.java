package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec4b;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;

public class MapData extends WorldSavedData {
   public int xCenter;
   public int zCenter;
   public byte dimension;
   public byte scale;
   public byte[] colors = new byte[16384];
   public List<MapData.MapInfo> playersArrayList = Lists.newArrayList();
   public Map<EntityPlayer, MapData.MapInfo> playersHashMap = Maps.newHashMap();
   public Map<String, Vec4b> mapDecorations = Maps.newLinkedHashMap();

   public MapData(String mapname) {
      super(mapname);
   }

   public void calculateMapCenter(double x, double z, int mapScale) {
      int n = 128 * (1 << mapScale);
      int n2 = MathHelper.floor_double((x + 64.0) / n);
      int n3 = MathHelper.floor_double((z + 64.0) / n);
      this.xCenter = n2 * n + n / 2 - 64;
      this.zCenter = n3 * n + n / 2 - 64;
   }

   @Override
   public void readFromNBT(NBTTagCompound nbt) {
      this.dimension = nbt.getByte("dimension");
      this.xCenter = nbt.getInteger("xCenter");
      this.zCenter = nbt.getInteger("zCenter");
      this.scale = nbt.getByte("scale");
      this.scale = (byte)MathHelper.clamp_int(this.scale, 0, 4);
      short short2 = nbt.getShort("width");
      short short3 = nbt.getShort("height");
      if (short2 == 128 && short3 == 128) {
         this.colors = nbt.getByteArray("colors");
      } else {
         byte[] byteArray = nbt.getByteArray("colors");
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
   public void writeToNBT(NBTTagCompound nbt) {
      nbt.setByte("dimension", this.dimension);
      nbt.setInteger("xCenter", this.xCenter);
      nbt.setInteger("zCenter", this.zCenter);
      nbt.setByte("scale", this.scale);
      nbt.setShort("width", (short)128);
      nbt.setShort("height", (short)128);
      nbt.setByteArray("colors", this.colors);
   }

   public void updateVisiblePlayers(EntityPlayer player, ItemStack mapStack) {
      if (!this.playersHashMap.containsKey(player)) {
         MapData.MapInfo mapInfo = new MapData.MapInfo(player);
         this.playersHashMap.put(player, mapInfo);
         this.playersArrayList.add(mapInfo);
      }

      if (!player.inventory.hasItemStack(mapStack)) {
         this.mapDecorations.remove(player.getName());
      }

      for (int i = 0; i < this.playersArrayList.size(); i++) {
         MapData.MapInfo mapInfo = this.playersArrayList.get(i);
         if (!mapInfo.entityplayerObj.isDead && (mapInfo.entityplayerObj.inventory.hasItemStack(mapStack) || mapStack.isOnItemFrame())) {
            if (!mapStack.isOnItemFrame() && mapInfo.entityplayerObj.dimension == this.dimension) {
               this.updateDecorations(
                  0,
                  mapInfo.entityplayerObj.worldObj,
                  mapInfo.entityplayerObj.getName(),
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

      if (mapStack.isOnItemFrame()) {
         EntityItemFrame itemFrame = mapStack.getItemFrame();
         BlockPos hangingPosition = itemFrame.getHangingPosition();
         this.updateDecorations(1, player.worldObj, "frame-" + itemFrame.getEntityId(), hangingPosition.getX(), hangingPosition.getZ(), itemFrame.facingDirection.getHorizontalIndex() * 90);
      }

      if (mapStack.hasTagCompound() && mapStack.getTagCompound().hasKey("Decorations", 9)) {
         NBTTagList tagList = mapStack.getTagCompound().getTagList("Decorations", 10);

         for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
            if (!this.mapDecorations.containsKey(compoundTagAt.getString("id"))) {
               this.updateDecorations(
                  compoundTagAt.getByte("type"), player.worldObj, compoundTagAt.getString("id"), compoundTagAt.getDouble("x"), compoundTagAt.getDouble("z"), compoundTagAt.getDouble("rot")
               );
            }
         }
      }
   }

   public void updateDecorations(int type, World worldIn, String entityIdentifier, double worldX, double worldZ, double rotation) {
      int n = 1 << this.scale;
      float f = (float)(worldX - this.xCenter) / n;
      float f2 = (float)(worldZ - this.zCenter) / n;
      byte b = (byte)(f * 2.0F + 0.5);
      byte b2 = (byte)(f2 * 2.0F + 0.5);
      byte b3 = 63;
      byte b4;
      if (f >= -b3 && f2 >= -b3 && f <= b3 && f2 <= b3) {
         rotation += rotation < 0.0 ? -8.0 : 8.0;
         b4 = (byte)(rotation * 16.0 / 360.0);
         if (this.dimension < 0) {
            int n2 = (int)(worldIn.getWorldInfo().getWorldTime() / 10L);
            b4 = (byte)(n2 * n2 * 34187121 + n2 * 121 >> 15 & 15);
         }
      } else {
         if (Math.abs(f) >= 320.0F || Math.abs(f2) >= 320.0F) {
            this.mapDecorations.remove(entityIdentifier);
            return;
         }

         type = 6;
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

      this.mapDecorations.put(entityIdentifier, new Vec4b((byte)type, b, b2, b4));
   }

   public Packet getMapPacket(ItemStack mapStack, World worldIn, EntityPlayer player) {
      MapData.MapInfo mapInfo = this.playersHashMap.get(player);
      return mapInfo == null ? null : mapInfo.getPacket(mapStack);
   }

   public void updateMapData(int x, int y) {
      super.markDirty();

      for (MapData.MapInfo mapInfo : this.playersArrayList) {
         mapInfo.update(x, y);
      }
   }

   public MapData.MapInfo getMapInfo(EntityPlayer player) {
      MapData.MapInfo mapInfo = this.playersHashMap.get(player);
      if (mapInfo == null) {
         mapInfo = new MapData.MapInfo(player);
         this.playersHashMap.put(player, mapInfo);
         this.playersArrayList.add(mapInfo);
      }

      return mapInfo;
   }

   public class MapInfo {
      public boolean dirty = true;
      public int minX = 0;
      public int minY = 0;
      public int maxX = 127;
      public int maxY = 127;
      public EntityPlayer entityplayerObj;
      public int emptyPacketsRequested;
      public int intValue;

      public MapInfo(EntityPlayer player) {
         this.entityplayerObj = player;
      }

      public Packet getPacket(ItemStack stack) {
         if (this.dirty) {
            this.dirty = false;
            return new S34PacketMaps(
               stack.getMetadata(),
               MapData.this.scale,
               MapData.this.mapDecorations.values(),
               MapData.this.colors,
               this.minX,
               this.minY,
               this.maxX + 1 - this.minX,
               this.maxY + 1 - this.minY
            );
         } else {
            return this.emptyPacketsRequested++ % 5 == 0
               ? new S34PacketMaps(stack.getMetadata(), MapData.this.scale, MapData.this.mapDecorations.values(), MapData.this.colors, 0, 0, 0, 0)
               : null;
         }
      }

      public void update(int x, int y) {
         if (this.dirty) {
            this.minX = Math.min(this.minX, x);
            this.minY = Math.min(this.minY, y);
            this.maxX = Math.max(this.maxX, x);
            this.maxY = Math.max(this.maxY, y);
         } else {
            this.dirty = true;
            this.minX = x;
            this.minY = y;
            this.maxX = x;
            this.maxY = y;
         }
      }
   }
}
