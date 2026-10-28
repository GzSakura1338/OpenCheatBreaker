package optifine.xdelta;

public class BitArray {
    public int[] implArray;
    public int size;
    public static final int INT_SIZE = 32;

    public BitArray(int n) {
        int n2 = n / 32 + 1;
        this.implArray = new int[n2];
    }

    public void set(int n, boolean flag) {
        int n2 = n / 32;
        int n3 = 1 << (n & 31);
        if (flag) {
            this.implArray[n2] = this.implArray[n2] | n3;
        } else {
            this.implArray[n2] = this.implArray[n2] & ~n3;
        }
    }

    public boolean get(int n) {
        int n2 = n / 32;
        int n3 = 1 << (n & 31);
        return (this.implArray[n2] & n3) != 0;
    }
}
