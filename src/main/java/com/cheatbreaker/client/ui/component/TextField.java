package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.font.CustomFontRenderer;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.theme.Theme;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.opengl.GL11;

public class TextField extends UIElement {
   public String text = "";
   public int maxStringLength = 32;
   public boolean enableBackgroundDrawing = true;
   public int lineScrollOffset;
   public boolean focused;
   public boolean canLoseFocus = true;
   public boolean editable = true;
   public int cursorCounter;
   public int enabledColor = 14737632;
   public int disabledColor = 7368816;
   public boolean visible = true;
   public int unusedValue;
   public CustomFontRenderer customFontRenderer;
   public int selectionEnd;
   public String placeholder;
   public int backgroundColor;
   public int cursorPosition;

   public TextField(CustomFontRenderer customFontRenderer, String str, int n, int n2) {
      this.customFontRenderer = customFontRenderer;
      this.placeholder = str;
      this.backgroundColor = n;
      this.enabledColor = n2;
   }

   public void setSelectionEnd(int n) {
      this.selectionEnd = n;
   }

   public void setUnusedValue(int n) {
      this.unusedValue = n;
   }

   public float getTextWidth() {
      return this.isEnableBackgroundDrawing() ? this.width - 8.0F : this.width;
   }

   @Override
   public void tick() {
      this.updateCursorCounter();
   }

   public int getCursorCounter() {
      return this.cursorCounter;
   }

   public void setPlaceholder(String str) {
      this.placeholder = str;
   }

   public int getLineScrollOffset() {
      return this.lineScrollOffset;
   }

