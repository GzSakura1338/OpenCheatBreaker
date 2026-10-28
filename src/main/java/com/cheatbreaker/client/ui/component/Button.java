package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.font.CustomFontRenderer;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.ScreenEdge;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import net.minecraft.util.ResourceLocation;
import net.optifine.GlStateManager;

public class Button extends SettingComponent {
    public boolean selected;
    public CustomFontRenderer tooltipFont;
    public boolean clickable = true;
    public boolean hoverFill = true;
    public int hoverProgress = 0;
    public String tooltip = "";
    public boolean tinyIcon;
    public boolean filled;
    public boolean hasTooltip = false;
    public String hoverLabel;
    public ScreenEdge screenEdge = ScreenEdge.TOP;
    public String label;
    public boolean smallIcon;
    public int hoverColor;
    public boolean redText;
    public Panel panel;
    public CustomFontRenderer font;
    public boolean outlined;
    public boolean compact;

    public Button(Panel panel, String str, int n, int n2, int n3, int n4, int n5, float f, boolean flag) {
        this(CheatBreaker.getInstance().getFontManager().playBold22, panel, str, n, n2, n3, n4, n5, f);
        this.outlined = flag;
    }

    public Button(CustomFontRenderer customFontRenderer, Panel panel, String str, int n, int n2, int n3, int n4, int n5, float f, boolean flag, boolean flag2) {
        this(customFontRenderer, panel, str, n, n2, n3, n4, n5, f);
        this.outlined = flag;
        this.filled = flag2;
    }

    public Button(Panel panel, String str, int n, int n2, int n3, int n4, int n5, float f, String str2) {
        this(CheatBreaker.getInstance().getFontManager().playBold22, panel, str, n, n2, n3, n4, n5, f);
        this.tooltipFont = CheatBreaker.getInstance().getFontManager().playRegular14;
        this.tooltip = str2;
        this.hasTooltip = !str2.isEmpty();
    }

    public Button(Panel panel, String str, int n, int n2, int n3, int n4, int n5, float f, boolean flag, boolean flag2) {
        this(CheatBreaker.getInstance().getFontManager().playBold22, panel, str, n, n2, n3, n4, n5, f);
        this.outlined = flag;
        this.filled = flag2;
    }

    public void setTinyIcon(boolean flag) {
        this.tinyIcon = flag;
    }

