package net.minecraft.client.renderer;

import java.nio.Buffer;
import java.nio.IntBuffer;
import org.lwjgl.opengl.GL11;

public class RenderList {
    public int renderChunkX;
    public int renderChunkY;
    public int renderChunkZ;
    public double cameraX;
    public double cameraY;
    public double cameraZ;
    public IntBuffer glLists = GLAllocation.createDirectIntBuffer(65536);
    public boolean valid;
    public boolean bufferFlipped;

    public void setupRenderList(int n, int n2, int n3, double d, double d2, double d3) {
        this.valid = true;
        ((Buffer)this.glLists).clear();
        this.renderChunkX = n;
        this.renderChunkY = n2;
        this.renderChunkZ = n3;
        this.cameraX = d;
        this.cameraY = d2;
        this.cameraZ = d3;
    }

    public boolean rendersChunk(int n, int n2, int n3) {
        return this.valid && n == this.renderChunkX && n2 == this.renderChunkY && n3 == this.renderChunkZ;
    }

    public void addGLRenderList(int n) {
        this.glLists.put(n);
        if (this.glLists.remaining() == 0) {
            this.callLists();
        }
    }

    public void callLists() {
        if (this.valid) {
            if (!this.bufferFlipped) {
                ((Buffer)this.glLists).flip();
                this.bufferFlipped = true;
            }

            if (this.glLists.remaining() > 0) {
                GL11.glPushMatrix();
                GL11.glTranslatef((float)(this.renderChunkX - this.cameraX), (float)(this.renderChunkY - this.cameraY), (float)(this.renderChunkZ - this.cameraZ));
                GL11.glCallLists(this.glLists);
                GL11.glPopMatrix();
            }
        }
    }

    public void resetList() {
        this.valid = false;
        this.bufferFlipped = false;
    }
}
