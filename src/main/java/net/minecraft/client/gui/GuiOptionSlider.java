package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings.Options;
import org.lwjgl.opengl.GL11;

public class GuiOptionSlider extends GuiButton {
    public float sliderValue = 1.0F;
    public boolean dragging;
    public Options options;
    public float min;
    public float max;

    public GuiOptionSlider(int n, int n2, int n3, Options options) {
        this(n, n2, n3, options, 0.0F, 1.0F);
    }

    public GuiOptionSlider(int n, int n2, int n3, Options options, float f, float f2) {
        super(n, n2, n3, 150, 20, "");
        this.options = options;
        this.min = f;
        this.max = f2;
        Minecraft minecraft = Minecraft.getMinecraft();
        this.sliderValue = options.normalizeValue(minecraft.gameSettings.getOptionFloatValue(options));
        this.displayString = minecraft.gameSettings.getKeyBinding(options);
    }

    @Override
    public int getHoverState(boolean mouseOver) {
        return 0;
    }

    @Override
    public void mouseDragged(Minecraft mc, int mouseX, int mouseY) throws java.io.IOException {
        if (this.visible) {
            if (this.dragging) {
                this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
                if (this.sliderValue < 0.0F) {
                    this.sliderValue = 0.0F;
                }

                if (this.sliderValue > 1.0F) {
                    this.sliderValue = 1.0F;
                }

                float denormalizeValue = this.options.denormalizeValue(this.sliderValue);
                mc.gameSettings.setOptionFloatValue(this.options, denormalizeValue);
                this.sliderValue = this.options.normalizeValue(denormalizeValue);
                this.displayString = mc.gameSettings.getKeyBinding(this.options);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            drawTexturedRect(this.xPosition + (int)(this.sliderValue * (this.width - 8)), this.yPosition, 0.0F, 66.0F, 4, 20);
            drawTexturedRect(this.xPosition + (int)(this.sliderValue * (this.width - 8)) + 4, this.yPosition, 196.0F, 66.0F, 4, 20);
        }
    }

    @Override
    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) throws java.io.IOException {
        if (super.mousePressed(mc, mouseX, mouseY)) {
            this.sliderValue = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
            if (this.sliderValue < 0.0F) {
                this.sliderValue = 0.0F;
            }

            if (this.sliderValue > 1.0F) {
                this.sliderValue = 1.0F;
            }

            mc.gameSettings.setOptionFloatValue(this.options, this.options.denormalizeValue(this.sliderValue));
            this.displayString = mc.gameSettings.getKeyBinding(this.options);
            this.dragging = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY) {
        this.dragging = false;
    }
}
