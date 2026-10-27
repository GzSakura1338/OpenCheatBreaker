package net.minecraft.client.gui;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.StringEvent2;
import com.cheatbreaker.client.font.CachedString;
import com.cheatbreaker.client.font.CachedStringPart;
import com.cheatbreaker.client.font.StringCacheKey;
import com.cheatbreaker.client.font.StringCacheMap;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.CustomColors;
import net.optifine.render.GlBlendState;
import net.optifine.util.FontUtils;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

public class FontRenderer implements IResourceManagerReloadListener {
   public static Tessellator tessellator = Tessellator.getInstance();
   public float[] floatArray = new float[256];
   public int FONT_HEIGHT = 9;
   public Random fontRandom = new Random();
   public byte[] glyphWidth = new byte[65536];
   public int[] colorCode = new int[32];
   public StringCacheMap<StringCacheKey, CachedString> stringCacheMap = new StringCacheMap<>();
   public float posX;
   public float posY;
   public boolean flag = true;
   public boolean bidiFlag;
   public float red;
   public float blue;
   public float green;
   public float alpha;
   public int textColor;
   public boolean randomStyle;
   public boolean boldStyle;
   public boolean italicStyle;
   public boolean underlineStyle;
   public boolean strikethroughStyle;
   public float offsetBold = 1.0F;
   public float[] charWidthFloat = new float[256];
   public boolean blend = true;
   public GlBlendState oldBlendState = new GlBlendState();
   public GameSettings gameSettings;
   public ResourceLocation locationFontTextureBase;
   public int intValue;
   public ResourceLocation resourceLocation;
   public int intValue2;
   public ResourceLocation locationFontTexture;
   public boolean[] booleanArray;
   public static WorldRenderer worldRenderer = tessellator.getWorldRenderer();
   public List<CachedStringPart> cachedStringPartList;
   public long longValue;
   public static ResourceLocation[] unicodePageLocations = new ResourceLocation[256];
   public TextureManager renderEngine;
   public static FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
   public boolean unicodeFlag;

   public FontRenderer(GameSettings gameSettingsIn, ResourceLocation location, TextureManager textureManagerIn, boolean unicode) {
      this.gameSettings = gameSettingsIn;
      this.locationFontTextureBase = location;
      this.locationFontTexture = location;
      this.renderEngine = textureManagerIn;
      this.unicodeFlag = unicode;
      this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);

      for (int i = 0; i < 32; i++) {
         Minecraft.getMinecraft().stream.run11();
         int n = (i >> 3 & 1) * 85;
         int n2 = (i >> 2 & 1) * 170 + n;
         int n3 = (i >> 1 & 1) * 170 + n;
         int n4 = (i >> 0 & 1) * 170 + n;
         if (i == 6) {
            n2 += 85;
         }

         if (gameSettingsIn.anaglyph) {
            int n5 = (n2 * 30 + n3 * 59 + n4 * 11) / 100;
            int n6 = (n2 * 30 + n3 * 70) / 100;
            int n7 = (n2 * 30 + n4 * 70) / 100;
            n2 = n5;
            n3 = n6;
            n4 = n7;
         }

         if (i >= 16) {
            n2 /= 4;
            n3 /= 4;
            n4 /= 4;
         }

         this.colorCode[i] = (n2 & 0xFF) << 16 | (n3 & 0xFF) << 8 | n4 & 0xFF;
      }

