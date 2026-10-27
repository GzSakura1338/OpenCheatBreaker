package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;

public class GuiTextField extends Gui {
   public String text = "";
   public int maxStringLength = 32;
   public boolean enableBackgroundDrawing = true;
   public boolean canLoseFocus = true;
   public boolean isEnabled = true;
   public int enabledColor = 14737632;
   public int disabledColor = 7368816;
   public boolean visible = true;
   public int cursorCounter;
   public Predicate<String> validator = Predicates.alwaysTrue();
   public int id;
   public boolean isFocused;
   public FontRenderer fontRendererInstance;
   public int lineScrollOffset;
   public int cursorPosition;
   public int selectionEnd;
   public int xPosition;
   public int yPosition;
   public int width;
   public GuiPageButtonList.GuiResponder updateListener;
   public int height;

   public GuiTextField(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width, int par6Height) {
      this.id = componentId;
      this.fontRendererInstance = fontrendererObj;
      this.xPosition = x;
      this.yPosition = y;
      this.width = par5Width;
      this.height = par6Height;
   }

   public void setListener(GuiPageButtonList.GuiResponder guiResponder) {
      this.updateListener = guiResponder;
   }

   public void updateCursorCounter() {
      this.cursorCounter++;
   }

   public void setText(String str) {
      if (this.validator.apply(str)) {
         if (str.length() > this.maxStringLength) {
            this.text = str.substring(0, this.maxStringLength);
         } else {
            this.text = str;
         }

         this.setCursorPositionEnd();
      }
   }

   public String getText() {
      return this.text;
   }

   public String getSelectedText() {
      int n = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
      int n2 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
      return this.text.substring(n, n2);
   }

   public void setValidator(Predicate<String> theValidator) {
      this.validator = theValidator;
   }

