package net.minecraft.client.shader;

public class TesselatorVertexState {
    public int[] rawBuffer;
    public int rawBufferIndex;
    public int vertexCount;
    public boolean hasTexture;
    public boolean hasBrightness;
    public boolean hasNormals;
    public boolean hasColor;

    public TesselatorVertexState(int[] ints, int n, int n2, boolean flag, boolean flag2, boolean flag3, boolean flag4) {
        this.rawBuffer = ints;
        this.rawBufferIndex = n;
        this.vertexCount = n2;
        this.hasTexture = flag;
        this.hasBrightness = flag2;
        this.hasNormals = flag3;
        this.hasColor = flag4;
    }

    public int[] getRawBuffer() {
        return this.rawBuffer;
    }

    public int getRawBufferIndex() {
        return this.rawBufferIndex;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    public boolean getHasTexture() {
        return this.hasTexture;
    }

    public boolean getHasBrightness() {
        return this.hasBrightness;
    }

    public boolean getHasNormals() {
        return this.hasNormals;
    }

    public boolean getHasColor() {
        return this.hasColor;
    }

    public void addTessellatorVertexState(TesselatorVertexState tesselatorVertexState) {
        if (tesselatorVertexState != null) {
            if (tesselatorVertexState.hasBrightness != this.hasBrightness
                || tesselatorVertexState.hasColor != this.hasColor
                || tesselatorVertexState.hasNormals != this.hasNormals
                || tesselatorVertexState.hasTexture != this.hasTexture) {
                throw new IllegalArgumentException("Incompatible vertex states");
            }

            int n = this.rawBufferIndex + tesselatorVertexState.rawBufferIndex;
            int[] ints = new int[n];
            System.arraycopy(this.rawBuffer, 0, ints, 0, this.rawBufferIndex);
            System.arraycopy(tesselatorVertexState.rawBuffer, 0, ints, this.rawBufferIndex, tesselatorVertexState.rawBufferIndex);
            this.rawBuffer = ints;
            this.rawBufferIndex = n;
            this.vertexCount = this.vertexCount + tesselatorVertexState.vertexCount;
        }
    }
}
