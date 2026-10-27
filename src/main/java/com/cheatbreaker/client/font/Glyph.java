package com.cheatbreaker.client.font;

public class Glyph {
   public int textureY;
   public int width;
   public int height;
   public AbstractFontRenderer abstractFontRenderer;
   public int textureX;

   public Glyph(AbstractFontRenderer abstractFontRenderer) {
      this.abstractFontRenderer = abstractFontRenderer;
   }
}
