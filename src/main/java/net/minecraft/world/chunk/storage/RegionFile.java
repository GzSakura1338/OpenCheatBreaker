package net.minecraft.world.chunk.storage;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
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
    public ArrayList sectorFree;

    public RegionFile(File file) {
        this.fileName = file;
        this.sizeDelta = 0;

        try {
            if (file.exists()) {
                this.lastModified = file.lastModified();
            }

            this.dataFile = new RandomAccessFile(file, "rw");
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
            this.sectorFree = new ArrayList(n);

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

    public synchronized DataInputStream getChunkDataInputStream(int n, int n2) {
        if (this.outOfBounds(n, n2)) {
            return null;
        }

        try {
            int offset = this.getOffset(n, n2);
            if (offset == 0) {
                return null;
            } else {
                int n3 = offset >> 8;
                int n4 = offset & 0xFF;
                if (n3 + n4 > this.sectorFree.size()) {
                    return null;
                } else {
                    this.dataFile.seek(n3 * 4096);
                    int readInt = this.dataFile.readInt();
                    if (readInt > 4096 * n4) {
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

    public DataOutputStream getChunkDataOutputStream(int n, int n2) {
        return this.outOfBounds(n, n2) ? null : new DataOutputStream(new DeflaterOutputStream(new RegionFile.ChunkBuffer(n, n2)));
    }

    public synchronized void write(int n, int n2, byte[] bytes, int n3) {
        try {
            int offset = this.getOffset(n, n2);
            int n4 = offset >> 8;
            int n5 = offset & 0xFF;
            int n6 = (n3 + 5) / 4096 + 1;
            if (n6 >= 256) {
                return;
            }

            if (n4 != 0 && n5 == n6) {
                this.write(n4, bytes, n3);
            } else {
                for (int i = 0; i < n5; i++) {
                    this.sectorFree.set(n4 + i, true);
                }

                int indexOf = this.sectorFree.indexOf(true);
                int n7 = 0;
                if (indexOf != -1) {
                    for (int i = indexOf; i < this.sectorFree.size(); i++) {
                        if (n7 != 0) {
                            if ((Boolean)this.sectorFree.get(i)) {
                                n7++;
                            } else {
                                n7 = 0;
                            }
                        } else if ((Boolean)this.sectorFree.get(i)) {
                            indexOf = i;
                            n7 = 1;
                        }

                        if (n7 >= n6) {
                            break;
                        }
                    }
                }

                if (n7 >= n6) {
                    n4 = indexOf;
                    this.setOffset(n, n2, indexOf << 8 | n6);

                    for (int i = 0; i < n6; i++) {
                        this.sectorFree.set(n4 + i, false);
                    }

                    this.write(n4, bytes, n3);
                } else {
                    this.dataFile.seek(this.dataFile.length());
                    n4 = this.sectorFree.size();

                    for (int i = 0; i < n6; i++) {
                        this.dataFile.write(emptySector);
                        this.sectorFree.add(false);
                    }

                    this.sizeDelta += 4096 * n6;
                    this.write(n4, bytes, n3);
                    this.setOffset(n, n2, n4 << 8 | n6);
                }
            }

            this.setChunkTimestamp(n, n2, (int)(MinecraftServer.getCurrentTimeMillis() / 1000L));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void write(int n, byte[] bytes, int n2) throws IOException {
        this.dataFile.seek(n * 4096);
        this.dataFile.writeInt(n2 + 1);
        this.dataFile.writeByte(2);
        this.dataFile.write(bytes, 0, n2);
    }

    public boolean outOfBounds(int n, int n2) {
        return n < 0 || n >= 32 || n2 < 0 || n2 >= 32;
    }

    public int getOffset(int n, int n2) {
        return this.offsets[n + n2 * 32];
    }

    public boolean isChunkSaved(int n, int n2) {
        return this.getOffset(n, n2) != 0;
    }

    public void setOffset(int n, int n2, int n3) throws IOException {
        this.offsets[n + n2 * 32] = n3;
        this.dataFile.seek((n + n2 * 32) * 4);
        this.dataFile.writeInt(n3);
    }

    public void setChunkTimestamp(int n, int n2, int n3) throws IOException {
        this.chunkTimestamps[n + n2 * 32] = n3;
        this.dataFile.seek(4096 + (n + n2 * 32) * 4);
        this.dataFile.writeInt(n3);
    }

    public void close() throws IOException {
        if (this.dataFile != null) {
            this.dataFile.close();
        }
    }

    public class ChunkBuffer extends ByteArrayOutputStream {
        public int chunkX;
        public int chunkZ;

        public ChunkBuffer(int n, int n2) {
            super(8096);
            this.chunkX = n;
            this.chunkZ = n2;
        }

        @Override
        public void close() throws IOException {
            RegionFile.this.write(this.chunkX, this.chunkZ, this.buf, this.count);
        }
    }
}
