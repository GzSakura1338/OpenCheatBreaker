package net.optifine;

public class CacheLocalByte {
    public int maxX = 18;
    public int maxY = 128;
    public int maxZ = 18;
    public int offsetX = 0;
    public int offsetY = 0;
    public int offsetZ = 0;
    public byte[][][] cache = (byte[][][])null;
    public byte[] lastZs = null;
    public int lastDz = 0;

    public CacheLocalByte(int n, int n2, int n3) {
        this.maxX = n;
        this.maxY = n2;
        this.maxZ = n3;
        this.cache = new byte[n][n2][n3];
        this.resetCache();
    }

    public void resetCache() {
        for (int i = 0; i < this.maxX; i++) {
            byte[][] byteses = this.cache[i];

            for (int j = 0; j < this.maxY; j++) {
                byte[] bytes = byteses[j];

                for (int k = 0; k < this.maxZ; k++) {
                    bytes[k] = -1;
                }
            }
        }
    }

    public void setOffset(int n, int n2, int n3) {
        this.offsetX = n;
        this.offsetY = n2;
        this.offsetZ = n3;
        this.resetCache();
    }

    public byte get(int n, int n2, int n3) {
        try {
            this.lastZs = this.cache[n - this.offsetX][n2 - this.offsetY];
            this.lastDz = n3 - this.offsetZ;
            return this.lastZs[this.lastDz];
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void setLast(byte b) {
        try {
            this.lastZs[this.lastDz] = b;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
