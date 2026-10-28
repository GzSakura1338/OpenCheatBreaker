package net.optifine;

public class BlockPos {
    public int intValue;
    public int intValue2;
    public int intValue3;

    public BlockPos(int n, int n2, int n3) {
        this.intValue = n;
        this.intValue2 = n2;
        this.intValue3 = n3;
    }

    public int getX() {
        return this.intValue;
    }

    public int getY() {
        return this.intValue2;
    }

    public int getZ() {
        return this.intValue3;
    }

    public BlockPos add(BlockPos blockPos) {
        return new BlockPos(this.intValue + blockPos.intValue, this.intValue2 + blockPos.intValue2, this.intValue3 + blockPos.intValue3);
    }

    public BlockPos sub(BlockPos blockPos) {
        return new BlockPos(this.intValue - blockPos.intValue, this.intValue2 - blockPos.intValue2, this.intValue3 - blockPos.intValue3);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof BlockPos)) {
            return false;
        }

        BlockPos blockPos = (BlockPos)obj;
        return this.getX() != blockPos.getX() ? false : (this.getY() != blockPos.getY() ? false : this.getZ() == blockPos.getZ());
    }

    @Override
    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    @Override
    public String toString() {
        return "" + this.intValue + ", " + this.intValue2 + ", " + this.intValue3;
    }
}
