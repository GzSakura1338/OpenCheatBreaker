package net.minecraft.realms;

import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.shader.TesselatorVertexState;

public class Tezzelator {
    public static Tessellator t = Tessellator.instance;
    public static Tezzelator instance = new Tezzelator();

    public int end() {
        return t.draw();
    }

    public void color(float f, float f2, float f3, float f4) {
        t.setColorRGBA_F(f, f2, f3, f4);
    }

    public void color(int n, int n2, int n3) {
        t.setColorOpaque(n, n2, n3);
    }

    public TesselatorVertexState sortQuads(float f, float f2, float f3) {
        return t.getVertexState(f, f2, f3);
    }

    public void restoreState(TesselatorVertexState tesselatorVertexState) {
        t.setVertexState(tesselatorVertexState);
    }

    public void vertexUV(double d, double d2, double d3, double d4, double d5) {
        t.addVertexWithUV(d, d2, d3, d4, d5);
    }

    public void color(int n, int n2) {
        t.setColorRGBA_I(n, n2);
    }

    public void color(int n, int n2, int n3, int n4) {
        t.setColorRGBA(n, n2, n3, n4);
    }

    public void tex(double d, double d2) {
        t.setTextureUV(d, d2);
    }

    public void color(byte b, byte b2, byte b3) {
        t.setColorOpaque(b, b2, b3);
    }

    public void setNormal(float f, float f2, float f3) {
        t.setNormal(f, f2, f3);
    }

    public void setColorOpaque_I(int n) {
        t.setColorOpaque_I(n);
    }

    public void addVertex(double d, double d2, double d3) {
        t.addVertex(d, d2, d3);
    }

    public void setBrightness(int n) {
        t.setBrightness(n);
    }

    public void disableColor() {
        t.disableColor();
    }

    public void setColorOpaque_F(float f, float f2, float f3) {
        t.setColorOpaque_F(f, f2, f3);
    }

    public void addTranslation(float f, float f2, float f3) {
        t.addTranslation(f, f2, f3);
    }

    public void startDrawing(int n) {
        t.startDrawing(n);
    }

    public void startDrawingQuads() {
        t.startDrawingQuads();
    }

    public void setTranslation(double d, double d2, double d3) {
        t.setTranslation(d, d2, d3);
    }
}
