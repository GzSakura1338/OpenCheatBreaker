package net.minecraft.block;

public class BlockEventData {
    public int coordX;
    public int coordY;
    public int coordZ;
    public int eventID;
    public int eventParameter;
    public Block block;

    public BlockEventData(int n, int n2, int n3, Block block, int n4, int n5) {
        this.coordX = n;
        this.coordY = n2;
        this.coordZ = n3;
        this.eventID = n4;
        this.eventParameter = n5;
        this.block = block;
    }

    public int getCoordX() {
        return this.coordX;
    }

    public int getCoordY() {
        return this.coordY;
    }

    public int getCoordZ() {
        return this.coordZ;
    }

    public int getEventID() {
        return this.eventID;
    }

    public int getEventParameter() {
        return this.eventParameter;
    }

    public Block getBlock() {
        return this.block;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BlockEventData)) {
            return false;
        }

        BlockEventData blockEventData = (BlockEventData)obj;
        return this.coordX == blockEventData.coordX
            && this.coordY == blockEventData.coordY
            && this.coordZ == blockEventData.coordZ
            && this.eventID == blockEventData.eventID
            && this.eventParameter == blockEventData.eventParameter
            && this.block == blockEventData.block;
    }

    @Override
    public String toString() {
        return "TE(" + this.coordX + "," + this.coordY + "," + this.coordZ + ")," + this.eventID + "," + this.eventParameter + "," + this.block;
    }
}
