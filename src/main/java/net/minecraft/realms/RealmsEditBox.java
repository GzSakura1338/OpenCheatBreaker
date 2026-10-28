package net.minecraft.realms;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.opengl.GL11;

public class RealmsEditBox {
    public static final int BACKWARDS = -1;
    public static final int CURSOR_INSERT_COLOR = -3092272;
    public static final String CURSOR_APPEND_CHARACTER = "_";
    public String value = "";
    public int maxLength = 32;
    public boolean bordered = true;
    public boolean canLoseFocus = true;
    public boolean isEditable = true;
    public int textColor = 14737632;
    public int textColorUneditable = 7368816;
    public int frame;
    public boolean visible = true;
    public FontRenderer font;
    public boolean inFocus;
    public int x;
    public int displayPos;
    public int cursorPos;
    public int highlightPos;
    public int y;
    public int width;
    public int height;
    public static final int CONSTANT_1 = 1;
    public static final int CONSTANT_1_2 = 1;

    public RealmsEditBox(int n, int n2, int n3, int n4) {
        this(Minecraft.getMinecraft().fontRendererObj, n, n2, n3, n4);
    }

    public RealmsEditBox(FontRenderer fontRenderer, int n, int n2, int n3, int n4) {
        this.font = fontRenderer;
        this.x = n;
        this.y = n2;
        this.width = n3;
        this.height = n4;
    }

    public void tick() {
        this.frame++;
    }

    public void setValue(String str) {
        if (str.length() > this.maxLength) {
            this.value = str.substring(0, this.maxLength);
        } else {
            this.value = str;
        }

        this.moveCursorToEnd();
    }

    public String getValue() {
        return this.value;
    }

    public String getHighlighted() {
        int n = this.cursorPos < this.highlightPos ? this.cursorPos : this.highlightPos;
        int n2 = this.cursorPos < this.highlightPos ? this.highlightPos : this.cursorPos;
        return this.value.substring(n, n2);
    }

    public void insertText(String str) {
        String str2 = "";
        String filterAllowedCharacters = ChatAllowedCharacters.filterAllowedCharacters(str);
        int n = this.cursorPos < this.highlightPos ? this.cursorPos : this.highlightPos;
        int n2 = this.cursorPos < this.highlightPos ? this.highlightPos : this.cursorPos;
        int n3 = this.maxLength - this.value.length() - (n - this.highlightPos);
        boolean flag = false;
        if (this.value.length() > 0) {
            str2 = str2 + this.value.substring(0, n);
        }

        int n4;
        if (n3 < filterAllowedCharacters.length()) {
            str2 = str2 + filterAllowedCharacters.substring(0, n3);
            n4 = n3;
        } else {
            str2 = str2 + filterAllowedCharacters;
            n4 = filterAllowedCharacters.length();
        }

        if (this.value.length() > 0 && n2 < this.value.length()) {
            str2 = str2 + this.value.substring(n2);
        }

        this.value = str2;
        this.moveCursor(n - this.highlightPos + n4);
    }

    public int getWordPosition(int n) {
        return this.getWordPosition(n, this.getCursorPosition());
    }

    public int getWordPosition(int n, int n2) {
        return this.getWordPosition(n, this.getCursorPosition(), true);
    }

    public int getWordPosition(int n, int n2, boolean flag) {
        int n3 = n2;
        boolean flag2 = n < 0;
        int abs = Math.abs(n);

        for (int i = 0; i < abs; i++) {
            if (!flag2) {
                int n4 = this.value.length();
                n3 = this.value.indexOf(32, n3);
                if (n3 == -1) {
                    n3 = n4;
                } else {
                    while (flag && n3 < n4 && this.value.charAt(n3) == ' ') {
                        n3++;
                    }
                }
            } else {
                while (flag && n3 > 0 && this.value.charAt(n3 - 1) == ' ') {
                    n3--;
                }

                while (n3 > 0 && this.value.charAt(n3 - 1) != ' ') {
                    n3--;
                }
            }
        }

        return n3;
    }

    public void moveCursor(int n) {
        this.moveCursorTo(this.highlightPos + n);
    }

    public void moveCursorTo(int n) {
        this.cursorPos = n;
        int n2 = this.value.length();
        if (this.cursorPos < 0) {
            this.cursorPos = 0;
        }

        if (this.cursorPos > n2) {
            this.cursorPos = n2;
        }

        this.setHighlightPos(this.cursorPos);
    }

    public void moveCursorToStart() {
        this.moveCursorTo(0);
    }

    public void moveCursorToEnd() {
        this.moveCursorTo(this.value.length());
    }

