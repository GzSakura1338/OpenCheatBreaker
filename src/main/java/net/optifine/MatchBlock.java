package net.optifine;

public class MatchBlock {
    public int blockId = -1;
    public int[] metadatas = null;

    public MatchBlock(int n) {
        this.blockId = n;
    }

    public MatchBlock(int n, int n2) {
        this.blockId = n;
        if (n2 >= 0 && n2 <= 15) {
            this.metadatas = new int[]{n2};
        }
    }

    public MatchBlock(int n, int[] ints) {
        this.blockId = n;
        this.metadatas = ints;
    }

    public int getBlockId() {
        return this.blockId;
    }

    public int[] getMetadatas() {
        return this.metadatas;
    }

    public boolean matches(int n, int n2) {
        if (n != this.blockId) {
            return false;
        }

        if (this.metadatas != null) {
            boolean flag = false;

            for (int i = 0; i < this.metadatas.length; i++) {
                int n3 = this.metadatas[i];
                if (n3 == n2) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                return false;
            }
        }

        return true;
    }

    public void addMetadata(int n) {
        if (this.metadatas != null && n >= 0 && n <= 15) {
            for (int i = 0; i < this.metadatas.length; i++) {
                if (this.metadatas[i] == n) {
                    return;
                }
            }

            this.metadatas = Config.addIntToArray(this.metadatas, n);
        }
    }

    @Override
    public String toString() {
        return "" + this.blockId + ":" + Config.arrayToString(this.metadatas);
    }
}