      this.readGlyphSizes();
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);
      Arrays.fill(unicodePageLocations, null);
      this.readFontTexture();
      this.readGlyphSizes();
   }

   public void readFontTexture() {
      BufferedImage bufferedImage;
      try {
         bufferedImage = TextureUtil.readBufferedImage(this.getResourceInputStream(this.locationFontTexture));
      } catch (IOException e) {
         throw new RuntimeException(e);
      }

      Properties readFontProperties = FontUtils.readFontProperties(this.locationFontTexture);
      this.blend = FontUtils.readBoolean(readFontProperties, "blend", false);
      int width = bufferedImage.getWidth();
      int height = bufferedImage.getHeight();
      int n = width / 16;
      int n2 = height / 16;
      float f = width / 128.0F;
      float limit = Config.limit(f, 1.0F, 2.0F);
      this.offsetBold = 1.0F / limit;
      float readFloat = FontUtils.readFloat(readFontProperties, "offsetBold", -1.0F);
      if (readFloat >= 0.0F) {
         this.offsetBold = readFloat;
      }

      int[] ints = new int[width * height];
      bufferedImage.getRGB(0, 0, width, height, ints, 0, width);

      for (int i = 0; i < 256; i++) {
         int n3 = i % 16;
         int n4 = i / 16;
         int n5 = 0;

         for (n5 = n - 1; n5 >= 0; n5--) {
            int n6 = n3 * n + n5;
            boolean flag = true;

            for (int j = 0; j < n2 && flag; j++) {
               int n7 = (n4 * n2 + j) * width;
               int n8 = ints[n6 + n7];
               int n9 = n8 >> 24 & 0xFF;
               if (n9 > 16) {
                  flag = false;
               }
            }

            if (!flag) {
               break;
            }
         }

         if (i == 32) {
            n5 = n <= 8 ? (int)(2.0F * f) : (int)(1.5F * f);
         }

         this.floatArray[i] = (n5 + 1) / f + 1.0F;
      }

      FontUtils.readCustomCharWidths(readFontProperties, this.floatArray);
   }

   public void readGlyphSizes() {
      InputStream inputStream = null;

      try {
         inputStream = this.getResourceInputStream(new ResourceLocation("font/glyph_sizes.bin"));
         inputStream.read(this.glyphWidth);
      } catch (IOException e) {
         throw new RuntimeException(e);
      } finally {
         IOUtils.closeQuietly(inputStream);
      }
   }

   public float renderChar(char ch, boolean italic) {
      if (ch == ' ') {
         this.run2();
         return !this.unicodeFlag ? this.floatArray[ch] : 4.0F;
      } else {
         int indexOf = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
            .indexOf(ch);
         return indexOf != -1 && !this.unicodeFlag ? this.renderDefaultChar(indexOf, italic) : this.renderUnicodeChar(ch, italic);
      }
   }

   public float renderDefaultChar(int ch, boolean italic) {
      if (this.resourceLocation == null && !this.booleanArray[this.intValue]) {
         this.resourceLocation = this.locationFontTexture;
         if (!worldRenderer.isDrawing) {
            int glGenLists = GL11.glGenLists(1);
            this.cachedStringPartList.add(new CachedStringPart(glGenLists, this.locationFontTexture));
            GL11.glNewList(glGenLists, 4864);
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         }
      }

      if (this.resourceLocation == this.locationFontTexture) {
         float f = ch % 16 * 8;
         float f2 = ch / 16 * 8;
         float f3 = italic ? 1.0F : 0.0F;
         float f4 = 7.99F;
         float f5 = this.textColor == -1 ? this.red : (this.textColor >> 16) / 255.0F;
         float f6 = this.textColor == -1 ? this.blue : (this.textColor >> 8 & 0xFF) / 255.0F;
         float f7 = this.textColor == -1 ? this.green : (this.textColor & 0xFF) / 255.0F;
         float alpha = this.alpha;
         worldRenderer.pos(this.posX + f3, this.posY, 0.0).tex(f / 128.0F, f2 / 128.0F).color(f5, f6, f7, alpha).endVertex();
         worldRenderer.pos(this.posX - f3, this.posY + 7.99F, 0.0).tex(f / 128.0F, (f2 + 7.99F) / 128.0F).color(f5, f6, f7, alpha).endVertex();
         worldRenderer.pos(this.posX + f4 - 1.0F - f3, this.posY + 7.99F, 0.0)
            .tex((f + f4 - 1.0F) / 128.0F, (f2 + 7.99F) / 128.0F)
            .color(f5, f6, f7, alpha)
            .endVertex();
         worldRenderer.pos(this.posX + f4 - 1.0F + f3, this.posY, 0.0)
            .tex((f + f4 - 1.0F) / 128.0F, f2 / 128.0F)
            .color(f5, f6, f7, alpha)
            .endVertex();
         this.run2();
      }

      return this.floatArray[ch];
   }

   public ResourceLocation getUnicodePageLocation(int page) {
      if (unicodePageLocations[page] == null) {
         unicodePageLocations[page] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", page));
         unicodePageLocations[page] = FontUtils.getHdFontLocation(unicodePageLocations[page]);
      }

      return unicodePageLocations[page];
   }

   public float renderUnicodeChar(char ch, boolean italic) {
      if (this.glyphWidth[ch] == 0) {
         this.run2();
         return 0.0F;
      }

      ResourceLocation unicodePageLocation = this.getUnicodePageLocation(ch / 256);
      int n = this.glyphWidth[ch] >>> 4;
      int n2 = this.glyphWidth[ch] & 15;
      int n3;
      float f = n3 = n & 15;
      float f2 = n2 + 1;
      if (this.resourceLocation == null && !this.booleanArray[this.intValue]) {
         this.resourceLocation = unicodePageLocation;
         if (!worldRenderer.isDrawing) {
            int glGenLists = GL11.glGenLists(1);
            this.cachedStringPartList.add(new CachedStringPart(glGenLists, unicodePageLocation));
            GL11.glNewList(glGenLists, 4864);
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         }
      }

      if (this.resourceLocation == unicodePageLocation) {
         float f3 = ch % 16 * 16 + f;
         float f4 = (ch & 255) / 16 * 16;
         float f5 = f2 - f - 0.02F;
         float f6 = italic ? 1.0F : 0.0F;
         float f7 = this.textColor == -1 ? this.red : (this.textColor >> 16) / 255.0F;
         float f8 = this.textColor == -1 ? this.blue : (this.textColor >> 8 & 0xFF) / 255.0F;
         float f9 = this.textColor == -1 ? this.green : (this.textColor & 0xFF) / 255.0F;
         float alpha = this.alpha;
         worldRenderer.pos(this.posX + f6, this.posY, 0.0).tex(f3 / 256.0F, f4 / 256.0F).color(f7, f8, f9, alpha).endVertex();
         worldRenderer.pos(this.posX - f6, this.posY + 7.99F, 0.0)
            .tex(f3 / 256.0F, (f4 + 15.98F) / 256.0F)
            .color(f7, f8, f9, alpha)
            .endVertex();
         worldRenderer.pos(this.posX + f5 / 2.0F - f6, this.posY + 7.99F, 0.0)
            .tex((f3 + f5) / 256.0F, (f4 + 15.98F) / 256.0F)
            .color(f7, f8, f9, alpha)
            .endVertex();
         worldRenderer.pos(this.posX + f5 / 2.0F + f6, this.posY, 0.0)
            .tex((f3 + f5) / 256.0F, f4 / 256.0F)
            .color(f7, f8, f9, alpha)
            .endVertex();
         this.run2();
      }

      return (f2 - f) / 2.0F + 1.0F;
   }

   public int drawStringWithShadow(String text, float x, float y, int color) {
      return this.drawString(text, x, y, color, true);
   }

   public int drawString(String text, float x, float y, int color, boolean dropShadow) {
      this.enableAlpha();
      if (this.blend) {
         GlStateManager.getBlendState(this.oldBlendState);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
      }

      this.resetStyles();
      int n;
      if (dropShadow) {
         n = this.renderString(text, x + 1.0F, y + 1.0F, color, true);
         n = Math.max(n, this.renderString(text, x, y, color, false));
      } else {
         n = this.renderString(text, x, y, color, false);
      }

      if (this.blend) {
         GlStateManager.setBlendState(this.oldBlendState);
      }

      return n;
   }

   public String bidiReorder(String text) {
      try {
         Bidi bidi = new Bidi(new ArabicShaping(8).shape(text), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException e) {
         return text;
      }
   }

   public void resetStyles() {
      this.randomStyle = false;
      this.boldStyle = false;
      this.italicStyle = false;
      this.underlineStyle = false;
      this.strikethroughStyle = false;
   }

   public int renderStringAligned(String text, int x, int y, int width, int color, boolean dropShadow) {
      if (this.bidiFlag) {
         int stringWidth = this.getStringWidth(this.bidiReorder(text));
         x = x + width - stringWidth;
      }

      return this.renderString(text, x, y, color, dropShadow);
   }

   public int renderString(String text, float x, float y, int color, boolean dropShadow) {
      if (text == null) {
         return 0;
      }

      StringEvent2 stringEvent2 = new StringEvent2(text);
      CheatBreaker.getInstance().getEventBus().handleEvent(stringEvent2);
      text = stringEvent2.getText();
      if (this.bidiFlag) {
         text = this.bidiReorder(text);
      }

      if ((color & -67108864) == 0) {
         color |= -16777216;
      }

      if (dropShadow) {
         color = (color & 16579836) >> 2 | color & 0xFF000000;
      }

      this.red = (color >> 16 & 0xFF) / 255.0F;
      this.blue = (color >> 8 & 0xFF) / 255.0F;
      this.green = (color & 0xFF) / 255.0F;
      this.alpha = (color >> 24 & 0xFF) / 255.0F;
      this.posX = x;
      this.posY = y;
      this.render(text, dropShadow, color);
      return (int)this.posX;
   }

   public int getStringWidth(String text) {
      if (text == null) {
         return 0;
      }

      StringEvent2 stringEvent2 = new StringEvent2(text);
      CheatBreaker.getInstance().getEventBus().handleEvent(stringEvent2);
      text = stringEvent2.getText();
      float f = 0.0F;
      boolean flag = false;

      for (int i = 0; i < text.length(); i++) {
         char charAt = text.charAt(i);
         float charWidthFloat = this.getCharWidthFloat(charAt);
         if (charWidthFloat < 0.0F && i < text.length() - 1) {
            if ((charAt = text.charAt(++i)) == 'l' || charAt == 'L') {
               flag = true;
            } else if (charAt == 'r' || charAt == 'R') {
               flag = false;
            }

            charWidthFloat = 0.0F;
         }

         f += charWidthFloat;
         if (flag && charWidthFloat > 0.0F) {
            f += this.unicodeFlag ? 1.0F : this.offsetBold;
         }
      }

      return Math.round(f);
   }

   public int getCharWidth(char character) {
      return Math.round(this.getCharWidthFloat(character));
   }

   public float getCharWidthFloat(char c) {
      if (c == 167) {
         return -1.0F;
      } else if (c == ' ') {
         return this.floatArray[32];
      } else {
         int indexOf = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
            .indexOf(c);
         if (c > 0 && indexOf != -1 && !this.unicodeFlag) {
            return this.floatArray[indexOf];
         } else if (this.glyphWidth[c] != 0) {
            int n = this.glyphWidth[c] >>> 4;
            int n2 = this.glyphWidth[c] & 15;
            n2++;
            int n3;
            return (n2 - (n3 = n & 15)) / 2 + 1;
         } else {
            return 0.0F;
         }
      }
   }

   public String trimStringToWidth(String text, int width) {
      return this.trimStringToWidth(text, width, false);
   }

   public String trimStringToWidth(String text, int width, boolean reverse) {
      StringBuilder sb = new StringBuilder();
      float f = 0.0F;
      int n = reverse ? text.length() - 1 : 0;
      int n2 = reverse ? -1 : 1;
      boolean flag = false;
      boolean flag2 = false;

      for (int i = n; i >= 0 && i < text.length() && f < width; i += n2) {
         char charAt = text.charAt(i);
         float charWidthFloat = this.getCharWidthFloat(charAt);
         if (flag) {
            flag = false;
            if (charAt == 'l' || charAt == 'L') {
               flag2 = true;
            } else if (charAt == 'r' || charAt == 'R') {
               flag2 = false;
            }
         } else if (charWidthFloat < 0.0F) {
            flag = true;
         } else {
            f += charWidthFloat;
            if (flag2) {
               f++;
            }
         }

         if (f > width) {
            break;
         }

         if (reverse) {
            sb.insert(0, charAt);
         } else {
            sb.append(charAt);
         }
      }

      return sb.toString();
   }

   public String trimStringNewline(String text) {
      while (text != null && text.endsWith("\n")) {
         text = text.substring(0, text.length() - 1);
      }

      return text;
   }

   public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor) {
      if (this.blend) {
         GlStateManager.getBlendState(this.oldBlendState);
         GlStateManager.enableBlend();
         GlStateManager.blendFunc(770, 771);
      }

      this.resetStyles();
      this.textColor = textColor;
      str = this.trimStringNewline(str);
      this.renderSplitString(str, x, y, wrapWidth, false);
      if (this.blend) {
         GlStateManager.setBlendState(this.oldBlendState);
      }
   }

   public void renderSplitString(String str, int x, int y, int wrapWidth, boolean addShadow) {
      for (String str2 : this.listFormattedStringToWidth(str, wrapWidth)) {
         this.renderStringAligned(str2, x, y, wrapWidth, this.textColor, addShadow);
         y += this.FONT_HEIGHT;
      }
   }

   public int splitStringWidth(String str, int maxLength) {
      return this.FONT_HEIGHT * this.listFormattedStringToWidth(str, maxLength).size();
   }

   public void setUnicodeFlag(boolean unicodeFlagIn) {
      this.unicodeFlag = unicodeFlagIn;
   }

   public boolean getUnicodeFlag() {
      return this.unicodeFlag;
   }

   public void setBidiFlag(boolean bidiFlagIn) {
      this.bidiFlag = bidiFlagIn;
   }

   public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
      return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
   }

   public String wrapFormattedStringToWidth(String str, int wrapWidth) {
      if (str.length() <= 1) {
         return str;
      }

      int sizeStringToWidth = this.sizeStringToWidth(str, wrapWidth);
      if (str.length() <= sizeStringToWidth) {
         return str;
      }

      String substring = str.substring(0, sizeStringToWidth);
      char charAt = str.charAt(sizeStringToWidth);
      boolean flag = charAt == ' ' || charAt == '\n';
      String str2 = getFormatFromString(substring) + str.substring(sizeStringToWidth + (flag ? 1 : 0));
      return substring + "\n" + this.wrapFormattedStringToWidth(str2, wrapWidth);
   }

   public int sizeStringToWidth(String str, int wrapWidth) {
      int n = str.length();
      float f = 0.0F;
      int n2 = -1;
      boolean flag = false;

      int n3;
      for (n3 = 0; n3 < n; n3++) {
         char charAt = str.charAt(n3);
         switch (charAt) {
            case '\n':
               n3--;
               break;
            case ' ':
               n2 = n3;
            default:
               f += this.getCharWidthFloat(charAt);
               if (flag) {
                  f++;
               }
               break;
            case '§':
               if (n3 < n - 1) {
                  char c;
                  if ((c = str.charAt(++n3)) == 'l' || c == 'L') {
                     flag = true;
                  } else if (c == 'r' || c == 'R' || isFormatColor(c)) {
                     flag = false;
                  }
               }
         }

         if (charAt == '\n') {
            n2 = ++n3;
            break;
         }

         if (Math.round(f) > wrapWidth) {
            break;
         }
      }

      return n3 != n && n2 != -1 && n2 < n3 ? n2 : n3;
   }

   public static boolean isFormatColor(char colorChar) {
      return colorChar >= '0' && colorChar <= '9' || colorChar >= 'a' && colorChar <= 'f' || colorChar >= 'A' && colorChar <= 'F';
   }

   public static boolean isFormatSpecial(char formatChar) {
      return formatChar >= 'k' && formatChar <= 'o' || formatChar >= 'K' && formatChar <= 'O' || formatChar == 'r' || formatChar == 'R';
   }

   public static String getFormatFromString(String text) {
      String str = "";
      int n = -1;
      int n2 = text.length();

      while ((n = text.indexOf(167, n + 1)) != -1) {
         if (n < n2 - 1) {
            char charAt = text.charAt(n + 1);
            if (isFormatColor(charAt)) {
               str = "§" + charAt;
            } else if (isFormatSpecial(charAt)) {
               str = str + "§" + charAt;
            }
         }
      }

      return str;
   }

   public boolean getBidiFlag() {
      return this.bidiFlag;
   }

   public int getColorCode(char character) {
      int indexOf = "0123456789abcdef".indexOf(character);
      if (indexOf >= 0 && indexOf < this.colorCode.length) {
         int n = this.colorCode[indexOf];
         if (Config.isCustomColors()) {
            n = CustomColors.getTextColor(indexOf, n);
         }

         return n;
      } else {
         return 16777215;
      }
   }

   public void enableAlpha() {
      GlStateManager.enableAlpha();
   }

   public InputStream getResourceInputStream(ResourceLocation resourceLocation) throws IOException {
      return Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
   }

   public int[] getColorCode() {
      return this.colorCode;
   }

   public int computeInt(String str, float f, float f2, int n) {
      float stringWidth = this.getStringWidth(str);
      return this.drawString(str, f - stringWidth / 2.0F, f2, n);
   }

   public int drawString(String str, float f, float f2, int n) {
      return !this.flag ? 0 : this.drawString(str, f, f2, n, false);
   }

   public void run() {
      ObjectIterator objectIterator = this.stringCacheMap.values().iterator();

      while (objectIterator.hasNext()) {
         CachedString cachedString = (CachedString)objectIterator.next();
         cachedString.releaseParts();
      }

      this.stringCacheMap.clear();
   }

   public void render(String str, boolean flag, int n) {
      CachedString lookup = this.stringCacheMap.lookup(str, n, flag);
      if (lookup != null) {
         lookup.setExpiryTime(this.longValue);
         GL11.glGetFloat(2816, floatBuffer);
         GL11.glPushMatrix();
         GL11.glTranslatef(this.posX, this.posY, 0.0F);
         lookup.prepareParts();
         GL11.glPopMatrix();
         GL11.glColor4f(floatBuffer.get(0), floatBuffer.get(1), floatBuffer.get(2), floatBuffer.get(3));
         this.posX = this.posX + lookup.getWidth();
      } else {
         float posX = this.posX;
         float posY = this.posY;
         this.posX = 0.0F;
         this.posY = 0.0F;
         this.cachedStringPartList = new ArrayList<>();
         this.booleanArray = new boolean[str.length()];
         this.intValue2 = str.length();
         boolean flag2 = false;

         while (this.intValue2 >= 0) {
            if (this.intValue2 == 0) {
               this.intValue2 = -1;
            }

            this.posX = 0.0F;
            this.resourceLocation = null;
            this.textColor = -1;
            this.resetStyles();

            for (this.intValue = 0; this.intValue < str.length(); this.intValue++) {
               char charAt = str.charAt(this.intValue);
               if (charAt == 167 && this.intValue + 1 < str.length()) {
                  int indexOf = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(str.charAt(this.intValue + 1)));
                  if (indexOf < 16) {
                     this.randomStyle = false;
                     this.boldStyle = false;
                     this.strikethroughStyle = false;
                     this.underlineStyle = false;
                     this.italicStyle = false;
                     if (indexOf < 0 || indexOf > 15) {
                        indexOf = 15;
                     }

                     if (flag) {
                        indexOf += 16;
                     }

                     this.textColor = this.colorCode[indexOf];
                  } else if (indexOf == 16) {
                     this.randomStyle = true;
                     flag2 = true;
                  } else if (indexOf == 17) {
                     this.boldStyle = true;
                  } else if (indexOf == 18) {
                     this.strikethroughStyle = true;
                  } else if (indexOf == 19) {
                     this.underlineStyle = true;
                  } else if (indexOf == 20) {
                     this.italicStyle = true;
                  } else if (indexOf == 21) {
                     this.randomStyle = false;
                     this.boldStyle = false;
                     this.strikethroughStyle = false;
                     this.underlineStyle = false;
                     this.italicStyle = false;
                     this.textColor = -1;
                  }

                  this.run2();
                  this.intValue++;
                  this.run2();
               } else {
                  int indexOf = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                     .indexOf(charAt);
                  if (this.randomStyle && indexOf != -1) {
                     int charWidth = this.getCharWidth(charAt);

                     char c;
                     while (
                        charWidth
                           != this.getCharWidth(
                              c = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                                 .charAt(
                                    indexOf = this.fontRandom
                                       .nextInt(
                                          "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                                             .length()
                                       )
                                 )
                           )
                     ) {
                     }

                     charAt = c;
                  }

                  float f = indexOf != -1 && !this.unicodeFlag ? this.offsetBold : 0.5F;
                  boolean flag3 = (charAt == 0 || indexOf == -1 || this.unicodeFlag) && flag;
                  if (flag3) {
                     this.posX -= f;
                     this.posY -= f;
                  }

                  float renderChar = this.renderChar(charAt, this.italicStyle);
                  if (flag3) {
                     this.posX += f;
                     this.posY += f;
                  }

                  if (this.boldStyle) {
                     this.posX += f;
                     if (flag3) {
                        this.posX -= f;
                        this.posY -= f;
                     }

                     this.renderChar(charAt, this.italicStyle);
                     this.posX -= f;
                     if (flag3) {
                        this.posX += f;
                        this.posY += f;
                     }

                     renderChar += f;
                  }

                  if (this.intValue2 == -1 && (this.strikethroughStyle || this.underlineStyle)) {
                     if (!worldRenderer.isDrawing) {
                        int glGenLists = GL11.glGenLists(1);
                        this.cachedStringPartList.add(new CachedStringPart(glGenLists, null));
                        GL11.glNewList(glGenLists, 4864);
                        worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
                     }

                     float f2 = this.textColor == -1 ? this.red : (this.textColor >> 16) / 255.0F;
                     float f3 = this.textColor == -1 ? this.blue : (this.textColor >> 8 & 0xFF) / 255.0F;
                     float f4 = this.textColor == -1 ? this.green : (this.textColor & 0xFF) / 255.0F;
                     float alpha = this.alpha;
                     if (this.strikethroughStyle) {
                        worldRenderer.pos(this.posX, this.posY + this.FONT_HEIGHT / 2, 0.0).color(f2, f3, f4, alpha).endVertex();
                        worldRenderer.pos(this.posX + renderChar, this.posY + this.FONT_HEIGHT / 2, 0.0).color(f2, f3, f4, alpha).endVertex();
                        worldRenderer.pos(this.posX + renderChar, this.posY + this.FONT_HEIGHT / 2 - 1.0F, 0.0).color(f2, f3, f4, alpha).endVertex();
                        worldRenderer.pos(this.posX, this.posY + this.FONT_HEIGHT / 2 - 1.0F, 0.0).color(f2, f3, f4, alpha).endVertex();
                     }

                     if (this.underlineStyle) {
                        worldRenderer.pos(this.posX - 1.0F, this.posY + this.FONT_HEIGHT, 0.0).color(f2, f3, f4, alpha).endVertex();
                        worldRenderer.pos(this.posX + renderChar, this.posY + this.FONT_HEIGHT, 0.0).color(f2, f3, f4, alpha).endVertex();
                        worldRenderer.pos(this.posX + renderChar, this.posY + this.FONT_HEIGHT - 1.0F, 0.0).color(f2, f3, f4, alpha).endVertex();
                        worldRenderer.pos(this.posX - 1.0F, this.posY + this.FONT_HEIGHT - 1.0F, 0.0).color(f2, f3, f4, alpha).endVertex();
                     }
                  }

                  this.posX += renderChar;
               }
            }

            if (worldRenderer.isDrawing) {
               tessellator.draw();
               GL11.glEndList();
            }
         }

         lookup = new CachedString(this.cachedStringPartList, this.posX, this.longValue, flag2);
         this.stringCacheMap.put(new StringCacheKey(str, n, flag), lookup);
         GL11.glPushMatrix();
         GL11.glTranslatef(posX, posY, 0.0F);
         lookup.prepareParts();
         GL11.glPopMatrix();
         this.cachedStringPartList = null;
         this.posX += posX;
      }
   }

   public void run2() {
      if (!this.booleanArray[this.intValue]) {
         this.booleanArray[this.intValue] = true;
         this.intValue2--;
      }
   }

   public void run3() {
      byte b = 10;
      long l = this.longValue - 20 * b;
      ObjectIterator objectIterator = this.stringCacheMap.values().iterator();

      while (objectIterator.hasNext()) {
         CachedString cachedString = (CachedString)objectIterator.next();
         if (cachedString.isShadow() || cachedString.getExpiryTime() < l) {
            cachedString.releaseParts();
            objectIterator.remove();
         }
      }

      if (this.longValue % 50L == 0L) {
         this.stringCacheMap.trim();
      }

      this.longValue++;
   }

   public int computeInt2(String str, float f, float f2, int n) {
      float stringWidth = this.getStringWidth(str);
      return this.drawStringWithShadow(str, f - stringWidth / 2.0F, f2, n);
   }
}
