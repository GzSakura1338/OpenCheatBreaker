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
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import net.optifine.CustomColorizer;
import net.optifine.FontUtils;
import org.lwjgl.opengl.GL11;

public class FontRenderer implements IResourceManagerReloadListener {
    public static ResourceLocation[] unicodePageLocations = new ResourceLocation[256];
    public float[] floatArray = new float[256];
    public int FONT_HEIGHT = 9;
    public Random fontRandom = new Random();
    public byte[] glyphWidth = new byte[65536];
    public int[] colorCode = new int[32];
    public boolean enabled = true;
    public float offsetBold = 1.0F;
    public float posX;
    public float posY;
    public StringCacheMap<StringCacheKey, CachedString> stringCacheMap = new StringCacheMap<>();
    public boolean bidiFlag;
    public int textColor;
    public boolean randomStyle;
    public boolean boldStyle;
    public boolean italicStyle;
    public boolean underlineStyle;
    public boolean strikethroughStyle;
    public GameSettings gameSettings;
    public ResourceLocation locationFontTextureBase;
    public ResourceLocation locationFontTexture;
    public TextureManager renderEngine;
    public boolean[] booleanArray;
    public ResourceLocation resourceLocation;
    public long longValue;
    public List<CachedStringPart> cachedStringPartList;
    public int intValue;
    public float floatValue;
    public float floatValue2;
    public float floatValue3;
    public int intValue2;
    public float floatValue4;
    public boolean unicodeFlag;

    public FontRenderer(GameSettings gameSettings, ResourceLocation resourceLocation, TextureManager textureManager, boolean flag) throws IOException {
        this.gameSettings = gameSettings;
        this.locationFontTextureBase = resourceLocation;
        this.locationFontTexture = resourceLocation;
        this.renderEngine = textureManager;
        this.unicodeFlag = flag;
        this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);