   public void drawTextBox() {
      Theme theme = CheatBreaker.getInstance().getConfigManager().theme;
      if (this.isVisible()) {
         if (this.isEnableBackgroundDrawing()) {
            RenderUtils.drawRoundedRectFilledPrecise(
               this.x, this.y, this.x + this.width, this.y + this.height, theme.getButtonCornerRadiusValue(), this.backgroundColor
            );
         }

         int n = this.editable ? this.enabledColor : this.disabledColor;
         int n2 = this.cursorPosition - this.lineScrollOffset;
         int n3 = this.selectionEnd - this.lineScrollOffset;
         String trimStringToWidth = this.customFontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getTextWidth());
         boolean flag = n2 >= 0 && n2 <= trimStringToWidth.length();
         boolean flag2 = this.focused && this.cursorCounter / 6 % 2 == 0 && flag;
         float f = this.enableBackgroundDrawing ? this.x + 4.0F : this.x;
         float f2 = this.enableBackgroundDrawing ? this.y + (this.height - 8.0F) / 2.0F : this.y;
         float f3 = f;
         if (n3 > trimStringToWidth.length()) {
            n3 = trimStringToWidth.length();
         }

         if (trimStringToWidth.length() > 0) {
            String str = flag ? trimStringToWidth.substring(0, n2) : trimStringToWidth;
            f3 = this.customFontRenderer.drawString(str, f, f2, n);
         } else if (!this.isFocused()) {
            this.customFontRenderer.drawString(this.placeholder, f, f2, n);
         }

         boolean flag3 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
         float f4 = f3;
         if (!flag) {
            f4 = n2 > 0 ? f + this.width : f;
         } else if (flag3) {
            f4 = f3 + 0.5F;
            f3++;
         }

         if (trimStringToWidth.length() > 0 && flag && n2 < trimStringToWidth.length()) {
            this.customFontRenderer.drawString(trimStringToWidth.substring(n2), f3, f2, n);
         }

         if (flag2) {
            if (flag3) {
               RenderUtils.drawRoundedRectFilledPrecise(
                  f4, f2 + 1.0F, f4 + 1.0F, f2 + 4.0F + this.customFontRenderer.getLineHeight(), theme.getButtonCornerRadiusValue(), -3092272
               );
            } else {
               this.customFontRenderer.drawString("_", f4, f2, n);
            }
         }

         if (n3 != n2) {
            float f5 = f + this.customFontRenderer.getStringWidth(trimStringToWidth.substring(0, n3));
            this.drawSelectionHighlight(f4, f2 - 1.0F + 2.0F, f5 - 1.0F, f2 + 1.0F + this.customFontRenderer.getLineHeight() + 2.0F);
         }
      }
   }

   public int getNthWordFromPos(int n, int n2) {
      return this.getNthWordFromPosSkipSpaces(n, this.getCursorPosition(), true);
   }

   public int getNthWordFromPosSkipSpaces(int n, int n2, boolean flag) {
      int n3 = n2;
      boolean flag2 = n < 0;
      int abs = Math.abs(n);

      for (int i = 0; i < abs; i++) {
         if (!flag2) {
            int n4 = this.text.length();
            if ((n3 = this.text.indexOf(32, n3)) == -1) {
               n3 = n4;
            } else {
               while (flag && n3 < n4 && this.text.charAt(n3) == ' ') {
                  n3++;
               }
            }
         } else {
            while (flag && n3 > 0 && this.text.charAt(n3 - 1) == ' ') {
               n3--;
            }

            while (n3 > 0 && this.text.charAt(n3 - 1) != ' ') {
               n3--;
            }
         }
      }

      return n3;
   }

   public void setDisabledColor(int n) {
      this.disabledColor = n;
   }

   public void setLineScrollOffset(int n) {
      this.lineScrollOffset = n;
   }

   public void setCursorPositionEnd() {
      this.setCursorPosition(this.text.length());
   }

   public void setVisible(boolean flag) {
      this.visible = flag;
   }

   public void writeText(String str) {
      String str2 = "";
      String filterAllowedCharacters = ChatAllowedCharacters.filterAllowedCharacters(str);
      int min = Math.min(this.cursorPosition, this.selectionEnd);
      int max = Math.max(this.cursorPosition, this.selectionEnd);
      int n = this.maxStringLength - this.text.length() - (min - this.selectionEnd);
      boolean flag = false;
      if (this.text.length() > 0) {
         str2 = str2 + this.text.substring(0, min);
      }

      int n2;
      if (n < filterAllowedCharacters.length()) {
         str2 = str2 + filterAllowedCharacters.substring(0, n);
         n2 = n;
      } else {
         str2 = str2 + filterAllowedCharacters;
         n2 = filterAllowedCharacters.length();
      }

      if (this.text.length() > 0 && max < this.text.length()) {
         str2 = str2 + this.text.substring(max);
      }

      this.text = str2;
      this.moveCursorBy(min - this.selectionEnd + n2);
   }

   @Override
   public void render(float f, float f2, boolean flag) {
      this.drawTextBox();
   }

   public int getSelectionEnd() {
      return this.selectionEnd;
   }

   public void setSelectionPos(int n) {
      int n2 = this.text.length();
      if (n > n2) {
         n = n2;
      }

      if (n < 0) {
         n = 0;
      }

      this.selectionEnd = n;
      if (this.customFontRenderer != null) {
         if (this.lineScrollOffset > n2) {
            this.lineScrollOffset = n2;
         }

         float textWidth = this.getTextWidth();
         String trimStringToWidth = this.customFontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), textWidth);
         int n3 = trimStringToWidth.length() + this.lineScrollOffset;
         if (n == this.lineScrollOffset) {
            this.lineScrollOffset = this.lineScrollOffset - this.customFontRenderer.trimToWidth(this.text, textWidth, true).length();
         }

         if (n > n3) {
            this.lineScrollOffset += n - n3;
         } else if (n <= this.lineScrollOffset) {
            this.lineScrollOffset = this.lineScrollOffset - (this.lineScrollOffset - n);
         }

         if (this.lineScrollOffset < 0) {
            this.lineScrollOffset = 0;
         }

         if (this.lineScrollOffset > n2) {
            this.lineScrollOffset = n2;
         }
      }
   }

   public int getNthWordFromCursor(int n) {
      return this.getNthWordFromPos(n, this.getCursorPosition());
   }

   public void setMaxStringLength(int n) {
      this.maxStringLength = n;
      if (this.text.length() > n) {
         this.text = this.text.substring(0, n);
      }
   }

   public void setText(String str) {
      this.text = str.length() > this.maxStringLength ? str.substring(0, this.maxStringLength) : str;
      this.setCursorPositionEnd();
   }

   public boolean getCanLoseFocus() {
      return this.canLoseFocus;
   }

   public void setCursorPosition(int n) {
      this.cursorPosition = n;
      int n2 = this.text.length();
      if (this.cursorPosition < 0) {
         this.cursorPosition = 0;
      }

      if (this.cursorPosition > n2) {
         this.cursorPosition = n2;
      }

      this.setSelectionPos(this.cursorPosition);
   }

   public void setCanLoseFocus(boolean flag) {
      this.canLoseFocus = flag;
   }

   public void moveCursorBy(int n) {
      this.setCursorPosition(this.selectionEnd + n);
   }

   public int getDisabledColor() {
      return this.disabledColor;
   }

   public boolean isEditable() {
      return this.editable;
   }

   public void setBackgroundColor(int n) {
      this.backgroundColor = n;
   }

   public void setCursorPositionZero() {
      this.setCursorPosition(0);
   }

   public int getBackgroundColor() {
      return this.backgroundColor;
   }

   public void updateCursorCounter() {
      this.cursorCounter++;
   }

   public void setEnabledColor(int n) {
      this.enabledColor = n;
   }

   public void drawSelectionHighlight(float f, float f2, float f3, float f4) {
      if (f < f3) {
         float f5 = f;
         f = f3;
         f3 = f5;
      }

      if (f2 < f4) {
         float f5 = f2;
         f2 = f4;
         f4 = f5;
      }

      if (f3 > this.x + this.width) {
         f3 = this.x + this.width;
      }

      if (f > this.x + this.width) {
         f = this.x + this.width;
      }

      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
      GlStateManager.disableTexture2D();
      GL11.glEnable(3058);
      GL11.glLogicOp(5387);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(f, f4, 0.0).endVertex();
      worldRenderer.pos(f3, f4, 0.0).endVertex();
      worldRenderer.pos(f3, f2, 0.0).endVertex();
      worldRenderer.pos(f, f2, 0.0).endVertex();
      instance.draw();
      GL11.glDisable(3058);
      GlStateManager.enableTexture2D();
   }

   public void deleteWords(int n) {
      if (this.text.length() != 0) {
         if (this.selectionEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            this.deleteFromCursor(this.getNthWordFromCursor(n) - this.cursorPosition);
         }
      }
   }

   public boolean isFocused() {
      return this.focused;
   }

   public void deleteFromCursor(int n) {
      if (this.text.length() != 0) {
         if (this.selectionEnd != this.cursorPosition) {
            this.writeText("");
         } else {
            boolean flag = n < 0;
            int n2 = flag ? this.cursorPosition + n : this.cursorPosition;
            int n3 = flag ? this.cursorPosition : this.cursorPosition + n;
            String str = "";
            if (n2 >= 0) {
               str = this.text.substring(0, n2);
            }

            if (n3 < this.text.length()) {
               str = str + this.text.substring(n3);
            }

            this.text = str;
            if (flag) {
               this.moveCursorBy(n);
            }
         }
      }
   }

   public boolean isVisible() {
      return this.visible;
   }

   @Override
   public void keyTyped(char c, int n) {
      if (n == 1) {
         this.setFocused(false);
      } else {
         this.textboxKeyTyped(c, n);
      }
   }

   public int getUnusedValue() {
      return this.unusedValue;
   }

   public void setCursorCounter(int n) {
      this.cursorCounter = n;
   }

   public void setEnableBackgroundDrawing(boolean flag) {
      this.enableBackgroundDrawing = flag;
   }

   public String getSelectedText() {
      int min = Math.min(this.cursorPosition, this.selectionEnd);
      int max = Math.max(this.cursorPosition, this.selectionEnd);
      return this.text.substring(min, max);
   }

   public int getEnabledColor() {
      return this.enabledColor;
   }

   public String getText() {
      return this.text;
   }

   public CustomFontRenderer getCustomFontRenderer() {
      return this.customFontRenderer;
   }

   public boolean textboxKeyTyped(char c, int n) {
      if (!this.focused) {
         return false;
      }

      switch (c) {
         case '\u0001':
            this.setCursorPositionEnd();
            this.setSelectionPos(0);
            return true;
         case '\u0003':
            GuiScreen.setClipboardString(this.getSelectedText());
            return true;
         case '\u0016':
            if (this.editable) {
               this.writeText(GuiScreen.getClipboardString());
            }

            return true;
         case '\u0018':
            GuiScreen.setClipboardString(this.getSelectedText());
            if (this.editable) {
               this.writeText("");
            }

            return true;
         default:
            switch (n) {
               case 14:
                  if (GuiScreen.isCtrlKeyDown()) {
                     if (this.editable) {
                        this.deleteWords(-1);
                     }
                  } else if (this.editable) {
                     this.deleteFromCursor(-1);
                  }

                  return true;
               case 199:
                  if (GuiScreen.isShiftKeyDown()) {
                     this.setSelectionPos(0);
                  } else {
                     this.setCursorPositionZero();
                  }

                  return true;
               case 203:
                  if (GuiScreen.isShiftKeyDown()) {
                     if (GuiScreen.isCtrlKeyDown()) {
                        this.setSelectionPos(this.getNthWordFromPos(-1, this.getSelectionEnd()));
                     } else {
                        this.setSelectionPos(this.getSelectionEnd() - 1);
                     }
                  } else if (GuiScreen.isCtrlKeyDown()) {
                     this.setCursorPosition(this.getNthWordFromCursor(-1));
                  } else {
                     this.moveCursorBy(-1);
                  }

                  return true;
               case 205:
                  if (GuiScreen.isShiftKeyDown()) {
                     if (GuiScreen.isCtrlKeyDown()) {
                        this.setSelectionPos(this.getNthWordFromPos(1, this.getSelectionEnd()));
                     } else {
                        this.setSelectionPos(this.getSelectionEnd() + 1);
                     }
                  } else if (GuiScreen.isCtrlKeyDown()) {
                     this.setCursorPosition(this.getNthWordFromCursor(1));
                  } else {
                     this.moveCursorBy(1);
                  }

                  return true;
               case 207:
                  if (GuiScreen.isShiftKeyDown()) {
                     this.setSelectionPos(this.text.length());
                  } else {
                     this.setCursorPositionEnd();
                  }

                  return true;
               case 211:
                  if (GuiScreen.isCtrlKeyDown()) {
                     if (this.editable) {
                        this.deleteWords(1);
                     }
                  } else if (this.editable) {
                     this.deleteFromCursor(1);
                  }

                  return true;
               default:
                  if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                     if (this.editable) {
                        this.writeText(Character.toString(c));
                     }

                     return true;
                  } else {
                     return false;
                  }
            }
      }
   }

   public int getMaxStringLength() {
      return this.maxStringLength;
   }

   @Override
   public boolean mouseClicked(float f, float f2, int n, boolean flag) {
      if (!flag) {
         this.setFocused(false);
         return true;
      }

      if (n == 1 && this.containsPoint(f, f2)) {
         this.setText("");
      }

      boolean flag2 = f >= this.x && f < this.x + this.width && f2 >= this.y && f2 < this.y + this.height;
      if (this.canLoseFocus) {
         this.setFocused(flag2);
      }

      if (this.focused && n == 0) {
         float f3 = f - this.x;
         if (this.enableBackgroundDrawing) {
            f3 -= 4.0F;
         }

         String trimStringToWidth = this.customFontRenderer.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getTextWidth());
         this.setCursorPosition(this.customFontRenderer.trimStringToWidth(trimStringToWidth, f3).length() + this.lineScrollOffset);
      }

      return false;
   }

   public boolean isEnableBackgroundDrawing() {
      return this.enableBackgroundDrawing;
   }

   public void setEditable(boolean flag) {
      this.editable = flag;
   }

   public int getCursorPosition() {
      return this.cursorPosition;
   }

   public void setFocused(boolean flag) {
      if (flag && !this.focused) {
         this.cursorCounter = 0;
      }

      this.focused = flag;
   }

   public String getPlaceholder() {
      return this.placeholder;
   }
}
