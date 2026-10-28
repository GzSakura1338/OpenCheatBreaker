package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiButton extends Gui {
    public static ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
    public int width = 200;
    public int height = 20;
    public boolean enabled = true;
    public boolean visible = true;
    public int id;
    public int xPosition;
    public int yPosition;
    public String displayString;
    public boolean hovered;

    public GuiButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public GuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        this.id = buttonId;
        this.xPosition = x;
        this.yPosition = y;
        this.width = widthIn;
        this.height = heightIn;
        this.displayString = buttonText;
    }

    public int getHoverState(boolean mouseOver) {
        byte b = 1;
        if (!this.enabled) {
            b = 0;
        } else if (mouseOver) {
            b = 2;
        }

        return b;
    }

    public void drawButton(Minecraft mc, int mouseX, int mouseY) throws java.io.IOException {
        if (this.visible) {
            FontRenderer fontRendererObj = mc.fontRendererObj;
            mc.getTextureManager().bindTexture(buttonTextures);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int hoverState = this.getHoverState(this.hovered);
            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glBlendFunc(770, 771);
            drawTexturedRect(this.xPosition, this.yPosition, 0.0F, 46 + hoverState * 20, this.width / 2, this.height);
            drawTexturedRect(this.xPosition + this.width / 2, this.yPosition, 200 - this.width / 2, 46 + hoverState * 20, this.width / 2, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            int n = 14737632;
            if (!this.enabled) {
                n = 10526880;
            } else if (this.hovered) {
                n = 16777120;
            }

            this.drawCenteredString(fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, n);
        }
    }

    public void mouseDragged(Minecraft mc, int mouseX, int mouseY) throws java.io.IOException {
    }

    public void mouseReleased(int mouseX, int mouseY) {
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) throws java.io.IOException {
        return this.enabled
            && this.visible
            && mouseX >= this.xPosition
            && mouseY >= this.yPosition
            && mouseX < this.xPosition + this.width
            && mouseY < this.yPosition + this.height;
    }

    public boolean isMouseOver() {
        return this.hovered;
    }

    public void drawButtonForegroundLayer(int mouseX, int mouseY) {
    }

    public void playPressSound(SoundHandler soundHandlerIn) {
        soundHandlerIn.playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
    }

    public int getButtonWidth() {
        return this.width;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getHeight2() {
        return this.height;
    }
}
