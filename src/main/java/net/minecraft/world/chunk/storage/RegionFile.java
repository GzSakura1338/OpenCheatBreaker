package net.minecraft.world.chunk.storage;

import com.google.common.collect.Lists;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.InflaterInputStream;
import net.minecraft.server.MinecraftServer;

public class RegionFile {
   public static byte[] emptySector = new byte[4096];
   public int[] offsets = new int[1024];
   public int[] chunkTimestamps = new int[1024];
   public File fileName;
   public int sizeDelta;
   public long lastModified;
   public RandomAccessFile dataFile;
   public List<Boolean> sectorFree;

   public RegionFile(File fileNameIn) {
      this.fileName = fileNameIn;
      this.sizeDelta = 0;

      try {
         if (fileNameIn.exists()) {
            this.lastModified = fileNameIn.lastModified();
         }

         this.dataFile = new RandomAccessFile(fileNameIn, "rw");
         if (this.dataFile.length() < 4096L) {
            for (int i = 0; i < 1024; i++) {
               this.dataFile.writeInt(0);
            }

            for (int i = 0; i < 1024; i++) {
               this.dataFile.writeInt(0);
            }

            this.sizeDelta += 8192;
         }

         if ((this.dataFile.length() & 4095L) != 0L) {
            for (int i = 0; i < (this.dataFile.length() & 4095L); i++) {
               this.dataFile.write(0);
            }
         }

         int n = (int)this.dataFile.length() / 4096;
         this.sectorFree = Lists.newArrayListWithCapacity(n);

         for (int i = 0; i < n; i++) {
            this.sectorFree.add(true);
         }

         this.sectorFree.set(0, false);
         this.sectorFree.set(1, false);
         this.dataFile.seek(0L);

         for (int i = 0; i < 1024; i++) {
            int readInt = this.dataFile.readInt();
            this.offsets[i] = readInt;
            if (readInt != 0 && (readInt >> 8) + (readInt & 0xFF) <= this.sectorFree.size()) {
               for (int j = 0; j < (readInt & 0xFF); j++) {
                  this.sectorFree.set((readInt >> 8) + j, false);
               }
            }
         }

         for (int i = 0; i < 1024; i++) {
            int readInt = this.dataFile.readInt();
            this.chunkTimestamps[i] = readInt;
         }
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public synchronized DataInputStream getChunkDataInputStream(int x, int z) {
      if (this.outOfBounds(x, z)) {
         return null;
      }

      try {
         int offset = this.getOffset(x, z);
         if (offset == 0) {
            return null;
         } else {
            int n = offset >> 8;
            int n2 = offset & 0xFF;
            if (n + n2 > this.sectorFree.size()) {
               return null;
            } else {
               this.dataFile.seek(n * 4096);
               int readInt = this.dataFile.readInt();
               if (readInt > 4096 * n2) {
                  return null;
               } else if (readInt <= 0) {
                  return null;
               } else {
                  byte readByte = this.dataFile.readByte();
                  if (readByte == 1) {
                     byte[] bytes = new byte[readInt - 1];
                     this.dataFile.read(bytes);
                     return new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bytes))));
                  } else if (readByte == 2) {
                     byte[] bytes = new byte[readInt - 1];
                     this.dataFile.read(bytes);
                     return new DataInputStream(new BufferedInputStream(new InflaterInputStream(new ByteArrayInputStream(bytes))));
                  } else {
                     return null;
                  }
               }
            }
         }
      } catch (IOException e) {
         return null;
      }
   }

   public DataOutputStream getChunkDataOutputStream(int x, int z) {
      return this.outOfBounds(x, z) ? null : new DataOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(x, z)));
   }

   public synchronized void write(int x, int z, byte[] data, int length) {
      try {
         int offset = this.getOffset(x, z);
         int n = offset >> 8;
         int n2 = offset & 0xFF;
         int n3 = (length + 5) / 4096 + 1;
         if (n3 >= 256) {
            return;
         }

         if (n != 0 && n2 == n3) {
            this.write(n, data, length);
         } else {
            for (int i = 0; i < n2; i++) {
               this.sectorFree.set(n + i, true);
            }

            int indexOf = this.sectorFree.indexOf(true);
            int n4 = 0;
            if (indexOf != -1) {
               for (int i = indexOf; i < this.sectorFree.size(); i++) {
                  if (n4 != 0) {
                     if (this.sectorFree.get(i)) {
                        n4++;
                     } else {
                        n4 = 0;
                     }
                  } else if (this.sectorFree.get(i)) {
                     indexOf = i;
                     n4 = 1;
                  }

                  if (n4 >= n3) {
                     break;
                  }
               }
            }

            if (n4 >= n3) {
               n = indexOf;
               this.setOffset(x, z, indexOf << 8 | n3);

               for (int i = 0; i < n3; i++) {
                  this.sectorFree.set(n + i, false);
               }

               this.write(n, data, length);
            } else {
               this.dataFile.seek(this.dataFile.length());
               n = this.sectorFree.size();

               for (int i = 0; i < n3; i++) {
                  this.dataFile.write(emptySector);
                  this.sectorFree.add(false);
               }

               this.sizeDelta += 4096 * n3;
               this.write(n, data, length);
               this.setOffset(x, z, n << 8 | n3);
            }
         }

         this.setChunkTimestamp(x, z, (int)(MinecraftServer.getCurrentTimeMillis() / 1000L));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   public void write(int sectorNumber, byte[] data, int length) throws IOException {
      this.dataFile.seek(sectorNumber * 4096);
      this.dataFile.writeInt(length + 1);
      this.dataFile.writeByte(2);
      this.dataFile.write(data, 0, length);
   }

   public boolean outOfBounds(int x, int z) {
      return x < 0 || x >= 32 || z < 0 || z >= 32;
   }

   public int getOffset(int x, int z) {
      return this.offsets[x + z * 32];
   }

   public boolean isChunkSaved(int x, int z) {
      return this.getOffset(x, z) != 0;
   }

   public void setOffset(int x, int z, int offset) throws IOException {
      this.offsets[x + z * 32] = offset;
      this.dataFile.seek((x + z * 32) * 4);
      this.dataFile.writeInt(offset);
   }

   public void setChunkTimestamp(int x, int z, int timestamp) throws IOException {
      this.chunkTimestamps[x + z * 32] = timestamp;
      this.dataFile.seek(4096 + (x + z * 32) * 4);
      this.dataFile.writeInt(timestamp);
   }

   public void close() throws IOException {
      if (this.dataFile != null) {
         this.dataFile.close();
      }
   }

   class ChunkBuffer extends ByteArrayOutputStream {
      public int chunkX;
      public int chunkZ;

      public ChunkBuffer(int x, int z) {
         super(8096);
         this.chunkX = x;
         this.chunkZ = z;
      }

      @Override
      public void close() throws IOException {
         RegionFile.this.write(this.chunkX, this.chunkZ, this.buf, this.count);
      }
   }
}
