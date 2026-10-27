package com.cheatbreaker.client.font;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CustomFontRenderer extends AbstractFontRenderer {
   public final char COLOR_CHAR = 167;
   public int[] colorCodes = new int[32];
   public final String COLOR_CODE_CHARS = "0123456789abcdefklmnor";
   public Glyph[] boldGlyphs = new Glyph[256];
   public DynamicTexture boldTexture;
   public Glyph[] italicGlyphs = new Glyph[256];
   public DynamicTexture italicTexture;
   public Glyph[] boldItalicGlyphs = new Glyph[256];
   public DynamicTexture boldItalicTexture;

   public String trimToWidth(String str, double d, boolean flag) {
      StringBuilder sb = new StringBuilder();
      float f = 0.0F;
      int n = flag ? str.length() - 1 : 0;
      int n2 = flag ? -1 : 1;
      boolean flag2 = false;
      boolean flag3 = false;

      for (int i = n; i >= 0 && i < str.length() && f < (float)d; i += n2) {
         char charAt = str.charAt(i);
         double stringWidth = this.getStringWidth(String.valueOf(charAt));
         if (flag2) {
            flag2 = false;
            if (charAt == 'l' || charAt == 'L') {
               flag3 = true;
            } else if (charAt == 'r' || charAt == 'R') {
               flag3 = false;
            }
         } else if (stringWidth < 0.0) {
            flag2 = true;
         } else {
            f = (float)(f + stringWidth);
            if (flag3) {
               f++;
            }
         }

         if (f > (float)d) {
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

   @Override
   public void setAntialiasing(boolean flag) {
      super.setAntialiasing(flag);
      this.reloadTextures();
   }

   public String wrapText(String str, double d) {
      StringBuilder sb = new StringBuilder();
      StringBuilder sb2 = new StringBuilder();
      boolean flag = false;

      for (char c : str.toCharArray()) {
         if (flag) {
            sb.append(c);
            flag = false;
         } else if (c == 167) {
            sb.append(c);
            flag = true;
         } else {
            sb.append(c);
            int stringWidth = this.getStringWidth(sb.toString());
            if (stringWidth >= d) {
               String str2 = sb.toString();
               String str3;
               String str4;
               if (str2.contains(" ")) {
                  str4 = str2.substring(0, str2.lastIndexOf(" "));
                  str3 = str2.substring(str2.lastIndexOf(" "));
                  if (str3.startsWith(" ")) {
                     str3 = str3.replaceFirst(" ", "");
                  }
               } else {
                  str4 = str2.substring(0, str2.length() - 1);
                  str3 = str2.substring(str2.length() - 1);
               }

               sb2.append(str4).append("\n");
               String formatText = EnumChatFormatting.formatText(sb.toString());
               sb.setLength(0);
               sb.append(str3).append(formatText);
            }
         }
      }

      sb2.append(sb);
      return sb2.length() == 0 ? str : sb2.toString();
   }

   public float drawCenteredStringWithShadow(String str, float f, float f2, int n) {
      this.renderString(str, f - this.getStringWidth(str) / 2 + 1.0, f2 + 1.0, n, true);
      return this.drawString(str, f - this.getStringWidth(str) / 2, f2, n);
   }

   public float renderString(String str, double d, double d2, int n, boolean flag) {
      d--;
      if (str == null) {
         return 0.0F;
      }

      if (n == 553648127) {
         n = 16777215;
      }

      if ((n & -67108864) == 0) {
         n |= -16777216;
      }

      if (flag) {
         n = (n & 16579836) >> 2 | n & 0xFF000000;
      }

      Glyph[] glyphs = this.glyphs;
      float f = (n >> 24 & 0xFF) / 255.0F;
      boolean flag2 = false;
      boolean flag3 = false;
      boolean flag4 = false;
      boolean flag5 = false;
      boolean flag6 = false;
      boolean flag7 = true;
      d *= 2.0;
      d2 = (d2 - 3.0) * 2.0;
      if (flag7) {
         GlStateManager.pushMatrix();
         GL11.glScaled(0.5, 0.5, 0.5);
         GlStateManager.enableBlend();
         GL11.glBlendFunc(770, 771);
         GlStateManager.color((n >> 16 & 0xFF) / 255.0F, (n >> 8 & 0xFF) / 255.0F, (n & 0xFF) / 255.0F, f);
         int n2 = str.length();
         GlStateManager.enableTexture2D();
         int glGetInteger = GL11.glGetInteger(32873);
         GL11.glBindTexture(3553, this.dynamicTexture.getGlTextureId());

         for (int i = 0; i < n2; i++) {
            char charAt = str.charAt(i);
            if (charAt == 167 && i < n2) {
               int n3 = 21;

               try {
                  n3 = "0123456789abcdefklmnor".indexOf(str.charAt(i + 1));
               } catch (Exception e) {
                  e.printStackTrace();
               }

               if (n3 >= 16) {
                  if (n3 == 16) {
                     flag2 = true;
                  } else if (n3 == 17) {
                     flag3 = true;
                     if (flag4) {
                        GL11.glBindTexture(3553, this.boldItalicTexture.getGlTextureId());
                        glyphs = this.boldItalicGlyphs;
                     } else {
                        GL11.glBindTexture(3553, this.boldTexture.getGlTextureId());
                        glyphs = this.boldGlyphs;
                     }
                  } else if (n3 == 18) {
                     flag5 = true;
                  } else if (n3 == 19) {
                     flag6 = true;
                  } else if (n3 == 20) {
                     flag4 = true;
                     if (flag3) {
                        GL11.glBindTexture(3553, this.boldItalicTexture.getGlTextureId());
                        glyphs = this.boldItalicGlyphs;
                     } else {
                        GL11.glBindTexture(3553, this.italicTexture.getGlTextureId());
                        glyphs = this.italicGlyphs;
                     }
                  } else if (n3 == 21) {
                     flag3 = false;
                     flag4 = false;
                     flag2 = false;
                     flag6 = false;
                     flag5 = false;
                     GlStateManager.color((n >> 16 & 0xFF) / 255.0F, (n >> 8 & 0xFF) / 255.0F, (n & 0xFF) / 255.0F, f);
                     GL11.glBindTexture(3553, this.dynamicTexture.getGlTextureId());
                     glyphs = this.glyphs;
                  }
               } else {
                  flag3 = false;
                  flag4 = false;
                  flag2 = false;
                  flag6 = false;
                  flag5 = false;
                  GL11.glBindTexture(3553, this.dynamicTexture.getGlTextureId());
                  glyphs = this.glyphs;
                  if (n3 < 0 || n3 > 15) {
                     n3 = 15;
                  }

                  if (flag) {
                     n3 += 16;
                  }

                  int n4 = this.colorCodes[n3];
                  GlStateManager.color((n4 >> 16 & 0xFF) / 255.0F, (n4 >> 8 & 0xFF) / 255.0F, (n4 & 0xFF) / 255.0F, f);
               }

               i++;
            } else if (charAt < glyphs.length && charAt >= 0) {
               GL11.glBegin(4);
               this.drawGlyph(glyphs, charAt, (float)d, (float)d2 + 6.0F);
               GL11.glEnd();
               if (flag5) {
                  this.drawLine(d, d2 + glyphs[charAt].height / 2, d + glyphs[charAt].width - 8.0, d2 + glyphs[charAt].height / 2, 1.0F);
               }

               if (flag6) {
                  this.drawLine(d, d2 + glyphs[charAt].height - 2.0, d + glyphs[charAt].width - 8.0, d2 + glyphs[charAt].height - 2.0, 1.0F);
               }

               d += glyphs[charAt].width - 8 + this.charSpacing;
            }
         }

         GlStateManager.disableBlend();
         GL11.glBindTexture(3553, glGetInteger);
         GL11.glHint(3155, 4352);
         GlStateManager.popMatrix();
      }

      return (float)d / 2.0F;
   }

   public CustomFontRenderer(ResourceLocation resourceLocation, float f) {
      super(resourceLocation, f);

      try {
         Minecraft.getMinecraft().stream.run11();
      } catch (NullPointerException e) {
      }

      this.setupColorCodes();
      this.reloadTextures();
   }

   public List<String> splitIntoLines(String str, double d) {
      ArrayList list = new ArrayList();
      String str2 = "";
      char c = '\uffff';
      char[] toCharArray = str.toCharArray();

      for (int i = 0; i < toCharArray.length; i++) {
         char c2 = toCharArray[i];
         if (c2 == 167 && i < toCharArray.length - 1) {
            c = toCharArray[i + 1];
         }

         StringBuilder sb = new StringBuilder();
         if (this.getStringWidth(sb.append(str2).append(c2).toString()) < d) {
            str2 = str2 + c2;
         } else {
            list.add(str2);
            str2 = 167 + c + String.valueOf(c2);
         }
      }

      if (str2.length() > 0) {
         list.add(str2);
      }

      return list;
   }

   public float drawCenteredString(String str, float f, float f2, int n) {
      return this.drawString(str, f - this.getStringWidth(str) / 2, f2, n);
   }

   public List<String> splitIntoLinesByWords(String str, double d) {
      ArrayList list = new ArrayList();
      if (this.getStringWidth(str) > d) {
         String[] split = str.split(" ");
         String str2 = "";
         char c = '\uffff';

         for (int i = 0; i < split.length; i++) {
            String str3 = split[i];

            for (int j = 0; j < str3.toCharArray().length; j++) {
               char c2 = str3.toCharArray()[j];
               if (c2 == 167 && j < str3.toCharArray().length - 1) {
                  c = str3.toCharArray()[j + 1];
               }
            }

            StringBuilder sb = new StringBuilder();
            if (this.getStringWidth(sb.append(str2).append(str3).append(" ").toString()) < d) {
               str2 = str2 + str3 + " ";
            } else {
               list.add(str2);
               str2 = 167 + c + str3 + " ";
            }
         }

         if (str2.length() > 0) {
            if (this.getStringWidth(str2) < d) {
               list.add(167 + c + str2 + " ");
               str2 = "";
            } else {
               for (String str3 : this.splitIntoLines(str2, d)) {
                  list.add(str3);
               }
            }
         }
      } else {
         list.add(str);
      }

      return list;
   }

   public float drawStringWithOutline(String str, double d, double d2, int n) {
      float renderString = this.renderString(str, d + 1.0, d2 + 1.0, n, true);
      return Math.max(renderString, this.renderString(str, d, d2, n, false));
   }

   @Override
   public void setFractionalMetrics(boolean flag) {
      super.setFractionalMetrics(flag);
      this.reloadTextures();
   }

   public float drawStringWithOutlineColor(String str, double d, double d2, int n, int n2) {
      float renderString = this.renderString(str, d + 1.0, d2 + 1.0, n2, false);
      return Math.max(renderString, this.renderString(str, d, d2, n, false));
   }

   public void reloadTextures() {
      this.boldTexture = this.createTexture(this.font.deriveFont(1), this.antialiasing, this.fractionalMetrics, this.boldGlyphs);
      this.italicTexture = this.createTexture(this.font.deriveFont(2), this.antialiasing, this.fractionalMetrics, this.italicGlyphs);
      this.boldItalicTexture = this.createTexture(this.font.deriveFont(3), this.antialiasing, this.fractionalMetrics, this.boldItalicGlyphs);
   }

   public String trimStringToWidth(String str, double d) {
      return this.trimToWidth(str, d, false);
   }

   public void setupColorCodes() {
      for (int i = 0; i < 32; i++) {
         int n = (i >> 3 & 1) * 85;
         int n2 = (i >> 2 & 1) * 170 + n;
         int n3 = (i >> 1 & 1) * 170 + n;
         int n4 = (i & 1) * 170 + n;
         if (i == 6) {
            n2 += 85;
         }

         if (i >= 16) {
            n2 /= 4;
            n3 /= 4;
            n4 /= 4;
         }

         this.colorCodes[i] = (n2 & 0xFF) << 16 | (n3 & 0xFF) << 8 | n4 & 0xFF;
      }
   }

   public void drawLine(double d, double d2, double d3, double d4, float f) {
      GlStateManager.disableTexture2D();
      GL11.glLineWidth(f);
      GL11.glBegin(1);
      GL11.glVertex2d(d, d2);
      GL11.glVertex2d(d3, d4);
      GL11.glEnd();
      GlStateManager.enableTexture2D();
   }

   public float drawString(String str, float f, float f2, int n) {
      return this.renderString(str, f, f2, n, false);
   }

   @Override
   public int getStringWidth(String str) {
      if (str == null) {
         return 0;
      }

      int n = 0;
      Glyph[] glyphs = this.glyphs;
      boolean flag = false;
      boolean flag2 = false;
      int n2 = str.length();

      for (int i = 0; i < n2; i++) {
         char charAt = str.charAt(i);
         if (charAt == 167 && i < n2) {
            int indexOf = "0123456789abcdefklmnor".indexOf(charAt);
            if (indexOf < 16) {
               flag = false;
               flag2 = false;
            } else if (indexOf == 17) {
               flag = true;
               glyphs = flag2 ? this.boldItalicGlyphs : this.boldGlyphs;
            } else if (indexOf == 20) {
               flag2 = true;
               glyphs = flag ? this.boldItalicGlyphs : this.italicGlyphs;
            } else if (indexOf == 21) {
               flag = false;
               flag2 = false;
               glyphs = this.glyphs;
            }

            i++;
         } else if (charAt < glyphs.length && charAt >= 0) {
            n += glyphs[charAt].width - 8 + this.charSpacing;
         }
      }

      return n / 2;
   }
}
