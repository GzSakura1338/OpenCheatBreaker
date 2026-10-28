package com.cheatbreaker.client.font;

public class Glyph {
    public int height;
    public int textureX;
    public AbstractFontRenderer abstractFontRenderer;
    public int textureY;
    public int width;

    public Glyph(AbstractFontRenderer abstractFontRenderer) {
        this.abstractFontRenderer = abstractFontRenderer;
    }
}