    @Override
    public void render(int n, int n2, float f) {
        boolean isMouseOver = this.isMouseOver(n, n2);
        float hoverBorderThicknessValue = this.theme.getHoverBorderThicknessValue();
        byte b = 120;
        if (isMouseOver && this.clickable) {
            if (this.outlined) {
                RenderUtils.drawRoundedRectFilledPrecise(
                    this.x - hoverBorderThicknessValue,
                    this.y - hoverBorderThicknessValue,
                    this.x + this.width + hoverBorderThicknessValue,
                    this.y + this.height + hoverBorderThicknessValue,
                    this.theme.getButtonCornerRadiusValue(),
                    this.theme.getPrimaryBackgroundColorValue()
                );
            }

            float animationStep = ModsScreen.animationStep(790.0F);
            this.hoverProgress = this.hoverProgress + animationStep < b ? (int)(this.hoverProgress + animationStep) : b;
        } else if (this.hoverProgress > 0) {
            float animationStep = ModsScreen.animationStep(790.0F);
            this.hoverProgress = this.hoverProgress - animationStep < 0.0F ? 0 : (int)(this.hoverProgress - animationStep);
        }

        if (this.filled) {
            RenderUtils.drawRoundedRectFilledPrecise(
                this.x,
                this.y,
                this.x + this.width,
                this.y + this.height,
                this.theme.getButtonCornerRadiusValue(),
                this.selected ? this.theme.getSelectedButtonColorValue() : this.theme.getButtonBackgroundColorValue()
            );
        }

        if (this.outlined) {
            if (this.clickable) {
                RenderUtils.drawRoundedRectFilledPrecise(
                    this.x, this.y, this.x + this.width, this.y + this.height, this.theme.getButtonCornerRadiusValue(), this.theme.getPrimaryBackgroundColorValue()
                );
            } else {
                RenderUtils.drawRoundedRectFilledPrecise(
                    this.x, this.y, this.x + this.width, this.y + this.height, this.theme.getButtonCornerRadiusValue(), this.theme.getDisabledButtonColorValue()
                );
            }
        }

        if (this.isMouseOver(n, n2) && this.hasTooltip) {
            float stringWidth = this.tooltipFont.getStringWidth(this.tooltip);
            float lineHeight = this.tooltipFont.getLineHeight();
            float f2;
            float f3;
            float f4;
            float f5;
            switch (this.screenEdge) {
                case BOTTOM:
                    f2 = this.x + this.width / 2.0F - stringWidth / 2.0F - 2.0F;
                    f3 = this.y + this.height + 5.0F;
                    f4 = this.x + this.width / 2.0F + stringWidth / 2.0F + 2.0F;
                    f5 = f3 + lineHeight * 2.0F;
                    break;
                case TOP:
                default:
                    f2 = this.x + this.width / 2.0F - stringWidth / 2.0F - 2.0F;
                    f3 = this.y - 15.0F;
                    f4 = this.x + this.width / 2.0F + stringWidth / 2.0F + 2.0F;
                    f5 = f3 + lineHeight * 2.0F;
            }

            RenderUtils.drawRoundedRectFilledPrecise(f2, f3, f4, f5, this.theme.getButtonCornerRadiusValue(), 1342177280);
            this.tooltipFont.drawCenteredString(this.tooltip, f2 + stringWidth / 2.0F + 2.0F, f3, -1);
        }

        if (this.hoverProgress > 0 && this.hoverFill) {
            float f2 = (float)this.hoverProgress / b * 100.0F;
            RenderUtils.drawRoundedRectFilledPrecise(
                this.x,
                (int)(this.y + (this.height - this.height * f2 / 100.0F)),
                this.x + this.width,
                this.y + this.height,
                this.theme.getButtonCornerRadiusValue(),
                this.hoverColor
            );
        }

        if (this.label != null && this.label.contains(".png")) {
            float f2 = this.tinyIcon ? 4.0F : (this.smallIcon ? 5.0F : (this.compact ? 3.5F : 8.0F));
            float f3 = this.smallIcon ? this.x + 2.0F : (this.compact ? (float)(this.x + 1.6) : this.x + 6);
            float f4 = this.smallIcon ? this.y + 2.0F : (this.compact ? (float)(this.y + 1.7) : this.y + 6);
            GlStateManager.pushMatrix();
            float red = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getRed();
            float green = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getGreen();
            float blue = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getBlue();
            GlStateManager.color(red, green, blue, 0.45F);
            RenderUtils.drawSquareTextureViaRenderEngine(new ResourceLocation("client/icons/" + this.label), f2, f3, f4);
            GlStateManager.popMatrix();
        } else {
            float f2 = this.font == CheatBreaker.getInstance().getFontManager().playBold22 ? 2.0F : 0.5F;
            this.font
                .drawCenteredString(
                    this.hoverLabel != null && this.isMouseOver(n, n2) ? this.hoverLabel.toUpperCase() : this.label.toUpperCase(),
                    this.x + this.width / 2,
                    this.y + this.height / 2 - this.font.getLineHeight() + f2,
                    this.redText ? 11141120 : this.theme.getPrimaryTextColorValue()
                );
        }
    }

    public void setHoverLabel(String str) {
        this.hoverLabel = str;
    }

    public Button(CustomFontRenderer customFontRenderer, Panel panel, String str, int n, int n2, int n3, int n4, int n5, float f) {
        super(f);
        this.label = str;
        this.setBounds(n, n2, n3, n4);
        this.hoverColor = n5;
        this.panel = panel;
        this.font = customFontRenderer;
        this.outlined = true;
    }

    @Override
    public void mouseClicked(int n, int n2, int n3) {
    }

    public void setSelected(boolean flag) {
        this.selected = flag;
    }

    public void setCompact(boolean flag) {
        this.compact = flag;
    }

    public Button(String str) {
        super(1.0F);
        this.label = str;
    }

    public Button(Panel panel, String str, int n, int n2, int n3, int n4, int n5, float f) {
        this(CheatBreaker.getInstance().getFontManager().playBold22, panel, str, n, n2, n3, n4, n5, f);
        this.outlined = true;
    }

    public Button(Panel panel, String str, int n, int n2, int n3, int n4, int n5, float f, String str2, ScreenEdge screenEdge) {
        this(panel, str, n, n2, n3, n4, n5, f, str2);
        this.screenEdge = screenEdge;
    }

    public void setRedText(boolean flag) {
        this.redText = flag;
    }

    public void setSmallIcon(boolean flag) {
        this.smallIcon = flag;
    }

    public void setHoverFill(boolean flag) {
        this.hoverFill = flag;
    }

    public void setClickable(boolean flag) {
        this.clickable = flag;
    }
}