    public boolean keyPressed(char c, int n) {
        if (!this.inFocus) {
            return false;
        }

        switch (c) {
            case '\u0001':
                this.moveCursorToEnd();
                this.setHighlightPos(0);
                return true;
            case '\u0003':
                GuiScreen.setClipboardString(this.getHighlighted());
                return true;
            case '\u0016':
                if (this.isEditable) {
                    this.insertText(GuiScreen.getClipboardString());
                }

                return true;
            case '\u0018':
                GuiScreen.setClipboardString(this.getHighlighted());
                if (this.isEditable) {
                    this.insertText("");
                }

                return true;
            default:
                switch (n) {
                    case 14:
                        if (GuiScreen.isCtrlKeyDown()) {
                            if (this.isEditable) {
                                this.process(-1);
                            }
                        } else if (this.isEditable) {
                            this.process2(-1);
                        }

                        return true;
                    case 199:
                        if (GuiScreen.isShiftKeyDown()) {
                            this.setHighlightPos(0);
                        } else {
                            this.moveCursorToStart();
                        }

                        return true;
                    case 203:
                        if (GuiScreen.isShiftKeyDown()) {
                            if (GuiScreen.isCtrlKeyDown()) {
                                this.setHighlightPos(this.getWordPosition(-1, this.getHighlightPos()));
                            } else {
                                this.setHighlightPos(this.getHighlightPos() - 1);
                            }
                        } else if (GuiScreen.isCtrlKeyDown()) {
                            this.moveCursorTo(this.getWordPosition(-1));
                        } else {
                            this.moveCursor(-1);
                        }

                        return true;
                    case 205:
                        if (GuiScreen.isShiftKeyDown()) {
                            if (GuiScreen.isCtrlKeyDown()) {
                                this.setHighlightPos(this.getWordPosition(1, this.getHighlightPos()));
                            } else {
                                this.setHighlightPos(this.getHighlightPos() + 1);
                            }
                        } else if (GuiScreen.isCtrlKeyDown()) {
                            this.moveCursorTo(this.getWordPosition(1));
                        } else {
                            this.moveCursor(1);
                        }

                        return true;
                    case 207:
                        if (GuiScreen.isShiftKeyDown()) {
                            this.setHighlightPos(this.value.length());
                        } else {
                            this.moveCursorToEnd();
                        }

                        return true;
                    case 211:
                        if (GuiScreen.isCtrlKeyDown()) {
                            if (this.isEditable) {
                                this.process(1);
                            }
                        } else if (this.isEditable) {
                            this.process2(1);
                        }

                        return true;
                    default:
                        if (ChatAllowedCharacters.isAllowedCharacter(c)) {
                            if (this.isEditable) {
                                this.insertText(Character.toString(c));
                            }

                            return true;
                        } else {
                            return false;
                        }
                }
        }
    }

    public void mouseClicked(int n, int n2, int n3) {
        boolean flag = n >= this.x && n < this.x + this.width && n2 >= this.y && n2 < this.y + this.height;
        if (this.canLoseFocus) {
            this.setFocus(flag);
        }

        if (this.inFocus && n3 == 0) {
            int n4 = n - this.x;
            if (this.bordered) {
                n4 -= 4;
            }

            String trimStringToWidth = this.font.trimStringToWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            this.moveCursorTo(this.font.trimStringToWidth(trimStringToWidth, n4).length() + this.displayPos);
        }
    }

