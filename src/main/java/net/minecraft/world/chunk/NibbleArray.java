package net.minecraft.world.chunk;

public class NibbleArray {
    public byte[] data;
    public int depthBits;
    public int depthBitsPlusFour;

    public NibbleArray(int n, int n2) {
        this.data = new byte[n >> 1];
        this.depthBits = n2;
        this.depthBitsPlusFour = n2 + 4;
    }

    public NibbleArray(byte[] bytes, int n) {
        this.data = bytes;
        this.depthBits = n;
        this.depthBitsPlusFour = n + 4;
    }

    public int get(int n, int n2, int n3) {
        int n4 = n2 << this.depthBitsPlusFour | n3 << this.depthBits | n;
        int n5 = n4 >> 1;
        int n6 = n4 & 1;
        return n6 == 0 ? this.data[n5] & 15 : this.data[n5] >> 4 & 15;
    }

    public void set(int n, int n2, int n3, int n4) {
        int n5 = n2 << this.depthBitsPlusFour | n3 << this.depthBits | n;
        int n6 = n5 >> 1;
        int n7 = n5 & 1;
        if (n7 == 0) {
            this.data[n6] = (byte)(this.data[n6] & 240 | n4 & 15);
        } else {
            this.data[n6] = (byte)(this.data[n6] & 15 | (n4 & 15) << 4);
        }
    }
}