        for (int i = 0; i < 32; i++) {
            Minecraft.getMinecraft().initializationScreen.advance();
            int n = (i >> 3 & 1) * 85;
            int n2 = (i >> 2 & 1) * 170 + n;
            int n3 = (i >> 1 & 1) * 170 + n;
            int n4 = (i >> 0 & 1) * 170 + n;
            if (i == 6) {
                n2 += 85;
            }

            if (gameSettings.anaglyph) {
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
        this.stringCacheMap.clear();
        this.locationFontTexture = FontUtils.getHdFontLocation(this.locationFontTextureBase);

        for (int i = 0; i < unicodePageLocations.length; i++) {
            unicodePageLocations[i] = null;
        }

        this.readFontTexture();
        this.readGlyphSizes();
    }

    public void readFontTexture() {
        BufferedImage bufferedImage;
        try {
            bufferedImage = ImageIO.read(this.getResourceInputStream(this.locationFontTexture));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Properties readFontProperties = FontUtils.readFontProperties(this.locationFontTexture);
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
            boolean flag = false;

            int n5;
            for (n5 = n - 1; n5 >= 0; n5--) {
                int n6 = n3 * n + n5;
                boolean flag2 = true;

                for (int j = 0; j < n2 && flag2; j++) {
                    int n7 = (n4 * n2 + j) * width;
                    int n8 = ints[n6 + n7];
                    int n9 = n8 >> 24 & 0xFF;
                    if (n9 > 16) {
                        flag2 = false;
                    }
                }

                if (!flag2) {
                    break;
                }
            }

            if (i == 65) {
            }

            if (i == 32) {
                n5 = n <= 8 ? (int)(2.0F * f) : (int)(1.5F * f);
            }

            this.floatArray[i] = (n5 + 1) / f + 1.0F;
        }

        FontUtils.readCustomCharWidths(readFontProperties, this.floatArray);
    }

    public void readGlyphSizes() {
        try {
            InputStream resourceInputStream = this.getResourceInputStream(new ResourceLocation("font/glyph_sizes.bin"));
            resourceInputStream.read(this.glyphWidth);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public float renderCharAtPos(int n, char c, boolean flag) {
        if (c == ' ') {
            this.markPageLoaded();
            return !this.unicodeFlag ? this.floatArray[c] : 4.0F;
        } else {
            int indexOf = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                .indexOf(c);
            return indexOf != -1 && !this.unicodeFlag ? this.renderDefaultChar(n, flag) : this.renderUnicodeChar(c, flag);
        }
    }

    public float renderDefaultChar(int n, boolean flag) {
        if (this.resourceLocation == null && !this.booleanArray[this.intValue2]) {
            this.resourceLocation = this.locationFontTexture;
            if (!Tessellator.instance.isDrawing) {
                int glGenLists = GL11.glGenLists(1);
                this.cachedStringPartList.add(new CachedStringPart(glGenLists, this.resourceLocation));
                GL11.glNewList(glGenLists, 4864);
                Tessellator.instance.startDrawingQuads();
            }
        }

        if (this.resourceLocation == this.locationFontTexture) {
            float f = n % 16 * 8;
            float f2 = n / 16 * 8;
            float f3 = flag ? 1.0F : 0.0F;
            float f4 = 7.99F;
            this.applyTextColor();
            Tessellator.instance.setTextureUV(f / 128.0F, f2 / 128.0F);
            Tessellator.instance.addVertex(this.posX + f3, this.posY, 0.0);
            Tessellator.instance.setTextureUV(f / 128.0F, (f2 + 7.99F) / 128.0F);
            Tessellator.instance.addVertex(this.posX - f3, this.posY + 7.99F, 0.0);
            Tessellator.instance.setTextureUV((f + f4 - 1.0F) / 128.0F, (f2 + 7.99F) / 128.0F);
            Tessellator.instance.addVertex(this.posX + f4 - 1.0F - f3, this.posY + 7.99F, 0.0);
            Tessellator.instance.setTextureUV((f + f4 - 1.0F) / 128.0F, f2 / 128.0F);
            Tessellator.instance.addVertex(this.posX + f4 - 1.0F + f3, this.posY, 0.0);
            this.markPageLoaded();
        }

        return this.floatArray[n];
    }

    public ResourceLocation getUnicodePageLocation(int n) {
        if (unicodePageLocations[n] == null) {
            unicodePageLocations[n] = new ResourceLocation(String.format("textures/font/unicode_page_%02x.png", n));
            unicodePageLocations[n] = FontUtils.getHdFontLocation(unicodePageLocations[n]);
        }

        return unicodePageLocations[n];
    }

    public float renderUnicodeChar(char c, boolean flag) {
        if (this.glyphWidth[c] == 0) {
            this.markPageLoaded();
            return 0.0F;
        }

        ResourceLocation unicodePageLocation = this.getUnicodePageLocation(c / 256);
        int n = this.glyphWidth[c] >>> 4;
        int n2 = this.glyphWidth[c] & 15;
        int n3;
        float f = n3 = n & 15;
        float f2 = n2 + 1;
        if (this.resourceLocation == null && !this.booleanArray[this.intValue2]) {
            this.resourceLocation = unicodePageLocation;
            if (!Tessellator.instance.isDrawing) {
                int glGenLists = GL11.glGenLists(1);
                this.cachedStringPartList.add(new CachedStringPart(glGenLists, this.resourceLocation));
                GL11.glNewList(glGenLists, 4864);
                Tessellator.instance.startDrawingQuads();
            }
        }

        if (this.resourceLocation == unicodePageLocation) {
            float f3 = c % 16 * 16 + f;
            float f4 = (c & 255) / 16 * 16;
            float f5 = f2 - f - 0.02F;
            float f6 = flag ? 1.0F : 0.0F;
            this.applyTextColor();
            Tessellator.instance.setTextureUV(f3 / 256.0F, f4 / 256.0F);
            Tessellator.instance.addVertex(this.posX + f6, this.posY, 0.0);
            Tessellator.instance.setTextureUV(f3 / 256.0F, (f4 + 15.98F) / 256.0F);
            Tessellator.instance.addVertex(this.posX - f6, this.posY + 7.99F, 0.0);
            Tessellator.instance.setTextureUV((f3 + f5) / 256.0F, (f4 + 15.98F) / 256.0F);
            Tessellator.instance.addVertex(this.posX + f5 / 2.0F - f6, this.posY + 7.99F, 0.0);
            Tessellator.instance.setTextureUV((f3 + f5) / 256.0F, f4 / 256.0F);
            Tessellator.instance.addVertex(this.posX + f5 / 2.0F + f6, this.posY, 0.0);
            this.markPageLoaded();
        }

        return (f2 - f) / 2.0F + 1.0F;
    }

    public int drawStringWithShadow(String text, int x, int y, int color) {
        int stringWidth = this.getStringWidth(text);
        return this.drawString(text, x - stringWidth / 2, y, color, true);
    }

    public int drawString(String text, int x, int y, int color) {
        return !this.enabled ? 0 : this.drawString(text, x, y, color, false);
    }

    public String bidiReorder(String str) {
        try {
            Bidi bidi = new Bidi(new ArabicShaping(8).shape(str), 127);
            bidi.setReorderingMode(0);
            return bidi.writeReordered(2);
        } catch (ArabicShapingException e) {
            return str;
        }
    }

    public void resetStyles() {
        this.randomStyle = false;
        this.boldStyle = false;
        this.italicStyle = false;
        this.underlineStyle = false;
        this.strikethroughStyle = false;
    }

    public int renderStringAligned(String str, int n, int n2, int n3, int n4, boolean flag) {
        if (this.bidiFlag) {
            int stringWidth = this.getStringWidth(this.bidiReorder(str));
            n = n + n3 - stringWidth;
        }

        return this.renderString(str, n, n2, n4, flag);
    }

    public int getStringWidth(String str) {
        if (str == null) {
            return 0;
        }

        StringEvent2 stringEvent2 = new StringEvent2(str);
        CheatBreaker.getInstance().getEventBus().handleEvent(stringEvent2);
        str = stringEvent2.getText();
        float f = 0.0F;
        boolean flag = false;

        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            float charWidthFloat = this.getCharWidthFloat(charAt);
            if (charWidthFloat < 0.0F && i < str.length() - 1) {
                if ((charAt = str.charAt(++i)) == 'l' || charAt == 'L') {
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

        return (int)f;
    }

    public int getCharWidth(char c) {
        return Math.round(this.getCharWidthFloat(c));
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
                n &= 15;
                n2++;
                return (n2 - n) / 2 + 1;
            } else {
                return 0.0F;
            }
        }
    }

    public String trimStringToWidth(String str, int n) {
        return this.trimStringToWidth(str, n, false);
    }

    public String trimStringToWidth(String str, int n, boolean flag) {
        StringBuilder sb = new StringBuilder();
        float f = 0.0F;
        int n2 = flag ? str.length() - 1 : 0;
        int n3 = flag ? -1 : 1;
        boolean flag2 = false;
        boolean flag3 = false;

        for (int i = n2; i >= 0 && i < str.length() && f < n; i += n3) {
            char charAt = str.charAt(i);
            float charWidthFloat = this.getCharWidthFloat(charAt);
            if (flag2) {
                flag2 = false;
                if (charAt == 'l' || charAt == 'L') {
                    flag3 = true;
                } else if (charAt == 'r' || charAt == 'R') {
                    flag3 = false;
                }
            } else if (charWidthFloat < 0.0F) {
                flag2 = true;
            } else {
                f += charWidthFloat;
                if (flag3) {
                    f++;
                }
            }

            if (f > n) {
                break;
            }

            if (flag) {
                sb.insert(0, charAt);
            } else {
                sb.append(charAt);
            }
        }

        return sb.toString();
    }

    public String trimStringNewline(String str) {
        while (str != null && str.endsWith("\n")) {
            str = str.substring(0, str.length() - 1);
        }

        return str;
    }

    public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor) {
        this.resetStyles();
        this.textColor = textColor;
        str = this.trimStringNewline(str);
        this.renderSplitString(str, x, y, wrapWidth, false);
    }

    public void renderSplitString(String str, int x, int y, int wrapWidth, boolean addShadow) {
        for (Object obj : this.listFormattedStringToWidth(str, wrapWidth)) {
            this.renderStringAligned((String)obj, x, y, wrapWidth, this.textColor, addShadow);
            y += this.FONT_HEIGHT;
        }
    }

    public int splitStringWidth(String str, int n) {
        return this.FONT_HEIGHT * this.listFormattedStringToWidth(str, n).size();
    }

    public void setUnicodeFlag(boolean flag) {
        this.unicodeFlag = flag;
    }

    public boolean getUnicodeFlag() {
        return this.unicodeFlag;
    }

    public void setBidiFlag(boolean flag) {
        this.bidiFlag = flag;
    }

    public List listFormattedStringToWidth(String str, int wrapWidth) {
        return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
    }

    public String wrapFormattedStringToWidth(String str, int wrapWidth) {
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

            if (f > wrapWidth) {
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

    public static String getFormatFromString(String str) {
        String str2 = "";
        int n = -1;
        int n2 = str.length();

        while ((n = str.indexOf(167, n + 1)) != -1) {
            if (n < n2 - 1) {
                char charAt = str.charAt(n + 1);
                if (isFormatColor(charAt)) {
                    str2 = "§" + charAt;
                } else if (isFormatSpecial(charAt)) {
                    str2 = str2 + "§" + charAt;
                }
            }
        }

        return str2;
    }

    public boolean getBidiFlag() {
        return this.bidiFlag;
    }

    public void enableAlpha() {
        GL11.glEnable(3008);
    }

    public InputStream getResourceInputStream(ResourceLocation resourceLocation) throws IOException {
        return Minecraft.getMinecraft().getResourceManager().getResource(resourceLocation).getInputStream();
    }

    public void applyTextColor() {
        if (this.textColor == -1) {
            Tessellator.instance.setColorRGBA_F(this.floatValue4, this.floatValue, this.floatValue2, this.floatValue3);
        } else {
            Tessellator.instance
                .setColorRGBA_F((this.textColor >> 16) / 255.0F, (this.textColor >> 8 & 0xFF) / 255.0F, (this.textColor & 0xFF) / 255.0F, this.floatValue3);
        }
    }

    public int drawString(String str, float f, float f2, int n, boolean flag) {
        this.enableAlpha();
        this.resetStyles();
        int n2;
        if (flag) {
            n2 = this.renderString(str, f + 1.0F, f2 + 1.0F, n, true);
            n2 = Math.max(n2, this.renderString(str, f, f2, n, false));
        } else {
            n2 = this.renderString(str, f, f2, n, false);
        }

        return n2;
    }

    public int renderString(String str, float f, float f2, int n, boolean flag) {
        if (str == null) {
            return 0;
        }

        StringEvent2 stringEvent2 = new StringEvent2(str);
        CheatBreaker.getInstance().getEventBus().handleEvent(stringEvent2);
        str = stringEvent2.getText();
        if (this.bidiFlag) {
            str = this.bidiReorder(str);
        }

        if ((n & -67108864) == 0) {
            n |= -16777216;
        }

        if (flag) {
            n = (n & 16579836) >> 2 | n & 0xFF000000;
        }

        this.floatValue4 = (n >> 16 & 0xFF) / 255.0F;
        this.floatValue = (n >> 8 & 0xFF) / 255.0F;
        this.floatValue2 = (n & 0xFF) / 255.0F;
        this.floatValue3 = (n >> 24 & 0xFF) / 255.0F;
        this.posX = f;
        this.posY = f2;
        this.renderCachedString(str, flag, n);
        return (int)this.posX;
    }

    public void renderCachedString(String str, boolean flag, int n) {
        CachedString lookup = this.stringCacheMap.lookup(str, n, flag);
        if (lookup != null) {
            lookup.setExpiryTime(this.longValue);
            GL11.glPushMatrix();
            GL11.glTranslatef(this.posX, this.posY, 0.0F);
            lookup.prepareParts();
            GL11.glPopMatrix();
            this.posX = this.posX + lookup.getWidth();
        } else {
            float posX = this.posX;
            float posY = this.posY;
            this.posX = 0.0F;
            this.posY = 0.0F;
            this.cachedStringPartList = new ArrayList<>();
            this.booleanArray = new boolean[str.length()];
            this.intValue = str.length();
            boolean flag2 = false;

            while (this.intValue >= 0) {
                if (this.intValue == 0) {
                    this.intValue = -1;
                }

                this.posX = 0.0F;
                this.resourceLocation = null;
                this.textColor = -1;
                this.resetStyles();

                for (this.intValue2 = 0; this.intValue2 < str.length(); this.intValue2++) {
                    char charAt = str.charAt(this.intValue2);
                    if (charAt == 167 && this.intValue2 + 1 < str.length()) {
                        int indexOf = "0123456789abcdefklmnor".indexOf(Character.toLowerCase(str.charAt(this.intValue2 + 1)));
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

                            int n2 = this.colorCode[indexOf];
                            if (Config.isCustomColors()) {
                                n2 = CustomColorizer.getTextColor(indexOf, n2);
                            }

                            this.textColor = n2;
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

                        this.markPageLoaded();
                        this.intValue2++;
                        this.markPageLoaded();
                    } else {
                        int indexOf = "ÀÁÂÈÊËÍÓÔÕÚßãõğİıŒœŞşŴŵžȇ\u0000\u0000\u0000\u0000\u0000\u0000\u0000 !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u0000ÇüéâäàåçêëèïîìÄÅÉæÆôöòûùÿÖÜø£Ø×ƒáíóúñÑªº¿®¬½¼¡«»░▒▓│┤╡╢╖╕╣║╗╝╜╛┐└┴┬├─┼╞╟╚╔╩╦╠═╬╧╨╤╥╙╘╒╓╫╪┘┌█▄▌▐▀αβΓπΣσμτΦΘΩδ∞∅∈∩≡±≥≤⌠⌡÷≈°∙·√ⁿ²■\u0000"
                            .indexOf(charAt);
                        if (this.randomStyle && indexOf != -1) {
                            int n2;
                            while ((int)this.floatArray[indexOf] != (int)this.floatArray[n2 = this.fontRandom.nextInt(this.floatArray.length)]) {
                            }

                            indexOf = n2;
                        }

                        float f = indexOf != -1 && !this.unicodeFlag ? this.offsetBold : 0.5F;
                        boolean flag3 = (charAt == 0 || indexOf == -1 || this.unicodeFlag) && flag;
                        if (flag3) {
                            this.posX -= f;
                            this.posY -= f;
                        }

                        float renderCharAtPos = this.renderCharAtPos(indexOf, charAt, this.italicStyle);
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

                            this.renderCharAtPos(indexOf, charAt, this.italicStyle);
                            this.posX -= f;
                            if (flag3) {
                                this.posX += f;
                                this.posY += f;
                            }

                            renderCharAtPos += f;
                        }

                        if (this.intValue == -1 && (this.strikethroughStyle || this.underlineStyle)) {
                            if (!Tessellator.instance.isDrawing) {
                                int glGenLists = GL11.glGenLists(1);
                                this.cachedStringPartList.add(new CachedStringPart(glGenLists, null));
                                GL11.glNewList(glGenLists, 4864);
                                Tessellator.instance.startDrawingQuads();
                            }

                            this.applyTextColor();
                            if (this.strikethroughStyle) {
                                Tessellator.instance.addVertex(this.posX, this.posY + this.FONT_HEIGHT / 2, 0.0);
                                Tessellator.instance.addVertex(this.posX + renderCharAtPos, this.posY + this.FONT_HEIGHT / 2, 0.0);
                                Tessellator.instance.addVertex(this.posX + renderCharAtPos, this.posY + this.FONT_HEIGHT / 2 - 1.0F, 0.0);
                                Tessellator.instance.addVertex(this.posX, this.posY + this.FONT_HEIGHT / 2 - 1.0F, 0.0);
                            }

                            if (this.underlineStyle) {
                                Tessellator.instance.addVertex(this.posX - 1.0F, this.posY + this.FONT_HEIGHT, 0.0);
                                Tessellator.instance.addVertex(this.posX + renderCharAtPos, this.posY + this.FONT_HEIGHT, 0.0);
                                Tessellator.instance.addVertex(this.posX + renderCharAtPos, this.posY + this.FONT_HEIGHT - 1.0F, 0.0);
                                Tessellator.instance.addVertex(this.posX - 1.0F, this.posY + this.FONT_HEIGHT - 1.0F, 0.0);
                            }
                        }

                        this.posX += renderCharAtPos;
                    }
                }

                if (Tessellator.instance.isDrawing) {
                    Tessellator.instance.draw();
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

    public void purgeExpiredCache() {
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

    public int drawCenteredString(String str, float f, float f2, int n) {
        int stringWidth = this.getStringWidth(str);
        return this.drawString(str, f - stringWidth / 2, f2, n, false);
    }

    public int drawShadowedString(String str, float f, float f2, int n) {
        return this.drawString(str, f, f2, n, true);
    }

    public void drawRainbowString(String str, float f, float f2) {
        float f3 = f;

        for (char c : str.toCharArray()) {
            long l = System.currentTimeMillis() - (long)(f3 * 50.0F - f2 * 50.0F);
            int n = Color.HSBtoRGB((float)(l % 10000L) / 10000.0F, 1.0F, 1.0F);
            String str2 = String.valueOf(c);
            this.drawString(str2, f3, f2, n, true);
            f3 += (int)(this.getCharWidth(c) * 1.0F);
        }
    }

    public void markPageLoaded() {
        if (!this.booleanArray[this.intValue2]) {
            this.booleanArray[this.intValue2] = true;
            this.intValue--;
        }
    }
}