    public void render() {
        if (this.isVisible()) {
            if (this.isBordered()) {
                Gui.drawRect(this.x - 1, this.y - 1, this.x + this.width + 1, this.y + this.height + 1, -6250336);
                Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -16777216);
            }

            int n = this.isEditable ? this.textColor : this.textColorUneditable;
            int n2 = this.cursorPos - this.displayPos;
            int n3 = this.highlightPos - this.displayPos;
            String trimStringToWidth = this.font.trimStringToWidth(this.value.substring(this.displayPos), this.getInnerWidth());
            boolean flag = n2 >= 0 && n2 <= trimStringToWidth.length();
            boolean flag2 = this.inFocus && this.frame / 6 % 2 == 0 && flag;
            int n4 = this.bordered ? this.x + 4 : this.x;
            int n5 = this.bordered ? this.y + (this.height - 8) / 2 : this.y;
            int n6 = n4;
            if (n3 > trimStringToWidth.length()) {
                n3 = trimStringToWidth.length();
            }

            if (trimStringToWidth.length() > 0) {
                String str = flag ? trimStringToWidth.substring(0, n2) : trimStringToWidth;
                n6 = this.font.drawShadowedString(str, n4, n5, n);
            }

            boolean flag3 = this.cursorPos < this.value.length() || this.value.length() >= this.getMaxLength();
            int n7 = n6;
            if (!flag) {
                n7 = n2 > 0 ? n4 + this.width : n4;
            } else if (flag3) {
                n7 = n6 - 1;
                n6--;
            }

            if (trimStringToWidth.length() > 0 && flag && n2 < trimStringToWidth.length()) {
                this.font.drawShadowedString(trimStringToWidth.substring(n2), n6, n5, n);
            }

            if (flag2) {
                if (flag3) {
                    Gui.drawRect(n7, n5 - 1, n7 + 1, n5 + 1 + this.font.FONT_HEIGHT, -3092272);
                } else {
                    this.font.drawShadowedString("_", n7, n5, n);
                }
            }

            if (n3 != n2) {
                int n8 = n4 + this.font.getStringWidth(trimStringToWidth.substring(0, n3));
                this.renderHighlight(n7, n5 - 1, n8 - 1, n5 + 1 + this.font.FONT_HEIGHT);
            }
        }
    }

    public void renderHighlight(int n, int n2, int n3, int n4) {
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

        if (n3 > this.x + this.width) {
            n3 = this.x + this.width;
        }

        if (n > this.x + this.width) {
            n = this.x + this.width;
        }

        Tessellator instance = Tessellator.instance;
        GL11.glColor4f(0.0F, 0.0F, 255.0F, 255.0F);
        GL11.glDisable(3553);
        GL11.glEnable(3058);
        GL11.glLogicOp(5387);
        instance.startDrawingQuads();
        instance.addVertex(n, n4, 0.0);
        instance.addVertex(n3, n4, 0.0);
        instance.addVertex(n3, n2, 0.0);
        instance.addVertex(n, n2, 0.0);
        instance.draw();
        GL11.glDisable(3058);
        GL11.glEnable(3553);
    }

    public void setMaxLength(int n) {
        this.maxLength = n;
        if (this.value.length() > n) {
            this.value = this.value.substring(0, n);
        }
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public int getCursorPosition() {
        return this.cursorPos;
    }

    public boolean isBordered() {
        return this.bordered;
    }

    public void setFocus(boolean flag) {
        if (flag && !this.inFocus) {
            this.frame = 0;
        }

        this.inFocus = flag;
    }

    public int getHighlightPos() {
        return this.highlightPos;
    }

    public int getInnerWidth() {
        return this.isBordered() ? this.width - 8 : this.width;
    }

    public void setHighlightPos(int n) {
        int n2 = this.value.length();
        if (n > n2) {
            n = n2;
        }

        if (n < 0) {
            n = 0;
        }

        this.highlightPos = n;
        if (this.font != null) {
            if (this.displayPos > n2) {
                this.displayPos = n2;
            }

            int innerWidth = this.getInnerWidth();
            String trimStringToWidth = this.font.trimStringToWidth(this.value.substring(this.displayPos), innerWidth);
            int n3 = trimStringToWidth.length() + this.displayPos;
            if (n == this.displayPos) {
                this.displayPos = this.displayPos - this.font.trimStringToWidth(this.value, innerWidth, true).length();
            }

            if (n > n3) {
                this.displayPos += n - n3;
            } else if (n <= this.displayPos) {
                this.displayPos = this.displayPos - (this.displayPos - n);
            }

            if (this.displayPos < 0) {
                this.displayPos = 0;
            }

            if (this.displayPos > n2) {
                this.displayPos = n2;
            }
        }
    }

    public boolean isVisible() {
        return this.visible;
    }

    public boolean canLoseFocus() {
        return this.canLoseFocus;
    }

    public int getTextColor() {
        return this.textColor;
    }

    public void process(int n) {
        if (this.value.length() != 0) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                this.process2(this.getWordPosition(n) - this.cursorPos);
            }
        }
    }

    public void process2(int n) {
        if (this.value.length() != 0) {
            if (this.highlightPos != this.cursorPos) {
                this.insertText("");
            } else {
                boolean flag = n < 0;
                int n2 = flag ? this.cursorPos + n : this.cursorPos;
                int n3 = flag ? this.cursorPos : this.cursorPos + n;
                String str = "";
                if (n2 >= 0) {
                    str = this.value.substring(0, n2);
                }

                if (n3 < this.value.length()) {
                    str = str + this.value.substring(n3);
                }

                this.value = str;
                if (flag) {
                    this.moveCursor(n);
                }
            }
        }
    }

    public void setTextColor(int n) {
        this.textColor = n;
    }

    public void setBordered(boolean flag) {
        this.bordered = flag;
    }

    public void setIsEditable(boolean flag) {
        this.isEditable = flag;
    }

    public void setCanLoseFocus(boolean flag) {
        this.canLoseFocus = flag;
    }

    public boolean isEditable() {
        return this.isEditable;
    }

    public int getTextColorUneditable() {
        return this.textColorUneditable;
    }

    public void setVisible(boolean flag) {
        this.visible = flag;
    }

    public boolean isInFocus() {
        return this.inFocus;
    }

    public void setTextColorUneditable(int n) {
        this.textColorUneditable = n;
    }
}