   public void writeText(String str) {
      String str2 = "";
      String filterAllowedCharacters = ChatAllowedCharacters.filterAllowedCharacters(str);
      int n = this.cursorPosition < this.selectionEnd ? this.cursorPosition : this.selectionEnd;
      int n2 = this.cursorPosition < this.selectionEnd ? this.selectionEnd : this.cursorPosition;
      int n3 = this.maxStringLength - this.text.length() - (n - n2);
      int n4 = 0;
      if (this.text.length() > 0) {
         str2 = str2 + this.text.substring(0, n);
      }

      if (n3 < filterAllowedCharacters.length()) {
         str2 = str2 + filterAllowedCharacters.substring(0, n3);
         n4 = n3;
      } else {
         str2 = str2 + filterAllowedCharacters;
         n4 = filterAllowedCharacters.length();
      }

      if (this.text.length() > 0 && n2 < this.text.length()) {
         str2 = str2 + this.text.substring(n2);
      }

      if (this.validator.apply(str2)) {
         this.text = str2;
         this.moveCursorBy(n - this.selectionEnd + n4);
         if (this.updateListener != null) {
            this.updateListener.setStringValue(this.id, this.text);
         }
      }
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

            if (this.validator.apply(str)) {
               this.text = str;
               if (flag) {
                  this.moveCursorBy(n);
               }

               if (this.updateListener != null) {
                  this.updateListener.setStringValue(this.id, this.text);
               }
            }
         }
      }
   }

   public int getId() {
      return this.id;
   }

   public int getNthWordFromCursor(int n) {
      return this.getNthWordFromPos(n, this.getCursorPosition());
   }

   public int getNthWordFromPos(int n, int n2) {
      return this.getWordSkipPosition(n, n2, true);
   }

   public int getWordSkipPosition(int n, int n2, boolean flag) {
      int n3 = n2;
      boolean flag2 = n < 0;
      int abs = Math.abs(n);

      for (int i = 0; i < abs; i++) {
         if (!flag2) {
            int n4 = this.text.length();
            n3 = this.text.indexOf(32, n3);
            if (n3 == -1) {
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

   public void moveCursorBy(int n) {
      this.setCursorPosition(this.selectionEnd + n);
   }

   public void setCursorPosition(int n) {
      this.cursorPosition = n;
      int n2 = this.text.length();
      this.cursorPosition = MathHelper.clamp_int(this.cursorPosition, 0, n2);
      this.setSelectionPos(this.cursorPosition);
   }

   public void setCursorPositionZero() {
      this.setCursorPosition(0);
   }

   public void setCursorPositionEnd() {
      this.setCursorPosition(this.text.length());
   }

   public boolean textboxKeyTyped(char c, int n) {
      if (!this.isFocused) {
         return false;
      }

      if (GuiScreen.isKeyComboCtrlA(n)) {
         this.setCursorPositionEnd();
         this.setSelectionPos(0);
         return true;
      }

      if (GuiScreen.isKeyComboCtrlC(n)) {
         GuiScreen.setClipboardString(this.getSelectedText());
         return true;
      }

      if (GuiScreen.isKeyComboCtrlV(n)) {
         if (this.isEnabled) {
            this.writeText(GuiScreen.getClipboardString());
         }

         return true;
      } else if (GuiScreen.isKeyComboCtrlX(n)) {
         GuiScreen.setClipboardString(this.getSelectedText());
         if (this.isEnabled) {
            this.writeText("");
         }

         return true;
      } else {
         switch (n) {
            case 14:
               if (GuiScreen.isCtrlKeyDown()) {
                  if (this.isEnabled) {
                     this.deleteWords(-1);
                  }
               } else if (this.isEnabled) {
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
                  if (this.isEnabled) {
                     this.deleteWords(1);
                  }
               } else if (this.isEnabled) {
                  this.deleteFromCursor(1);
               }

               return true;
            default:
               if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                  if (this.isEnabled) {
                     this.writeText(Character.toString(c));
                  }

                  return true;
               } else {
                  return false;
               }
         }
      }
   }

   public void mouseClicked(int n, int n2, int n3) {
      boolean flag = n >= this.xPosition && n < this.xPosition + this.width && n2 >= this.yPosition && n2 < this.yPosition + this.height;
      if (this.canLoseFocus) {
         this.setFocused(flag);
      }

      if (this.isFocused && flag && n3 == 0) {
         int n4 = n - this.xPosition;
         if (this.enableBackgroundDrawing) {
            n4 -= 4;
         }

         String trimStringToWidth = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
         this.setCursorPosition(this.fontRendererInstance.trimStringToWidth(trimStringToWidth, n4).length() + this.lineScrollOffset);
      }
   }

   public void drawTextBox() {
      if (this.getVisible()) {
         if (this.getEnableBackgroundDrawing()) {
            drawRect(this.xPosition - 1, this.yPosition - 1, this.xPosition + this.width + 1, this.yPosition + this.height + 1, -6250336);
            drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, -16777216);
         }

         int n = this.isEnabled ? this.enabledColor : this.disabledColor;
         int n2 = this.cursorPosition - this.lineScrollOffset;
         int n3 = this.selectionEnd - this.lineScrollOffset;
         String trimStringToWidth = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), this.getWidth());
         boolean flag = n2 >= 0 && n2 <= trimStringToWidth.length();
         boolean flag2 = this.isFocused && this.cursorCounter / 6 % 2 == 0 && flag;
         int n4 = this.enableBackgroundDrawing ? this.xPosition + 4 : this.xPosition;
         int n5 = this.enableBackgroundDrawing ? this.yPosition + (this.height - 8) / 2 : this.yPosition;
         int n6 = n4;
         if (n3 > trimStringToWidth.length()) {
            n3 = trimStringToWidth.length();
         }

         if (trimStringToWidth.length() > 0) {
            String str = flag ? trimStringToWidth.substring(0, n2) : trimStringToWidth;
            n6 = this.fontRendererInstance.drawStringWithShadow(str, n4, n5, n);
         }

         boolean flag3 = this.cursorPosition < this.text.length() || this.text.length() >= this.getMaxStringLength();
         int n7 = n6;
         if (!flag) {
            n7 = n2 > 0 ? n4 + this.width : n4;
         } else if (flag3) {
            n7 = n6 - 1;
            n6--;
         }

         if (trimStringToWidth.length() > 0 && flag && n2 < trimStringToWidth.length()) {
            n6 = this.fontRendererInstance.drawStringWithShadow(trimStringToWidth.substring(n2), n6, n5, n);
         }

         if (flag2) {
            if (flag3) {
               Gui.drawRect(n7, n5 - 1, n7 + 1, n5 + 1 + this.fontRendererInstance.FONT_HEIGHT, -3092272);
            } else {
               this.fontRendererInstance.drawStringWithShadow("_", n7, n5, n);
            }
         }

         if (n3 != n2) {
            int n8 = n4 + this.fontRendererInstance.getStringWidth(trimStringToWidth.substring(0, n3));
            this.drawCursorVertical(n7, n5 - 1, n8 - 1, n5 + 1 + this.fontRendererInstance.FONT_HEIGHT);
         }
      }
   }

   public void drawCursorVertical(int n, int n2, int n3, int n4) {
      if (n < n3) {
         int n5 = n;
         n = n3;
         n3 = n5;
      }

      if (n2 < n4) {
         int n5 = n2;
         n2 = n4;
         n4 = n5;
      }

      if (n3 > this.xPosition + this.width) {
         n3 = this.xPosition + this.width;
      }

      if (n > this.xPosition + this.width) {
         n = this.xPosition + this.width;
      }

      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      GlStateManager.color(0.0F, 0.0F, 255.0F, 255.0F);
      GlStateManager.disableTexture2D();
      GlStateManager.enableColorLogic();
      GlStateManager.colorLogicOp(5387);
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(n, n4, 0.0).endVertex();
      worldRenderer.pos(n3, n4, 0.0).endVertex();
      worldRenderer.pos(n3, n2, 0.0).endVertex();
      worldRenderer.pos(n, n2, 0.0).endVertex();
      instance.draw();
      GlStateManager.disableColorLogic();
      GlStateManager.enableTexture2D();
   }

   public void setMaxStringLength(int n) {
      this.maxStringLength = n;
      if (this.text.length() > n) {
         this.text = this.text.substring(0, n);
      }
   }

   public int getMaxStringLength() {
      return this.maxStringLength;
   }

   public int getCursorPosition() {
      return this.cursorPosition;
   }

   public boolean getEnableBackgroundDrawing() {
      return this.enableBackgroundDrawing;
   }

   public void setEnableBackgroundDrawing(boolean flag) {
      this.enableBackgroundDrawing = flag;
   }

   public void setTextColor(int n) {
      this.enabledColor = n;
   }

   public void setDisabledTextColour(int n) {
      this.disabledColor = n;
   }

   public void setFocused(boolean flag) {
      if (flag && !this.isFocused) {
         this.cursorCounter = 0;
      }

      this.isFocused = flag;
   }

   public boolean isFocused() {
      return this.isFocused;
   }

   public void setEnabled(boolean flag) {
      this.isEnabled = flag;
   }

   public int getSelectionEnd() {
      return this.selectionEnd;
   }

   public int getWidth() {
      return this.getEnableBackgroundDrawing() ? this.width - 8 : this.width;
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
      if (this.fontRendererInstance != null) {
         if (this.lineScrollOffset > n2) {
            this.lineScrollOffset = n2;
         }

         int width = this.getWidth();
         String trimStringToWidth = this.fontRendererInstance.trimStringToWidth(this.text.substring(this.lineScrollOffset), width);
         int n3 = trimStringToWidth.length() + this.lineScrollOffset;
         if (n == this.lineScrollOffset) {
            this.lineScrollOffset = this.lineScrollOffset - this.fontRendererInstance.trimStringToWidth(this.text, width, true).length();
         }

         if (n > n3) {
            this.lineScrollOffset += n - n3;
         } else if (n <= this.lineScrollOffset) {
            this.lineScrollOffset = this.lineScrollOffset - (this.lineScrollOffset - n);
         }

         this.lineScrollOffset = MathHelper.clamp_int(this.lineScrollOffset, 0, n2);
      }
   }

   public void setCanLoseFocus(boolean flag) {
      this.canLoseFocus = flag;
   }

   public boolean getVisible() {
      return this.visible;
   }

   public void setVisible(boolean flag) {
      this.visible = flag;
   }
}
